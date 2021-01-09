package com.naldrix.vmssupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MessageByID_Activity extends ActionBarActivity {
	DatabaseHelper myDB;
	TextView txtFullname, txtDate, txtTime, txtPurpose, txtFrom,txtCurrUser; 
	ImageView imgAvatar,imgBackImg,imgConnStat;
	ImageLoader imgLoader;
	String currServer,currUser,image_url,visitor_logged_id,strID;
	Button btnAccept,btnReject,btnBackToMessage;
	currBackgroundTask currBGTask;
	TableRow trResponse;
	TableLayout tblLayoutMessageByID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView( R.layout.messagebyid_layout );
		
		Bundle bundle = getIntent().getExtras();
		strID = bundle.getString("id");		
		myDB = new DatabaseHelper(this);
		
		
		
		imgConnStat = (ImageView)findViewById(R.id.imgMessageByIDConnStatus);
		
		/** start - change the current connection status **/
		Cursor ConnectionStatusRecord = myDB.getAllData("tblCurrConn");
		
		ConnectionStatusRecord.moveToFirst();
		
		String connStat = ConnectionStatusRecord.getString(1).toString();
		
		if(connStat.equals("online")) {
			imgConnStat.setImageResource(R.drawable.online);
		} else {
			imgConnStat.setImageResource(R.drawable.offline);
		}		
		/** end - change the current connection status **/
		
		
		imgBackImg = (ImageView)findViewById(R.id.imgBackMenu);
		
		txtFullname = (TextView)findViewById(R.id.textFullName);
		txtDate = (TextView)findViewById(R.id.textDate);
		txtTime = (TextView)findViewById(R.id.textTime);
		txtPurpose = (TextView)findViewById(R.id.textPurpose);
		txtFrom = (TextView)findViewById(R.id.txtTitleSettings);
		
		btnAccept = (Button)findViewById(R.id.buttonAccept);
		btnReject = (Button)findViewById(R.id.buttonReject);
		btnBackToMessage = (Button)findViewById(R.id.buttonBackToMessage);
		
		
		tblLayoutMessageByID = (TableLayout)findViewById(R.id.tblWriteNewMessage);
		
		
		Cursor server_record = myDB.getRecordByID("tblServer","isDefault","1");
		
		Cursor user_record = myDB.getRecordByID("tblUser","user_id","1");
		
		
		if( server_record.getCount() > 0 || user_record.getCount() > 0 ) {			
			server_record.moveToFirst(); 
			user_record.moveToFirst();
			currServer = server_record.getString(1);
			
			txtCurrUser = (TextView)findViewById(R.id.textViewName);
			
			String fullname = user_record.getString(2)+" "+user_record.getString(3)+" "+user_record.getString(4);
			
			txtCurrUser.setText(fullname);
		}
				
		
		Cursor message_record = myDB.getRecordByID("tblMessage","message_id",strID);
		
		if( message_record.getCount() > 0 ) {
			
			message_record.moveToFirst();
			
			String fullname = message_record.getString(2);
			String arrFullname[] = fullname.split(" ");
			String initialName = "From: "+arrFullname[0];
			
			String date_created = message_record.getString(3);
			String visitor_img = message_record.getString(8);
			String purpose = message_record.getString(7);
			visitor_logged_id = message_record.getString(6);
			String arrDate_created[] = date_created.split(" ");
			String strDate = arrDate_created[0]+" "+arrDate_created[1]+" "+arrDate_created[2];
			String strTime = arrDate_created[3]+" "+arrDate_created[4];
						
			String isRead = message_record.getString(4);
			String visit_status = message_record.getString(5);
			
			if( !(visit_status.equals("not decided")) ) {
				trResponse = (TableRow)findViewById(R.id.tableRow8);
				tblLayoutMessageByID.removeView(trResponse);
			}
			
			
			txtFullname.setText( fullname );
			txtDate.setText( strDate );
			txtTime.setText( strTime );
			txtPurpose.setText( purpose );
			txtFrom.setText( initialName );
			
			/*** start image get ***/
			imgAvatar = (ImageView)findViewById(R.id.imgAvatarVisitor);
			
			int loader = R.drawable.loader;
			
			
			if( visitor_img.equals("")) {
				 image_url = "http://"+currServer+"/vms/public/images/visitors/default.png";
			} else {
				 image_url = "http://"+currServer+"/vms/public/images/visitors/"+visitor_img;
			}
			
		
	        // ImageLoader class instance
			imgLoader = new ImageLoader( getApplicationContext() );
	         
	        // whenever you want to load an image from url
	        // call DisplayImage function
	        // url - image url to load
	        // loader - loader image, will be displayed before getting image
	        // image - ImageView 
			imgLoader.DisplayImage(image_url, loader, imgAvatar);
		
			/*** end image get ***/
			
			/*** START change message status to READ ***/
			ArrayList<String> arrFields = new ArrayList<String>();
			ArrayList<String> arrValues = new ArrayList<String>();
	
			arrFields.add("message_id");
			arrFields.add("isRead");

			arrValues.add( strID );
			arrValues.add( "1" );
		
			int status = myDB.UpdateRecord("tblMessage", arrFields, arrValues);
			
			/*** END  change message status to READ ***/
		}
		
		
		/*** start decision response ***/		
		AcceptVisitor();
		RejectVisitor();
		BackToMessage();
		BackToMessages_launcher();
		/*** end decision response ***/
	}
	
	public void BackToMessages_launcher() {
		imgBackImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),Messages_Activity.class);
				startActivity(i);
			}
		});
	}
	
	public void BackToMessage() {
		btnBackToMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),Messages_Activity.class);
				startActivity(i);
			}
		});
		
	}
	
	public void AcceptVisitor() {
		btnAccept.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currBGTask = new currBackgroundTask(getApplicationContext());
            	currBGTask.execute( currServer,visitor_logged_id,"accept" );
			}
		});
	}
	
	public void RejectVisitor() {
		btnReject.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currBGTask = new currBackgroundTask(getApplicationContext());
            	currBGTask.execute( currServer,visitor_logged_id,"reject" );
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
			String currVisitor_logged_id =  param[1];
			String decision=  param[2];
			String api_url;
			String data;
			
			api_url = "http://"+serverIP_add+"/vms/api/json.php";
			data = "?q=employee_response&decision="+decision+"&visitor_logged_id="+currVisitor_logged_id+"&vms_mobile=true";
			
				
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
				
				
				/*** START change message status to READ ***/
				ArrayList<String> arrFields = new ArrayList<String>();
				ArrayList<String> arrValues = new ArrayList<String>();
		
				arrFields.add("message_id");
				arrFields.add("visit_status");

				arrValues.add( strID );
				arrValues.add( decision );
			
				int status = myDB.UpdateRecord("tblMessage", arrFields, arrValues);
				
				/*** END  change message status to READ ***/
				
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
			
			if( result != null ) {				
				AlertBox("Server Response","Successfully responded.");	
			} else {			
				AlertBox("Server Response","Fail to response. Please check your connection.");
			}
			
			
		}
		
		
		public void AlertBox(String title, String message ) {
			AlertDialog.Builder alert = new AlertDialog.Builder(MessageByID_Activity.this);
			
			alert.setTitle("Server Response");
			alert.setMessage(message);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					Intent i = new Intent(ctx, Messages_Activity.class);
					startActivity( i );
				}
			});
			alert.show();
			
		}
	
	}
	
	
	
	
	
}
