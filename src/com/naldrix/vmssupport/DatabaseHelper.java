package com.naldrix.vmssupport;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DBASE = "vms_db";
	public static final String tblUser = "tblUser";
	public static final String tblServer = "tblServer";
	public static final String tblNotifLogs = "tblNotification_Logs";
	public static final String tblMessage = "tblMessage";
	public static final String tblCurrConn = "tblCurrConn";
	
	public int currentID;

	public DatabaseHelper(Context context) {
		super(context, DBASE, null, 1);
		// TODO Auto-generated constructor stub
		SQLiteDatabase db = this.getWritableDatabase();
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE "+tblUser+" ( user_id INTEGER PRIMARY KEY AUTOINCREMENT,emp_id TEXT, user_fname TEXT, user_mname TEXT, user_lname TEXT, user_type TEXT, valid_code TEXT, date_validated TEXT )");
		db.execSQL("CREATE TABLE "+tblServer+" ( server_id INTEGER PRIMARY KEY AUTOINCREMENT,server_addr TEXT, isDefault TEXT )");
		db.execSQL("CREATE TABLE "+tblNotifLogs+" ( notif_id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT, message_num TEXT )");
		db.execSQL("CREATE TABLE "+tblMessage+" ( message_id INTEGER PRIMARY KEY AUTOINCREMENT,message_desc TEXT, from_visitor_name TEXT, date_created TEXT, isRead TEXT, visit_status TEXT, visitor_logged_id TEXT, purpose TEXT, imgAvatar TEXT )");
		db.execSQL("CREATE TABLE "+tblCurrConn+" ( currConn_id INTEGER PRIMARY KEY AUTOINCREMENT,status TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+tblUser);
		db.execSQL("DROP TABLE IF EXISTS "+tblServer);
		db.execSQL("DROP TABLE IF EXISTS "+tblNotifLogs);
		db.execSQL("DROP TABLE IF EXISTS "+tblMessage);
		db.execSQL("DROP TABLE IF EXISTS "+tblCurrConn);
		onCreate(db);
	}
	
	public long InsertNewRecord( String tblname, ArrayList<String> fields, ArrayList<String> values ) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues db_values = new ContentValues();
		
		int countArr = values.size();
		
		for( int i = 0; i < countArr; i++  ) {
			
			db_values.put( fields.get(i), values.get(i) );
			
		}
		
		long status = db.insert(tblname, null, db_values);
		
		return status;
	}
	
	
	public Cursor getAllData(String tblname) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor result = db.rawQuery("SELECT * FROM "+tblname, null);
		
		return result;
	}
	
	public Cursor getAllDataWithDescOrder(String tblname,String field) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor result = db.rawQuery("SELECT * FROM "+tblname+" order by "+field+" DESC", null);
		
		return result;
	}
	
	public void DeleteAllByTable(String tblname) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(tblname , null, null);
	}
	
	
	public int DeleteRecord(String tblname, String strField, String strValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		int status = db.delete(tblname , " "+strField+" = ? ", new String[] { strValue });
		
		return status;
	}
	
	public int UpdateRecord(String tblname, ArrayList<String> Fields, ArrayList<String> Values) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues db_values = new ContentValues();
		
		int countMaxArr = Fields.size();
		
		String strFieldID = Fields.get(0);
		String strFieldValue = Values.get(0);
		
		
		for( int i = 0; i < countMaxArr; i++ ) {
			
			db_values.put(Fields.get(i), Values.get(i));
			
		}
		
		int status = db.update(tblname, db_values, strFieldID+" = ? ", new String[] { strFieldValue });
		
		return status;
	}
	
	public void setcurrID(int ID) {		
		currentID = ID;
	}		
	
	public int getcurrID() {
		return currentID;
	}
	
	
	public Cursor getRecordByID(String tablename, String Fieldname, String Fieldvalue) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor result = db.rawQuery("SELECT * FROM "+tablename+" WHERE "+Fieldname+" = "+Fieldvalue, null);
		
		return result;
		
	}
	
}