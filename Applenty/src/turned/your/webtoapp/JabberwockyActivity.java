package turned.your.webtoapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class JabberwockyActivity extends ActionBarActivity {
	WebView jabView;
	MediaPlayer jabSound;

	/**
	 * Used picture: Jabberwocky, Nick Cave and The Golden Age Sandman
	 * http://1.bp
	 * .blogspot.com/-zCtVipp7Wx4/TrfNQ07IkJI/AAAAAAAAA_k/LFq8WJkIoDo/
	 * s1600/Jabberwock.jpg from Boatwright Artwork: November 2011
	 * boatwrightartwork.blogspot.com
	 * http://boatwrightartwork.blogspot.com/2011_11_01_archive.html
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jabberwocky);
		// find jabberwocky view
		jabView = (WebView) findViewById(R.id.jabberView);
		// load poem
		jabView.loadUrl("file:///android_asset/jabberwocky.html");
		jabView.getSettings().setBuiltInZoomControls(true);
		// find right button
		View pictureButton = findViewById(R.id.pictureButton);
		pictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button button = (Button) v;
				if ("Picture".equals(button.getText())) {
					jabView.loadUrl("file:///android_asset/jabberwocky_picture.jpg");
					button.setText("Poem");
				} else {
					jabView.loadUrl("file:///android_asset/jabberwocky.html");
					button.setText("Picture");
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && jabView.canGoBack()) {
			jabView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Show Wikipedia page about the Jabberwocky poem by Lewis Carroll
	 * 
	 * @param v
	 *            not used
	 */
	public void showWiki(View v) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("https://en.wikipedia.org/wiki/Jabberwocky"));
		startActivity(i);
	}

	/**
	 * Used 30 second sound sample: sample_3ds_poetry_jabberwocky.mp3 file from
	 * http://www.phoenixrecords.org/albums/poetry/jabberwocky.php
	 */
	@Override
	protected void onResume() {
		jabSound = MediaPlayer.create(this, R.raw.sample_jabberwocky);
		jabSound.start();
		jabSound.setLooping(true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		jabSound.stop();
		jabSound.release();
		super.onPause();
	}
}
