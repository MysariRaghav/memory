package memory.example.com.memory;



import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MemoryScrollActivity extends Activity {

    private static final String className= MenuActivity.class.getSimpleName();
    private static final String TAG= MemoryScrollActivity.class.getSimpleName();
    private static boolean isJpgToBmpDone = false;
    Context iContext = null;
    private List<ImageView> mCards;
    GridView gridview;
    private ScrollView mCardScrollView;
    private ImageAdapter mAdapter;
    private FileOperations mfileOperations;
    private Handler mHandler;
    private Runnable jpgRun;
    private JpgToBmpTask jpgToBmp = new JpgToBmpTask();
    private ArrayList<String> memTitles = new ArrayList<String>();
    private ArrayList<String> memDates = new ArrayList<String>();
    private ArrayList<String> memTimes = new ArrayList<String>();
    private ArrayList<String> memPictures = new ArrayList<String>();
    private ArrayList<String> memVideos = new ArrayList<String>();

    private boolean mWasVideoPlayed = false;
    private boolean firstResume = false;
    private int mLastVideoPlayedPosition = -1;

    public static void setJpgToBmpDone(boolean isJpgToBmpDoneParam) {
        Log.i(App.TAG, className + "---> " + "setJpgToBmpDone called");
        isJpgToBmpDone = isJpgToBmpDoneParam;
    }

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
        super.onCreate(savedInstanceState);

        iContext = MemoryScrollActivity.this.getApplicationContext();
        Intent intent = new Intent(this, ElephantService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Log.i(App.TAG, className + "---> " + "onCreate called");
        Log.i(TAG,"    ------>  onCreate");

        firstResume = false;
        if (jpgToBmp.getBitmapArrayListHasValidValueFlag()) {
            setupGlobalsWhenBMPsAvailable();
            readMemoriesFromFile();
            setGridView();

        } else {
            mHandler = new Handler();
            jpgRun = new Runnable() {
                public void run() {
                    if (isJpgToBmpDone) {
                        cancelJpgRunnable();
                        setGridView();

                    } else {
                        mHandler.postDelayed(this, 1000);
                    }
                }
            };

            createCards();

            mCardScrollView = new ScrollView(this);
        }

    }



    private void createCards() {
        Log.i(App.TAG, className + "---> " + "createCards called");
        Log.i(TAG, "    ------>  createCards");

        mCards = new ArrayList<ImageView>();

        readMemoriesFromFile();

        jpgToBmp.execute(memPictures);
        mHandler.postDelayed(jpgRun, 1000);

    }

    private void readMemoriesFromFile() {
        Log.i(TAG,"    ------>  readMemoriesFromFile");

        Log.i(App.TAG, className + "---> " + "readMemoriesFromFile called");

        iContext = this.getApplicationContext();

        mfileOperations = new FileOperations(iContext);
        String memoryFilesContents = mfileOperations.readFromFile();

        String lineString = "";
        String artifactString = "";
        int memoryCounter = 0;
        int artifactCounter = 0;

        StringTokenizer lt = new StringTokenizer(memoryFilesContents, "\n");
        while (lt.hasMoreElements()) {
            //each line string is a valid memory stored on the text file
            lineString = lt.nextToken();
            artifactCounter = 0;
            memoryCounter++;

            StringTokenizer at = new StringTokenizer(lineString, "+++");
            while (at.hasMoreElements()) {
                artifactCounter++;
                artifactString = at.nextToken();
                Log.i("MemoryScrollActivity: memory " + memoryCounter +" artifact: " + artifactCounter+" ",artifactString);
                saveArtifactGlobally(artifactCounter, artifactString);
            }

        }
    }

    private void saveArtifactGlobally(int artifactIndex, String artifactValue) {

        Log.i(App.TAG, className + "---> " + "saveArtifactGlobally called");
        Log.i(TAG,"    ------>  saveArtifactGlobally");

        //artifacts for each memory are indexed as follows:
        //1. Date and time (eg. 11/08/2014 17:18:15)
        //2. Title.  Note that this can contain multiple words, numbers, symbols, spaces, etc
        //3. Picture. (eg. /storage/emulated/0/DCIM/Camera/20141108_171829_442.jpg)
        //4. Video. (eg. /storage/emulated/0/DCIM/Camera/20141108_172011_054.mp4)
        if (artifactIndex == 1) {
            int portionCounter = 0;
            String portion = "";
            StringTokenizer wt = new StringTokenizer(artifactValue, " ");
            while (wt.hasMoreElements()) {
                portionCounter++;
                portion = wt.nextToken();
                if (portionCounter == 1) {
                    memDates.add(portion);
                } else {
                    memTimes.add(portion);
                }
            }
        }
        if (artifactIndex == 2) {
            memTitles.add(artifactValue);
        }
        if (artifactIndex == 3) {
            memPictures.add(artifactValue);
        }
        if (artifactIndex == 4) {
            memVideos.add(artifactValue);
        }
    }


    private void setGridView()
    {
        Log.i(App.TAG, className + "---> " + "setGridView called");

        Log.i(TAG,"    ------>  setGridView");

        setContentView(R.layout.activity_main);
        gridview = (GridView) findViewById(R.id.grid1);
        gridview.setAdapter(new ImageAdapter(MemoryScrollActivity.this));
        //mCardScrollView.setOnItemClickListener(this);
        gridview.setFocusable(true);
        gridview.setFocusableInTouchMode(true);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                setContentView(R.layout.activity_main);
                Toast.makeText(MemoryScrollActivity.this, "" + position, Toast.LENGTH_SHORT).show();


                Log.i(App.TAG, className + "---> " + "onItemClick called");

                if (jpgToBmp.getBmpArray().size() == 0) {
                    finish();
                } else {
                    Log.i("MemoryScrollActivity onItemClick() ", "playing vid at position: " + position);
                    mWasVideoPlayed = true;
                    mLastVideoPlayedPosition = position;
                    Intent playSelectedVideo = new Intent();
                    playSelectedVideo.setAction(Intent.ACTION_VIEW);
                    playSelectedVideo.setDataAndType(Uri.parse(memVideos.get(position)), "video/*");
                    MemoryScrollActivity.this.startActivity(playSelectedVideo);
                }


            }
        });
    }

    private void setupGlobalsWhenBMPsAvailable() {

        Log.i(TAG,"    ------>  setupGlobalsWhenBMPsAvailable");

        Log.i(App.TAG, className + "---> " + "setupGlobalsWhenBMPsAvailable called");

        memTitles = new ArrayList<String>();
        memDates = new ArrayList<String>();
        memTimes = new ArrayList<String>();
        memPictures = new ArrayList<String>();
        memVideos = new ArrayList<String>();

        mHandler = new Handler();

        mCards = new ArrayList<ImageView>();
    }

    @Override
    protected void onResume() {
        // To receive touch events from the Screen, the view should have focus.
        super.onResume();
        Log.i(TAG, "    ------>  onResume");

        Log.i(App.TAG, className + "---> " + "onResume called");
        if(firstResume){
            setupGlobalsWhenBMPsAvailable();
            readMemoriesFromFile();
            setGridView();
        }
        firstResume=true;
        if (!isJpgToBmpDone) {
            //gridview.requestFocus();
        } else {
            if (mWasVideoPlayed && mLastVideoPlayedPosition != -1) {
                gridview.requestFocus();

                mWasVideoPlayed = false;
                mLastVideoPlayedPosition = -1;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "    ------>  onPause");

        Log.i(App.TAG, className + "---> " + "onPause called");

        if (isFinishing()) {
            setJpgToBmpDone(false);
            mHandler.removeCallbacks(jpgRun);
            jpgToBmp.setBitmapArrayListHasValidValueFlag(false);
            jpgToBmp.getBmpArray().clear();
            jpgToBmp.cancel(true);
            mCards.clear();
            jpgToBmp = null;
            nullifyGlobalVariables();
            Intent intent = new Intent(MemoryScrollActivity.this, MenuActivity.class);
            startActivity(intent);
        } else {
            nullifyGlobalVariables();
        }

        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    private void nullifyGlobalVariables() {
        Log.i(App.TAG, className + "---> " + "nullifyGlobalVariables called");
        Log.i(TAG, "    ------>  nullifyGlobalVariables");

        mCards.clear();
        mAdapter = null;
        mfileOperations = null;
        iContext = null;
        memTitles = null;
        memDates = null;
        memTimes = null;
        memPictures = null;
        memVideos = null;
        mHandler = null;
        jpgRun = null;
        mService = null;

    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"    ------>  onStop");

        Log.i(App.TAG, className + "---> " + "onStop called");
        finish();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void cancelJpgRunnable() {
        Log.i(App.TAG, className + "---> " + "cancelJpgRunnable called");
        Log.i(TAG, "    ------>  cancelJpgRunnable");

        mHandler.removeCallbacks(jpgRun);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i(TAG,"    ------>  onCreateOptionsMenu");

        Log.i(App.TAG, className + "---> " + "onCreateOptionsMenu called");

        getMenuInflater().inflate(R.menu.stopwatch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.i(TAG,"    ------>  onOptionsItemSelected");

        Log.i(App.TAG, className + "---> " + "onOptionsItemSelected called");

        int id = item.getItemId();
        if (id == R.id.deleteMemories) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        List<Bitmap> mThumbIds;

        public ImageAdapter(Context c) {
            Log.i(TAG,"    ------>  ImageAdapter constru");
            Log.i(App.TAG, className + "---> " + "ImageAdapter constru called");


            mContext = c;
            mThumbIds=jpgToBmp.getBmpArray();
        }


        public int getCount() {
            Log.i(TAG,"    ------>  ImageAdapter getCount");
            Log.i(App.TAG, className + "---> " + "ImageAdapter getCount");
            return mThumbIds.size();
        }


        public Object getItem(int position) {
            Log.i(TAG,"    ------>  ImageAdapter getItem");
            Log.i(App.TAG, className + "---> " + "ImageAdapter getItem");
            return null;
        }


        public long getItemId(int position) {
            Log.i(TAG,"    ------> ImageAdapter getItemId");
            Log.i(App.TAG, className + "---> " + "ImageAdapter getItemId");
            return 0;
        }


        // create a new ImageView for each item referenced by the Adapter

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG,"    ------> ImageAdapter getView");
            Log.i(App.TAG, className + "---> " + "ImageAdapter getView");
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(mThumbIds.get(position));
            return imageView;
        }

    }

    public class JpgToBmpTask extends AsyncTask<ArrayList<String>, Integer, ArrayList<Bitmap>> {

        private final String className= MenuActivity.class.getSimpleName();
        private ArrayList<String> mPicStringArrayList;
        private ArrayList<Bitmap> mBitmapArrayList;
        private boolean bitmapArrayListHasValueFlag = false;

        public JpgToBmpTask() {
            Log.i(App.TAG, className + "---> " + "JpgToBmpTask cnstrc called");
            Log.i(TAG,"    ------>  JpgToBmpTask cnstrc");


        }

        @Override
        protected ArrayList<Bitmap> doInBackground(ArrayList<String>... params) {
            Log.i(App.TAG, className + "---> " + "doInBackground called");
            Log.i(TAG,"    ------> JpgToBmpTask doInBackground");


            mPicStringArrayList = params[0];
            mBitmapArrayList = new ArrayList<Bitmap>();

            int count = mPicStringArrayList.size();

            for (int z = 0; z < count; z++) {
                String jpgPath = mPicStringArrayList.get(z);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                options.inSampleSize = 4;
                Bitmap bMap = BitmapFactory.decodeFile(jpgPath, options);
                mBitmapArrayList.add(bMap);

                publishProgress((int) ((z / (float) count) * 100));
            }

            return mBitmapArrayList;
        }

        @Override

        protected void onProgressUpdate(Integer... progress) {
            Log.i(App.TAG, className + "---> " + "onProgressUpdate called");
            Log.i(TAG,"    ------> JpgToBmpTask onProgressUpdate");


            int percentComplete = progress[0];
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            Log.i(App.TAG, className + "---> " + "onPostExecute called");
            Log.i(TAG, "    ------> JpgToBmpTask onPostExecute");

            MemoryScrollActivity.setJpgToBmpDone(true);
            setBitmapArrayListHasValidValueFlag(true);
        }

        protected ArrayList<Bitmap> getBmpArray() {
            Log.i(TAG,"    ------> JpgToBmpTask getBmpArray");
            Log.i(App.TAG, className + "---> " +"getBmpArray");
            return mBitmapArrayList;
        }

        protected boolean getBitmapArrayListHasValidValueFlag() {
            Log.i(TAG,"    ------> JpgToBmpTask getBitmapArrayListHasValidValueFlag");
            Log.i(App.TAG, className + "---> " +"getBitmapArrayListHasValidValueFlag");
            return bitmapArrayListHasValueFlag;
        }

        protected void setBitmapArrayListHasValidValueFlag(boolean value) {
            Log.i(TAG,"    ------> JpgToBmpTask setBitmapArrayListHasValidValueFlag");
            Log.i(App.TAG, className + "---> " +"setBitmapArrayListHasValidValueFlag");
                    bitmapArrayListHasValueFlag = value;
        }

        protected Bitmap getBitmapAtIndex(int index) {
            Log.i(TAG,"    ------> JpgToBmpTask getBitmapAtIndex");
            Log.i(App.TAG, className + "---> " +"getBitmapAtIndex");
            return mBitmapArrayList.get(index);
        }

    }
}