package com.example.rss;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ChannelsListScreen extends Activity implements OnItemClickListener {

	private static final int CM_DELETE_ID = 1;
	ListView channelList;
	ChannelDatabase db;

	SimpleCursorAdapter scAdapter;
	Cursor cursor;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_list);

		// Подключаемся (или создаем) базу данных для хранения RSS-каналов
		db = new ChannelDatabase(this);
		db.open();

		// Создаём курсор и загружаем в него содержимое БД
		cursor = db.getAllData();
		startManagingCursor(cursor);

		// Устанавливаем соответствие между столбцами таблицы БД и View (для адаптера)
		String[] from = new String[] { ChannelDatabase.COLUMN_TITLE, ChannelDatabase.COLUMN_URL };
		int[] to = new int[] { R.id.channelTitle, R.id.channelURL };

		// Соединяем курсор и ListView посредством адаптера
		scAdapter = new SimpleCursorAdapter(this, R.layout.channel_view_layout, cursor, from, to);
		channelList = (ListView) findViewById(R.id.channelList);
		channelList.setAdapter(scAdapter);

		// Для реализации контекстного меню (и удаления соответственно)
		registerForContextMenu(channelList);
		channelList.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Пользователь тыкнул по названию одного из каналов, нужно отобразить статьи на этом канале
		Log.d("channelList", "Item clicked: position = " + position + ", id = " + id);
		
		// Готовимся к переходу в другую активити
		String channelURL   = ( (TextView) (view.findViewById(R.id.channelURL)) ).getText().toString();
		String channelTitle = ( (TextView) (view.findViewById(R.id.channelTitle)) ).getText().toString();
		
		// RSSLoadTask в фоновом режиме загружает xml-файл канала, парсит его и добавляет информацию в базу данных 
		new RSSLoadTask(this, id).execute( channelURL );
		
		// Посредством intent переходим в другую активити
		Intent intent = new Intent(this, EntriesListScreen.class);
		intent.putExtra("cid", id);
		intent.putExtra("title", channelTitle );
		startActivity(intent);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		// Создаем один единственный пункт в меню - "удалить"
		menu.add( 0, CM_DELETE_ID, 0, getString( R.string.delete_label ) );
	}

	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		// Функция удаления каналов реализуется через контекстное меню
		
		// Нет необходимости в этой проверке - в меню всего один пункт
		if (item.getItemId() == CM_DELETE_ID) {
			// Получим ID (суть порядковый номер) выбранного канала
			AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
			
			// Удалим канал из таблицы каналов
			db.deleteChannel(acmi.id);
			
			// Удалим из таблицы статей все статьи этого канала
			EntryDatabase edb = new EntryDatabase(this);
			edb.open();
			edb.deleteChannel(acmi.id);
			edb.close();
			
			// Обновим информацию в курсоре
			cursor.requery();

			return true;
		}
		
		return super.onContextItemSelected(item);
	}

	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Создает красивую кнопку с плюсиком для добавления канала
		getMenuInflater().inflate(R.menu.activity_channel_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Нет необходимости в проверке, кнопка в меню всего одна. Но всё же.
		if (item.getItemId() == R.id.channelAddAction) {
			Intent intent = new Intent(this, AddChannelScreen.class);
			startActivityForResult(intent, 1);
			// После выхода из активити с запросом выполнение проолжится в onActivityResult()
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Через этот метод мы получаем ответ от активити где добавляется новый RSS канал
		// Если пользователь нажал кнопку "назад", то вернется null
		
		if (data != null) {
			String title = data.getStringExtra("title");
			String url = data.getStringExtra("url");
			
			db.addChannel(title, url);
			cursor.requery();
		}
	}
}