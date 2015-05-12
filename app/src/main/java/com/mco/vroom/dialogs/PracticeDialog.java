package com.mco.vroom.dialogs;

import com.mco.vroom.MainActivity;
import com.mco.vroomvroomvroom.R;

import android.app.Activity;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class PracticeDialog extends DialogFragment implements OnClickListener {
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

		View view = inflater.inflate(R.layout.practicedialog, container, false);
		Button bYesNewGame = (Button) view.findViewById(R.id.bYesPractice);
		Button bNoNewGame = (Button) view.findViewById(R.id.bNoPractice);
		
		bYesNewGame.setOnClickListener(this);
		bNoNewGame.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.bYesPractice){
            String[] menu = getResources().getStringArray(R.array.menu);
            ma.setActionBarTitle(menu[1]);
			ma.newGameWithoutShow();
			ma.practiceMode();
		} 
		
		dismiss();
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
