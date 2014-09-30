package com.mastercard.simplifynymisample;

import com.mastercard.simplifynymisample.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PaymentResponseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_response);
		Intent intent = getIntent();
		TextView response_Text = (TextView) findViewById(R.id.txt_paymentResponse);
		response_Text.setText(intent.getStringExtra("Response"));
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());

	}
}
