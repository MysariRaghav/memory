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


public class FileOperationsWriteActivity extends Activity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(App.TAG, className + "---> " + "onCreate called");
        super.onCreate(savedInstanceState);

        iContext = FileOperationsWriteActivity.this.getApplicationContext();
        Intent intent = new Intent(this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Log.i("FileOperationsActivity", "MemDateAndTime contains " + FileOperations.getMemDateAndTime());
        Log.i("FileOperationsActivity", "MemTitle contains " + FileOperations.getMemTitle());
        Log.i("FileOperationsActivity", "MemPicture contains " + FileOperations.getMemPicture());
        Log.i("FileOperationsActivity", "MemVideo contains " + FileOperations.getMemVideo());

        setContentView(R.layout.simple_text);
        FileOperations fOps = new FileOperations(iContext);
        boolean result = fOps.writeToFile();

        updateView(result);

        Log.i("FileOperationsActivity", "wrote to file?  " + result);

    }

    @Override
    protected void onPause() {
        Log.i(App.TAG, className + "---> " + "onPause called");

        super.onPause();
        if (isFinishing()) {
            Log.i("FileOperationsWriteActivity ", "isFinishing()");
            nullifyGlobalVariables();
        } else {
            Log.i("FileOperationsWriteActivity ", "is pausing, but not finishing");
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

        Log.i(App.TAG, className + "---> " + "onResume called");

        // To receive touch events from the Screen, the view should have focus.
        view.requestFocus();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Log.i(App.TAG, className + "---> " + "onClick called");
    	Intent intent= new Intent(this, MenuActivity.class);
        startActivity(intent);
        Log.i("FileOperationsActivity: onClick", " Screen was tapped.");
        finish();
    }


    private void nullifyGlobalVariables() {
        Log.i(App.TAG, className + "---> " + "nullifyGlobalVariables called");

        iContext = null;
        view = null;
        mService = null;
    }

    private void updateView(boolean isWriteSuccessful) {
        Log.i(App.TAG, className + "---> " + "updateView called");


        if (isWriteSuccessful) {
        	view=(TextView)findViewById(R.id.ourplaintextcaption);

            view.setText("Memory captured\nTab To Continue");
            view.setOnClickListener(this);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            
        } else {
            view=(TextView)findViewById(R.id.ourplaintextcaption);
            view.setText("Warning: Your memory was not captured. "
                            + "Title, picture and video must be accepted for memory to be saved.\n\n" +
                            "Please create memory again. Tap to continue.");

            view.setOnClickListener(this);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }


    }
    @Override
    public void onBackPressed() { }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        if(keycode ==KeyEvent.KEYCODE_BACK)
            return false;
        return super.onKeyDown(keycode, event);
    }
}

