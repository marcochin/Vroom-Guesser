package com.mco.vroom.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.mco.vroom.MainActivity;
import com.mco.vroomvroomvroom.R;

public class CorrectDialog extends DialogFragment {

	MainActivity ma;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		View view = inflater.inflate(R.layout.correctdialog, container, false);

		ImageView ivCar = (ImageView)view.findViewById(R.id.ivCar);
		Button bNextRound = (Button) view.findViewById(R.id.bNextRound);
		ivCar.setImageResource(ma.getCurrentPic());
		bNextRound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ma.setIsFromDialog(true);
				ma.invokeNextRound();
				dismiss();
			}
		});
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
