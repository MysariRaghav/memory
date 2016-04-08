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

public class DeleteMemoriesActivity extends Activity implements View.OnClickListener {


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
            Log.i(App.TAG, className + "---> " + "onServiceDisconnected() called");

            mBound = false;
        }
    };
    int mtapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG, className + "---> " + "onCreate called");
        Intent intent = new Intent(this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        iContext = DeleteMemoriesActivity.this.getApplicationContext();
        setContentView(R.layout.simple_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(App.TAG, className + "---> " + "onStart called");
        view=(TextView)findViewById(R.id.ourplaintextcaption);
        view.setText("Are you sure?\nTap to delete all memories. press back to cancel.");


        // To receive touch events, the view should be focusable.
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
        Log.i("DeleteMemoriesActivity:", "onStop()");
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    protected void onResume() {
        // To receive touch events from the Screen, the view should have focus.
        Log.i(App.TAG, className + "---> " + "onResume called");
    	view.requestFocus();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Log.i(App.TAG, className + "---> " + "onClick called");
        if (mtapCount == 0) {
            mtapCount++;
            //code for performing delete of file
            FileOperations fOps = new FileOperations(iContext);
            boolean result = fOps.deleteContentsOfFile();

            Log.i("DeleteMemoriesActivity:", "deleted file?  " + result);

            updateView(result);
        } else {
            Intent intent= new Intent(this, MenuActivity.class);
            startActivity(intent);
            Log.i("DeleteMemoryActivity: onClick", "Screen was tapped.");
            finish();
        }

    }

    private void updateView(boolean isDeleteSuccessful) {
        Log.i(App.TAG, className + "---> " + "updateView called");
        if (isDeleteSuccessful) {

            view.setText("All memories have been deleted\nTap to continue");
            view.setOnClickListener(this);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();

        } else {
            view.setText("Error - Memories were not deleted\nContact developer. Tap to continue");
            view.setOnClickListener(this);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }


    }

    private void nullifyGlobalVariables() {
        Log.i(App.TAG, className + "---> " + "nullifyGlobalVariables called");
    	iContext = null;
        view.invalidate();
        view = null;
        mService = null;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        if(keycode ==KeyEvent.KEYCODE_BACK)
            return false;
        return super.onKeyDown(keycode, event);
    }

    @Override
    public void onBackPressed() { }
}

