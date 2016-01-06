package com.example.fudanbbs;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
public class FudanBBSApplication extends Application {

	// test
	final String helloworld = "fudanbbs";
	private String TAG = "##################"+this.getClass().getName();
	
	// global configuration 
	final boolean ENABLE = true;
	final boolean DISABLE = false;
	
	// login related:
	private String lastUsername;
	private HashMap<String, String> account;
	private boolean rememberPassword, autoLogin;

	private boolean isCurrentUserGuest = true;
	private String currentusername;
	private HashMap<String, String> cookie;
	private boolean flag;
	// message related:
	private int checkInterval;
	private boolean checkMessage, vibrateNotification;
	
	// picture related:
	final byte ONLYWIFI = 11;
	final byte BOTH = 12;
	final byte NOPIC = 13;
	private byte threshold;
	private boolean economicMode, exceedNotification, saveWhileBrowse;
	private byte picReadPrivilege;
	
	// read related:
	private boolean nightmode, displayBottomArticle;

	// screen rotation
	final byte LANDSCAPE = 41;
	final byte PORTRAIT = 42;
	final byte BYSYSTEM = 43;
	private byte rotation;

	// signature 
	private boolean appendSignature;
	private String appendContent;
	
	// user behavior 
	private String IPAddress, loginTime, logoutTime, currentSection;
	
	// server related
	private String serverAddress;
	private boolean upload2Server;
	

	
	
	
	// check if the current user is guest
	public boolean isCurrentUserGuest(){
		return isCurrentUserGuest;
	}
	public void setCurrentUserGuest(boolean isGuest){
		isCurrentUserGuest = isGuest;
	}

	public void setCurrentUsername(String aUsername){
		this.currentusername = aUsername;
	}
	public String getCurrentUsername(){
		return this.currentusername;
	}	
	// get user account information from shared preference file 
	public HashMap <String, String> getAccountInfo(){
		String PrefsName = "AccountInfo";
		SharedPreferences setting = getSharedPreferences(PrefsName, 0);
		HashMap <String, String> map = new HashMap <String, String>();
		map.put("username", setting.getString("username", ""));
		map.put("password", setting.getString("password", ""));	
		map.put("rememberpassword", setting.getString("rememberpassword", ""));
		map.put("autologin", setting.getString("autologin", ""));	
		return map;
	}
	
	// save user account information from shared preference file 
	public boolean saveAccountInfo(HashMap <String, String> map){
		account = map;
		String PrefsName = "AccountInfo";
		SharedPreferences setting = getSharedPreferences(PrefsName, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putString("username", map.get("username"));
		editor.putString("password", map.get("password"));
		editor.putString("rememberpassword", map.get("rememberpassword"));
		editor.putString("autologin", map.get("autologin"));		
		return editor.commit();
		
	}	
	
	//
	public class loginAsyncTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(null != cookie){
				cookie.clear();
			}else{
				cookie = new HashMap<String, String>();
			}
			Log.v(TAG, "onPreExecute");
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.v(TAG, "onPostExecute");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
			String loginurl = "http://bbs.fudan.edu.cn/bbs/login";
			Response res;
			try {
				res = Jsoup.connect(loginurl).data("id",account.get("username"),"pw",account.get("password"))
						.timeout(15000).method(Method.POST).execute();
//				HashMap<String, String> cookie = new HashMap<String, String>();
				if(null != res.cookie("utmpuser")){
    				cookie.put("utmpuser", res.cookie("utmpuser"));
    				Log.v(TAG, res.cookie("utmpuser"));
    				cookie.put("utmpkey", res.cookie("utmpkey"));
 //   				cookie.put("utmpnum", res.cookie("utmpnum"));
				}else{
					Log.v(TAG, "res is null");				
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			flag = true;
			return null;
		}
		
	}
	
	public void reLogin(){
		flag = false;
		new loginAsyncTask().execute();
		while(!flag){
			try {
				Thread.sleep(200);
				Log.v(TAG, "sleep for 200ms");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			continue;
		}
	}
	
	// get global cookie
	public HashMap<String, String> get_cookie(){

		while(null == this.cookie.get("utmpuser")){
			Log.v(TAG, "cookie null");
			if(true == flag){
				reLogin();				
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.v(TAG, this.cookie.get("utmpuser"));
		return this.cookie;
	}
	// set global cookie
	public void setCookie(HashMap<String, String> cookie){
		this.cookie = cookie;
	}
	
	// get last user name
	public String getLastUsername(){
		return null;
	}
	
	// read configuration from shared preference and initiate environment
	public boolean initEnv(){

		return readSharedPref() && checkVersion() ;
	}
	
	// save configuration to shared preference and shutdown environment
	public boolean shutdown(){
		return writeSharedPref();
	}
	



	
	// read in data from shared preference file to memory (variables within this application) 
	public boolean readSharedPref(){
		return true;
	}
	
	// write out data from memory(variables within this application) to shared preference file
	public boolean writeSharedPref(){
		return true;
	}
	
	// check software version and update
	public boolean checkVersion(){
		return true;
	}
	
	// check network status
	public boolean checkNetwork(){
		ConnectivityManager conmanager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conmanager.getActiveNetworkInfo();
		if(null != info){
			return info.isConnected();
		}else{
			return false;
		}
	}
	
	public void shortToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	public void longToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
}
