package com.mastercard.simplifynymisample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mastercard.lib.model.CreditCard;
import com.mastercard.lib.nymi.NymiAuthentication;
import com.mastercard.lib.simplify.SimplifyPayment;
import com.mastercard.lib.utils.ResponseCodes;

public class MainActivity extends Activity
{

	private ProgressBar progress;
	private Context mContext;
	private Button makePaymentBtn;
	private NymiAuthentication nymiAuth;
	private Handler myTaskHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		myTaskHandler = new Handler();
		progress = (ProgressBar)findViewById(R.id.progressFooter);
		makePaymentBtn = (Button) findViewById(R.id.btnMakePayment);
		makePaymentBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				nymiAuth = new NymiAuthentication(getApplicationContext(),messageHandler);
				nymiAuth.init();
				progress.setVisibility(View.VISIBLE);
				makePaymentBtn.setClickable(false);
			}
		});
	}

	public Handler messageHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch(msg.what){
			case ResponseCodes.RESPONSE_SUCCESS:
				// custom dialog
				final Dialog dialog = new Dialog(mContext,android.R.style.Theme_Holo_Light_Dialog);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialog.setContentView(R.layout.custom);
				dialog.setTitle("Confirm Payment");
				final CreditCard ccDetailsObj = (CreditCard) msg.obj;

				TextView text_CC = (TextView) dialog.findViewById(R.id.textView_CC);
				text_CC.setText("Credit Card No: XXXXXXXXXXXX"+ ccDetailsObj.getLastFourDigits());

				TextView text_CVV = (TextView) dialog.findViewById(R.id.textView_CVV);
				text_CVV.setText("CVV No: "+ ccDetailsObj.getCvv());


				TextView ExpirationDate = (TextView) dialog.findViewById(R.id.textView_ExpDate);
				ExpirationDate.setText("ExpirationDate: "+ ccDetailsObj.getExpMonth() + "/" + ccDetailsObj.getExpYear());

				Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
				// if button is clicked, close the custom dialog
				buttonOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (ccDetailsObj.getCcNo()!=null) {
							SimplifyPayment payment = new SimplifyPayment();
							payment.makePayment(ccDetailsObj,paymentMessageHandler);      
							dialog.dismiss();
							progress.setVisibility(View.VISIBLE);
						}else{
							sendResponse("Could not find Credit Card details!");
						}

					}
				});
				Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
				// if button is clicked, close the custom dialog
				buttonCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						sendResponse("Payment Cancelled !");
						dialog.dismiss();
					}
				}); 

				dialog.show();	
				progress.setVisibility(View.GONE);
				break;
			case ResponseCodes.RESPONSE_FAILURE:
				sendResponse("Unknown error: Nymi Authentication Failed, Try Again!");
				break;
			case ResponseCodes.AGREEMENT_FAILURE:
				sendResponse("Nymi Agreement Failed, Try Again!");
				break;
			case ResponseCodes.DISCOVER_FAILURE:
				sendResponse("Nymi Discover Failed, Try Again!");
				myTaskHandler.removeCallbacks(discoverTask);
				break;
			case ResponseCodes.INIT_FAILURE:
				sendResponse("Nymi Init Failed, Try Again!");
				break;
			case ResponseCodes.LOAD_PROVISION_EXCEPTION:
				sendResponse("Loading Nymi Provision Failed!");
				break;
			case ResponseCodes.SAVE_PROVISION_EXCEPTION:
				sendResponse("Saving Nymi Provision Failed!");
				break;
			case ResponseCodes.FIND_FAILURE:				
				sendResponse("Finding Nymi Provision Failed!");
				myTaskHandler.removeCallbacks(foundTask);
				break;
			case ResponseCodes.INIT_SUCCESS:				
				if (nymiAuth!=null) {
					myTaskHandler.postDelayed(foundTask, 15000);
					nymiAuth.find();
				}
				break;
			case ResponseCodes.NOT_FOUND:
				if (nymiAuth!=null) {
					myTaskHandler.removeCallbacks(foundTask);
					myTaskHandler.postDelayed(discoverTask, 30000);
					nymiAuth.discover();
				}
				break;
			case ResponseCodes.NOT_DISCOVERED:
				sendResponse("Nymi Not Found in range!");

				myTaskHandler.removeCallbacks(discoverTask);

				if (nymiAuth!=null) {
					nymiAuth.disconnect();
				}
				break;
			case ResponseCodes.DISCOVER_SUCCESS:				
				if (nymiAuth!=null) {
					myTaskHandler.removeCallbacks(discoverTask);
				}
				break;
			case ResponseCodes.FIND_SUCCESS:				
				if (nymiAuth!=null) {
					myTaskHandler.removeCallbacks(foundTask);
					nymiAuth.validate();
				}
			case ResponseCodes.AGREE_SUCCESS:				
				if (nymiAuth!=null) {
					nymiAuth.provision();
				}
				break;
			case ResponseCodes.ERROR_READING_IP:			
				sendResponse("Could not read IP from File. Please Make Sure NymiPayTextFile Exists in SD card!");
				break;
			}
		}
	};
	public Handler paymentMessageHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Bundle bundle = msg.getData();

			Intent intent = new Intent(mContext,PaymentResponseActivity.class);
			intent.putExtra("Response", "Status Code: "+bundle.getLong("responseCode")+" Response Message: "+bundle.getString("responseMessage"));
			startActivity(intent);
			finish();
			progress.setVisibility(View.GONE);
			makePaymentBtn.setClickable(true);


		}
	};
	Runnable foundTask = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = ResponseCodes.NOT_FOUND;
			messageHandler.sendMessage(msg);
		}
	};

	Runnable discoverTask = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = ResponseCodes.NOT_DISCOVERED;
			messageHandler.sendMessage(msg);
		}
	};

	private void sendResponse(String message){
		Intent i = new Intent(mContext,PaymentResponseActivity.class);
		i.putExtra("Response", message);
		startActivity(i);
		finish();
		makePaymentBtn.setClickable(true);
		progress.setVisibility(View.GONE);
	}

}
