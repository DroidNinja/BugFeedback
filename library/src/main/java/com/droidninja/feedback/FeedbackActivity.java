package com.droidninja.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import com.droidninja.feedback.utils.DeviceData;

import static com.droidninja.feedback.BaseActivity.EXTRA_PATH;

public class FeedbackActivity extends AppCompatActivity {

  private ImageView imageView;
  private String path;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);

    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Send Feedback");
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    path = getIntent().getStringExtra(EXTRA_PATH);
    imageView = findViewById(R.id.screenshot);
    imageView.setImageBitmap(BitmapFactory.decodeFile(path));

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ImageEditorActivity.openImageEditorActivity(FeedbackActivity.this, path);
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case ImageEditorActivity.REQUEST_CODE_EDIT:
        if (resultCode == RESULT_OK && data != null) {
          path = data.getStringExtra(EXTRA_PATH);
          if (path != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
          }
        }
        break;
    }
  }

  public static void openFeedbackActivity(Context context, String path) {
    Intent intent = new Intent(context, FeedbackActivity.class);
    intent.putExtra(EXTRA_PATH, path);
    context.startActivity(intent);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_feedback, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    int i = item.getItemId();
    if (i == R.id.action_send) {
      EditText feedbackET = findViewById(R.id.feedback_et);
      String feedback = "=== User Feedback===\n";
      feedback += feedbackET.getText().toString() + "\n";
      feedback += "=== Device Info ===\n";
      CheckBox checkBox = findViewById(R.id.checkbox);
      if (checkBox.isChecked()) {
        feedback += new DeviceData(this).toString();
        shareFeedback(path, feedback);
      } else {
        shareFeedback(null, feedback);
      }
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  public void shareFeedback(String path, String logs) {
    final Intent result = new Intent(Intent.ACTION_SEND);
    result.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
    result.putExtra(Intent.EXTRA_TEXT, logs);
    if (path != null) {
      result.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
      result.setType("image/*");
    } else {
      result.setType("text/plain");
    }
    if (result.resolveActivity(getPackageManager()) != null) {
      startActivity(Intent.createChooser(result, "Share to.."));
    }
  }
}
