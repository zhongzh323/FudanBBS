package com.example.fudanbbs;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	private DrawerAdapter adapter;

	private FudanBBSApplication currentApplication;
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		currentApplication = (FudanBBSApplication)this.getActivity().getApplication();
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        adapter = new DrawerAdapter(getActionBar().getThemedContext(), mCurrentSelectedPosition, mCurrentSelectedPosition,   
        		new String[]{
                        getString(R.string.top10board),
                        getString(R.string.recommendboard),
                        getString(R.string.myfavorite),
                        getString(R.string.allboard),
                        getString(R.string.mymail),      
                        getString(R.string.mypreference),                  
                });
        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

//    @Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		Log.v("navigation drawer", "on resumed");
//		new Handler().postDelayed(new Runnable(){
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				adapter.notifyDataSetChanged();
//			}
//			
//		}, 0);
//	}
    
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("title");

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
        		Log.v("navigation drawer", "on drawer opened");
        		new Handler().postDelayed(new Runnable(){
        			@Override
        			public void run() {
        				// TODO Auto-generated method stub
        				adapter.notifyDataSetChanged();
        			}
        			
        		}, 0);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

//        if (item.getItemId() == R.id.action_example) {
//            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.fudanNavigation)));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public class DrawerAdapter extends ArrayAdapter{
    	@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return super.getCount();
    		return 1;
		}

		private Context context;
    	private String[] stringarray;
		private LayoutInflater inflater;
		private ListView listview;
		public DrawerAdapter(Context context, int resource,
				int textViewResourceId, Object[] objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.stringarray = (String[]) objects;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
//			return super.getView(position, convertView, parent);
			convertView = inflater.inflate(R.layout.drawercontent, null);
			TextView textview = (TextView) convertView.findViewById(R.id.drawerusername);
//			textview.setTextSize(20);
			if(currentApplication.isCurrentUserGuest()){
				textview.setText(R.string.notloginyet);
				Log.v("iscurrentuserguest", "yes");
			}else{
				textview.setText(currentApplication.getCurrentUsername());
				Log.v("iscurrentuserguest", "no");
			}		
			
	        LinearLayout layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
	        LinearLayout layout2 = (LinearLayout) convertView.findViewById(R.id.layout2);
	        LinearLayout layout3 = (LinearLayout) convertView.findViewById(R.id.layout3);
	        LinearLayout layout4 = (LinearLayout) convertView.findViewById(R.id.layout4);
	        LinearLayout layout5 = (LinearLayout) convertView.findViewById(R.id.layout5);
	        LinearLayout layout6 = (LinearLayout) convertView.findViewById(R.id.layout6);
   
	        LinearLayout layout;
			MainActivity ma = (MainActivity) getActivity();
			switch(ma.currentposition){
	        case 1:
	        	layout = layout1;
	            break;
	        case 2:
	        	layout = layout2;
	            break;
	        case 3:
	        	layout = layout3;
	            break;
	        case 4:
	        	layout = layout4;
	            break;
	        case 5:
	        	layout = layout5;
	            break;
	        case 6:
	        	layout = layout6;
	            break;
	        default:
	        	layout = layout2;	
	            				
			}
	        layout.setBackgroundColor(getResources().getColor(R.color.coral));
	        OnClickListener listener = new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch(v.getId()){
					case R.id.layout1:
						selectItem(1);
						break;
					case R.id.layout2:
						selectItem(2);
						break;
					case R.id.layout3:
						selectItem(3);
						break;
					case R.id.layout4:
						selectItem(4);
						break;
					case R.id.layout5:
						selectItem(5);
						break;
					case R.id.layout6:
						selectItem(6);
						break;
					}
				}
	        	
	        };
	        
	        layout1.setOnClickListener(listener);
	        layout2.setOnClickListener(listener);
	        layout3.setOnClickListener(listener);
	        layout4.setOnClickListener(listener);
	        layout5.setOnClickListener(listener);
	        layout6.setOnClickListener(listener);
			return convertView;
		}
    	
    }
    
    
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
