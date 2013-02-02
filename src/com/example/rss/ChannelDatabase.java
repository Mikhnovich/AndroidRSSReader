package com.example.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//Класс-обертка над SQL, для удобства взаимодействия с таблицей каналов
public class ChannelDatabase {
  
  private static final String DB_NAME = "rssdatabase";
  private static final int DB_VERSION = 1;
  private static final String DB_TABLE = "channel_table";
  
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_URL = "url";
  
  public static final String DB_CREATE = 
    "create table " + DB_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, " +
      COLUMN_TITLE + " text, " +
      COLUMN_URL + " text" +
    ");";
  
  private final Context context;
  
  // У класса есть двойник: EntryDatabase.java
  
  private DBHelper mDBHelper;
  private SQLiteDatabase mDB;
  
  public ChannelDatabase(Context context) {
      this.context = context;
  }
  
  public void open() {
      mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
      mDB = mDBHelper.getWritableDatabase();
  }
  
  public void close() {
      if (mDBHelper!=null) mDBHelper.close();
  }
  

  public Cursor getAllData() {
      return mDB.query(DB_TABLE, null, null, null, null, null, null);
  }
  
  public void addChannel(String title, String url) {
      ContentValues cv = new ContentValues();
      cv.put(COLUMN_TITLE, title);
      cv.put(COLUMN_URL, url);
      mDB.insert(DB_TABLE, null, cv);
  }
  
  public void deleteChannel(long id) {
      mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
  }
  
}