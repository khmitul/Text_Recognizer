package com.example.textrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {

    EditText mResultEt;
    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);

        //Camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.CAMERA};

        //Storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Floating send button handle
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Val = mResultEt.getText().toString();
                //Showes the sending media
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,Val);
                startActivity(Intent.createChooser(intent,"Send copied data"));
            }
        });
    }



    //Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handle actiobar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addImage){
            showImageImportDialog();
        }
        if(id == R.id.copy_content){
            String Value = mResultEt.getText().toString();
           if( Value.isEmpty() ){
               Toast.makeText(getApplicationContext(),"Nothing to copy!!!",Toast.LENGTH_SHORT).show();
           }else{
               ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
               ClipData clipData = ClipData.newPlainText("Data",Value);
               clipboardManager.setPrimaryClip(clipData);
               Toast.makeText(getApplicationContext(),"Text copied",Toast.LENGTH_SHORT).show();
           }
        }
        if(id == R.id.delete_content){
            String Value = mResultEt.getText().toString();
            if( Value.isEmpty() && imgconfirmchk == 0  ){
                Toast.makeText(getApplicationContext(),"Already empty!!!",Toast.LENGTH_SHORT).show();
            }
            if(!Value.isEmpty() || imgconfirmchk == 1 ){
                mResultEt.setText("");
                mPreviewIv.setImageDrawable(null);
                Toast.makeText(getApplicationContext(),"Cleared",Toast.LENGTH_SHORT).show();
                imgconfirmchk = 0;
            }
        }
        if(id == R.id.instructions){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

            builder1.setMessage(Html.fromHtml("Instructions:-<br />1. Tap on the image logo in the actionbar menu.<br />" +
                    "2. Select camera or gallery to take image to recognize text from it.<br />" +
                    "3. Tap on the crop option in the actionbar to select area.<br />" +
                    "4. See the output where the recognized text can be modified.<br />"));

            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog Alert1 = builder1.create();
            Alert1 .show();
        }
        if(id == R.id.update){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

            builder1.setMessage(Html.fromHtml("Click on the link below to update the app: <br /> <a href=\"https://drive.google.com/open?id=1oQnp8WlRq1qkue-AUZNgO8Bl693rIHrX\">Google drive link</a>"));

            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog Alert1 = builder1.create();
            Alert1 .show();
            ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        if(id == R.id.settings){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

            builder1.setMessage(Html.fromHtml("This section is on under development<br />"));

            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog Alert1 = builder1.create();
            Alert1 .show();
            ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        if(id == R.id.feedback){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

            builder1.setMessage(Html.fromHtml("Please provide your valuable feedback here: <br />Gmail: <a href=\"kamalhossainmitul0@gmail.com\">kamalhossainmitul0@gmail.com</a><br />Facebook: <a href=\"https://www.facebook.com/profile.php?id=100003957677793\">Kamal Hossain Mitul</a>"));

            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog Alert1 = builder1.create();
            Alert1 .show();
            ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        if(id == R.id.share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String body = "https://drive.google.com/open?id=1oQnp8WlRq1qkue-AUZNgO8Bl693rIHrX";

            intent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(intent,"Share my app link"));
        }
        if(id == R.id.about){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

            builder1.setMessage(Html.fromHtml("Developed by: Md. Kamal Hossain Mitul <br />Gmail: <a href=\"kamalhossainmitul0@gmail.com\">kamalhossainmitul0@gmail.com</a> <br />Facebook: <a href=\"https://www.facebook.com/profile.php?id=100003957677793\">Kamal Hossain Mitul</a>"));

            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog Alert1 = builder1.create();
            Alert1 .show();
            ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        if(id == R.id.quit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to quit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Exit dialog box on backpress button
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showImageImportDialog() {
        //Items to display in the dialog
        String[] items = {"Gallery","Camera"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //set title
        dialog.setTitle("Select Image");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //Gallery option clicked
                    if(!checkStoragePermission()){
                        //Storage permission not allowed,so, request it here
                        requestStoragePermission();
                    }
                    else{
                        //Permission allowed, take picture from gallery
                        pickGallery();
                    }
                }
                if(which == 1){
                    /*For OS marshmallow and above we need to ask runtime permission of camera and storage*/

                    //Camera option clicked
                    if(!checkCameraPermission()){
                        //Camera permission not allowed,so, request it here
                        requestCameraPermission();
                    }
                    else{
                        //Permission allowed, take picture from camera
                        pickCamera();
                    }
                }
            }
        });
        dialog.create().show(); //show dialog
    }

    private void pickGallery() {
        //Intent to take image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //Set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //Intent to take image from camera,it will also be saved to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic"); //Title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image To text");  //Description
        image_uri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        /*Check camera permission and return the result
        * In order to get high quality image we have to save image to our external storage first before inserting to image view
        * that's why storage permission will also be required  */

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }

    //Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        pickCamera();
                    }

                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    //Handle image result
    int imgconfirmchk = 0; // To provide clear message when deleting the image from the interface
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //Got image from gallery now crop it
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)     //Enable image guidelines
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)    //Enable image guidelines
                        .start(this);
            }
        }
        //Get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();    //Get image uri
                //Set image to image view
                mPreviewIv.setImageURI(resultUri);

                //Get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    //Toast.makeText(this,"Error!Google Vision is incompatible with this device",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                    builder1.setMessage(Html.fromHtml("Error!Google Vision is incompatible with this device<br />"));

                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Try again later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog Alert1 = builder1.create();
                    Alert1.show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);

                    StringBuilder sb = new StringBuilder();
                    //Get text from array until there is no text left
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                    }
                    //Set text to editable
                    mResultEt.append(sb.toString());

                    //If no text found then this alert
                    if (items.size() == 0) {
                        imgconfirmchk = 1;  // To provide clear message when deleting the image from the interface

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        builder1.setMessage(Html.fromHtml("Text Detection failed!Try with proper image <br />"));

                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog Alert1 = builder1.create();
                        Alert1.show();
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //If there is any error show it
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
