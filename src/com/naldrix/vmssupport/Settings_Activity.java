package com.naldrix.vmssupport;


import java.util.ArrayList;







import com.naldrix.vmssupport.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Settings_Activity extends ActionBarActivity {
	DatabaseHelper myDB;
	TableLayout tblIPAddr_layout;
	TableRow newTr;
	TextView newTxtIP,txtCurrUser;
	EditText txtaddNewIP;
	Button btnNewDefault,btnAddIP;
	ImageView imgBackMenu,imgConnStat;
	int intCount = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		tblIPAddr_layout = (TableLayout)findViewById(R.id.tblWriteNewMessage);
		txtaddNewIP = (EditText)findViewById(R.id.editTextIP);
		
		
		tblIPAddr_layout.setColumnStretchable(0, true);
		tblIPAddr_layout.setColumnStretchable(1, true);
		tblIPAddr_layout.setColumnStretchable(2, true);
		
		btnAddIP = (Button)findViewById(R.id.btnAddIP);		
		
		imgBackMenu = (ImageView)findViewById(R.id.imgBackMenu);
		
		myDB = new DatabaseHelper(this);
		
		imgConnStat = (ImageView)findViewById(R.id.imgSettingsConnStatus);
		
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
		
	
		ViewAllIPAddress();
		
		AddNewIPAddress();
		
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
		
		imgBackMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), UserMainMenu_Activity.class);
				startActivity(i);
			}
		});
		
	}
	
	public void AddNewIPAddress() {
	
		btnAddIP.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String strNewIP = txtaddNewIP.getText().toString();
				
				ArrayList<String> ArrValues = new ArrayList<String>();
				ArrayList<String> ArrFields = new ArrayList<String>();
				
				ArrFields.add("server_addr");
				ArrFields.add("isDefault");	
			
				ArrValues.add( strNewIP );	
				ArrValues.add( "0" );	
			
				long status = myDB.InsertNewRecord("tblServer", ArrFields, ArrValues);
				
				if(status == -1) {
					AlertBox("Message","Fail to add.");
					
				} else {
					AlertBox("Message","Successfully added.");
					
				}
			}
		});
		
	}
	
	public void ViewAllIPAddress() {
		
		Cursor result = myDB.getAllData("tblServer");
		
		if( result.getCount() > 0 ) {
			
			while( result.moveToNext() ) {
				String strIsDefault = result.getString(2).toString();
				intCount = intCount + 1;
				
				newTr = new TableRow(Settings_Activity.this);
				newTxtIP = new TextView(Settings_Activity.this);
				btnNewDefault = new Button(Settings_Activity.this);
				
				
				int intID = Integer.parseInt( result.getString(0).toString() );
				
				newTxtIP.setText( result.getString(1).toString()  );
				newTxtIP.setPadding(3, 13, 0, 13);
				
				btnNewDefault.setText("Set as Default");				
				btnNewDefault.setTextSize(16);
				btnNewDefault.setBackgroundColor( Color.parseColor("#CCCCCC") );
				btnNewDefault.setId(intID);
				
				newTr.setBackgroundColor( Color.parseColor("#F0F0F0") );
		
				
				if( strIsDefault.equals( "1" ) ) {
					btnNewDefault.setBackgroundColor( Color.parseColor("#3960d0") );
					newTr.setBackgroundColor( Color.parseColor("#99CCFF") );
					btnNewDefault.setText("Default");					
				}				
				
				newTr.setPadding(5, 0, 2, 0);				
				newTr.addView(newTxtIP);
				newTr.addView(btnNewDefault);
				 
				
				tblIPAddr_layout.addView( newTr );
				
				newDefault();
				
			} 
			
		} else {
			
			Toast.makeText(getApplicationContext(), "No record.", Toast.LENGTH_LONG).show();
		
		}
		
	}
	
	public void newDefault() {
		
		btnNewDefault.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strGetID =  String.valueOf( v.getId() );
				int status = 0;
				
				
				Cursor result = myDB.getAllData("tblServer");
				if( result.getCount() > 0 ) {
					
					while( result.moveToNext() ) {
						String strIsDefault = result.getString(2).toString();
							String strID = result.getString(0).toString();
						
							ArrayList<String> arrFields = new ArrayList<String>();
							ArrayList<String> arrValues = new ArrayList<String>();
							
							arrFields.add("server_id");
							arrFields.add("isDefault");
						
							
							arrValues.add( strID );
							arrValues.add( "0" );
							
							status = myDB.UpdateRecord("tblServer", arrFields, arrValues);
							
					}
				}
				
			
				
				if( status > 0 ) {
					ArrayList<String> arrFields2 = new ArrayList<String>();
					ArrayList<String> arrValues2 = new ArrayList<String>();
					
					arrFields2.add("server_id");
					arrFields2.add("isDefault");
				
					
					arrValues2.add( strGetID );
					arrValues2.add( "1" );
					
					myDB.UpdateRecord("tblServer", arrFields2, arrValues2);
					
					AlertBox("Message","Successfully updated.");
				} else {
					AlertBox("Message","Fail to update.");
				}	
				
			}
			
		});
		
	}
	
	public void AlertBox(String title, String message) {
		
		AlertDialog.Builder alert =  new AlertDialog.Builder(this);
		alert.setCancelable(true);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface d, int arg1) {
				// TODO Auto-generated method stub
				d.cancel();
				Intent i = new Intent(getApplicationContext(), Settings_Activity.class);
				
				startActivity(i);
			}
		});
		alert.show();
		
	}
	
}
