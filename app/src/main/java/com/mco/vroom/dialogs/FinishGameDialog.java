package com.mco.vroom.dialogs;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mco.vroom.MainActivity;
import com.mco.vroomvroomvroom.R;

public class FinishGameDialog extends DialogFragment{
	MainActivity ma;
	MediaPlayer endSong;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity)activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		endSong = MediaPlayer.create(getActivity(), R.raw.jump);
		endSong.start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.finishgamedialog, container, false);
		TextView tvYouWinScoreValue = (TextView)view.findViewById(R.id.tvYouWinScoreValue);
		Button bYouWinEnd = (Button)view.findViewById(R.id.bYouWinEnd);
		
		/*int score = Integer.parseInt(ma.getScore());
		int rounds = Integer.parseInt(ma.getAmountOfRounds());
		float percentageFloat = (float)score/rounds;
		
		double roundOff = Math.round(percentageFloat * 1000) / 10;
		String percentage = Double.toString(roundOff);*/
		
		tvYouWinScoreValue.setText(ma.getScore() + " out of " + ma.getAmountOfRounds() + " max");
		
		ma.setIsFromNavNewGame(false);
		ma.newGame();
		
		bYouWinEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		endSong.release();
        super.onDestroy();
	}
	
}
