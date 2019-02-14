package com.example.councellorbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class UserSessionManager {


	// Shared Preferences reference
	SharedPreferences pref;
	
	// Editor reference for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREFER_NAME = "AndroidExamplePref";
	
	// All Shared Preferences Keys
	private static final String IS_USER_LOGIN = "IsUserLoggedIn";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_USER_PASS = "password";

	public static final String KEY_USER_ADDRESS = "address";
	public static final String KEY_USER_CONTACT = "contact";
	public static final String KEY_NAME = "name";
	public static final String KEY_ROLE_ID = "role_id";
	public static final String KEY_TEST = "test";
	public static final String KEY_FEILD = "field";
	// Constructor
	public UserSessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	//Create login session
	public void createUserLoginSession(String email, String user_id, String user_pass, String username,String name,String contact,String address){
		// Storing login value as TRUE
		editor.putBoolean(IS_USER_LOGIN, true);
		
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_USER_ID, user_id);
		editor.putString(KEY_USER_PASS, user_pass);
		editor.putString(KEY_USERNAME, username);

		editor.putString(KEY_NAME, name);
		editor.putString(KEY_USER_CONTACT, contact);
		editor.putString(KEY_USER_ADDRESS, address);
		//editor.putString(KEY_ROLE_ID, role_id);

		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method will check user login status
	 * If false it will redirect user to login page
	 * Else do anything
	 * */
	public boolean checkLogin(){
		// Check login status
		if(!this.isUserLoggedIn()){
			
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, login.class);
			
			// Closing all the Activities from stack
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
			
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		
		//Use hashmap to store user credentials
		HashMap<String, String> user = new HashMap<String, String>();
		
		// user email id
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
		user.put(KEY_USER_PASS, pref.getString(KEY_USER_PASS, null));
		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

		user.put(KEY_USER_ADDRESS, pref.getString(KEY_USER_ADDRESS, null));
		user.put(KEY_USER_CONTACT, pref.getString(KEY_USER_CONTACT, null));
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));

		user.put(KEY_FEILD, pref.getString(KEY_FEILD, null));
		user.put(KEY_TEST, pref.getString(KEY_TEST, null));
		//user.put(KEY_ROLE_ID, pref.getString(KEY_ROLE_ID, null));

		// return user
		return user;
	}
	
	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		
		// Clearing all user data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, login.class);
		
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}
	
	// Check for login
	public boolean isUserLoggedIn(){
		return pref.getBoolean(IS_USER_LOGIN, false);
	}

	public void recordMarks(String marks) {
		editor.putString(KEY_TEST, marks);
		// commit changes
		editor.commit();
	}
	public void recordField(String feild) {
		editor.putString(KEY_FEILD, feild);
		// commit changes
		editor.commit();
	}
}
