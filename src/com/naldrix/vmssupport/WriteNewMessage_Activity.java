package com.naldrix.vmssupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;
import com.naldrix.vmssupport.Index_Activity.currBackgroundTask;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WriteNewMessage_Activity extends ActionBarActivity {
	private String array_spinner[];
	String currServer,currUser,currUserType;
	DatabaseHelper myDB;
	currBackgroundTask currBGTask;
	currBackgroundTask2 currBGTask2;
	Spinner dd_spinner;
	ArrayAdapter adapter;
	Button btnSend;
	EditText txtMessage;
	ImageView btnImageBack;
	TextView txtCurrUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writenewmessage_layout);
		
		myDB = new DatabaseHelper(this);
		currBGTask = new currBackgroundTask(this);
		
		dd_spinner = (Spinner)findViewById(R.id.spinner1);
		
		btnSend = (Button)findViewById(R.id.buttonSend);
	    
		txtMessage = (EditText)findViewById(R.id.txtNewMessageContent);
		
		btnImageBack = (ImageView)findViewById(R.id.imgBackMenu);
		
	
	        
	   /*** start get value for combo box ***/  
	        
	        Cursor server_record = myDB.getRecordByID("tblServer","isDefault","1");
    		Cursor user_record = myDB.getRecordByID("tblUser","user_id","1");
    		
    		if( server_record.getCount() > 0 || user_record.getCount() > 0) {
    			
    			server_record.moveToFirst();
    			user_record.moveToFirst();
    			
    			currServer = server_record.getString(1);	
    			currUser = user_record.getString(1);
    			currUserType =  user_record.getString(5);
    			
    			txtCurrUser = (TextView)findViewById(R.id.textViewName);
    			
    			String fullname = user_record.getString(2)+" "+user_record.getString(3)+" "+user_record.getString(4);
    			
    			txtCurrUser.setText(fullname);
    			
    		}
        	
        	//Toast.makeText(getApplicationContext(), "run in every sec", Toast.LENGTH_SHORT).show();
        	currBGTask = new currBackgroundTask(getApplicationContext());
        	currBGTask.execute( currServer,currUser );
	        
	   /*** end  get value for combo box ***/   
        	
        SubmitLocation(); 	
        Back_launcher();
        
       
	}
	
	
	public void Back_launcher() {
		btnImageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),UserMainMenu_Activity.class);
				startActivity(i);
			}
		});
	}
	
	public void SubmitLocation() {
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String strDDLocation = dd_spinner.getSelectedItem().toString();
				String strMessage = txtMessage.getText().toString();
				
				
				currBGTask2 = new currBackgroundTask2(getApplicationContext());
	        	currBGTask2.execute( currServer,currUser, currUserType, strMessage, strDDLocation );
				
				
			}
		});
	}
	
	
	/** start sky class for bgTask 1 ***/
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
			String api_url;
			String data;
			
			
			
			
		
			api_url = "http://"+serverIP_add+"/vms/api/json.php";
			data = "?q=location";
			
			
			
						
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
			JSONObject jo;
			
			
		//	Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
			
			try {	
				
				 JSONArray jA = new JSONArray(result);
				 int array_count = jA.length();
				/*** start spinner content ***/
				 	array_spinner= new String[array_count];
				 	
			        array_spinner[0]="Select a location";
			 
				/*** end  spinner content ***/
				 
				
				String strCount = String.valueOf( jA.length() );
						
					for(int i = 0; i < jA.length(); i++ ) {
						jo = jA.getJSONObject(i);	
						
						String location_id = jo.getString("location_id").toString();
						String location_name = jo.getString("location_name").toString();
						
						int intLocationID = Integer.parseInt(location_id);
						
						array_spinner[i] = location_name;
						
					}
					
					
				   adapter = new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, array_spinner);
				   dd_spinner.setAdapter(adapter);
					
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

	}
	/** end  sky class for bgTask 1 ***/
	
	/** start sky class for bgTask 1 ***/
	class currBackgroundTask2 extends AsyncTask<String,Void,String> {
		
		
		Context ctx;
		public currBackgroundTask2(Context ctx) {
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
			String type = param[2];
			String message = param[3];
			String location = param[4];
			String api_url;
			String data;
			
			
			
			
		
			
			
						
			try {

				api_url = "http://"+serverIP_add+"/vms/api/json.php";
				data = "?q=user_post&user_id="+currUser_id+"&category="+type+"&message="+URLEncoder.encode(message,"UTF-8")+"&location="+URLEncoder.encode(location,"UTF-8");
				
				
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
			JSONObject jo;
			String message="";
			
		//	Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
			if(result != null ) {
				try {	
					
					jo = new JSONObject( result );
					JSONArray jA = jo.getJSONArray("server_response");
					
					
					String strCount = String.valueOf( jA.length() );
					
					for(int i = 0; i < jA.length(); i++ ) {
						jo = jA.getJSONObject(i);							
						message = jo.getString("response").toString();					
					}
						
					
					
					if ( message.equals("success") ) {				
						AlertBox("Server Response","Successfully send.");	
					} else {			
						AlertBox("Server Response","Fail to send. Please check your connection.");
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				AlertBox("Server Response","Not connected to the server.");	
			}
		
		}
		
		public void AlertBox(String title, String message ) {
			AlertDialog.Builder alert = new AlertDialog.Builder(WriteNewMessage_Activity.this);
			
			alert.setTitle("Server Response");
			alert.setMessage(message);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					Intent i = new Intent(ctx, UserMainMenu_Activity.class);
					startActivity( i );
				}
			});
			
			alert.show();			
		}

	}
	/** end  sky class for bgTask 1 ***/
	
	
}
