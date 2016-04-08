package memory.example.com.memory;

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


public class VideoInstructionsActivity extends Activity implements View.OnClickListener {

    private static final String className= MenuActivity.class.getSimpleName();

    Context iContext = null;
    TextView view = null;

    ElephantService mService;
    boolean mBound = false;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(App.TAG, className + "---> " + "onServiceConnected called");

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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // Bind to LocalService
        Log.i(App.TAG, className + "---> " + "onCreate called");

        Intent intent = new Intent(this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.simple_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(App.TAG, className + "---> " + "onStart called");

        iContext = VideoInstructionsActivity.this.getApplicationContext();
        view=(TextView)findViewById(R.id.ourplaintextcaption);
       view.setText("Lastly, you will take a video for this memory. Use this video to interact " +
                        "with your subject in ways that are meaningful to you.\n\n" +
                        "Tap when you are ready to record.");

        // To receive touch events from the touchpad, the view should be focusable.
        view.setOnClickListener(this);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(App.TAG, className + "---> " + "onPause called");

        if (isFinishing()) {
            Log.i("DeleteMemoriesActivity ", "isFinishing()");
            nullifyGlobalVariables();
        } else {
            Log.i("DeleteMemoriesActivity ", "is pausing, but not finishing");
        }

        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(App.TAG, className + "---> " + "onStop called");

        Log.i("IntroInstructionsActivity: onStop()", "ElephantService is bound? " + mBound);

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    protected void onResume() {
        // To receive touch events from the touchpad, the view should have focus.
        view.requestFocus();
        super.onResume();
        Log.i(App.TAG, className + "---> " + "onResume called");

    }

    @Override
    public void onClick(View v) {
        // perform desired action
        Log.i(App.TAG, className + "---> " + "onClick called");

    	Log.i("IntroInstructionsActivity: onClick", " Glass was tapped.");
    	Intent intent3 = new Intent(this, VideoActivity.class);
    	startActivity(intent3);
        finish();

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

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        return keycode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keycode, event);
    }
}
