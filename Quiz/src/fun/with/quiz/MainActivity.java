package fun.with.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private Map<String, List<String>> questionMap = new HashMap<String, List<String>>();
	private int lastChoice = -1;
	{
		populate("To change your app's minimum sdk version you need to edit (select the correct file):",
				"AndroidManifest.xml", "strings.xml", "project.properties", "layout.xml",
				"MainActivity.java");
		populate("On modern phones (4.2 and higher) the developer settings can be made visible by going to Settings-About this phone and",
				"tapping the build number 7 times",
				"whispering \"I can see the matrix\" quietly to your phone",
				"typing \"Beetlejuice\" 3 times");
		populate("Project files added or changed outside of Eclipse may be ignored until you:",
				"Right-click/Control-click in the Package Explorer on the Project name and select \"Refresh\"",
				"Gently turn your laptop upside down twice",
				"Use the Reset perspective option in the Windows menu");
		populate("In Java, to tell the compiler you are a writing a hexadecimal (base 16) number instead of a base-10 number, you first write:",
				"0x For example 0x1ff",
				"(hex) For example (hex)123", "Nothing - hexadecimal is the default base",
				"hx For example hx1ff", "#$ For example #$1ff");
		populate("Each java statement is usually finished with a:",
				"semicolon ;", "asterisk *", "exclamation !", "colon :");
		populate("The maximum amount of available memory for an app on some devices is:",
				"16 MB", "128 MB", "512 MB");
		populate("To display an image in your app's screen you can add an ImageView to:",
				"The layout xml file", "The Android manifest file", "The string xml file");
		populate("What is the difference between \"@+id/paper\" and \"@id/paper\"?",
				"The plus symbol creates a new resource identifier, without the plus symbol you are referring to an existing identifier.",
				"Nothing, they are both the same.",
				"The first version is used to debug layouts.");
		populate("The ILLIAC used what kind of switches to perform computations?",
				"Vacuum tubes or \"Valves\"", "Solid-state capacitors", "Rotating wheels", "Transistors");
		populate("Which of the following refers to an existing id named 'banana' ?",
				"@id/banana", "banana", "#banana", "id/banana", "id.banana", "@id\\banana");
		populate("Which one of the following correctly sets the text font size using Android best practices?",
				"android:textSize=\"16sp\"", "android:textSize=\"16dp\"",
				"android:textSize=\"16\"", "android:textSize=\"16px\"");
	}

	private void populate(String question, String... answers) {
		List<String> list = new ArrayList<String>();
		for (String s : answers) {
			list.add(s);
		}
		questionMap.put(question, list);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		Set<String> keySet = questionMap.keySet();
		String[] keys = keySet.toArray(new String[0]);
		int len = keys.length;
		int randomNum = new Random().nextInt(len);
		while (randomNum == lastChoice) {
			randomNum = new Random().nextInt(len);
		}
		lastChoice = randomNum;
		String keyQuestion = keys[randomNum];
		List<String> answersList = questionMap.get(keyQuestion);
		String[] answers = answersList.toArray(new String[0]);
		final String keyAnswer = answers[0];
		Collections.shuffle(answersList);
		String[] items = answersList.toArray(new String[0]);
		TextView textView = (TextView) findViewById(R.id.question);
		textView.setText(keyQuestion);

		ListView listView = (ListView) findViewById(R.id.answers);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CharSequence answer = ((TextView) view).getText();
				boolean isCorrect = keyAnswer.equals(answer);
				String show = isCorrect ? "Correct!" : "Wrong";
				Toast.makeText(getApplicationContext(), show, Toast.LENGTH_LONG).show();				
				Class<?> activityClass = isCorrect ? CorrectAnswerActivity.class
						: WrongAnswerActivity.class;
				Intent intent = new Intent(MainActivity.this, activityClass);
				startActivity(intent);
			}
		});
		super.onResume();
	}
}
