package com.mco.vroom;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mco.vroomvroomvroom.R;

public class Splash extends Activity{
	//black splash screen to hide default action bar on load
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);	
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish(); //gets rid of the splash onPause
	}
}
