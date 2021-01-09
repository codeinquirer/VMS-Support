package com.naldrix.vmssupport;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naldrix.vmssupport.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
	JSONObject jo;
	DatabaseHelper myDB;	
	String gblTitle = "", data ="",strCountJO="";
	@Override
	public void onReceive(Context ctx, Intent i) {
		// TODO Auto-generated method stub
		Bundle bundle = i.getExtras();
		data = bundle.getString("result_json");
		String action = bundle.getString("action");
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
				
		int intCount = 0;
		
		myDB = new DatabaseHelper(ctx);
		
		try {
			jo = new JSONObject( data );
			JSONArray jA = jo.getJSONArray("server_response");
			intCount = jA.length();
			strCountJO = String.valueOf(intCount);
			
			for(int x = 0; x < jA.length(); x++ ) {
				jo = jA.getJSONObject(x);							
				String visitor_logged_id = jo.getString("visitor_logged_id").toString();
				String visitor_fullname = jo.getString("visitor_fullname").toString();
				String visit_purpose = jo.getString("visit_purpose").toString();
				String visitor_img = jo.getString("visitor_img").toString();
				
				Cursor sqlite_result = myDB.getRecordByID("tblMessage","visitor_logged_id",visitor_logged_id);
				
				if(  sqlite_result.getCount() == 0 ) {
					
					ArrayList<String> ArrValues = new ArrayList<String>();
					ArrayList<String> ArrFields = new ArrayList<String>();
					
		
					ArrFields.add("message_desc");
					ArrFields.add("from_visitor_name");	
					ArrFields.add("date_created");	
					ArrFields.add("isRead");	
					ArrFields.add("visit_status");	
					ArrFields.add("visitor_logged_id");	
					ArrFields.add("purpose");	
					ArrFields.add("imgAvatar");
					
					ArrValues.add( visitor_fullname+" wants to visit you." );
					ArrValues.add( visitor_fullname );	
					ArrValues.add( currentDateTimeString );	
					ArrValues.add( "0" );	
					ArrValues.add( "not decided" );	
					ArrValues.add( visitor_logged_id );	
					ArrValues.add( visit_purpose );	
					ArrValues.add( visitor_img );
					
					myDB.InsertNewRecord("tblMessage", ArrFields, ArrValues);
				} else {
					intCount = intCount - 1;
				}
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(intCount > 0 ) {
			String title = String.valueOf( intCount );
			gblTitle = title+" New Message(s)";
			createNotification(ctx, gblTitle ,"You have new visit.","Visit Notification Alert");		
		}
		
		
	}

	
	public void createNotification(Context ctx, String title, String msg,String msgAlert) {
		
		//Toast.makeText(ctx, "test", Toast.LENGTH_LONG).show();
	
		
		
		/*** start save to tblMessage 
		ArrayList<String> ArrValues = new ArrayList<String>();
		ArrayList<String> ArrFields = new ArrayList<String>();
		
		ArrFields.add("message");
		ArrFields.add("message_num");		
		
		ArrValues.add( gblTitle );
		ArrValues.add( strCountJO );	
		
		myDB.InsertNewRecord("tblNotification_Logs", ArrFields, ArrValues);
		 end save to tblMessage ***/
		
		Intent i_menu = new Intent( ctx,UserMainMenu_Activity.class );
	
		PendingIntent p_i = PendingIntent.getActivity(ctx, 0, i_menu, 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
		
		mBuilder.setSmallIcon(R.drawable.messages);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(msg);
		mBuilder.setTicker(msgAlert);
		
		mBuilder.setContentIntent( p_i );
		mBuilder.setDefaults( NotificationCompat.PRIORITY_DEFAULT );
		
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(alarmSound);
		
		
		
		mBuilder.setAutoCancel(true);
		
		NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Random rn = new Random();
		int randNum = rn.nextInt(1000 - 1 + 1) + 1;
		
		
		mNotificationManager.notify( 1,mBuilder.build() );
	}
	
}