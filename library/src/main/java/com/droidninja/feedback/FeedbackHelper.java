package com.droidninja.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import com.droidninja.feedback.utils.DeviceData;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.MeasureSpec;

/**
 * The type FeedbackHelper class.
 */
public class FeedbackHelper {
  private static final FeedbackHelper ourInstance = new FeedbackHelper();

  public static final String FEEDBACK_FILE_NAME = "feedback.jpg";

  private FeedbackHelper() {
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static FeedbackHelper getInstance() {
    return ourInstance;
  }

  /**
   * Take screen shot of root view.
   *
   * @param v
   *     the v
   * @return the bitmap
   */
  public Bitmap takeScreenShotOfRootView(View v) {
    v = v.getRootView();
    return takeScreenShotOfView(v);
  }

  /**
   * Take screen shot of the View with spaces as per constraints
   *
   * @param v
   *     the v
   * @return the bitmap
   */
  public Bitmap takeScreenShotOfView(View v) {
    v.setDrawingCacheEnabled(true);
    v.buildDrawingCache(true);

    // creates immutable clone
    Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
    v.setDrawingCacheEnabled(false); // clear drawing cache
    return b;
  }

  /**
   * Take screen shot of texture view as bitmap.
   *
   * @param v
   *     the TextureView
   * @return the bitmap
   */
  public Bitmap takeScreenShotOfTextureView(TextureView v) {
    return v.getBitmap();
  }

  /**
   * Take screen shot of just the View without any constraints
   *
   * @param v
   *     the v
   * @return the bitmap
   */
  public Bitmap takeScreenShotOfJustView(View v) {
    v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    return takeScreenShotOfView(v);
  }

  /**
   * Save screenshot to pictures folder.
   *
   * @param context
   *     the context
   * @param image
   *     the image
   * @param filename
   *     the filename
   * @return the bitmap file object
   * @throws Exception
   *     the exception
   */
  public File saveScreenshotToPicturesFolder(Context context, Bitmap image, String filename)
      throws Exception {
    File bitmapFile = getOutputMediaFile(filename);
    if (bitmapFile == null) {
      throw new NullPointerException("Error creating media file, check storage permissions!");
    }
    FileOutputStream fos = new FileOutputStream(bitmapFile);
    image.compress(Bitmap.CompressFormat.PNG, 90, fos);
    fos.close();

    // Initiate media scanning to make the image available in gallery apps
    MediaScannerConnection.scanFile(context, new String[] { bitmapFile.getPath() },
        new String[] { "image/jpeg" }, null);
    return bitmapFile;
  }

  private File getOutputMediaFile(String filename) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    File mediaStorageDirectory = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + File.separator);
    // Create the storage directory if it does not exist
    if (!mediaStorageDirectory.exists()) {
      if (!mediaStorageDirectory.mkdirs()) {
        return null;
      }
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
    File mediaFile;
    String mImageName = filename + ".jpg";
    mediaFile = new File(mediaStorageDirectory.getPath() + File.separator + mImageName);
    return mediaFile;
  }

  public void sendFeedBack(View view){
    Bitmap bitmap = FeedbackHelper.getInstance().takeScreenShotOfRootView(view);
    try {
      File file =saveScreenshotToPicturesFolder(view.getContext(),bitmap,FEEDBACK_FILE_NAME);
      FeedbackActivity.openFeedbackActivity(view.getContext(), file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}