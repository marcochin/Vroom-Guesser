package com.mco.vroom;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.mco.vroom.dialogs.NewGameDialog;
import com.mco.vroom.dialogs.PracticeDialog;
import com.mco.vroomvroomvroom.R;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {

	private DrawerLayout dlDrawerLayout;
	private ListView lvDrawerList;
	private String menu[];
	private FragmentManager fmanager;
	private FragmentTransaction ftransaction;
	private ActionBarDrawerToggle drawerToggle;
	private String menuTitle;
	private GameFragment game;
	private PracticeMode pm;
	private AboutFragment about;
	private HowToPlayFragment htp;
	private boolean isFromNavNewGame;
	private AdView avBottomAd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//make sure phone has google play services installed
		/*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resultCode != ConnectionResult.SUCCESS){
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
			dialog.show();
		}*/
		
		initializeViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (avBottomAd != null) {
			avBottomAd.resume();
		}
	}
	
	@Override
	protected void onPause() {
		if (avBottomAd != null) {
			avBottomAd.pause();
		}

		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if (avBottomAd != null) {
            avBottomAd.removeAllViews();
			avBottomAd.destroy();
		}

		super.onDestroy();
	}
	
	

	private void initializeViews() {
		menu = getResources().getStringArray(R.array.menu);
		menuTitle = (String)getTitle(); //when app opens menu title default is the app title until user clicks on a menu item

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		dlDrawerLayout = (DrawerLayout) findViewById(R.id.dlDrawerLayout);
		lvDrawerList = (ListView)findViewById(R.id.lvDrawerList);
		lvDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu));
		lvDrawerList.setOnItemClickListener(this);

        setSupportActionBar(toolbar);

        //initializes & configures the drawer toggle on action bar
        drawerToggle = new ActionBarDrawerToggle(this,
                dlDrawerLayout,
                toolbar,
                R.string.openNav,
                R.string.closeNav){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(getTitle());

                if(game != null){//pause game when drawer opened
                    game.pause();
                }
                if(pm != null){//pause game when drawer opened
                    pm.pause();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(menuTitle);
            }
        };

		dlDrawerLayout.setDrawerListener(drawerToggle);		
		//set shadow of the drawer
		dlDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true); //need both statements for some reason??
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //allows back(arrow)indicator to be shown + home button to be clickable
        }
		//getSupportActionBar().setDisplayShowHomeEnabled(false); //removes the app icon from action bar
		
		fmanager = getFragmentManager();
		
		if(fmanager.findFragmentByTag("game") == null){
			game = new GameFragment();
			ftransaction = fmanager.beginTransaction();
			ftransaction.add(R.id.rlMainContent, game, "game");
			ftransaction.commit();
		}
		
		//add ad!
	    // Look up the AdView as a resource and load a request.
	    avBottomAd = (AdView) this.findViewById(R.id.avBottomAd);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    avBottomAd.loadAd(adRequest);
	}

    //special lifecycle method called after oncreate() and after onRestoreInstanceState()
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState(); //enables the drawer icon(3bars) to be seen
	}

    //gets called when you switch orientations
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //will call this when you click an menu item. Im guessing items on action bar are menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(drawerToggle.onOptionsItemSelected(item)){ //opens/closes drawer when home button is clicked?
			return true; //need to return true as said in docs
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		about = (AboutFragment)fmanager.findFragmentByTag("about");
		htp = (HowToPlayFragment)fmanager.findFragmentByTag("How To Play");
		pm = (PracticeMode)fmanager.findFragmentByTag("practice");
		
		ftransaction = fmanager.beginTransaction();
		//ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		switch(position){
		case 0: //newgame
			NewGameDialog ngd = null;
			isFromNavNewGame = true;
			if(fmanager.findFragmentByTag("ngd")== null){
				ngd = new NewGameDialog();
			}

            if(ngd!=null)
			    ngd.show(fmanager, "ngd");

			break;
			
		case 1: //practice mode
			if(pm != null && pm.isVisible()){
				break;
			}
			
			PracticeDialog pd = null;
			if(fmanager.findFragmentByTag("pd")== null){
				pd = new PracticeDialog();
			}

            if(pd!=null)
			    pd.show(fmanager, "pd");

			break;
			
		case 2: //howtoplay
			if(game.isVisible()){
				ftransaction.hide(game);
			}
			else if(about != null && about.isVisible()){
				ftransaction.hide(about);
			} 
			else if(pm != null && pm.isVisible()){
				ftransaction.hide(pm);
			}
			if(htp == null){			
				htp = new HowToPlayFragment();
				ftransaction.add(R.id.rlMainContent, htp, "How To Play");
				ftransaction.addToBackStack("How To Play");
			}
			else if(!htp.isVisible()){
				ftransaction.show(htp);
			}
			ftransaction.commit();
			
			break;

		case 3: //about
			if(game.isVisible()){
				ftransaction.hide(game);
			}
			else if(htp != null && htp.isVisible()){
				ftransaction.hide(htp);
			} else if(pm != null && pm.isVisible()){
				ftransaction.hide(pm);
			}
			
			if(about == null){			
				about = new AboutFragment();			
				ftransaction.add(R.id.rlMainContent, about, "about");
				ftransaction.addToBackStack("About");
			}
			else if(!about.isVisible()){
				ftransaction.show(about);
			}
			ftransaction.commit();
			break;

		case 4: //resume game
            //dlDrawerLayout.closeDrawer(Gravity.START);
			if(htp != null && htp.isVisible()){
				ftransaction.hide(htp);
				
			} else if(about != null && about.isVisible()){
				ftransaction.hide(about);
				
			} else if(pm != null && pm.isVisible()){
				ftransaction.hide(pm);
			}
			removeBackStackIfExist(0);
			
			ftransaction.show(game);
			ftransaction.commit();
			break;
		}

        //if new game or resume game set title to app name
		if(position == 0 || position ==4){
            if(getSupportActionBar() != null)
			    getSupportActionBar().setTitle(getTitle());
		}
        //if it is not practice mode
        else if(position != 1){
			menuTitle = menu[position];

            if(getSupportActionBar() != null)
			    getSupportActionBar().setTitle(menuTitle);
		}
		lvDrawerList.setItemChecked(position, true); //do we need this?
		dlDrawerLayout.closeDrawer(Gravity.START);
	}
	
	public void invokeNextRound(){
		game.nextRound();
	}
	
	public String getAnswer(){
		return game.getAnswer();
	}
	
	public int[] getCarSoundIds(){
		return game.getCarSoundIds();
	}
	
	public String[] getListOfCars(){
		return game.getListOfCars();
	}
	
	public int getCurrentPic(){
		return game.getCurrentPic();
	}
	
	public String getScore(){
		return game.getScore();
	}
	
	public String getAmountOfRounds(){
		return game.getAmountOfRounds();
	}

    public boolean getIsFromNavNewGame(){
        return isFromNavNewGame;
    }
	
	public void setIsFromDialog(boolean dialog){
		game.setIsFromDialog(dialog);
	}
	
	public void setIsFromNavNewGame(boolean nav){
		isFromNavNewGame = nav;
	}

    public void setActionBarTitle(String title){
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            menuTitle = title;
        }
    }
	
	//with out show means, without need to call ftransaction.show(game);
	public void newGameWithoutShow(){
		game.newGame();
		game.nextRound();
	}
	
	public void newGame(){
		if(!isFromNavNewGame){
			ftransaction = fmanager.beginTransaction();
		}
		
		game.newGame();
		game.nextRound();
		//are you sure you want to start a new game dialog?
		if(htp != null && htp.isVisible()){
			ftransaction.hide(htp);
		} else if(about != null && about.isVisible()){
			ftransaction.hide(about);
		} else if(pm != null && pm.isVisible()){
			ftransaction.hide(pm);
		}
		removeBackStackIfExist(0);
		
		ftransaction.show(game);
		ftransaction.commit();
	}
	
	public void practiceMode(){	
		if(game.isVisible()){
			ftransaction.hide(game);
			
		} else if(about != null && about.isVisible()){
			ftransaction.hide(about);
			
		} else if(htp != null && htp.isVisible()){
			ftransaction.hide(htp);
		}
		if(pm == null){			
			pm = new PracticeMode();
			ftransaction.add(R.id.rlMainContent, pm, "practice");
			ftransaction.addToBackStack("practice");
		}
		else if(!pm.isVisible()){
			ftransaction.show(pm);
		}
		ftransaction.commit();	
	}
	
	@Override
	public void onBackPressed() {
        //TODO only pop 0 indez if it is the only one on the stack
		removeBackStackIfExist(1); //pops off every except 0 index
		
		super.onBackPressed(); //pops off 0 index
	}
	
	public void removeBackStackIfExist(int i){
		int backStackCount;
		backStackCount = fmanager.getBackStackEntryCount();
		if(backStackCount != 0){ //if 0 means nothing on backstack

            if(getSupportActionBar() != null)
			    getSupportActionBar().setTitle(getTitle());

			menuTitle = (String)getTitle();
			if(backStackCount >= 1){
				fmanager.popBackStack(i, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
	}
}
