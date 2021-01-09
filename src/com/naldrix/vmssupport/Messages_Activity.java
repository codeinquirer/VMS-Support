package com.naldrix.vmssupport;



import java.text.DateFormat;
import java.util.Date;

import com.naldrix.vmssupport.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Messages_Activity extends ActionBarActivity {
	DatabaseHelper myDB;
	TableLayout tblMessage_layout;
	TableRow newTr,newTrSep;
	TextView newTxtMessDesc, newtxtStatus, newtxtDate,txtCurrUser;
	ImageView imgBack,imgConnStat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView( R.layout.messages_layout );
		
		
		imgBack = (ImageView)findViewById(R.id.imgBackMenu);
		
		imgConnStat = (ImageView)findViewById(R.id.imgMessagesConnStatus);
		
		myDB = new DatabaseHelper(this);
		
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
		
		
		tblMessage_layout = (TableLayout)findViewById(R.id.tblWriteNewMessage);
		
		tblMessage_layout.setColumnStretchable(0, true);
		tblMessage_layout.setColumnStretchable(1, true);
		tblMessage_layout.setColumnStretchable(2, true);
		
		
		
		ViewAllStudent();
		
		BackToMainMenu();
		
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
	
	public void BackToMainMenu() {
		
		imgBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), UserMainMenu_Activity.class);
				startActivity(i);
			}
		});
	}
	
	
	
	public void ViewAllStudent() {
		
		Cursor result = myDB.getAllDataWithDescOrder("tblMessage","visitor_logged_id");
		int intCount = 0;
		
		if( result.getCount() > 0 ) {
			
			while( result.moveToNext() ) {
				newTr = new TableRow(Messages_Activity.this);
				newTrSep = new TableRow(Messages_Activity.this);
				newTxtMessDesc = new TextView(Messages_Activity.this);
				newtxtStatus = new TextView(Messages_Activity.this);
				newtxtDate = new TextView(Messages_Activity.this);
				
				
				intCount = intCount + 1 ;
				
			
				int intID = Integer.parseInt( result.getString(0).toString() );
				
				String getMessage = result.getString(2).toString();
				String getDate = result.getString(3).toString();
				String getIsRead = result.getString(4).toString();
				
				String arrMessage[] = getMessage.split(" ", 2);
				String firstName = arrMessage[0]; 
				
				String arrDate[] = getDate.split(" ");
				String date_today = arrDate[0]+" "+arrDate[1]+" "+arrDate[2]; 
				
				
				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
				String arrcurrDate[] = currentDateTimeString.split(" ");
				String strCurrentDate = arrcurrDate[0]+" "+arrcurrDate[1]+" "+arrcurrDate[2]; 
				
				
				

				newTxtMessDesc.setText( firstName+" wants to visit you." );	
				newTxtMessDesc.setTextSize(14);				
				newTxtMessDesc.setId( intID );
				
				
				
				
				if( getIsRead.equals("0") ) {
					getIsRead = "unread";
					newTxtMessDesc.setTypeface(null, Typeface.BOLD);
					newTxtMessDesc.setPadding(3, 10, 0, 10);
					newTxtMessDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
					newtxtStatus.setBackgroundColor( Color.parseColor("#E46969") );
					newtxtStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
					newtxtStatus.setTextColor( Color.parseColor("#FFFFFF") );
					newtxtStatus.setGravity(Gravity.CENTER);
					
					
				} else {
					getIsRead = "read";	
					newTxtMessDesc.setTypeface(null, Typeface.NORMAL);
					newTxtMessDesc.setPadding(3, 10, 0, 10);
					newTxtMessDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
					newtxtStatus.setBackgroundColor( Color.parseColor("#DFF0D8") );
					newtxtStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
					newtxtStatus.setTypeface(null, Typeface.BOLD);
					newtxtStatus.setTextColor( Color.parseColor("#3C736D") );
					newtxtStatus.setGravity(Gravity.CENTER);;	
				}
				
				
				if( strCurrentDate == date_today ) {
					date_today = "Today";
				}
				
			
				
				newtxtStatus.setText( getIsRead );
				//newtxtStatus.setLayoutParams(paramsIsRead);
				newtxtStatus.setTextSize(12);
				newtxtStatus.setId( intID );
				
				newtxtDate.setText( "    "+date_today );
				//newtxtDate.setLayoutParams(paramsDate);
				newtxtDate.setTextSize(10);
				newtxtDate.setId( intID );
				
				
				if( (intCount % 2) == 0 ) {
					newTr.setBackgroundColor( Color.parseColor("#F0F0F0") );
				} else {
					newTr.setBackgroundColor( Color.parseColor("#99CCFF") );
				}
				
		
				newTr.addView( newTxtMessDesc );
				newTr.addView( newtxtStatus );
				newTr.addView( newtxtDate );
		
				tblMessage_layout.addView( newTr );
				
				ViewMessageByID();
				
			} 
			
		} else {
			
			Toast.makeText(getApplicationContext(), "No record.", Toast.LENGTH_LONG).show();
			
		}
		
		
	}
	
	public void ViewMessageByID() {
		
		newTxtMessDesc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strGetID = String.valueOf( v.getId() );
				
				Intent i = new Intent( getApplicationContext(), MessageByID_Activity.class );
				
				Bundle bundle = new Bundle();
				bundle.putString("id", strGetID);
				
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		
	}

	
	
}
