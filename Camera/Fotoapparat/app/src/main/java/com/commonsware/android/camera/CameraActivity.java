/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.github.clans.fab.FloatingActionButton;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;

public class CameraActivity extends Activity {
  private CameraView camera;
  private FloatingActionButton fab;
  private Fotoapparat fotoapparat;

  public static void takePhoto(Activity requester, int requestCode) {
    Intent i=new Intent(requester, CameraActivity.class);

    requester.startActivityForResult(i, requestCode);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.camera);

    camera=findViewById(R.id.camera);
    fab=findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        takePhoto();
      }
    });

    fotoapparat=Fotoapparat
      .with(this)
      .into(camera)
      .previewScaleType(ScaleType.CENTER_CROP)
      .photoSize(biggestSize())
      .lensPosition(back())
      .focusMode(firstAvailable(continuousFocus(), autoFocus(), fixed()))
      .flash(firstAvailable(autoRedEye(), autoFlash()))
      .build();
  }

  @Override
  protected void onStart() {
    super.onStart();
    fotoapparat.start();
  }

  @Override
  protected void onStop() {
    fotoapparat.stop();
    super.onStop();
  }

  private void takePhoto() {
    fab.setEnabled(false);

    PhotoResult result=fotoapparat.takePicture();

    result.toBitmap().whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
      @Override
      public void onResult(BitmapPhoto bitmapPhoto) {
        // TODO: do something with picture

        setResult(RESULT_OK);
        finish();
      }
    });
  }
}
