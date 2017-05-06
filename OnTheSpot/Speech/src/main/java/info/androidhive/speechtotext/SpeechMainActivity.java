package info.androidhive.speechtotext;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.sample.locationaddress.LocationMainActivity;

public class SpeechMainActivity extends Activity {

	private TextView txtSpeechInput;
    private TextView txtCaption;

    private ImageButton btnSpeak;
	private Button buttonSubmit;
    private String identifiedObject;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speech_activity_main);


		txtSpeechInput = (TextView) findViewById(R.id.multiAutoCompleteTextView);
        txtCaption = (TextView) findViewById(R.id.textView7);

        Intent in= getIntent();
        Bundle b = in.getExtras();
        if(b!=null)
        {
            String objectName =(String) b.get("identifiedObject");
            identifiedObject=objectName;
            objectName= txtCaption.getText().toString()+objectName;
            txtCaption.setText(objectName);
        }


		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
		// hide the action bar
		//this.getActionBar().hide();

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});
		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitComplaint();
			}
		});
	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void submitComplaint() {
		Intent intent = new Intent(getApplicationContext(),LocationMainActivity.class);
		intent.putExtra("description",txtSpeechInput.getText());
        intent.putExtra("identifiedObject",identifiedObject);

        try {
			startActivity(intent);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					"Problem",
					Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				txtSpeechInput.setText(result.get(0));
			}
			break;
		}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
