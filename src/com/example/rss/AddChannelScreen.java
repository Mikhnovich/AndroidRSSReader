package com.example.rss;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddChannelScreen extends Activity implements OnClickListener {

	Button FormButton;
	EditText FormTitle;
	EditText FormURL;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_add);
        
        FormButton = (Button) findViewById(R.id.channelFormButton);
        FormTitle = (EditText) findViewById(R.id.channelFormTitle);
    	FormURL = (EditText) findViewById(R.id.channelFormURL);
        
        Log.d("ChannelAddActivity", "ChannelAddActivity Created");
        FormButton.setOnClickListener(this);
    }

	public void onClick(View v) {
		// У нас всего одна кнопка, которая может быть нажата - кнопка отправки
		Intent data = new Intent();
	    data.putExtra("title", FormTitle.getText().toString());
	    data.putExtra("url", FormURL.getText().toString());
	    
	    // Результат: успешен, вернуть такую-то информацию
	    setResult(RESULT_OK, data);
	    finish();
	}
}
