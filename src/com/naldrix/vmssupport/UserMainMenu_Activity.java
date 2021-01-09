package com.naldrix.vmssupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMainMenu_Activity extends ActionBarActivity {	
	DatabaseHelper myDB;
	TextView lblMessage,txtCurrUser;
	ImageView imgMessage,imgWriteNew,imgSetting,imgConnStat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermainmenu_layout);
		
		lblMessage = (TextView)findViewById(R.id.textViewMenuLabelMessage);
		
		imgMessage = (ImageView)findViewById(R.id.imgMessage );
		imgSetting = (ImageView)findViewById(R.id.imgSetting);
		imgWriteNew = (ImageView)findViewById(R.id.imgWriteNewMessage);

		myDB = new DatabaseHelper(this);
		
		
		imgConnStat = (ImageView)findViewById(R.id.imgUserMenuConnStatus);
		
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
		
	
		
		Cursor sqlite_result = myDB.getRecordByID("tblMessage","isRead","0");
		
		
		int countUnreadmessages = sqlite_result.getCount();
		String strcountUnreadmessages = String.valueOf(countUnreadmessages);
		
		lblMessage.setText( strcountUnreadmessages + " unread messages."  );
		
		
		Message_launcher();
		
		
		Settings_launcher();
		
		
		WriterNew_launcher();
		
		
		/*** start ***/

		Cursor user_record = myDB.getRecordByID("tblUser","user_id","1");
		
		
		if( user_record.getCount() > 0 ) {			
			
			user_record.moveToFirst();
		
			txtCurrUser = (TextView)findViewById(R.id.textViewName);
			
			String fullname = user_record.getString(2)+" "+user_record.getString(3)+" "+user_record.getString(4);
			
			txtCurrUser.setText(fullname);
			
		}
		/*** end ***/
	}
	
	
	public void WriterNew_launcher() {
		
		imgWriteNew.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent( getApplicationContext(), WriteNewMessage_Activity.class);
				startActivity(i);
			}
		});
		
	}
	
	public void Message_launcher() {
		
		
		imgMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "aw", Toast.LENGTH_LONG).show();
				Intent i = new Intent( getApplicationContext(), Messages_Activity.class);
				startActivity(i);
			}
		});
		
		
	}
	
	public void WriteMessage_launcher() {
		
	}
	
	public void Settings_launcher() {
		
		imgSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent( getApplicationContext(), Settings_Activity.class );
				startActivity( i );
			}
		});
		
	}
	
	
	

}
