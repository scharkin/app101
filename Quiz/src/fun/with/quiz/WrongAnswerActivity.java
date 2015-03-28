package fun.with.quiz;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class WrongAnswerActivity extends ActionBarActivity {
	MediaPlayer sound;

	/**
	 * Used l-Oh-noes.jpeg image
	 * from forums.wildstar-online.com
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_correct_answer);
		// find view
		WebView webView = (WebView) findViewById(R.id.jabberView);
		// load poem
		webView.loadUrl("file:///android_asset/ohnoes_cat.jpg");
		webView.getSettings().setBuiltInZoomControls(true);
		// find right button
		View okButton = findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.correct_answer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		sound = MediaPlayer.create(this, R.raw.aaaahhhh);
		sound.start();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		sound.stop();
		sound.release();
		super.onPause();
	}
}
