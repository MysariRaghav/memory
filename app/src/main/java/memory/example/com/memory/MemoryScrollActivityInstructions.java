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


public class MemoryScrollActivityInstructions extends Activity implements View.OnClickListener {


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
        super.onCreate(bundle);
        Log.i(App.TAG, className + "---> " + "onCreate called");

        // Bind to LocalService
        Intent intent = new Intent(this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.simple_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(App.TAG, className + "---> " + "onStart called");

        iContext = MemoryScrollActivityInstructions.this.getApplicationContext();
        view=(TextView)findViewById(R.id.ourplaintextcaption);
        view.setText("Your memories will be displayed in chronological order starting with " +
                        "your oldest memory." +
                        "\n\n\nTap to continue");


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
            Log.i("MemoryScrollActivityInstructions ", "isFinishing()");
            nullifyGlobalVariables();
        } else {
            tapCounter = 0;
        }

        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(App.TAG, className + "---> " + "onStop called");

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        // To receive touch events from the Screen, the view should have focus.
        view.requestFocus();
        super.onResume();
        Log.i(App.TAG, className + "---> " + "requestFocus called");

    }

    @Override
    public void onClick(View v) {
        Log.i(App.TAG, className + "---> " + "onClick called");

        Log.i("IntroInstructionsActivity: onClick", " Glass was tapped.");
        if (tapCounter == 0) {
            updateView(tapCounter);
            tapCounter = 1;
        } else {
            finish();
        }

    }

    private void updateView(int numberOfTaps) {

        Log.i(App.TAG, className + "---> " + "updateView called");
view=(TextView)findViewById(R.id.ourplaintextcaption);
    	if (numberOfTaps == 0) {
            view.setText("Swipe Screen Up and Down to move through your memories. " +
                            "Tap card to view a memory's movie. Swipe down to stop browsing memories.\n\n" +
                            "Tap to continue");
        }

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
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        return keycode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keycode, event);
    }
}

