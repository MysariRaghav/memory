package memory.example.com.memory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;


public class PictureActivity extends Activity {

    private static final String className= MenuActivity.class.getSimpleName();


    private static final int TAKE_PICTURE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(App.TAG, className + "---> " + "onCreate called");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(App.TAG, className + "---> " + "onActivityResult called");

        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            Uri tempUri = getImageUri(getApplicationContext(),photo);
        	
        	
        	String picturePath = getRealPathFromURI(tempUri);
            Log.i("PictureActivity", "picturePath is " + picturePath);
            FileOperations.setMemPicture(picturePath);

            processPictureWhenReady(picturePath);
            // Show the thumbnail to the user while the full picture is being processed, if desired.
        } else if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_CANCELED) {
            Log.e("PictureActivity: onActivityResult() ", "Picture was cancelled, so entire memory should be re-created.");
        }

        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        //Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
    	String[] proj = { Images.Media.DATA };
    	Cursor cursor = getContentResolver().query(uri, proj, null, null, null);         
    	cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(Images.ImageColumns.DATA);
        return cursor.getString(idx); 
    }

    private void processPictureWhenReady(final String picturePath) {
        Log.i(App.TAG, className + "---> " + "processPictureWhenReady called");
    	
    	Log.i("VideoActivity: processPictureWhenReady() ", "ran processPictureWhenReady");
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
        	Log.i("processPictureWhenReady", "processPictureWhenReady");
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).
        	Log.i("processPictureWhenReady", "ran processPictureWhenReady");

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
                                    processPictureWhenReady(picturePath);
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
        Log.i(App.TAG, className + "---> " + "onKeyDown called");
        return keycode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keycode, event);
    }
}
