/**
 * 
 */
package com.mastercard.lib.simplify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mastercard.lib.model.CreditCard;
import com.mastercard.lib.utils.Macro;
import com.mastercard.lib.utils.Utils;
import com.simplify.android.sdk.Simplify;
import com.simplify.android.sdk.model.Card;
import com.simplify.android.sdk.model.SimplifyError;
import com.simplify.android.sdk.model.Token;

/**
 * Class representing the main communication entry point for the Simplify make
 * payment API
 * 
 * @author sonal.agarwal
 * 
 */
public class SimplifyPayment {

	private Simplify mSimplify;
	private static final String TAG = SimplifyPayment.class.getSimpleName();
	private Handler mHandler;

	/**
	 * Makes the Payment using Simplify Server. This method returns
	 * responseMessage and responseCode.
	 * 
	 * @param ccObj
	 *            Credit Card Object
	 * @param paymentHandler
	 *            Message Handler to send response back to the user
	 */
	public void makePayment(CreditCard ccObj, final Handler paymentHandler) {
		mHandler = paymentHandler;

		mSimplify = new Simplify(Macro.PUBLIC_KEY);

		final Card card = new Card().setNumber(ccObj.getCcNo()).setCvc(ccObj.getCvv()).setYear(ccObj.getExpYear()).setMonth(ccObj.getExpMonth());

		mSimplify.createCardToken(card, new Simplify.CreateTokenListener() {
			@Override
			public void onSuccess(final Token token) {
				Utils.writeToAppLog(TAG, "Created Token: " + token.getId(), Macro.INFO_LOG_LEVEL);
				new Thread(new Runnable() {
					@Override
					public void run() {
						chargeToken(token.getId());
					}
				}).start();
			}

			@Override
			public void onError(SimplifyError error) {
				Utils.writeToAppLog(TAG, "Error Creating Token: " + error.getStatusCode(), Macro.ERROR_LOG_LEVEL);
				Utils.writeToAppLog(TAG, "Error Creating Token: " + error.getStatusCode(), Macro.ERROR_LOG_LEVEL);
				Utils.writeToAppLog(TAG, "Error Creating Token: " + error.getStatusCode(), Macro.ERROR_LOG_LEVEL);
				// Send response code and status code to the user
				sendResponseCode(error.getStatusCode(), error.getMessage());

			}
		});
	}

	/**
	 * This method sends the Simplify Token to Server for Payment.
	 * 
	 * @param tokenId
	 *            Simplify token generated from Credit Card Details
	 * 
	 */
	private void chargeToken(String tokenId) {
		try {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Macro.SIMPLIFY_URL);

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("simplifyToken", tokenId));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httpPost);
			String text = EntityUtils.toString(response.getEntity());

			Utils.writeToAppLog(TAG, "response: " + text, Macro.INFO_LOG_LEVEL);
			Utils.writeToAppLog(TAG, "response code: " + response.getStatusLine().getStatusCode(), Macro.INFO_LOG_LEVEL);
			Utils.writeToAppLog(TAG, "reason: " + response.getStatusLine().getReasonPhrase(), Macro.INFO_LOG_LEVEL);

			sendResponseCode(response.getStatusLine().getStatusCode(), text);

		} catch (IOException e) {
			e.printStackTrace();
			// Send response code and status code to the user
			sendResponseCode(-1, e.getMessage());
		}

	}

	/**
	 * This method Send response code and status code to the user
	 * 
	 * @param responseCode
	 * @param responseMessage
	 */
	private void sendResponseCode(int responseCode, String responseMessage) {
		// Send response code and status code to the user
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("responseMessage", responseMessage);
		bundle.putLong("responseCode", responseCode);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
}
