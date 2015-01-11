package com.example.fudanbbs;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	public int currentposition;
	static final String TAG = "MainActivity";
	private MyPreferenceFragment mypreferencefragment;
	private Top10Fragment top10fragment;
	private RecommendBoardFragment recommendboardfragment;
	private MyFavoriteFragment myfavoritefragment;
	private AllBoardFragment allboardfragment;
	private MyMailFragment mymailfragment;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		moveTaskToBack(true);
	}
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch(position){
        case 1:
        	currentposition = 1;
        	if(null==mypreferencefragment){
        		mypreferencefragment = new MyPreferenceFragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, mypreferencefragment)
            .commit();  
            break;
        case 2:
        	currentposition = 2;
        	if(null == top10fragment){
        		top10fragment = new Top10Fragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, top10fragment)
            .commit();
            break;
        case 3:
        	currentposition = 3;
        	if(null == recommendboardfragment){
        		recommendboardfragment = new RecommendBoardFragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, recommendboardfragment)
            .commit();       	
            break;
        case 4:
        	currentposition = 4;
        	if(null == myfavoritefragment){
        		myfavoritefragment = new MyFavoriteFragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, myfavoritefragment)
            .commit();
            break;
        case 5:
        	currentposition = 5;
        	if(null == allboardfragment){
        		allboardfragment = new AllBoardFragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, allboardfragment)
            .commit();    
            break;
        case 6:
        	currentposition = 6;
        	if(null == mymailfragment){
        		mymailfragment = new MyMailFragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, mymailfragment)
            .commit();
            break;
        default:
        	currentposition = 2;
        	if(null == top10fragment){
        		top10fragment = new Top10Fragment();
        	}
            fragmentManager.beginTransaction()
            .replace(R.id.container, top10fragment)
            .commit();          	
            	
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.top10board);
                break;
            case 2:
                mTitle = getString(R.string.recommendboard);
                break;
            case 3:
                mTitle = getString(R.string.myfavorite);
                break;
            case 4:
                mTitle = getString(R.string.allboard);
                break;
            case 5:
                mTitle = getString(R.string.mymail);
                break;        
            case 6:
                mTitle = getString(R.string.mypreference);
                break;                     
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
