package com.abc.ewallet;

import com.abc.ewallet.activities.ConfirmActivity;
import com.abc.ewallet.activities.PayActivity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class EWalletActivity extends TabActivity {
 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    Resources res = getResources();
	    TabHost tabHost = getTabHost();
	    TabSpec spec;
	    Intent intent;
 
	    intent = new Intent(this,PayActivity.class);
	    spec = tabHost.newTabSpec("Pay")
	    .setIndicator("Pay", res.getDrawable(R.drawable.paytab))
	    .setContent(intent);
	    tabHost.addTab(spec);
 
	    intent = new Intent(this,ConfirmActivity.class);
	    spec = tabHost.newTabSpec("Confirm")
	    .setIndicator("Confirm", res.getDrawable(R.drawable.conftab))
	    .setContent(intent);
	    tabHost.addTab(spec);
 
	    tabHost.setCurrentTab(0);
	}
}