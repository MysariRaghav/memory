package memory.example.com.memory;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import java.io.File;

public class VideoActivity extends Activity {
    private static final String className= MenuActivity.class.getSimpleName();


    private static final int TAKE_VIDEO_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG, className + "---> " + "onCreate called");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, TAKE_VIDEO_REQUEST);

    }

    @Override
    protected void onStop() {
    	super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(App.TAG, className + "---> " + "onActivityResult called");

        if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
        	Uri vid = data.getData();
        	String videoPath = getRealPathFromURI(vid);
            Log.i("VideoActivity: onActivityResult() ", "videoPath is " + videoPath);
            FileOperations.setMemVideo(videoPath);

            processVideoWhenReady(videoPath);
            // Show the thumbnail to the user while the full video is being processed, if desired.
        } else if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_CANCELED) {
            Log.e("VideoActivity: onActivityResult() ", "Video was cancelled. Entire memory needs to be be re-created.");
        }

        super.onActivityResult(requestCode, resultCode, data);
        Intent intent4 = new Intent(this, FileOperationsWriteActivity.class);
        startActivity(intent4);
        finish();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void processVideoWhenReady(final String videoPath) {
        Log.i("VideoActivity: processVideoWhenReady() ", "ran processVideoWhenReady");
        Log.i(App.TAG, className + "---> " + "processVideoWhenReady called");

        final File pictureFile = new File(videoPath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
        	Log.i("processVideoWhenReady", "processPictureWhenReady"+ videoPath);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).
        	Log.i("processVideoWhenReady", "Ran processPictureWhenReady"+ videoPath);

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processVideoWhenReady(videoPath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        Log.i(App.TAG, className + "---> " +"onKeyDown called");
        return keycode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keycode, event);
    }
}

