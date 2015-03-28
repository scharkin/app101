package turned.your.webtoapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.webkit.WebView;

public class RoundballActivity extends ActionBarActivity {
	WebView view;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page);
		view = (WebView) findViewById(R.id.webView);
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setDomStorageEnabled(true);
		view.getSettings().setBuiltInZoomControls(true);
		view.loadUrl("file:///android_asset/roundball/roundball.html");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()) {
			view.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}
}
