package memory.example.com.memory;

import java.util.logging.Logger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class IntroInstructionsActivity extends Activity implements View.OnClickListener {

    private static final String className= MenuActivity.class.getSimpleName();
	TextView view;
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
    int tapCounter = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        Log.i(App.TAG, className + "---> " + "onCreate called");

        super.onCreate(bundle);
        // Bind to LocalService
        Intent intent = new Intent(this, ElephantService.class);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        Log.i(App.TAG, className + "---> " + "onStart called");


        iContext = IntroInstructionsActivity.this.getApplicationContext();
        setContentView(R.layout.simple_text);
        view=(TextView)findViewById(R.id.ourplaintextcaption);
        
        view.setText("You need information to create a memory. All of this information " +
                        "must be gathered successfully or your memory will not be saved.\n\nTap to continue");


        // To receive touch events from the Screen, the view should be focusable.
        view.setOnClickListener(this);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        super.onStart();

    }

    @Override
    protected void onPause() {
        Log.i(App.TAG, className + "---> " + "onPause called");

        super.onPause();
        if (isFinishing()) {
            Log.i("DeleteMemoriesActivity ", "isFinishing()");
            nullifyGlobalVariables();
        } else {
            tapCounter = 0;
            Log.i("DeleteMemoriesActivity ", "is pausing, but not finishing");
        }

        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onStop() {
        Log.i(App.TAG, className + "---> " + "onStop called");

        super.onStop();
        Log.i("IntroInstructionsActivity: onStop()", "ElephantService is bound? " + mBound);

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        Log.i(App.TAG, className + "---> " + "onResume called");
        // To receive touch events from the Screen, the view should have focus.
        view.requestFocus();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Log.i(App.TAG, className + "---> " + "onClick called");

        Log.i("IntroInstructionsActivity: onClick", " Screen was tapped.");
        if (tapCounter == 0) {
            tapCounter = 1;
            updateView();

        } else {
        	Intent intent2 = new Intent(this, SpeechRecognitionActivity.class);
            startActivity(intent2);
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        if(keycode ==KeyEvent.KEYCODE_BACK)
        return false;
        return super.onKeyDown(keycode, event);
    }

    private void updateView() {
        Log.i(App.TAG, className + "---> " + "updateView called");

    	view=(TextView)findViewById(R.id.ourplaintextcaption);
    			view.setText("To start, decide on a title for your memory. You will speak this title " +
                        "to app when you see a microphone.\n\n" +
                        "Tap when you are ready to speak your title.");


        // To receive touch events from the Screen, the view should be focusable.
        view.setOnClickListener(this);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        view.requestFocus();
        super.onResume();
    }

    private void nullifyGlobalVariables() {
        Log.i(App.TAG, className + "---> " + "nullifyGlobalVariables called");

        iContext = null;
        view.invalidate();
        view = null;
        mService = null;
    }
    @Override
    public void onBackPressed() { }

}
