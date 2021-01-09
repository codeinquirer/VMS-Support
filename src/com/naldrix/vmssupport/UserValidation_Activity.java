package com.naldrix.vmssupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UserValidation_Activity extends ActionBarActivity {
	Button btnValidate;
	EditText txtServerIP_Addr, txtSecCode;
	RadioGroup rdoGrpUserType;
	RadioButton rdoBtnSelected;
	currBackgroundTask currBGTask;
	TextView txtValidationMessage;
	String strType="",strSecCode="",strServerApi="";
	DatabaseHelper myDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uservalidation_layout);
		
		btnValidate = (Button)findViewById(R.id.btnValidate);
		txtServerIP_Addr = (EditText)findViewById(R.id.txtServerIP);
		txtSecCode = (EditText)findViewById(R.id.txtSecCode);
		rdoGrpUserType = (RadioGroup)findViewById(R.id.rdoUserType);
		
		txtValidationMessage = (TextView)findViewById(R.id.txtValidationMessage);
		
		
		myDB =  new DatabaseHelper(this);
		
		currBGTask = new currBackgroundTask(this);
		
		/*** broadCast reciever ***/
		
		
		
		
		User_Validation();
		
		/***  strip the comment if final
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			btnValidate.setEnabled(true);
		} else {
			btnValidate.setEnabled(false); 
			txtValidationMessage.setText( "Not connected to WiFi.Please connect to VMS Wifi first." );
		}
		***/
		
	}
	
	
	
	
	public void User_Validation() {
		
		btnValidate.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
					
			
					strServerApi = txtServerIP_Addr.getText().toString();
					strSecCode = txtSecCode.getText().toString();
				
					int selectedType = rdoGrpUserType.getCheckedRadioButtonId();
					
					if(selectedType > 0) {
						rdoBtnSelected = (RadioButton)findViewById( selectedType );
						strType = rdoBtnSelected.getText().toString();
					} 
					
					
					
					
					if( (strServerApi.trim().equals("") ||  strSecCode.trim().equals("")) || (strType.trim().equals("")) ){

						txtValidationMessage.setText( "You must fill up all the fields." );
							
					} else {	
			
						currBGTask.execute( strServerApi, strSecCode, strType );
						
						btnValidate.setEnabled(false);
						btnValidate.setText("Validating...");
			
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
			String secCode = param[1];
			String userType = param[2];
			userType = userType.toLowerCase();
			
			String api_url = "http://"+serverIP_add+"/vms/api/json.php";
			String data = "?q="+userType+"&vms_mobile=true&validation_code="+secCode;
						
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
			
			if( result == null ) {
				
				txtValidationMessage.setText( "Invalid IP address or validation Code." );
								
				btnValidate.setText("Validate");
				
				Handler myHandler = new Handler();
				myHandler.postDelayed(BackToIndex_Page, 2000);		
				
			} else {
				JSONObject jo;
				try {
					jo = new JSONObject( result );
					JSONArray jA = jo.getJSONArray("server_response");
					String strCount = String.valueOf( jA.length() );
					
					
					JSONObject jo_data = jA.getJSONObject(0);
					
					String id = jo_data.getString("id").toString();	
					Integer intID = Integer.parseInt( id );
					
					if( intID > 0 ) {			
						String isValidated = jo_data.getString("isValidated").toString();
											
						if( !(isValidated.equals("1")) ) {
							String userID = jo_data.getString("id").toString();	
							String fname = jo_data.getString("fname").toString();
							String lname = jo_data.getString("lname").toString();
							String mname = jo_data.getString("mname").toString();	
							String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
							/*** start save in phone dbase ***/
							ArrayList<String> ArrValues = new ArrayList<String>();
							ArrayList<String> ArrServer = new ArrayList<String>();
							ArrayList<String> FieldsArr = new ArrayList<String>();
							ArrayList<String> FieldsServer = new ArrayList<String>();
							
							ArrValues.add( userID );
							ArrValues.add( fname );
							ArrValues.add( mname );
							ArrValues.add( lname );
							ArrValues.add( strType );
							ArrValues.add( strSecCode );
							ArrValues.add( currentDateTimeString );
							
							
							FieldsArr.add( "emp_id" );
							FieldsArr.add( "user_fname" );
							FieldsArr.add( "user_mname" );
							FieldsArr.add( "user_lname" );
							FieldsArr.add( "user_type" );
							FieldsArr.add( "valid_code" );
							FieldsArr.add( "date_validated" );
							
							
							
							ArrServer.add( strServerApi );
							ArrServer.add( "1" );
							
							FieldsServer.add( "server_addr" );
							FieldsServer.add( "isDefault" );
							
							
							myDB.InsertNewRecord("tblUser", FieldsArr, ArrValues);
							
							myDB.InsertNewRecord("tblServer", FieldsServer, ArrServer);
							
							Intent i = new Intent( getApplicationContext(), UserMainMenu_Activity.class );
							startActivity( i );
						} else {
							txtValidationMessage.setText( "The code has been already used." );
							
							btnValidate.setText("Validate");
							
							Handler myHandler = new Handler();
							myHandler.postDelayed(BackToIndex_Page, 2000);
						}
						/*** end  save in phone dbase ***/
						
						//Toast.makeText(getApplicationContext(), fname+" "+lname, Toast.LENGTH_LONG).show();
					} else {
						
						txtValidationMessage.setText( "Invalid IP address or validation Code." );
						
						btnValidate.setText("Validate");
						
						Handler myHandler = new Handler();
						myHandler.postDelayed(BackToIndex_Page, 2000);
						
					}
						
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			 
			
		}
		
		private Runnable BackToIndex_Page = new Runnable()
		{
		    @Override
		    public void run()
		    {
		    	Intent i = new Intent( getApplicationContext(), Index_Activity.class );
				startActivity( i );
				
		    }
		 };
		
		
	}
	
	
}
