package memory.example.com.memory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Activity showing the Elephant options menu.
 */
public class MenuActivity extends Activity {
	
    private static final String className= MenuActivity.class.getSimpleName();

    private final Handler mHandler = new Handler();
    ElephantService mService;
    boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(App.TAG, className + "---> " + "onAttachedToWindow called");

        Intent intent = new Intent(MenuActivity.this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        openOptionsMenu();
    }

    @Override
    protected void onStart() {
        Log.i(App.TAG, className + "---> " + "onStart called");

        super.onStart();
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        Log.i(App.TAG, className + "---> " + "onCreate called");
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.simple_text);

    	setContentView(R.layout.simple_text);
    	((TextView)findViewById(R.id.ourplaintext)).setText("Elephant");
    	((TextView)findViewById(R.id.ourplaintextcaption)).setText("Never Forget");
		super.onCreate(savedInstanceState);
	}

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(App.TAG, className + "---> " + "onCreateOptionsMenu called");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stopwatch, menu);

        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(App.TAG, className + "---> " + "onOptionsItemSelected called");

        switch (item.getItemId()) {
            case R.id.memory:
                post(new Runnable() {

                         @Override
                         public void run() {

                             Log.i("MenuActivity: ", "Started memory menu selection.");

                             FileOperations.clearMemoryArtifacts();

                             Intent intent8 = new Intent(MenuActivity.this, VideoInstructionsActivity.class);
                             startActivity(intent8);

                             Intent intent7 = new Intent(MenuActivity.this, PictureInstructionsActivity.class);
                             startActivity(intent7);

                             Intent intent5 = new Intent(MenuActivity.this, IntroInstructionsActivity.class);
                             startActivity(intent5);
                            

                             DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                             Date date = new Date();
                             String dateFormatted = dateFormat.format(date);
                             Log.i("MenuActivity: ", "date formatted is " + dateFormatted);
                             FileOperations.setMemDateAndTime(dateFormatted);

                         }
                     }

                );
                return true;

            case R.id.browseMemories:
                post(new Runnable() {

                         @Override
                         public void run() {
                             Log.i("MenuActivity: ", "Started browseMemories menu selection.");


                             Intent intent7 = new Intent(MenuActivity.this, MemoryScrollActivity.class);
                             startActivity(intent7);

                             Intent intent8 = new Intent(MenuActivity.this, MemoryScrollActivityInstructions.class);
                             startActivity(intent8);
                         }
                     }
                );
                return true;

            case R.id.deleteMemories:
                post(new Runnable() {

                         @Override
                         public void run() {
                             Log.i("MenuActivity: ", "Started deleteMemories menu selection.");

                             Intent intent10 = new Intent(MenuActivity.this, DeleteMemoriesActivity.class);
                             startActivity(intent10);

                         }
                     }
                );
                return true;


            case R.id.quit:
                // Stop the service at the end of the message queue for proper options menu
                // animation. This is only needed when starting a new Activity or stopping a Service
                // that published a LiveCard.
                post(new Runnable() {

                    @Override
                    public void run() {

                        stopService(new Intent(MenuActivity.this, ElephantService.class));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
        Log.i(App.TAG, className + "---> " + "onOptionsMenuClosed called");

    }

    /**
     * Posts a {@link Runnable} at the end of the message loop, overridable for testing.
     */
    protected void post(Runnable runnable) {
        Log.i(App.TAG, className + "---> " + "post called");
        mHandler.post(runnable);
    }

    

    

}

