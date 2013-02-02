package com.example.rss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

// SQLiteOpenHelper - абстрактный класс. Приходится на его основе создавайть свой.
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    // Наш класс при подготовке БД создает в ней необходимые таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(ChannelDatabase.DB_CREATE);
      db.execSQL(EntryDatabase.DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
  }
