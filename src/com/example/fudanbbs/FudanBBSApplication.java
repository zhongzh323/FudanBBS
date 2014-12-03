package com.example.fudanbbs;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
public class FudanBBSApplication extends Application {
	// test
	final String helloworld = "whatsapp";
	
	// global configuration 
	final boolean ENABLE = true;
	final boolean DISABLE = false;
	
	// login related:
	private String lastUsername;
	private HashMap<String, String> account;
	private boolean rememberPassword, autoLogin;
	private String cookie = null;
	
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
	
	// advertise
	
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
		String PrefsName = "AccountInfo";
		SharedPreferences setting = getSharedPreferences(PrefsName, 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putString("username", map.get("username"));
		editor.putString("password", map.get("password"));
		editor.putString("rememberpassword", map.get("rememberpassword"));
		editor.putString("autologin", map.get("autologin"));		
		return editor.commit();
		
	}	
	// get global cookie
	public String get_cookie(){
		return cookie;
	}
	// set global cookie
	public void setCookie(String aCookie){
		cookie = aCookie;
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
