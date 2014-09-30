/*
 * 
 */
package com.mastercard.lib.nymi;

import java.util.ArrayList;
import java.util.HashMap;

import nclSDK.Ncl;
import nclSDK.NclBool;
import nclSDK.NclCallback;
import nclSDK.NclEvent;
import nclSDK.NclEventType;
import nclSDK.NclMode;
import nclSDK.NclProvision;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.mastercard.lib.model.CreditCard;
import com.mastercard.lib.utils.Macro;
import com.mastercard.lib.utils.ResponseCodes;
import com.mastercard.lib.utils.Utils;

/**
 * <ul>
 * <li>Class representing the main communication entry point for the Nymi Band
 * Authentication API</li>
 * <li>Below are the responses if any Nymi authentication step fails:</li>
 * <li>RESPONSE_SUCCESS = 0</li>
 * <li>RESPONSE_FAILURE = 1</li>
 * <li>AGREEMENT_FAILURE = 2</li>
 * <li>DISCOVER_FAILURE = 3</li>
 * <li>INIT_FAILURE = 4</li>
 * <li>LOAD_PROVISION_EXCEPTION = 5</li>
 * <li>SAVE_PROVISION_EXCEPTION = 6</li>
 * <li>FIND_FAILURE = 7</li>
 * <li>INIT_SUCCESS = 8</li>	
 * <li>NOT_FOUND = 9</li>
 * <li>NOT_DISCOVERED = 10</li>
 * <li>DISCOVER_SUCCESS = 11</li>	
 * <li>FIND_SUCCESS = 12</li>	
 * <li>AGREE_SUCCESS = 13</li>	
 * <li>ERROR_READING_IP = 14</li>	
 * </ul>
 * 
 * @author sonal.agarwal
 * 
 */
public class NymiAuthentication{

	public int nymiHandle = 0;
	public ArrayList<NclProvision> provisions;
	private HashMap<String, CreditCard> map;
	private CreditCard ccDetails;
	private Context mContext;
	private Handler mMessageHandler;

	private static final String TAG = NymiAuthentication.class.getSimpleName();

	/**
	 * This is the constructor which initiates the Nymi authentication.
	 * 
	 * @param context
	 *            application context
	 * @param msgHandler
	 *            handler to send response back to the user
	 */
	public NymiAuthentication(Context context, Handler msgHandler) {

		mContext = context;
		mMessageHandler = msgHandler;

		provisions = new ArrayList<NclProvision>();
		map = new HashMap<String, CreditCard>();
		ccDetails = new CreditCard();

		// load any provisions if saved
		loadProvisions();

	}

	/**
	 * This method loads the library and initiates the Nymi auth process
	 */
	public void init() {
		// Load library
		try {
			String ip = Utils.getStringFromFile(Macro.fileName);
			if (ip!=null) {
				Ncl.InitiateLibrary(mContext, ip, Macro.port);
				final NclCallback cb = new NclCallback(this, "handleCallBack", NclEventType.NCL_EVENT_ANY);

				new Thread(new Runnable() {

					@Override
					public void run() {

						// set error log path
						final String path = Environment.getExternalStorageDirectory() + "";
						if (path != null) {
							// Initiate library
							Boolean IsInit = Ncl.init(cb, null, "MasterCard", NclMode.NCL_MODE_DEV, path + "/MasterCardLog.txt");
							if (!IsInit) {
								sendResponseMessage(ResponseCodes.INIT_FAILURE);
							}
						}

					}
				}).start();
			}else {
				sendResponseMessage(ResponseCodes.ERROR_READING_IP);
			}
		} catch (Exception e) {
			sendResponseMessage(ResponseCodes.ERROR_READING_IP);
			e.printStackTrace();
		}

	}

	/**
	 * This method discovers new Nymi if no Nymi is provisioned
	 */
	public void discover() {
		// search for new Nymi
		Boolean b = Ncl.startDiscovery();
		if (!b) {
			sendResponseMessage(ResponseCodes.DISCOVER_FAILURE);
		}
	}

	/**
	 * This method searches for already provisioned Nymi
	 */
	public void find() {
		// if already provisioned find the already provisioned Nymi
		// at a time only one Nymi will be provisioned
		if (provisions != null && provisions.size() > 0) {
			Ncl.startFinding(provisions, provisions.size(), NclBool.NCL_FALSE);
		}else{
			Message msg = new Message();
			msg.what = ResponseCodes.NOT_FOUND;
			mMessageHandler.sendMessage(msg);
		}
	}



	/**
	 * This method validates the initially provisioned and found Nymi
	 */
	public void validate() {
		// validation with found Nymi
		Ncl.validate(nymiHandle);
	}

	/**
	 * This method disconnects Nymi connection
	 */
	public void disconnect() {
		// disconnects Nymi connection
		if (nymiHandle!=0) {
			Ncl.disconnect(nymiHandle);
		}
	}

	/**
	 * This method provision the discovered Nymi
	 */
	public void provision() {		
		// provision the discovered Nymi
		Ncl.provision(nymiHandle);
	}

	/**
	 * This Method send the Error Message to the User
	 * 
	 * @param responseCode
	 */
	private void sendResponseMessage(int responseCode) {
		Message msg = new Message();
		msg.what = responseCode;
		mMessageHandler.sendMessage(msg);
	}

	/**
	 * This method saves the Nymi Provision Key,Id and Credit Card Details in
	 * Shared Preferences
	 * 
	 */
	private void save() {

		JSONArray jArray = new JSONArray();

		for (NclProvision provision : provisions) {

			JSONArray jKey = new JSONArray();
			JSONArray jId = new JSONArray();

			for (int a = 0; a < NclProvision.NCL_PROVISION_KEY_SIZE; a++) {
				jKey.put(provision.key[a]);
				jId.put(provision.id[a]);
			}

			try {
				JSONObject creditCardDetails = new JSONObject();
				creditCardDetails.put("CC_No", Macro.CC_No);
				creditCardDetails.put("CVV", Macro.CVV);
				creditCardDetails.put("Exp_Month", Macro.Exp_Month);
				creditCardDetails.put("Exp_Year", Macro.Exp_Year);
				creditCardDetails.put("lastFourDigits", Macro.lastFourDigits);
				JSONObject jPro = new JSONObject();
				jPro.putOpt("key", jKey);
				jPro.putOpt("id", jId);
				jPro.putOpt("ccDetails", creditCardDetails);
				jArray.put(jPro);
			} catch (JSONException e) {
				e.printStackTrace();
				sendResponseMessage(ResponseCodes.SAVE_PROVISION_EXCEPTION);
			}
		}

		JSONObject jObj = new JSONObject();

		try {
			jObj.put("provisions", jArray);
			Utils.writeToAppLog(TAG, "Java Code Save" + jObj.toString(), Macro.DEBUG_LOG_LEVEL);
		} catch (JSONException e) {
			e.printStackTrace();
			sendResponseMessage(ResponseCodes.SAVE_PROVISION_EXCEPTION);
		}

		SharedPreferences prefs = mContext.getSharedPreferences("MasterPass", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("provisions", jObj.toString());
		editor.commit();
	}

	/**
	 * This method fetches the saved Provision values
	 */
	private void loadProvisions() {
		final SharedPreferences prefs = mContext.getSharedPreferences("MasterPass", Context.MODE_PRIVATE);
		String str = prefs.getString("provisions", "NULL");

		if (!str.equals("NULL")) {
			loadSavedProvisions(str);
		}
	}

	/**
	 * This method gets theCredit Card Details corresponding to the
	 * authenticated Nymi user credentials
	 * 
	 * @param c
	 *            Detected Nymi's provision Id
	 */
	private void loadCrediCardDetails(char[] c) {
		for (int i = 0; i < provisions.size(); i++) {
			NclProvision provision = provisions.get(i);
			if (provision != null) {
				// if the current Nymi user is already provisioned
				String provisionIdStr = Utils.someWhatReadable(provision.id);
				if (Utils.someWhatReadable(c).equalsIgnoreCase(provisionIdStr)) {
					ccDetails = map.get(provisionIdStr);
					break;
				}
			}
		}
	}

	/**
	 * This method gets the saved Provision values
	 * 
	 * @param str
	 *            Stored provision object
	 */
	private void loadSavedProvisions(String str) {

		try {
			JSONArray jKey = new JSONArray();
			JSONArray jId = new JSONArray();
			JSONObject creditCardDetails = new JSONObject();
			JSONObject jObj = new JSONObject(str);
			JSONArray jArray = jObj.getJSONArray("provisions");

			int len = jArray.length();

			for (int i = 0; i < len; i++) {

				JSONObject jPro = jArray.getJSONObject(i);
				jKey = jPro.getJSONArray("key");
				jId = jPro.getJSONArray("id");
				creditCardDetails = jPro.getJSONObject("ccDetails");
				Utils.writeToAppLog(TAG, "Java Code getSavedProvisions" + jPro.toString(), Macro.DEBUG_LOG_LEVEL);

				NclProvision provisonObj = new NclProvision();
				CreditCard cc_Obj = new CreditCard();

				for (int a = 0; a < NclProvision.NCL_PROVISION_KEY_SIZE; a++) {

					provisonObj.key[a] = (char) jKey.getInt(a);
					provisonObj.id[a] = (char) jId.getInt(a);

				}

				cc_Obj.setCcNo(creditCardDetails.getString("CC_No"));
				cc_Obj.setCvv(creditCardDetails.getString("CVV"));
				cc_Obj.setExpMonth(creditCardDetails.getString("Exp_Month"));
				cc_Obj.setExpYear(creditCardDetails.getString("Exp_Year"));
				cc_Obj.setLastFourDigits(creditCardDetails.getString("lastFourDigits"));

				if (provisions != null) {
					provisions.add(provisonObj);
				}

				if (map != null) {
					map.put(Utils.someWhatReadable(provisonObj.id), cc_Obj);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			sendResponseMessage(ResponseCodes.LOAD_PROVISION_EXCEPTION);
		}
	}

	/**
	 * This is a Nymi Callback which is called for all Nymi events
	 * 
	 * @param event
	 *            Nymi Event
	 * @param userData
	 *            Any data sent by the Nymi
	 */
	public void handleCallBack(NclEvent event, Object userData) {

		Utils.writeToAppLog(TAG, "NclEvent: " + NclEventType.values()[event.type], Macro.DEBUG_LOG_LEVEL);

		switch (NclEventType.values()[event.type]) {
		case NCL_EVENT_INIT: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_INIT Returned " + NclBool.values()[event.init.success] + "\n", Macro.INFO_LOG_LEVEL);

			if (event.init.success == 1) {
				// send init success response to user
				sendResponseMessage(ResponseCodes.INIT_SUCCESS);
			}else {
				// send init success response to user
				sendResponseMessage(ResponseCodes.INIT_FAILURE);
			}
			break;
		}
		case NCL_EVENT_DISCOVERY: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_DISCOVERY	Rssi: \n", Macro.INFO_LOG_LEVEL);
			nymiHandle = event.discovery.nymiHandle;
			// found Nymi stop scanning
			Ncl.stopScan();
			// send discover success response to the user
			Message msg = new Message();
			msg.what = ResponseCodes.DISCOVER_SUCCESS;
			mMessageHandler.sendMessage(msg);
			// When Nymi is discovered Agree to authenticate with it
			if (!Ncl.agree(nymiHandle)) {
				Utils.writeToAppLog(TAG, "nclAgree Failed\n", Macro.INFO_LOG_LEVEL);
				sendResponseMessage(ResponseCodes.AGREEMENT_FAILURE);
			}

			break;
		}
		case NCL_EVENT_FIND: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_FIND Rssi: \n", Macro.INFO_LOG_LEVEL);
			// get credit card details for the saved provision id
			loadCrediCardDetails(event.find.provisionId);
			// found Nymi stop scanning
			Ncl.stopScan();
			nymiHandle = event.find.nymiHandle;
			// send find success response to the user
			sendResponseMessage(ResponseCodes.FIND_SUCCESS);

			break;
		}
		case NCL_EVENT_AGREEMENT: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_AGREEMENT\n", Macro.INFO_LOG_LEVEL);
			nymiHandle = event.agreement.nymiHandle;
			// send agree success response to the user
			sendResponseMessage(ResponseCodes.AGREE_SUCCESS);

			break;
		}
		case NCL_EVENT_PROVISION: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_PROVISION\n", Macro.INFO_LOG_LEVEL);

			if (provisions != null) {
				provisions.add(event.provision.provision);
				// save the provision in shared preference
				save();
				//load the credit card details mapped with this provision id
				provisions.clear();
				map.clear();
				loadProvisions();
				loadCrediCardDetails(event.provision.provision.id);
			}

			break;
		}
		case NCL_EVENT_VALIDATION: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_VALIDATION\n", Macro.INFO_LOG_LEVEL);
			nymiHandle = event.validation.nymiHandle;
			Ncl.disconnect(nymiHandle);

			break;
		}
		case NCL_EVENT_DISCONNECTION: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_DISCONNECTION\n", Macro.INFO_LOG_LEVEL);

			Message msg = new Message();
			msg.what = ResponseCodes.RESPONSE_SUCCESS;
			msg.obj = ccDetails;
			mMessageHandler.sendMessage(msg);

			break;
		}
		case NCL_EVENT_ERROR: {
			Utils.writeToAppLog(TAG, "NCL_EVENT_ERROR\n", Macro.INFO_LOG_LEVEL);
			sendResponseMessage(ResponseCodes.RESPONSE_FAILURE);

			break;
		}
		default:
			Utils.writeToAppLog(TAG, "NCL_EVENT_UNDEFINED\n" + NclEventType.values()[event.type], Macro.INFO_LOG_LEVEL);
			break;
		}
	}
}
