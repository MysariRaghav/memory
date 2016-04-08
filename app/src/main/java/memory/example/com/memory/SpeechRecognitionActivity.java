package memory.example.com.memory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import java.util.List;


public class SpeechRecognitionActivity extends Activity {
    private static final String className= MenuActivity.class.getSimpleName();


    private static final int RECOGNIZE_SPEECH_REQUEST = 1;

    Context iContext = null;
    ElephantService mService;
    boolean mBound = false;
    /*
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(App.TAG, className + "---> " + "onServiceConnected called");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ElephantService.LocalBinder binder = (ElephantService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(App.TAG, className + "---> " + "onServiceDisconnected called");
            mBound = false;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG, className + "---> " + "onCreate called");
    	Intent service = new Intent(this, ElephantService.class);
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, RECOGNIZE_SPEECH_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(App.TAG, className + "---> " + "onActivityResult called");
        if (requestCode == RECOGNIZE_SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            // Do something with spokenText.
            Log.i("SpeechRecognitionActivity: onActivityResult() ", "result is " + spokenText);
            FileOperations.setMemTitle(spokenText);

        } else if (requestCode == RECOGNIZE_SPEECH_REQUEST && resultCode == RESULT_CANCELED) {
            Log.e("PictureActivity: onActivityResult() ", "Spoken title was cancelled, so entire memory should be re-created.");
        }
        super.onActivityResult(requestCode, resultCode, data);

        finish();
    }
    @Override
    public void onBackPressed() { }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " +"onKeyDown called");
        return keycode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keycode, event);
    }
}

