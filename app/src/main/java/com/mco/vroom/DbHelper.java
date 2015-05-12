package com.mco.vroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	//column names
	private static final String KEY_ROWID = "_id"; //primary key
	private static final String KEY_HIGH_SCORE = "highScore";
	private static final String KEY_ROUND = "round";
	private static final String KEY_YOUR_SCORE = "yourScore";
	private static final String KEY_LIVES = "lives";
	private static final String KEY_SKIPS = "skips";
	private static final String KEY_ANSWER_CHOICES = "answerChoices";
	private static final String KEY_SOUND_SEQUENCE = "soundSequence";
	
	private static final String DATABASE_NAME = "GameDB"; //dbname
	private static final String DATABASE_TABLE = "GameTable"; //dbname
	
	private static final int DATABASE_VERSION = 3;
	
	private SQLiteDatabase ourDatabase;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) { //gets called when it gets started. If you want onCreate() to run you need to use adb to delete the SQLite database file.
		//setup db
		db.beginTransaction();
			try{
			db.execSQL( "CREATE TABLE " + DATABASE_TABLE + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_HIGH_SCORE + " VARCHAR(255) NOT NULL, " + 
					KEY_ROUND + " VARCHAR(255), " +
					KEY_YOUR_SCORE + " VARCHAR(255), " +
					KEY_LIVES + " VARCHAR(255), " + 
					KEY_SKIPS + " VARCHAR(255), " +
					KEY_ANSWER_CHOICES + " VARCHAR(255), " +
					KEY_SOUND_SEQUENCE + " VARCHAR(255));"
			);
			
			ContentValues cv = new ContentValues();
			cv.put(KEY_HIGH_SCORE, 0); //insert initial highScore of 0
			db.insert(DATABASE_TABLE, null, cv);
			
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
		
	}

	@Override // If you want the onUpgrade() method to be called, you need to increment the version number in your code.
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
	}
	
	public void setHighScore(int highScore){
		ContentValues cv = new ContentValues();
		cv.put(KEY_HIGH_SCORE, highScore);
		ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + " = " + 1, null);
	}
	
	public void setSavePoint(String round, String yourScore, String lives, String skips, String answerChoices, String soundSequence) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROUND, round);
		cv.put(KEY_YOUR_SCORE, yourScore);
		cv.put(KEY_LIVES, lives);
		cv.put(KEY_SKIPS, skips);
		cv.put(KEY_ANSWER_CHOICES, answerChoices);
		cv.put(KEY_SOUND_SEQUENCE, soundSequence);
		
		ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + " = " + 1, null);
	}
	
	public Cursor getSavePoint(){
		String[] columns = {KEY_YOUR_SCORE, KEY_ROUND, KEY_LIVES, KEY_SKIPS, KEY_ANSWER_CHOICES, KEY_SOUND_SEQUENCE, KEY_HIGH_SCORE};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + " = " + 1, null, null, null, null);
		
		return c;
	}
	
	public void open(){
		ourDatabase = getWritableDatabase();//gets our db if you can write to it you can also read
	}
	
	public void dbBeginTransaction(){
		ourDatabase.beginTransaction();
	}
	public void dbSetTransactionSuccessful(){
		ourDatabase.setTransactionSuccessful();
	}
	public void dbEndTransaction(){
		ourDatabase.endTransaction();
	}	
	
}