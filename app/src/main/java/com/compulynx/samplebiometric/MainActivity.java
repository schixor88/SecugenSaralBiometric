package com.compulynx.samplebiometric;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.compulynx.secugensaralbiometric.SecugenReader;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SecugenReader secugenReader;

    private Button mButtonCapture, mButtonRegister, mButtonMatch;
    private TextView mTextViewResult;
    private CheckBox mCheckBoxMatched;
    private ImageView mImageViewFingerprint, mImageViewRegister, mImageViewVerify;

    private String registerFpData = "";

    private Bitmap bitmapS = null;

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
            bitmapS = null;
            secugenReader.captureFingerPrint(new SecugenReader.FingerPrintCallback() {
                @Override
                public void onFingerPrintCapture(Bitmap bitmap) {
                    bitmapS = bitmap;
                    mImageViewFingerprint.setImageBitmap(bitmap);
                }
            });
        } else if (v == this.mButtonRegister) {
//            this.mCheckBoxMatched.setChecked(false);
//            secugenReader.captureFingerPrintWithEncodeData(new SecugenReader.FingerPrintDataCallback() {
//                @Override
//                public void onFingerPrintCapture(Bitmap bitmap, String encodedCaptureData, int captureQuality) {
//                    mImageViewFingerprint.setImageBitmap(bitmap);
//                    mImageViewRegister.setImageBitmap(bitmap);
//                    mTextViewResult.setText("Click Verify");
//                    registerFpData = encodedCaptureData;
//                }
//
//                @Override
//                public void onFingerPrintQualityError(int captureQuality) {
//
//                }
//            });

            if (bitmapS==null){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to cancel the process?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(
                        AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                            Bitmap myLogo = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                            Uri uri = getImageUri(context, myLogo);
//                            Intent intent = new Intent();
//                            intent.setClipData( ClipData.newRawUri(null, uri));
//                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                );
                alertDialog.show();
            }else {
                Uri uri = getImageUri(MainActivity.this, bitmapS);
                Toast.makeText(this,bitmapS.toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClipData( ClipData.newRawUri(null, uri));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                setResult(RESULT_OK, intent);
                finish();
            }


            Toast.makeText(this, "Intent Return Here", Toast.LENGTH_SHORT).show();

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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
