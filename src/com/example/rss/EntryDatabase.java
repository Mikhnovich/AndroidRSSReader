package com.example.rss;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// Класс-обертка над SQL, для удобства взаимодействия с таблицей статей
public class EntryDatabase {
  
  private static final String DB_NAME = "rssdatabase";
  private static final int DB_VERSION = 1;
  private static final String DB_TABLE = "entry_table";
  
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_CID = "cid";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_URL = "url";
  public static final String COLUMN_DESC = "desc";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_DATESTR = "datestr";
  public static final String COLUMN_GUID = "guid";
  
  public static final String DB_CREATE = 
    "create table " + DB_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, " +
      COLUMN_CID + " integer, " +
      COLUMN_TITLE + " text, " +
      COLUMN_URL + " text, " +
      COLUMN_DESC + " text, " +
      COLUMN_DATE + " integer, " +
      COLUMN_DATESTR + " text, " +
      COLUMN_GUID + " text " +
    ");";
  
  private final Context context;
  
  private DBHelper mDBHelper;
  private SQLiteDatabase mDB;
  
  public EntryDatabase(Context context) {
    this.context = context;
  }
  
  public void open() {
    mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    mDB = mDBHelper.getWritableDatabase();
  }
  
  public void close() {
    if (mDBHelper != null) 
    	mDBHelper.close();
  }
  
  // Возвращает всю информацию в БД
  public Cursor getAllData() {
    return mDB.query(DB_TABLE, null, null, null, null, null, null);
  }
  
  //Возвращает все статьи, относящиеся к каналу с указанным channel id в виде курсора
  public Cursor getChannelData(long channel) {
	  return mDB.query(DB_TABLE, null, COLUMN_CID + " = " + channel, null, null, null, COLUMN_DATE + " DESC");
  }
  
  // Добавляет новую статью
  public void addEntry(long channel, String title, String url, String desc, Date date, String guid) {
	  mDB.delete(DB_TABLE, COLUMN_GUID + "=?", new String[] {guid});
      ContentValues cv = new ContentValues();
      cv.put(COLUMN_TITLE, title);
      cv.put(COLUMN_CID, channel);
      cv.put(COLUMN_URL, url);
      cv.put(COLUMN_DESC, desc);
      cv.put(COLUMN_DATE, date.getTime());
      cv.put(COLUMN_DATESTR, (new SimpleDateFormat()).format(date).toString());
      cv.put(COLUMN_GUID, guid);
      mDB.insert(DB_TABLE, null, cv);
  }
  
  // Удаляет все статьи канала с указанным channel id
  public void deleteChannel(long cid) {
      mDB.delete(DB_TABLE, COLUMN_CID + " = " + cid, null);
  }
}