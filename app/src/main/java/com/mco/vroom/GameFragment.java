package com.mco.vroom;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.app.FragmentManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mco.vroom.dialogs.CorrectDialog;
import com.mco.vroom.dialogs.FinishGameDialog;
import com.mco.vroom.dialogs.GameOverDialog;
import com.mco.vroom.dialogs.WrongDialog;
import com.mco.vroomvroomvroom.R;

public class GameFragment extends Fragment implements OnClickListener,
		OnSeekBarChangeListener {

    private MainActivity ma;
	private TextView tvLivesValue, tvSkipsValue, tvHighScoreValue,
			tvYourScoreValue, tvRoundValue, tvProgressTime, tvDurationTime;

	private String[] listOfCars;
	private int[] carSoundIds, carDrawableIds;

	private Button bPlay, bPause, bRepeat, bRepeatNegate;
	private RadioGroup rgCars;
	private RadioButton[] rbCar;
	private MediaPlayer exhaust;
	private SeekBar seekBar;
	private boolean isFirstPlay, isSaveActivated, isNewGame,
            isFirstGame, isFromDialog, isLooping;

    private volatile boolean isStopped, isPaused, isSkipped, isTouchingSeekBar;

	private Thread thread;
	private DecimalFormat myFormatter;
	private SparseArray<String> answerKey;
	private SparseIntArray pictureKey;
	private ArrayList<Integer> soundResource;
	private ArrayList<String> answerChoices, carAnswerPool;
	private int exhaustDuration, round, currentPic;
	private String answer;

	// ---------------------Save variables
	private String saveRound, saveYourScore, saveLives, saveHighScore,
			saveSkips;

	private String[] fourChoices, savedSoundSequence; // array to hold data
														// gotten from saved
														// game
	Random rand;

	@Override
	public void onAttach(Activity activity) {
        ma = (MainActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//TODO put progress dialog if long load time

		myFormatter = new DecimalFormat("00");
		soundResource = new ArrayList<>(51); // IF YOU ADD MORE CARS
													// CHANGE THIS
		answerKey = new SparseArray<>(51); // IF YOU ADD MORE CARS CHANGE
													// THIS
		pictureKey = new SparseIntArray();
		answerChoices = new ArrayList<>(4); // 4 choices to choose from
		carAnswerPool = new ArrayList<>(51); // IF YOU ADD MORE CARS
													// CHANGE THIS
		rand = new Random();

		listOfCars = new String[] {
                "2014 Aston Martin Vanquish V12",
				"2012 Audi R8 GT 5.2 FSI",
                "2015 Audi RS 7",
				"2015 Audi S3 2.0T",
                "2014 Bentley Continental GT V8 S",
				"2015 BMW M3 Sedan",
                "2014 BMW M6 Gran Coupe",
				"2008 Bugatti Veyron 16.4",
                "2014 Cadillac CTS V-Sport",
				"2013 Chevrolet Camaro ZL1",
				"2014 Chevrolet Corvette Stingray Z51",
				"2014 Chevrolet SS (Commodore)",
                "2015 Chrysler 200C AWD V6",
				"2003 Dodge Viper SRT-10",
                "2010 Ferrari 458 Italia",
				"2009 Ferrari 599 GTB Fiorano",
                "2014 Ferrari F12 Berlinetta",
				"2012 Fiat 500 Abarth",
                "2013 Ford Shelby GT500",
				"1970 Ford Torino King Cobra Boss 429",
                "2012 Honda Civic SI",
				"1992 Honda NSX",
                "2006 Hummer H1 Alpha Passenger Wagon",
				"2011 Hyundai Genesis Coupe 3.8",
                "2015 Jaguar F-Type R Coupe",
				"2014 Jaguar XJL R Supercharged",
                "2015 Kia K900 V8 VIP",
				"2009 Koenigsegg CCXR",
                "2014 Lamborghini Aventador LP720-4",
				"2012 Lamborghini Gallardo Spyder",
                "2014 Lexus IS-F",
				"2012 Lexus LFA Nurburgring Ed.",
                "2012 Lotus Evora S",
				"2013 Maserati GranTurismo MC SL",
                "2011 Maybach 62 S",
				"2015 McLaren 650S Spider",
				"2014 Mercedes-Benz C63 AMG Ed. 507",
				"2014 Mercedes-Benz CLA45 AMG",
                "2014 Mercedes-Benz E63 AMG S",
				"2012 Mercedes-Benz SLS AMG",
                "2014 Mini Cooper S",
				"2013 Mini John Cooper Works GP",
                "2009 Nissan 370Z NISMO",
				"2013 Nissan GTR",
                "2012 Porsche 911 Carrera S",
				"2014 Porsche Cayman S",
                "2015 Porsche Macan Turbo",
				"2011 Porsche Panamera Turbo",
                "2013 Subaru BRZ",
				"2011 Subaru Impreza WRX",
                "2011 Volkswagen GTI" };

		carSoundIds = new int[] {
                R.raw.vanquishv12,
                R.raw.r8,
                R.raw.rs7,
				R.raw.s3,
                R.raw.continentalgtv8s,
                R.raw.m3sedan,
				R.raw.m6grancoupe,
                R.raw.veyron,
                R.raw.ctsvsport,
				R.raw.camarozl1,
                R.raw.stingray,
                R.raw.sscommodore,
				R.raw.c200v6,
                R.raw.viper,
                R.raw.italia458,
				R.raw.fioranogtb599,
                R.raw.berlinettaf12,
                R.raw.abarth500,
				R.raw.gt500,
                R.raw.torino,
                R.raw.civicsi,
                R.raw.nsx,
				R.raw.h1alpha,
                R.raw.genesis38coupe,
                R.raw.ftype,
				R.raw.xjlrsupercharged,
                R.raw.k900v8,
                R.raw.ccxr,
				R.raw.aventador,
                R.raw.gallardospyder,
                R.raw.isf,
                R.raw.lfa,
				R.raw.evoras,
                R.raw.granturismomc,
                R.raw.s62,
                R.raw.spider650s,
				R.raw.c63amg,
                R.raw.cla45amg,
                R.raw.e63amgs,
                R.raw.slsamg,
				R.raw.coopers,
                R.raw.johncooperworksgp,
                R.raw.z370,
                R.raw.gtr,
				R.raw.carreras911,
                R.raw.caymans,
                R.raw.macanturbo,
				R.raw.panameraturbo,
                R.raw.brz,
                R.raw.wrx,
                R.raw.gti };

		carDrawableIds = new int[] {
                R.drawable.vanquishv12,
                R.drawable.r8,
				R.drawable.rs7,
                R.drawable.s3,
                R.drawable.continentalgtv8s,
				R.drawable.m3sedan,
                R.drawable.m6grancoupe,
                R.drawable.veyron,
				R.drawable.ctsvsport,
                R.drawable.camarozl1,
				R.drawable.stingray,
                R.drawable.sscommodore,
                R.drawable.c200v6,
				R.drawable.viper,
                R.drawable.italia458,
				R.drawable.fiorano599gtb,
                R.drawable.berlinettaf12,
				R.drawable.abarth500,
                R.drawable.gt500,
                R.drawable.torino,
				R.drawable.civicsi,
                R.drawable.nsx,
                R.drawable.h1alpha,
				R.drawable.genesis38coupe,
                R.drawable.ftype,
				R.drawable.xjlrsupercharged,
                R.drawable.k900v8,
				R.drawable.ccxr,
                R.drawable.aventador,
				R.drawable.gallardospyder,
                R.drawable.isf,
                R.drawable.lfa,
				R.drawable.evoras,
                R.drawable.granturismomc,
                R.drawable.s62,
				R.drawable.spider650s,
                R.drawable.c63amg,
                R.drawable.cla45amg,
				R.drawable.e63amgs,
                R.drawable.slsamg,
                R.drawable.coopers,
				R.drawable.johncooperworksgp,
                R.drawable.z370,
                R.drawable.gtr,
				R.drawable.carreras911,
                R.drawable.caymans,
				R.drawable.macanturbo,
                R.drawable.panameraturbo,
				R.drawable.brz,
                R.drawable.wrx,
                R.drawable.gti };

		populateAnswerKey();
		populatePictureKey();

		for (int i = 0; i < answerKey.size(); i++) {
			carAnswerPool.add(answerKey.valueAt(i)); // transfer values to the
														// carAnswerPool so it
														// can be shuffled for
														// answerChoices
			soundResource.add(answerKey.keyAt(i)); // transfer keys to
													// soundResource to be
													// shuffled for new game
		}

		// Try to load save game is there is one
		try {
			DbHelper ourDB = new DbHelper(getActivity());
			ourDB.open();
			Cursor c = ourDB.getSavePoint();

			if (c.moveToNext()) {

				saveYourScore = c.getString(0);
				if (saveYourScore == null) {
					throw new NullPointerException();
				}
				saveRound = c.getString(1);
				saveLives = c.getString(2);
				saveSkips = c.getString(3);

				fourChoices = c.getString(4).split("~");
				savedSoundSequence = c.getString(5).split("~");
				saveHighScore = c.getString(6);

				isSaveActivated = true;
			}
			ourDB.close();

		} catch (NullPointerException e) {
			//Log.d("bull", "No saved game.");
			isFirstGame = true;
		}

		if (!isSaveActivated) {
			Collections.shuffle(soundResource); // shuffle if game is not saved.
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.gamefragment, container, false);
		tvLivesValue = (TextView) view.findViewById(R.id.tvLivesValue);
		tvSkipsValue = (TextView) view.findViewById(R.id.tvSkipsValue);
		tvHighScoreValue = (TextView) view.findViewById(R.id.tvHighScoreValue);
		tvYourScoreValue = (TextView) view.findViewById(R.id.tvYourScoreValue);
		tvRoundValue = (TextView) view.findViewById(R.id.tvRoundValue);

		bPlay = (Button) view.findViewById(R.id.bPlay);
		bPause = (Button) view.findViewById(R.id.bPause);
		Button bStop = (Button) view.findViewById(R.id.bStop);
		// stop does not need to be global for now
		bRepeat = (Button) view.findViewById(R.id.bRepeat);
		bRepeatNegate = (Button) view.findViewById(R.id.bRepeatNegate);

		Button bSkip = (Button) view.findViewById(R.id.bSkip);
		Button bConfirm = (Button) view.findViewById(R.id.bConfirm);
		rbCar = new RadioButton[4];

		rbCar[0] = (RadioButton) view.findViewById(R.id.rbCar1);
		rbCar[1] = (RadioButton) view.findViewById(R.id.rbCar2);
		rbCar[2] = (RadioButton) view.findViewById(R.id.rbCar3);
		rbCar[3] = (RadioButton) view.findViewById(R.id.rbCar4);

		rbCar[0].setId(0);
		rbCar[1].setId(1);
		rbCar[2].setId(2);
		rbCar[3].setId(3);

		seekBar = (SeekBar) view.findViewById(R.id.sbExhaustSound);

		tvProgressTime = (TextView) view.findViewById(R.id.tvProgressTime);
		tvDurationTime = (TextView) view.findViewById(R.id.tvDurationTime);

		rgCars = (RadioGroup) view.findViewById(R.id.rgCars);

		bPlay.setOnClickListener(this);
		bPause.setOnClickListener(this);
		bStop.setOnClickListener(this);
		bRepeat.setOnClickListener(this);
		bRepeatNegate.setOnClickListener(this);
		bSkip.setOnClickListener(this);
		bConfirm.setOnClickListener(this);

		seekBar.setOnSeekBarChangeListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			nextRound();
		}
	}

	@Override
	public void onResume() {
        if(round == 0 && saveRound != null)
            round = Integer.parseInt(saveRound);

		exhaust = MediaPlayer.create(getActivity(), soundResource.get(round - 1));
		if (isLooping) {
			exhaust.setLooping(true);
		}

		isFirstPlay = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		if (thread != null) {
			thread = null;
		}
		if (exhaust != null) {
			exhaust.release(); // end state
			exhaust = null; // cant use exhaust variable as media player until
							// it is null??
		}
		super.onPause();
	}

	@Override
	public void onStop() {// save if activity goes out of view
		super.onStop();
		saveRound = Integer.toString(round);
		saveYourScore = tvYourScoreValue.getText().toString();
		saveLives = tvLivesValue.getText().toString();
		saveSkips = tvSkipsValue.getText().toString();

		String saveAnswerChoices = "";// reset string or it will keep appending and
								// messing up! ah headache
		for (RadioButton choice : rbCar) {
			saveAnswerChoices += (choice.getText().toString() + "~");// ~ is the
																		// delimiter
		}

		String saveSoundSequence = "";
		for (int sound : soundResource) {
			saveSoundSequence += (Integer.toString(sound) + "~");// ~ is the
																	// delimiter
		}

		DbHelper ourDB = new DbHelper(getActivity());
		ourDB.open();
		ourDB.setSavePoint(saveRound, saveYourScore, saveLives, saveSkips,
				saveAnswerChoices, saveSoundSequence);
		ourDB.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bPlay:

			playExhaust();
			break;

		case R.id.bPause:
			isPaused = true;
			showPlayHidePause();
			if (exhaust.isPlaying()) {
				exhaust.pause();
			}
			break;

		case R.id.bStop:
			isStopped = true;
			// will set seekbar to 0 in the asynctask
			if (exhaust.isPlaying()) {
				exhaust.pause();
			}
			tvProgressTime.setText("00:00");
			seekBar.setProgress(0);
			exhaust.seekTo(0);
			if (bPlay.getVisibility() != View.VISIBLE) {
				showPlayHidePause();
			}
			break;

		case R.id.bRepeat:
			isLooping = false;
			exhaust.setLooping(false);
			bRepeat.setVisibility(View.GONE);
			bRepeatNegate.setVisibility(View.VISIBLE);
			break;

		case R.id.bRepeatNegate:
			isLooping = true;
			exhaust.setLooping(true);
			bRepeatNegate.setVisibility(View.GONE);
			bRepeat.setVisibility(View.VISIBLE);
			break;

		case R.id.bSkip:
			isSkipped = true;

			Toast t = new Toast(getActivity());
			t.setDuration(Toast.LENGTH_SHORT);
			t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 20);
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view;

			if (!tvSkipsValue.getText().toString().equals("0")) {
				if (exhaust.isPlaying()) {
					exhaust.pause();
				}

				view = inflater.inflate(R.layout.minusskip, null);
				t.setView(view);
				t.show();

				int skips = Integer.parseInt(tvSkipsValue.getText().toString()); // minus
																					// 1
																					// skip
				skips--;
				tvSkipsValue.setText(Integer.toString(skips));

				int score = Integer.parseInt(tvYourScoreValue.getText()
						.toString()); // minus 1 point
				score--;
				tvYourScoreValue.setText(Integer.toString(score));

				setIsFromDialog(true);
				nextRound();

			} else {
				view = inflater.inflate(R.layout.nomoreskips, null);
				t.setView(view);
				t.show();
			}
			break;

		case R.id.bConfirm:
			if (rgCars.getCheckedRadioButtonId() > -1) {
				if (exhaust.isPlaying()) {
					exhaust.pause();
				}
				FragmentManager manager = getFragmentManager();

				if (answer.equals(rbCar[rgCars.getCheckedRadioButtonId()]
						.getText().toString())) {
					addToScore(); // add to Score
					CorrectDialog cd = new CorrectDialog();
					cd.setCancelable(false);
					cd.show(manager, "cd"); // adds fragment to the manager
				} else {
                    //TODO subtract life, to debug comment out either of these two lines
					int minusLife = Integer.parseInt(tvLivesValue.getText().toString()) - 1;
					tvLivesValue.setText(Integer.toString(minusLife));

					if (tvLivesValue.getText().toString().equals("0")) {
						GameOverDialog god = new GameOverDialog();
						god.setCancelable(false);
						god.show(manager, "god");
					} else {
						WrongDialog wd = new WrongDialog();
						wd.setCancelable(false);
						wd.show(manager, "wd");
					}
				}
			}
			break;
		}
	}

	private void populateAnswerKey() {
		for (int i = 0; i < listOfCars.length; i++) {
			answerKey.put(carSoundIds[i], listOfCars[i]);
		}
	}

	private void populatePictureKey() {
		for (int i = 0; i < listOfCars.length; i++) {
			pictureKey.put(carSoundIds[i], carDrawableIds[i]);
		}
	}

	public void nextRound() {
		if (round == 25) {
			FragmentManager manager = getFragmentManager();
			FinishGameDialog fgd = new FinishGameDialog();
			fgd.setCancelable(false);
			fgd.show(manager, "fgd");
			return;
		}

		// set views to the correct saved game
		if (isSaveActivated) {
			isSaveActivated = false;

			tvHighScoreValue.setText(saveHighScore);
			tvYourScoreValue.setText(saveYourScore);
			if (saveRound.equals("25")) {
				tvRoundValue.setText("Final");
			} else {
				tvRoundValue.setText(saveRound + " of 25");
			}
			round = Integer.parseInt(saveRound);

			tvLivesValue.setText(saveLives);
			tvSkipsValue.setText(saveSkips);

			for (int i = 0; i < 4; i++) {
				rbCar[i].setText(fourChoices[i]);
			}

			soundResource.clear();

			for (String sound : savedSoundSequence) {
				soundResource.add(Integer.parseInt(sound));
			}

			answer = answerKey.get(soundResource.get(round - 1)); // store
																	// answer
																	// from
																	// answerkey
		} else {
			if (isFirstGame) {
                //this is to determine if we should show the volume toast or not
                ma.setIsFromNavNewGame(true);

				newGame();
				tvHighScoreValue.setText("0");
				isFirstGame = false;
			}
			if (isNewGame) {
				Collections.shuffle(soundResource);
				isNewGame = false;
			}

			++round;
			if (round == 25) {
				tvRoundValue.setText("Final");
			} else {
				tvRoundValue.setText(Integer.toString(round) + " of 25");
			}

            // show toast to press play button if round 1 and if user just opened app
			if (round == 1 && ma.getIsFromNavNewGame()) {

				Toast t = new Toast(getActivity());
				t.setDuration(Toast.LENGTH_LONG);
				t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 20);
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.pressplaytostart, null);
				t.setView(view);
				t.show();
			}

			if (!answerChoices.isEmpty()) {
				answerChoices.clear();
			}

			answer = answerKey.get(soundResource.get(round - 1)); // store
																	// answer
																	// from
																	// answerkey
			answerChoices.add(answer);

			String[] extractMakeFromAnswer = answer.split("\\s"); // index 1 is
																	// the make

			Collections.shuffle(carAnswerPool); // shuffle and take 3 cars from
												// a random position

			String car;
			String[] extractMakeFromCar, extractMakeFromrb;
			Boolean validrb = false;

			for (int i = 1; i <= 3; i++) { // 1-2-3 to fill in the other 3
											// choices

				do {
					int carAnswerCount = rand.nextInt(51); // get a random car
															// out of the number
															// of cars //IF YOU
															// ADD MORE CARS
															// CHANGE THIS
					car = carAnswerPool.get(carAnswerCount);
					extractMakeFromCar = car.split("\\s");

					// answer cant have same make as other choices
					// continue if same make
					if (!extractMakeFromCar[1].equals(extractMakeFromAnswer[1])) { // carmake
																					// cant
																					// equal
																					// caranswer
						if (i >= 2) {
							for (int j = 1; j <= i - 1; j++) { // 3rd rb compare
																// to 2nd, 4th
																// rb compare to
																// 2 and 1
								extractMakeFromrb = answerChoices.get(j).split(
										"\\s");

                                //past rb'
								if (!extractMakeFromrb[1]
										.equals(extractMakeFromCar[1])) { // past
																			// rb's
																			// cant
																			// equal
																			// the
																			// new
																			// rb
																			// either
									validrb = true;
								} else {
									validrb = false;
									break;
								}
							}
						} else {
							validrb = true;
						}
					}
				} while (!validrb);

				validrb = false;
				answerChoices.add(car);
			} // end for, adding the answers

			Collections.shuffle(answerChoices); // shuffle answer positions
			for (int i = 0; i < rbCar.length; i++) {
				rbCar[i].setText(answerChoices.get(i)); // set the radiobuttons
														// to the car names
			}
		} // end else

		currentPic = pictureKey.get(soundResource.get(round - 1));

		if (exhaust != null) {
			exhaust.reset();
		}
		exhaust = MediaPlayer.create(getActivity(),
				soundResource.get(round - 1)); // create sound from the round #

		// Setting the time of the soundclip
		exhaustDuration = exhaust.getDuration(); // need duration for
													// onprogresschanged
		int seconds = exhaustDuration / 1000;
		String output = myFormatter.format(seconds);
		String duration = "00:" + output; // set textview duration for seekbar

		tvProgressTime.setText("00:00");
		tvDurationTime.setText(duration);

		// uncheck all radio
		if (rgCars.getCheckedRadioButtonId() > -1) {
			rgCars.clearCheck();
		}
		if (isLooping) {
			exhaust.setLooping(true);
		}

		if (isFromDialog && round != 1) { // auto play the sound when you skip
											// or confirm
			thread = null;
			isFromDialog = false;
			playExhaust();
		}

	}

	// set all textviews to default values
	public void newGame() {
		tvLivesValue.setText(Integer.toString(3));
		tvSkipsValue.setText(Integer.toString(2));

		tvYourScoreValue.setText(Integer.toString(0));
		round = 0;
		seekBar.setProgress(0);

		isNewGame = true;
		// reshuffle sounds
		// recalibrate radiobuttons
	}

	public void playExhaust() {
		if (thread == null) {
			showPauseHidePlay();
			isFirstPlay = true;
			isStopped = isPaused = false;
			SeekBarMover sbm = new SeekBarMover(getActivity());
			thread = new Thread(sbm);
			thread.start();
		}
	}

	public String getAnswer() {
		return answer;
	}

	public int[] getCarSoundIds() {
		return carSoundIds;
	}

	public String[] getListOfCars() {
		return listOfCars;
	}

	public int getCurrentPic() {
		return currentPic;
	}

	public String getScore() {
		return tvYourScoreValue.getText().toString();
	}

	public String getAmountOfRounds() {
		return Integer.toString(25); // there will be 25 rounds
	}

	public void setIsFromDialog(boolean dialog) {
		isFromDialog = dialog;
	}

	private void addToScore() {
		int yourScore = Integer.parseInt(tvYourScoreValue.getText().toString());
		int highScore = Integer.parseInt(tvHighScoreValue.getText().toString());
		++yourScore;

		tvYourScoreValue.setText(Integer.toString(yourScore));
		if (yourScore > highScore) {
			DbHelper ourDB = new DbHelper(getActivity());
			ourDB.open();
			ourDB.setHighScore(yourScore);
			ourDB.close();

			tvHighScoreValue.setText(Integer.toString(yourScore));
		}
	}

	@Override
	// called when progress changes from user input
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
		if (fromUser) {
			double ratio = (double) progress / 100;
			int position = (int) (ratio * exhaustDuration);
			double seconds = (double) position / 1000;

			String output = myFormatter.format(seconds);
			String progressTime = "00:" + output;

			tvProgressTime.setText(progressTime);
		}
	}

	@Override
	// pause sound when you touch seek bar
	public void onStartTrackingTouch(SeekBar sb) {
        isTouchingSeekBar = true;
		/*exhaust.pause();
		showPlayHidePause();*/
	}

	@Override
	// sync seekbar with sound when you stop touching seekbar
	public void onStopTrackingTouch(SeekBar sb) {
        isTouchingSeekBar = false;

		if (!isFirstPlay) {
			exhaust = MediaPlayer.create(getActivity(),
					soundResource.get(round - 1));
		}

		float sbPosition = (float) sb.getProgress() / 100;
		float soundPosition = sbPosition * exhaust.getDuration();
		exhaust.seekTo((int) soundPosition);

	}

	public void showPlayHidePause() {
		bPlay.setVisibility(View.VISIBLE);
		bPause.setVisibility(View.GONE);
	}

    public void showPauseHidePlay() {
		bPause.setVisibility(View.VISIBLE);
		bPlay.setVisibility(View.GONE);
	}

	public void pause() {
		if (isFirstPlay) { // can only pause if you hit the play button first or
							// else wrong state
			isPaused = true;
			exhaust.pause();
			showPlayHidePause();
		}
	}

	// --------------------------------------------------------------------------------------------------
	public class SeekBarMover implements Runnable {
        Activity activity;
		String progressTime;

        public SeekBarMover(Activity activity){
            this.activity = activity;
        }

		@Override
		public void run() {
			float ratio;
			float position;

			exhaust.start();

			try {
				while (exhaust.isPlaying()) {
                    //sleep for 200 as a way to prevent it from spamming constantly to update the time
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

                //if not paused and not stopped, it means it finished playing
				if ((!isPaused && !isStopped) || isStopped) {

					if (seekBar.getProgress() != 0) {
						seekBar.setProgress(0);
                        resetProgressTime();
					}
					if (!isSkipped) {
                        showPlayHidePauseDelegate();
					}
					isSkipped = false;
				}

				thread = null;// only one thread at a time
			} catch (Exception e) { // in case something happens run the cleanup
									// code again
				isSkipped = false;

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
	} // end thread
}
