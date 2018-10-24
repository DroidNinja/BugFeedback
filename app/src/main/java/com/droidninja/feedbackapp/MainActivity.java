package com.droidninja.feedbackapp;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.droidninja.feedback.FeedbackHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button feedbackBtn = findViewById(R.id.feedback_btn);
    feedbackBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
        new RxPermissions(MainActivity.this).request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new io.reactivex.functions.Consumer<Boolean>() {
          @Override public void accept(Boolean grant) throws Exception {
            if(grant){
              FeedbackHelper.getInstance().sendFeedBack(view);
            }
            else{
              //permission error
            }
          }
        });
      }
    });
  }


}
