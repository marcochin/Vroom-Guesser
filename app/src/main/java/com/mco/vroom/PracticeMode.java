package com.mco.vroom;

import java.text.DecimalFormat;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mco.vroomvroomvroom.R;

public class PracticeMode extends Fragment implements OnClickListener, OnSeekBarChangeListener {
	
	private TextView tvProgressTime, tvDurationTime;
	private SeekBar seekBar;
	private Button bPlay, bPause, bRepeat, bRepeatNegate;
	private RadioGroup rgCars;
	private MainActivity ma;
	private String[] listOfCars;
	private int[] carSoundIds;
	private MediaPlayer exhaust;
	private boolean isFirstPlay, isLooping, isCheckedChanged;
    private volatile boolean isStopped, isPaused, isTouchingSeekBar;

	private Thread thread;
	private DecimalFormat myFormatter;
	private int exhaustDuration;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ma = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listOfCars = ma.getListOfCars();
		carSoundIds = ma.getCarSoundIds();
		myFormatter = new DecimalFormat("00");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.practicemode, null);

		bPlay = (Button) view.findViewById(R.id.bPracticePlay);
		bPause = (Button) view.findViewById(R.id.bPracticePause);
		Button bStop = (Button) view.findViewById(R.id.bPracticeStop);
		// stop does not need to be global for now
		bRepeat = (Button) view.findViewById(R.id.bPracticeRepeat);
		bRepeatNegate = (Button) view.findViewById(R.id.bPracticeRepeatNegate);
		rgCars = (RadioGroup) view.findViewById(R.id.rgPracticeCars);
		
		seekBar = (SeekBar) view.findViewById(R.id.sbPracticeExhaustSound);
		
		tvProgressTime = (TextView)view.findViewById(R.id.tvPracticeProgressTime);
		tvDurationTime = (TextView)view.findViewById(R.id.tvPracticeDurationTime);

		bPlay.setOnClickListener(this);
		bPause.setOnClickListener(this);
		bStop.setOnClickListener(this);
		bRepeat.setOnClickListener(this);
		bRepeatNegate.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);
		
		rgCars.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				isCheckedChanged = true;
				if(exhaust!= null && exhaust.isPlaying()){
					exhaust.pause();
					seekBar.setProgress(0);
				}
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
        int pxBottomMargin = (int) (getResources().getDimension(R.dimen.vroom_radio_button_bottom_margin) / getResources().getDisplayMetrics().density);

		params.setMargins(0, 0, 0, pxBottomMargin);

		for (int i = 0; i < listOfCars.length; i++) {
			RadioButton rb = new RadioButton(getActivity());
			rb.setId(carSoundIds[i]);
			rb.setText(listOfCars[i]);
			rb.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.vroom_radio_button_text_size));
			rb.setLayoutParams(params);
			rgCars.addView(rb);
		}
	}
	
	public void onResume() {
			isCheckedChanged = true;
			isFirstPlay = false;
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(thread != null){
			thread = null;
		}
		if(exhaust!=null){
			exhaust.release(); //end state
			exhaust = null; //cant use exhaust variable as media player until it is null??
		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bPracticePlay:
			//play only if a rb is checked
			if(rgCars.getCheckedRadioButtonId() != -1){
				if(isCheckedChanged){
					isCheckedChanged = false;
					if(exhaust!=null){
						exhaust.reset();
					}
					exhaust = MediaPlayer.create(getActivity(), rgCars.getCheckedRadioButtonId());
					exhaustDuration = exhaust.getDuration(); //need duration for onprogresschanged

					int seconds = exhaustDuration/1000;
					String output = myFormatter.format(seconds);
					String duration = "00:" + output; //set textview duration for seekbar
							
					tvProgressTime.setText("00:00");
					tvDurationTime.setText(duration);
					
					float sbPosition = (float)seekBar.getProgress()/100;
					float soundPosition = sbPosition * exhaust.getDuration();
					exhaust.seekTo((int)soundPosition);
					
					if (isLooping){
						exhaust.setLooping(true);
					} else {
						exhaust.setLooping(false);
					}
				}
				playExhaust();
			}
			break;

		case R.id.bPracticePause:
			isPaused = true;
			showPlayHidePause();
			if(exhaust.isPlaying()){
				exhaust.pause();
			}
			break;

		case R.id.bPracticeStop:
			isStopped = true;
			// will set seekbar to 0 in the asynctask
            if(exhaust != null) {
                if (exhaust.isPlaying()) {
                    exhaust.pause();
                }
                tvProgressTime.setText("00:00");
                seekBar.setProgress(0);
                exhaust.seekTo(0);
                if (bPlay.getVisibility() != View.VISIBLE) {
                    showPlayHidePause();
                }
            }
			break;
			
		case R.id.bPracticeRepeat:		
			isLooping = false;
			if(exhaust!=null){
				exhaust.setLooping(false);
			}
			bRepeat.setVisibility(View.GONE);
			bRepeatNegate.setVisibility(View.VISIBLE);
			break;
			
		case R.id.bPracticeRepeatNegate:
			isLooping = true;
			if(exhaust!=null){
				exhaust.setLooping(true);
			}
			bRepeatNegate.setVisibility(View.GONE);
			bRepeat.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	@Override //called when progress changes from user input
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
		if(fromUser){
			double ratio = (double)progress / 100;
			int position = (int)(ratio * exhaustDuration);
			double seconds = (double)position/1000;
			
			String output = myFormatter.format(seconds);
			String progressTime = "00:" + output;
			
			tvProgressTime.setText(progressTime);
		}
		
		// TODO Auto-generated method stub
	}

	@Override //pause sound when you touch seek bar
	public void onStartTrackingTouch(SeekBar sb) {
        isTouchingSeekBar = true;
		/*if(exhaust!=null){
			exhaust.pause(); //dont need to check for isfirstplay here for some reason
			showPlayHidePause();
		}*/
	}

	@Override //sync seekbar with sound when you stop touching seekbar
	public void onStopTrackingTouch(SeekBar sb) {
        isTouchingSeekBar = false;
			
		if(isFirstPlay){
			float sbPosition = (float)sb.getProgress()/100;
			float soundPosition = sbPosition * exhaust.getDuration();
			exhaust.seekTo((int)soundPosition);
		}
		
	}
	
	public void pause(){
		if(isFirstPlay){ //can only pause if you hit the play button first or else wrong state
			isPaused = true;
			exhaust.pause();
			showPlayHidePause();
		}
	}

	public void playExhaust() {
		if (thread == null) {
			showPauseHidePlay();
			isFirstPlay = true;
			isStopped = isPaused = false;
			SeekBarMover sbm = new SeekBarMover();
			thread = new Thread(sbm);
			thread.start();
		}
	}
	
	private void showPlayHidePause(){
		bPlay.setVisibility(View.VISIBLE);
		bPause.setVisibility(View.GONE);
	}
	
	private void showPauseHidePlay(){
		bPause.setVisibility(View.VISIBLE);
		bPlay.setVisibility(View.GONE);
	}

//--------------------------------------------------------------------------------------------------
	public  class SeekBarMover implements Runnable{

		String progressTime;

		@Override
		public void run() {
			float ratio;
			float position;

			exhaust.start();

			try {
				while (exhaust.isPlaying()) {
				    Thread.sleep(200);

                    if(!isTouchingSeekBar) {
                        ratio = (float) exhaust.getCurrentPosition() / exhaust.getDuration(); // get ratio of song
                        position = ratio * 100; // apply ratio to seekbar

                        //only reaches 99% so +1 to make it 100%
                        seekBar.setProgress((int) Math.ceil(position) + 1);

                        int seconds = exhaust.getCurrentPosition() / 1000;
                        String output = myFormatter.format(seconds);
                        progressTime = "00:" + output;
                        updateProgressTime();
                    }
				}

				if((!isPaused && !isStopped) || isStopped){ //if not paused and not stopped, it means it finished playing
                    if (seekBar.getProgress() != 0) {
                        seekBar.setProgress(0);
                        resetProgressTime();
                    }
                    showPlayHidePauseDelegate();
				}

				thread = null;//only one thread at a time
			}
			catch (Exception e) {
                showPlayHidePauseDelegate(); // if playing and hits power button, it will change pause to play

                seekBar.setProgress(0); // reset to 0 if hit power button/home
                resetProgressTime();
                e.printStackTrace();
			}
		}

        private void showPlayHidePauseDelegate(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPlayHidePause();
                }
            });
        }

        private void updateProgressTime(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvProgressTime.setText(progressTime);
                }
            });
        }

        private void resetProgressTime(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvProgressTime.setText("00:00");
                }
            });
        }
	} //end thread
}
