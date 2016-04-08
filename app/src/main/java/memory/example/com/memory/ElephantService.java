package memory.example.com.memory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ElephantService extends Service {
	
    private static final String className= MenuActivity.class.getSimpleName();

    private static final String LIVE_CARD_TAG = "elephant";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(App.TAG, className + "---> " + "onBind called");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(App.TAG, className + "---> " + "onStartCammand called");
        setTitleViewInitialOnly();
        // Return START_NOT_STICKY to prevent the system from restarting the service if it is killed
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(App.TAG, className + "---> " + "onDestroy called");
        super.onDestroy();
    }

    private void setTitleViewInitialOnly() {
        Log.i(App.TAG, className + "---> " + "setTitleViewInitialOnly called");

    	Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
        menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	startActivity(menuIntent);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ElephantService getService() {
            Log.i(App.TAG, className + "---> " + "getService called");
            // Return this instance of LocalService so clients can call public methods
            return ElephantService.this;
        }
    }

}

