# Saral Secugen Reader

[![Download](https://api.bintray.com/packages/compulynx/SecugenSaralBiometric/SaralBiometric/images/download.svg)](https://bintray.com/compulynx/SecugenSaralBiometric/SaralBiometric/_latestVersion)

Implementation Guide :
-----------------------------------------

1. Adding Saral Secugen Reader dependency in **build.gradle** (Module: app)
```gradle
dependencies {
    implementation 'com.compulynx.secugensaralbiometric:SaralBiometric:1.0.0'
}
```

2. Manifest data
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.compulynx.samplebiometric">

    <!--add this line-->
    <uses-feature android:name="android.hardware.usb.host" />  

    <!--add this line-->
    <uses-permission android:name="android.permission.USB_PERMISSION" /> 

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity
            android:name="com.compulynx.samplebiometric.MainActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:launchMode="singleTop"
            android:screenOrientation="portrait">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
            <!--add below intent filter-->
            <intent-filter>
                <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
            </intent-filter>

            <!--add this metadata-->
            <!--find device filter file in sample app-->
            <meta-data
                android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
                android:resource="@xml/device_filter" />
        </activity>

    </application>

</manifest>
```


3. Using SaralSecugenReader in Activity

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SecugenReader secugenReader;
    
    //store old fingerprint data
    private String registerFpData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Saral secugen reader init
        secugenReader = SecugenReader.getInstance(this);
        secugenReader.setMatchPercentage(65);
        secugenReader.setTemplateFormat(SecugenReader.TEMPLATE_FORMAT_SG400);
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonCapture) {
            //capture bitmap only
            secugenReader.captureFingerPrint((bitmap) -> mImageViewFingerprint.setImageBitmap(bitmap));
        } else if (v == this.mButtonRegister) {
            this.mCheckBoxMatched.setChecked(false);
            
            //capture bitmap with encodedCaptureData (Base64) and imageQuality
            secugenReader.captureFingerPrintWithEncodeData(new SecugenReader.FingerPrintDataCallback() {
                @Override
                public void onFingerPrintCapture(Bitmap bitmap, String encodedCaptureData, int captureQuality) {
                    mImageViewFingerprint.setImageBitmap(bitmap);
                    mImageViewRegister.setImageBitmap(bitmap);
                    mTextViewResult.setText("Click Verify");
                    registerFpData = encodedCaptureData;
                }

                @Override
                public void onFingerPrintQualityError(int captureQuality) {

                }
            });
        } else if (v == this.mButtonMatch) {
            //verify old fingerprint with current.
            secugenReader.verifyCaptureFingerPrint(registerFpData, new SecugenReader.FingerPrintVerifyCallback() {
                @Override
                public void onFingerPrintVerify(Bitmap bitmap, String encodedCaptureData, boolean isVerify) {
                    mImageViewFingerprint.setImageBitmap(bitmap);
                    mImageViewVerify.setImageBitmap(bitmap);
                    if (isVerify) {
                        mTextViewResult.setText("MATCHED!!\n");
                        mCheckBoxMatched.setChecked(true);
                    } else {
                        mTextViewResult.setText("NOT MATCHED!!");
                        mCheckBoxMatched.setChecked(false);
                    }
                }

                @Override
                public void onFingerPrintQualityError(int captureQuality) {

                }
            });
        }
    }
}

```

LICENSE :
-----------------------------------------

Copyright (C) The Android Open Source Project
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
