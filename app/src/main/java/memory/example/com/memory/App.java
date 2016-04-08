package memory.example.com.memory;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class App extends Application{

    private static final String className= MenuActivity.class.getSimpleName();
    public static final String TAG = "App";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

        Log.i(App.TAG, className + "---> " + "onCreate() called");
        Intent serviceIntent = new Intent(getApplicationContext(),ElephantService.class);
        startService(serviceIntent);
		super.onCreate();
	}



}
