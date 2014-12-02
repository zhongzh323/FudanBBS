package com.example.fudanbbs;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Joseph.Zhong
 *
 */
public class LoginActivity extends Activity {
    private String username, password;
    private boolean rememberPassword, autoLogin;
    private EditText ETusername, ETpassword;
    private CheckBox CBrememberPassword, CBautoLogin;
    private Button BtnLogin, BtnGuestLogin;
    private TextView TVretrievePassword, TVregister;
    private HashMap <String, String> accountInfo;
    private FudanBBSApplication currentApplication;
    private AlertDialog loginingDialog;
    private ProgressDialog progressdialog;
	private String cookieValue;
    OnClickListener listener = new OnClickListener(){
    
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		switch(v.getId()){
    		case R.id.login:
    			login(false);
    			break;
    		case R.id.guestLogin:
    			login(true);
    			break;
    		case R.id.register:
    			callBrowser2page("http://bbs.fudan.edu.cn/reg.htm");
    			break;
    		}
    	}
	
    };
    public void login(boolean isGuest){
//		currentApplication.shortToast("login clicked!");
		ETusername = (EditText)findViewById(R.id.username);
		username = ETusername.getText().toString().trim();
		ETpassword = (EditText)findViewById(R.id.password);
		password = ETpassword.getText().toString().trim();
		loginingDialog = new AlertDialog.Builder(LoginActivity.this).create();
		loginingDialog.setCanceledOnTouchOutside(true);    			
		if(username.isEmpty() || password.isEmpty()){
			loginingDialog.setMessage(getResources().getString(R.string.accountNotNull));	
			loginingDialog.show();
		}else if(!currentApplication.checkNetwork()){
			loginingDialog.setMessage(getResources().getString(R.string.networkNotAvailable));	
			loginingDialog.show();
		}else{
			progressdialog = new ProgressDialog(LoginActivity.this);
			progressdialog.setMessage(getString(R.string.loading));
			progressdialog.setCancelable(false);
			progressdialog.setCanceledOnTouchOutside(false);
			progressdialog.setProgressStyle(progressdialog.STYLE_SPINNER);		
			progressdialog.show();	
			loginTask login = new loginTask();
			login.execute();
		}    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logintitlebar);
		ETusername = (EditText)findViewById(R.id.username);
		ETpassword = (EditText)findViewById(R.id.password);
		currentApplication = (FudanBBSApplication)getApplication();
		accountInfo = currentApplication.getAccountInfo();
		if(null != accountInfo){
			username = accountInfo.get("username");
			if("true" == accountInfo.get("rememberpassword")){
				rememberPassword = true;
				password = accountInfo.get("password");
			}else{
				rememberPassword = false;
			}
			if("true" == accountInfo.get("autologin")){
				autoLogin = true;
			}else{
				autoLogin = false;
			}
		}

		
		if(null != username){
			ETusername.setText(username);
		}
		if(null != password){
			ETpassword.setText(password);
		}
		
		// for convenience of debug
		username = "joancruise";
		password = "38268349220";
		
		CBrememberPassword = (CheckBox)findViewById(R.id.checkRememberPassword);
		CBautoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);
		BtnLogin = (Button)findViewById(R.id.login);
		BtnLogin.setOnClickListener(listener);
		BtnGuestLogin = (Button)findViewById(R.id.guestLogin);
		BtnGuestLogin.setOnClickListener(listener);
		
		TVregister = (TextView)findViewById(R.id.register);
		TVregister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		TVregister.setOnClickListener(listener);
//		Toast.makeText(getApplicationContext(), ((FudanBBSApplication)getApplication()).LANDSCAPE, Toast.LENGTH_LONG).show();
	}
	

	// call system internet browser to access web page
	public void callBrowser2page(String aWebpage){
		Intent callBrowser = new Intent(Intent.ACTION_VIEW);
		callBrowser.setData(Uri.parse(aWebpage));
		startActivity(callBrowser);
		
	}
	
	//
	public void popLoginDialog(){
		
	}

	// login page Async task class
	public class loginTask extends AsyncTask<Object, Object, Object>{
	//	private TaskItem aTaskItem;
		private int httpResponseCode;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			cookieValue = "";

		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(302 == httpResponseCode && "" != cookieValue){
				loginingDialog.setMessage(getResources().getString(R.string.loginSucceed));
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				loginingDialog.dismiss();
				finish();
			}else if (200 == httpResponseCode){
				loginingDialog.setMessage(getResources().getString(R.string.accountError)+httpResponseCode+cookieValue);
//				loginingDialog.cancel();
			}else if (0 ==httpResponseCode ){
				loginingDialog.setMessage(getResources().getString(R.string.loginFailedServerError));
			}else{
				loginingDialog.setMessage(getResources().getString(R.string.loginFailed)+" error code="+httpResponseCode);				
			}
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			URL url;
			HttpURLConnection connection = null;

			try {
				url = new URL("http://bbs.fudan.edu.cn/bbs/login");
				connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(10000);
				connection.setInstanceFollowRedirects(false);
				connection.connect();
				
				OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
				osw.write("id="+username+"&pw="+password);
				osw.flush();
				osw.close();
				httpResponseCode = connection.getResponseCode();
				cookieValue = connection.getHeaderField("Set-Cookie");
				currentApplication.setCookie(cookieValue);    	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				connection.disconnect();
			}
			return null;
		}
		
	}
	
}
