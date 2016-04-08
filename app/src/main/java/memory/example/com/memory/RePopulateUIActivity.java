package memory.example.com.memory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;


public class RePopulateUIActivity extends Activity {
    private static final String className= MenuActivity.class.getSimpleName();

    Context iContext = null;
    View view = null;

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(App.TAG, className + "---> " + "onStart called");

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RePopulateUIActivity: onStop()", "ElephantService is bound? "+ mBound);

        Log.i(App.TAG, className + "---> " + "onStop called");

        Intent intent= new Intent(this, MenuActivity.class);
        startActivity(intent);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        nullifyGlobalVariables();
        finish();
    }

    private void nullifyGlobalVariables() {
        Log.i(App.TAG, className + "---> " + "nullifyGlobalVariables called");

    	iContext = null;
        view = null;
        mService = null;
    }

}




