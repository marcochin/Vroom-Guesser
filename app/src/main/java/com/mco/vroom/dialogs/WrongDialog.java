package com.mco.vroom.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mco.vroom.MainActivity;
import com.mco.vroomvroomvroom.R;

public class WrongDialog extends DialogFragment{
	
	MainActivity ma;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.wrongdialog, container, false);
		
		Button nextRound = (Button) view.findViewById(R.id.bNextRoundWrong);
		TextView tvCorrectAnswerValue = (TextView) view.findViewById(R.id.tvCorrectAnswerValue);
		
		tvCorrectAnswerValue.setText(ma.getAnswer());
		
		nextRound.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
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
