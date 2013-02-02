package com.example.rss;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

//Аналог ChannelsListScreen, но является списком тем (записей - entry) RSS канала
public class EntriesListScreen extends Activity implements OnItemClickListener {
	long cid;
	EntryDatabase edb;
	Cursor cursor;
	ListView entryList;
	SimpleCursorAdapter scAdapter;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);
        
        Intent creator = getIntent();
        cid = creator.getLongExtra("cid", 0);
        setTitle(creator.getStringExtra("title"));
        
        edb = new EntryDatabase(this);
		edb.open();
		
		entryList = (ListView) findViewById(R.id.entryList);
		
		String[] from = new String[] { EntryDatabase.COLUMN_TITLE, EntryDatabase.COLUMN_DATESTR, EntryDatabase.COLUMN_DESC, EntryDatabase.COLUMN_URL };
		int[] to = new int[] { R.id.entryTitle, R.id.entryDate, R.id.entryDesc, R.id.entryURL };
		
		cursor = edb.getChannelData(cid);
		startManagingCursor(cursor);
		
		scAdapter = new SimpleCursorAdapter(this, R.layout.article_view_layout, cursor, from, to);
        
		entryList.setAdapter(scAdapter);
		entryList.setOnItemClickListener(this);
    }
    
    protected void onDestroy() {
		super.onDestroy();
		
		edb.close();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Пользователь тыкнул на статью
		
		TextView entryURL = (TextView) (view.findViewById(R.id.entryURL));
		String URL = entryURL.getText().toString();
		
		Uri uri = Uri.parse( URL );
		
		// Запускаем активити-системный браузер для просмотра полной версии статьи
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	    startActivity(intent);
	}
}
