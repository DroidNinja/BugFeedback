package com.droidninja.feedback;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {

  public static final String EXTRA_PATH = "EXTRA_PATH";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public Dialog getLoadingDialog(Context context, int titleId,
      boolean canCancel) {
    return getLoadingDialog(context,context.getString(titleId),canCancel);
  }


  public Dialog getLoadingDialog(Context context, String title,
      boolean canCancel) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setCancelable(canCancel);
    dialog.setMessage(title);
    return dialog;
  }
}
