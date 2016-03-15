package it.unibg.p3d4amb.carracingcarboard.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import it.unibg.p3d4amb.carracingcarboard.Exception.MyException;
import it.unibg.p3d4amb.carracingcarboard.Manager.Enum.Eye;
import it.unibg.p3d4amb.carracingcarboard.R;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class SettingsActivity extends ActionBarActivity implements RecognitionListener {


    private String id_user;
    private static final String DIGITS_SEARCH = "digits";
    private Eye eye= Eye.LEFT_EYE;
    private Eye lazyEye;
    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    private TextView textEyeLeft,textEyeRight,textSelectLeft,textSelectRight,textStartLeft,textStartRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        View decorView = getWindow().getDecorView();
        captions = new HashMap<String, Integer>();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Typeface font = Typeface.createFromAsset(getAssets(), "orange juice 2.0.ttf");

        Intent intent=getIntent();
        Bundle data = getIntent().getExtras();


        id_user=(String) intent.getSerializableExtra("id_user");



        textEyeLeft=(TextView) findViewById(R.id.textViewEye1);
        textEyeLeft.setText("Left");
        textEyeLeft.setTypeface(font);

        textEyeRight=(TextView) findViewById(R.id.textViewEye2);
        textEyeRight.setText("Left");
        textEyeRight.setTypeface(font);

        textStartLeft=(TextView) findViewById(R.id.textSettingsStart1);
        textStartLeft.setText("SAY OKAY TO BEGIN");
        textStartLeft.setTextSize(16);
        textStartLeft.setTypeface(font);

        textStartRight=(TextView) findViewById(R.id.textSettingsStart2);
        textStartRight.setText("SAY OKAY TO BEGIN");
        textStartRight.setTextSize(16);
        textStartRight.setTypeface(font);

        textSelectLeft=(TextView) findViewById(R.id.textViewSelectEyeLeft);
        textSelectLeft.setText("SELECT THE LAZY EYE");
        textSelectLeft.setTypeface(font);

        textSelectRight=(TextView) findViewById(R.id.textViewSelectEyeRight);
        textSelectRight.setText("SELECT THE LAZY EYE");
        textSelectRight.setTypeface(font);


        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(SettingsActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {

                switchSearch(DIGITS_SEARCH);
            }
        }.execute();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            if (text.contains("left")) {
                textEyeLeft.setText("Left");
                textEyeRight.setText("Left");
                lazyEye = Eye.LEFT_EYE;
            }
            else if (text.contains("right")) {
                textEyeLeft.setText("Right");
                textEyeRight.setText("Right");
                lazyEye = Eye.RIGHT_EYE;
            }
            else if (text.contains("okay"))
                goToMain();
        }

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        switchSearch(DIGITS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName, 15000);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                        //.setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                        //  .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                        .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create grammar-based search for digit recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);

    }

    @Override
    public void onError(Exception error) {
    }

    @Override
    public void onTimeout() {
        switchSearch(DIGITS_SEARCH);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void goToMain(){

        Intent startGame = new Intent(SettingsActivity.this, MainActivity.class);
        if(eye.equals(Eye.LEFT_EYE)){
            lazyEye=Eye.RIGHT_EYE;
        }else{
            lazyEye=Eye.LEFT_EYE;
        }
        startGame.putExtra("eye", lazyEye);

        startGame.putExtra("id_user",id_user);
        startActivity(startGame);
        finish();
    }


    @Override
    protected void onPause() {

        super.onPause();
        finish();
    }

    private void changeEye(){
        if(textEyeLeft.getText().equals("Left")){
            textEyeLeft.setText("Right");
            textEyeRight.setText("Right");
            eye=Eye.RIGHT_EYE;
        }else if(textEyeLeft.getText().equals("Right")){
            textEyeLeft.setText("Left");
            textEyeRight.setText("Left");
            eye=Eye.LEFT_EYE;
        }else{
            try {
                throw new MyException("No eye");
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
    }
}
