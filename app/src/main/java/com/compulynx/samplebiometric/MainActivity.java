package com.compulynx.samplebiometric;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.secugensaralbiometric.SecugenReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SecugenReader secugenReader;

    private Button mButtonCapture, mButtonRegister, mButtonMatch;
    private TextView mTextViewResult;
    private CheckBox mCheckBoxMatched;
    private ImageView mImageViewFingerprint, mImageViewRegister, mImageViewVerify;

    private String registerFpData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mButtonCapture = findViewById(R.id.buttonCapture);
        mButtonCapture.setOnClickListener(this);
        mButtonRegister = findViewById(R.id.buttonRegister);
        mButtonRegister.setOnClickListener(this);
        mButtonMatch = findViewById(R.id.buttonMatch);
        mButtonMatch.setOnClickListener(this);

        mImageViewFingerprint = findViewById(R.id.imageViewFingerprint);
        mImageViewRegister = findViewById(R.id.imageViewRegister);
        mImageViewVerify = findViewById(R.id.imageViewVerify);
        findViewById(R.id.buttonSave).setOnClickListener(this);

        mTextViewResult = findViewById(R.id.textViewResult);
        mCheckBoxMatched = findViewById(R.id.checkBoxMatched);


        secugenReader = SecugenReader.getInstance(this);
        secugenReader.setMatchPercentage(65);
        secugenReader.setTemplateFormat(SecugenReader.TEMPLATE_FORMAT_SG400);
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonCapture) {
            secugenReader.captureFingerPrint((bitmap) -> mImageViewFingerprint.setImageBitmap(bitmap));
        } else if (v == this.mButtonRegister) {
            this.mCheckBoxMatched.setChecked(false);
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
