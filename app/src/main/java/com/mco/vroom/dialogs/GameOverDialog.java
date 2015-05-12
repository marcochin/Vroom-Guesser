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

public class GameOverDialog extends DialogFragment{
	MainActivity ma;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.gameoverdialog, container, false);
		
		TextView tvCorrectAnswerValue = (TextView)view.findViewById(R.id.tvCorrectAnswerValue);
		TextView tvGameOverScoreValue = (TextView)view.findViewById(R.id.tvGameOverScoreValue);
		Button bGameOverContinue = (Button)view.findViewById(R.id.bGameOverContinue);
		
		tvCorrectAnswerValue.setText(ma.getAnswer());
		
		/*int score = Integer.parseInt(ma.getScore());
		int rounds = Integer.parseInt(ma.getAmountOfRounds());
		float percentageFloat = (float)score/rounds;
		
		double roundOff = Math.round(percentageFloat * 1000) / 10;
		String percentage = Double.toString(roundOff);*/
		
		tvGameOverScoreValue.setText(ma.getScore() + " out of " + ma.getAmountOfRounds() +  " max");

        //means this is not from the nav drawer
        ma.setIsFromNavNewGame(false);
        ma.newGame();
		
		bGameOverContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
