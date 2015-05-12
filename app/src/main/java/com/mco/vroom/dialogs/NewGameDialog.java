package com.mco.vroom.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.mco.vroom.MainActivity;
import com.mco.vroomvroomvroom.R;

public class NewGameDialog extends DialogFragment implements OnClickListener {
	
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

		View view = inflater.inflate(R.layout.newgamedialog, container, false);
		Button bYesNewGame = (Button) view.findViewById(R.id.bYesNewGame);
		Button bNoNewGame = (Button) view.findViewById(R.id.bNoNewGame);
		
		bYesNewGame.setOnClickListener(this);
		bNoNewGame.setOnClickListener(this);
		
		return view;

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.bYesNewGame){
			ma.newGame();
		} 
		
		dismiss();
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
