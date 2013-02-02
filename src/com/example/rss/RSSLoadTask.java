package com.example.rss;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

// Этот класс занимается 
public class RSSLoadTask extends AsyncTask<String, Void, Void> {

	Context context;
	long channel;
	EntryDatabase edb;

	public RSSLoadTask(Context context, long channel) {
		this.context = context;
		this.channel = channel;
	}

	@Override
	protected Void doInBackground(String... feed) {
		EntryDatabase edb = new EntryDatabase(context);
		edb.open();
		try {
			// ��� ������ - ������ GET-������ �� RSS-�� �� ������� ������
			URL url = new URL(feed[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				// DocumentBuilderFactory, DocumentBuilder are used for
				// xml parsing
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// using db (Document Builder) parse xml data and assign
				// it to Element
				Document document = db.parse(is);
				Element element = document.getDocumentElement();

				// take rss nodes to NodeList
				NodeList nodeList = element.getElementsByTagName("item");

				if (nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {

						// take each entry (corresponds to <item></item> tags in
						// xml data

						Element entry = (Element) nodeList.item(i);

						Element _titleE = (Element) entry.getElementsByTagName(
								"title").item(0);
						Element _descriptionE = (Element) entry
								.getElementsByTagName("description").item(0);
						Element _pubDateE = (Element) entry
								.getElementsByTagName("pubDate").item(0);
						Element _linkE = (Element) entry.getElementsByTagName(
								"link").item(0);
						Element _guidE = (Element) entry.getElementsByTagName(
								"guid").item(0);

						String _title = _titleE.getFirstChild().getNodeValue();
						String _description = _descriptionE.getFirstChild()
								.getNodeValue();
						SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
						Date _pubDate = new Date();
						try {_pubDate =  df.parse(_pubDateE.getFirstChild().getNodeValue());}
						catch (ParseException e) {
							e.printStackTrace();
							Log.w("RSS", "Date parsing failed: " + _pubDateE.getFirstChild().getNodeValue());
							}
						
						String _link = _linkE.getFirstChild().getNodeValue();
						String _guid = _guidE.getFirstChild().getNodeValue();
						// ������� ������ � ����
						edb.addEntry(channel, _title, _link, _description, _pubDate, _guid);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
