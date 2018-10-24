package com.droidninja.feedback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import com.droidninja.feedback.imagezoom.ImageViewTouch;
import java.io.File;
import java.lang.ref.WeakReference;

public class ImageEditorActivity extends BaseActivity implements View.OnClickListener {

  public static final int REQUEST_CODE_EDIT = 234;
  private CustomPaintView mPaintView;
  public ImageViewTouch imageView;
  private Bitmap mainBitmap;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_editor);

    String path = getIntent().getStringExtra(EXTRA_PATH);
    imageView = findViewById(R.id.screenshot);
    ImageView redColorView = findViewById(R.id.red_color_iv);
    ImageView blackColorView = findViewById(R.id.black_color_iv);
    ImageView undoBtn = findViewById(R.id.undo_btn);
    ImageView saveBtn = findViewById(R.id.save_btn);
    ImageView closeBtn = findViewById(R.id.close_btn);
    redColorView.setOnClickListener(this);
    blackColorView.setOnClickListener(this);
    undoBtn.setOnClickListener(this);
    saveBtn.setOnClickListener(this);
    closeBtn.setOnClickListener(this);

    mainBitmap = BitmapFactory.decodeFile(path);
    imageView.setImageBitmap(mainBitmap);

    mPaintView = findViewById(R.id.paintView);
    mPaintView.setWidth(20);
  }

  public static void openImageEditorActivity(Activity activity, String path) {
    Intent intent = new Intent(activity, ImageEditorActivity.class);
    intent.putExtra(EXTRA_PATH, path);
    activity.startActivityForResult(intent, REQUEST_CODE_EDIT);
  }

  @Override public void onClick(View view) {
    if (view.getId() == R.id.black_color_iv) {
      mPaintView.setColor(ContextCompat.getColor(this, R.color.paint_black));
    } else if (view.getId() == R.id.red_color_iv) {
      mPaintView.setColor(ContextCompat.getColor(this, R.color.paint_red));
    } else if (view.getId() == R.id.undo_btn) {

    } else if (view.getId() == R.id.save_btn) {
      new SaveCustomPaintTask(new WeakReference<>(this)).execute(mainBitmap);
    } else if (view.getId() == R.id.close_btn) {
      setResult(RESULT_CANCELED);
      finish();
    }
  }

  public void setImageBitmap(Bitmap result) {
    mainBitmap = result;
    imageView.setImageBitmap(result);
    try {
      File file = FeedbackHelper.getInstance()
          .saveScreenshotToPicturesFolder(ImageEditorActivity.this, result,
              FeedbackHelper.FEEDBACK_FILE_NAME);
      Intent intent = new Intent();
      intent.putExtra(EXTRA_PATH, file.getAbsolutePath());
      setResult(RESULT_OK, intent);
      finish();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static class SaveCustomPaintTask extends StickerTask {

    public SaveCustomPaintTask(WeakReference<ImageEditorActivity> activity) {
      super(activity);
    }

    @Override public void handleImage(Canvas canvas, Matrix m) {
      float[] f = new float[9];
      m.getValues(f);
      int dx = (int) f[Matrix.MTRANS_X];
      int dy = (int) f[Matrix.MTRANS_Y];
      float scale_x = f[Matrix.MSCALE_X];
      float scale_y = f[Matrix.MSCALE_Y];
      canvas.save();
      canvas.translate(dx, dy);
      canvas.scale(scale_x, scale_y);

      if (mContext.get().mPaintView.getPaintBit() != null) {
        canvas.drawBitmap(mContext.get().mPaintView.getPaintBit(), 0, 0, null);
      }
      canvas.restore();
    }

    @Override public void onPostResult(Bitmap result) {
      mContext.get().mPaintView.reset();
      mContext.get().setImageBitmap(result);
    }
  }
}
