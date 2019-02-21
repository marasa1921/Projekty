// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package com.fiszki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "DBAdapter";

	// DB Fields
	public static final String KEY_ROWID = "_id";
	/*
	 * CHANGE 1:
	 */
	// TODO: Setup your fields here:
	public static final String KEY_LG1 = "LG1";
	public static final String KEY_LG2 = "LG2";
	public static final String KEY_OK = "OK";
	public static final String KEY_PACKAGE_NAME_LG1="PACKAGE_NAME_LG1";
	public static final String KEY_PACKAGE_NAME_LG2="PACKAGE_NAME_LG2";
	public static final String KEY_PACKAGE_OWN = "C";

	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	public static final String[] ALL_PACKAGES_KEYS = new String[] {KEY_ROWID, KEY_PACKAGE_NAME_LG1,KEY_PACKAGE_NAME_LG2,KEY_PACKAGE_OWN,};
	public static final String[] ALL_WORDS_KEYS = new String[] {KEY_ROWID, KEY_LG1, KEY_LG2, KEY_OK,};
	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "MyDb";
	public static final String DATABASE_TABLE_WORDS = "Tablewords";
	public static final String DATABASE_TABLE_PACKAGES = "Tablepackages";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 4;

	private static final String DATABASE_CREATE_SQL_TABLE_WORDS =
			"create table " + DATABASE_TABLE_WORDS
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_LG1 + " TEXT not null, "
			+ KEY_LG2 + " TEXT not null, "
			+ KEY_OK + " integer not null " // NO COMMA
			+ ");";


	private static final String DATABASE_CREATE_SQL_TABLE_PACKAGE =
			"create table " + DATABASE_TABLE_PACKAGES
					+ " (" + KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_PACKAGE_NAME_LG1 + " TEXT not null, "
					+ KEY_PACKAGE_NAME_LG2 + " TEXT not null, "
					+ KEY_PACKAGE_OWN + " integer not null "
					+ ");";

	// Context of application who uses us.
	private final Context context;
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////

	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	//////////////////////////////////////////////////////////////
	//Table Words												//
	// Add a new set of values to the database.					//
	//////////////////////////////////////////////////////////////
	public long insertTableWordstRow(String LG1, String LG2, int OK) {
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LG1, LG1);
		initialValues.put(KEY_LG2, LG2);
		initialValues.put(KEY_OK, OK);
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_WORDS, null, initialValues);
	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteTableWordsRow(long rowId) {
	//	Log.v("rowId","rowId = "+rowId);
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE_WORDS, where, null) != 0;
	}

	public void deleteTableWordsAll() {
		Cursor c = getTableWordsAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteTableWordsRow(c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// Return all data in the database.
	public Cursor getTableWordsAllRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_TABLE_WORDS, ALL_WORDS_KEYS,
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getTableWordsRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_TABLE_WORDS, ALL_WORDS_KEYS,
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Change an existing row to be equal to new data.
	public boolean updateTableWordsRow(long rowId, String LG1, String LG2, int OK) {
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_LG1, LG1);
		newValues.put(KEY_LG2, LG2);
		newValues.put(KEY_OK, OK);
		// Insert it into the database.
		return db.update(DATABASE_TABLE_WORDS, newValues, where, null) != 0;
	}

	public int getTableWordsLenght(){
		Cursor c = db.query(DATABASE_TABLE_WORDS,new String[]{"COUNT(_id) AS COUNT"},null,null,null,null,null,null);
		c.moveToFirst();
		int zlop = c.getInt(0);
		return zlop;
	}
	public int getTableWordsOKLenght(){
		Cursor c = db.query(DATABASE_TABLE_WORDS,new String[]{"COUNT(_id) AS COUNT"},KEY_OK+"= ?",new String[]{"1"},null,null,null,null);
		c.moveToFirst();
		int zlop = c.getInt(0);
		return zlop;
	}


	//////////////////////////////////////////////////////////////
	//Table Package												//
	// Add a new set of values to the database.					//
	//////////////////////////////////////////////////////////////
	public long insertTablePackagesRow(String LG1, String LG2,int OWN) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_PACKAGE_NAME_LG1, LG1);
		initialValues.put(KEY_PACKAGE_NAME_LG2, LG2);
		initialValues.put(KEY_PACKAGE_OWN, OWN);
		return db.insert(DATABASE_TABLE_PACKAGES, null, initialValues);
	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteTablePackagesRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE_PACKAGES, where, null) != 0;
	}

	// Return all data in the database.
	public Cursor getTablePackagesAllRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_TABLE_PACKAGES, ALL_PACKAGES_KEYS,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
						//				//				//				//				//				//
	public int getTablePackagesCheck(String mlg1, String mlg2){
		Cursor c = db.query(DATABASE_TABLE_PACKAGES,new String[]{"COUNT(_id) AS COUNT"},KEY_PACKAGE_NAME_LG1+"= ? AND "+KEY_PACKAGE_NAME_LG2+"= ?"  ,new String[]{mlg1,mlg2},null,null,null,null);
		c.moveToFirst();
		int zlop = c.getInt(0);
		return zlop;
	}

	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL_TABLE_WORDS);
			_db.execSQL(DATABASE_CREATE_SQL_TABLE_PACKAGE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {

			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_WORDS);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PACKAGES);
			// Recreate new database:
			onCreate(_db);
		}
	}
}
