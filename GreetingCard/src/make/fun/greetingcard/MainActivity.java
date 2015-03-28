package make.fun.greetingcard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int REQUEST_CODE = 1;

	private EditText mSendTo; // holds Person name or phone number to send greetings to
	private CheckBox mSmsOnly; // SMS check box - we send SMS when it is checked
	private EditText mGreetings; // Greetings text to send
	private EditText mSignature; // sender name
	private Bitmap mBitmap; // card picture holder

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create text watcher we use to hide and show open gallery and share buttons
		TextWatcher tw = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			} // NO-OP unused method

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			} // NO-OP unused method

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean validToGreeting = isNotEmpty(mSendTo) && isNotEmpty(mGreetings);
				boolean validSignature = isNotEmpty(mSignature);
				boolean showGalleryButton = validToGreeting && validSignature;
				boolean showShareButton = validToGreeting && (mSmsOnly.isChecked() || validSignature && mBitmap != null);
				View galleryButton = findViewById(R.id.galleryButton);
				View shareButton = findViewById(R.id.shareButton);
				boolean isGalleryButtonVisible = galleryButton.getVisibility() == View.VISIBLE;
				boolean isShareButtonVisible = shareButton.getVisibility() == View.VISIBLE;
				if (isGalleryButtonVisible != showGalleryButton) {
					animateView(galleryButton, showGalleryButton);
				}
				if (isShareButtonVisible != showShareButton) {
					animateView(shareButton, showShareButton);
				}
			}

			void animateView(View view, boolean hasShow) {
				Animation anim;
				if (hasShow) {
					view.setVisibility(View.VISIBLE);
					anim = AnimationUtils.makeInAnimation(MainActivity.this, true);
				} else {
					anim = AnimationUtils.makeOutAnimation(MainActivity.this, true);
					view.setVisibility(View.INVISIBLE);
				}
				view.startAnimation(anim);
			}
		};

		// set views after we've created them
		mSendTo = (EditText) findViewById(R.id.to_whom);
		mSendTo.addTextChangedListener(tw);

		mSmsOnly = (CheckBox) findViewById(R.id.checkboxSmsOnly);

		mGreetings = (EditText) findViewById(R.id.greetings_text);
		mGreetings.addTextChangedListener(tw);

		mSignature = (EditText) findViewById(R.id.signature);
		mSignature.addTextChangedListener(tw);
	}

	private static boolean isNotEmpty(EditText field) {
		return field != null && field.getText().toString().length() > 0;
	}

	// check box handler
	public void processSMSOnly(View v) {
		boolean isSms = v instanceof CheckBox && ((CheckBox) v).isChecked();
		if (isSms) {
			findViewById(R.id.galleryButton).setVisibility(View.INVISIBLE);
			mSignature.setVisibility(View.INVISIBLE);
			mSendTo.setHint(R.string.phone_number);
		} else {
			mSignature.setVisibility(View.VISIBLE);
			mSendTo.setHint(R.string.to_person_name);
			if (isNotEmpty(mSendTo) && isNotEmpty(mGreetings) && isNotEmpty(mSignature)) {
				findViewById(R.id.galleryButton).setVisibility(View.VISIBLE);
			}
		}
		if (isNotEmpty(mSendTo) && isNotEmpty(mGreetings) && (isSms || isNotEmpty(mSignature) && mBitmap != null)) {
			findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
		}
	}

	// share button handler - we share Greeting card by default and send SMS when check box is checked
	public void processForm(View duck) {
		if (mSmsOnly.isChecked()) {
			sendSMS();
		} else {
			saveAndShare(duck);
		}
	}

	public void sendSMS() {
		String phone = mSendTo.getText().toString();

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.fromParts("sms", phone, null));
		intent.putExtra("sms_body", mGreetings.getText().toString());

		try { // startActivity can throw ActivityNotFoundException
			startActivity(intent); // So to be robust our app catch the exception
		} catch (Exception ex) {
			Log.e(TAG, "Could not send message", ex); // print the exception in the log
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.d(TAG, uri.toString());
			Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
			try {
				InputStream stream = getContentResolver().openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(stream, null, options);
				stream.close();

				int bitmapWidth = options.outWidth;
				int bitmapHeight = options.outHeight;
				Log.d(TAG, "Bitmap raw size:" + bitmapWidth + " x " + bitmapHeight);

				options.inJustDecodeBounds = false;
				options.inSampleSize = calculateSampleSize(bitmapWidth, bitmapHeight);
				stream = getContentResolver().openInputStream(uri);
				Bitmap bm = BitmapFactory.decodeStream(stream, null, options);
				stream.close();
				if (mBitmap != null) {
					mBitmap.recycle();
				}

				makeMutableBitmap(bm, bitmapWidth, bitmapHeight);
				ImageView v = (ImageView) findViewById(R.id.imageView1);
				v.setImageBitmap(mBitmap);
				findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Log.e(TAG, "Decoding Bitmap", e);
			}
		}
	}

	private void makeMutableBitmap(Bitmap fromBitmap, int bitmapWidth, int bitmapHeight) {
		mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(mBitmap);
		c.drawBitmap(fromBitmap, 0, 0, null);
		TextPaint tp = createTextPaint(bitmapHeight / 16); // use font size 1/16 of image height
		drawGreetinsText(c, tp, bitmapWidth, bitmapHeight);
		fromBitmap.recycle();
	}

	private int calculateSampleSize(int bitmapWidth, int bitmapHeight) {
		int displayW = getResources().getDisplayMetrics().widthPixels;
		int displayH = getResources().getDisplayMetrics().heightPixels;
		int sample = 1;
		while (bitmapWidth > displayW * sample || bitmapHeight > displayH * sample) {
			sample = sample * 2;
		}
		Log.d(TAG, "Sampling at " + sample);
		return sample;
	}

	private TextPaint createTextPaint(float textSize) {
		TextPaint tp = new TextPaint();
		tp.setTextSize(textSize);
		tp.setTextSkewX((float) -0.25); // skew text a little bit for better look
		tp.setColor(0xC0C00000); // transparent red
		tp.setAntiAlias(true); // make text look nicer without rough edges
		return tp;
	}

	private List<String> splitIntoLines(String text, float maximumWidth, TextPaint tp) {
		List<String> lines = new ArrayList<String>();
		while (text.length() > 0) { // split text string into lines shorter than maximumWidth
			int newLength = tp.breakText(text, true, maximumWidth, null);
			lines.add(text.substring(0, newLength));
			text = text.substring(newLength);
		}
		return lines;
	}

	private void drawGreetinsText(Canvas c, TextPaint tp, int bitmapWidth, int bitmapHeight) {
		Rect bounds = new Rect();
		float leftMargin = bitmapWidth / 16;
		String head = mSendTo.getText().toString(); // header text
		tp.getTextBounds(head, 0, head.length(), bounds); // measure header bounds
		c.drawText(head, bitmapWidth / 3, bitmapHeight / 3, tp); // draw header
		float y = bitmapHeight / 3; // top margin
		float yOffset = bounds.height() * 2; // keep vertical space between lines
		for (String line : splitIntoLines(mGreetings.getText().toString(), 3 * bitmapWidth / 4, tp)) { // 75% max width
			c.drawText(line, leftMargin, y + yOffset, tp); // draw body line
			tp.getTextBounds(line, 0, line.length(), bounds); // measure line bounds
			yOffset += bounds.height(); // make vertical space for the next line
		}
		yOffset += bounds.height(); // make vertical space for the footer
		c.drawText(mSignature.getText().toString(), bitmapWidth / 4, y + yOffset, tp); // draw footer
	}

	public void openGallery(View v) { // Intent magic to open the Gallery
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select..."), REQUEST_CODE);
	}

	@SuppressLint("SimpleDateFormat")
	public void saveAndShare(View v) {
		if (mBitmap != null) {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			Log.d(TAG, "saveAndShare path = " + path);
			path.mkdirs();

			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String filename = "Imagen_" + timestamp + ".jpg";
			File file = new File(path, filename);
			FileOutputStream stream;
			try { // This can fail if the external storage is mounted via USB
				stream = new FileOutputStream(file);
				mBitmap.compress(CompressFormat.JPEG, 100, stream);
				stream.close();
			} catch (Exception e) {
				Log.e(TAG, "saveAndShare (compressing):", e);
				return; // Do not continue
			}

			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(uri);
			sendBroadcast(intent); // Tell Android that a new public picture exists

			Intent share = new Intent(Intent.ACTION_SEND); // send picture file
			share.setType("image/jpeg");
			share.putExtra(Intent.EXTRA_STREAM, uri);
			share.putExtra(Intent.EXTRA_TEXT, mGreetings.getText().toString());
			share.putExtra(Intent.EXTRA_SUBJECT, mSendTo.getText().toString());
			startActivity(Intent.createChooser(share, "Share using..."));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	} // unused method

}
