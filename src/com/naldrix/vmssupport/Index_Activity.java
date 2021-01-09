package com.naldrix.vmssupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;
import com.naldrix.vmssupport.UserValidation_Activity.currBackgroundTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Index_Activity extends ActionBarActivity {
	DatabaseHelper myDB;
	ImageView imgLogo,imageConnStat;
	currBackgroundTask currBGTask;
	String currServer="",currUser="",currUserType="";
	String ConnectectionStatus = "notconnected";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_layout);
			
		imgLogo = (ImageView)findViewById(R.id.imgBigLogo);
		
		
		
		myDB = new DatabaseHelper(this);
		
		UserValidation_Or_MainUserMenu_Launcher();
		
		Cursor server_record = myDB.getAllData("tblServer");
		
		Cursor user_record = myDB.getAllData("tblUser");
		
		
		
		if( server_record.getCount() > 0 || user_record.getCount() > 0) {
			
			RunInEverySecond();
		} else {
			/** start update the connection ***/
			myDB.DeleteAllByTable("tblCurrConn");
			
			ArrayList<String> ArrValues = new ArrayList<String>();
			ArrayList<String> ArrFields = new ArrayList<String>();
		
			ArrFields.add("status");	
		
			ArrValues.add( "online" );						
			
			myDB.InsertNewRecord("tblCurrConn", ArrFields, ArrValues);
			/** end update the connection ***/
		}
		
	}
	
	public void RunInEverySecond( ) {
		final Handler handler = new Handler();
		
		
		
		
		handler.postDelayed(new Runnable() {
		                @Override
		                public void run() {
		                	Cursor server_record = myDB.getRecordByID("tblServer","isDefault","1");
		            		Cursor user_record = myDB.getRecordByID("tblUser","user_id","1");
		            		
		            		if( server_record.getCount() > 0 || user_record.getCount() > 0) {
		            			
		            			server_record.moveToFirst();
		            			user_record.moveToFirst();
		            			
		            			currServer = server_record.getString(1);	
		            			currUser = user_record.getString(1);
		            			currUserType = user_record.getString(5);
		            		}
		                	
		                	//Toast.makeText(getApplicationContext(), "run in every sec", Toast.LENGTH_SHORT).show();
		                	currBGTask = new currBackgroundTask(getApplicationContext());
		                	currBGTask.execute( currServer,currUser,currUserType );
		                    handler.postDelayed(this, 5000);
		                }
		 }, 1000);
	}
	
	
	public void UserValidation_Or_MainUserMenu_Launcher() {
		
		imgLogo.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Cursor result = myDB.getAllData("tblUser");				
				int count_result = result.getCount();
				
				if( count_result > 0 ) {
					Intent i = new Intent( getApplicationContext(), UserMainMenu_Activity.class );
					startActivity(i);
				} else {
					Intent i = new Intent( getApplicationContext(), UserValidation_Activity.class );
					startActivity(i);
				}
				
				
			}
			
		});
		
	}
	
	/** sky class for bgTask ***/
	class currBackgroundTask extends AsyncTask<String,Void,String> {
		
		
		Context ctx;
		public currBackgroundTask(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub
			String serverIP_add =  param[0];
			String currUser_id =  param[1];
			String currUser_Type =  param[2];
			String api_url;
			String data;
			
			
			
			
			if( ConnectectionStatus=="connected" ) {
				 api_url = "http://"+serverIP_add+"/vms/api/json.php";
				 data = "?q=visitor_logged&visited_id="+currUser_id+"&vms_mobile=true&type="+currUser_Type;
			} else {
				 api_url = "http://"+serverIP_add+"/vms/api/json.php";
				 data = "?q=vms_mobile_connect";
			}
			
			
						
			try {
				URL url = new URL( api_url+data );
				HttpURLConnection connect = (HttpURLConnection)url.openConnection();
								
				InputStream IS = connect.getInputStream();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(IS));
				String line;
				StringBuilder strRecord = new StringBuilder();
				while( ( line = br.readLine() ) != null ) {
					
					strRecord.append( line );
					
				}
				connect.disconnect();
				IS.close();
				br.close();
				return strRecord.toString().trim();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
		//	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			
			if( result != null ) {
				JSONObject jo;
				String connected = "";
				/** start update the connection ***/
					myDB.DeleteAllByTable("tblCurrConn");
					
					ArrayList<String> ArrValues = new ArrayList<String>();
					ArrayList<String> ArrFields = new ArrayList<String>();
				
					ArrFields.add("status");	
				
					ArrValues.add( "online" );						
					
					myDB.InsertNewRecord("tblCurrConn", ArrFields, ArrValues);
				/** end update the connection ***/
				if( ConnectectionStatus=="connected" ) {
					
					try {
						jo = new JSONObject( result );
						JSONArray jA = jo.getJSONArray("server_response");
						/***String strCount = String.valueOf( jA.length() );
						
						for(int i = 0; i < jA.length(); i++ ) {
							jo = jA.getJSONObject(i);							
							String visitor_id = jo.getString("visitor_id").toString();
							String visitor_img = jo.getString("visitor_img").toString();
						}***/
						alertNofication( result,"new_visit" );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				} else {
					try {
						jo = new JSONObject( result );
						connected = jo.getString("server_response").toString();
						//alertInFiveNofication();
						
						if( connected.equals("connected") ) {
							ConnectectionStatus = "connected";
														
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} else {				 
			//	Toast.makeText(ctx, "Not connected.", Toast.LENGTH_SHORT).show();
				/** start update the connection ***/
					myDB.DeleteAllByTable("tblCurrConn");
					
					ArrayList<String> ArrValues = new ArrayList<String>();
					ArrayList<String> ArrFields = new ArrayList<String>();
				
					ArrFields.add("status");	
				
					ArrValues.add( "offline" );						
					
					myDB.InsertNewRecord("tblCurrConn", ArrFields, ArrValues);
				/** end update the connection ***/
			}
			
			
		}
		
		public void alertNofication(String data,String action) {
			//Toast.makeText(getApplicationContext(), "Hello!", Toast.LENGTH_LONG).show();
			Long AlerTime = new GregorianCalendar().getTimeInMillis()+1*1000;
			
			Intent i = new Intent(getApplicationContext(), AlertReceiver.class);
			
			Bundle bundle = new Bundle();
			bundle.putString("result_json", data);
			bundle.putString("action", action);
			
			i.putExtras(bundle);
			
			AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			
			alarmManager.set(AlarmManager.RTC_WAKEUP, AlerTime, PendingIntent.getBroadcast(getApplicationContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT));
		}
		
	}
	

}
