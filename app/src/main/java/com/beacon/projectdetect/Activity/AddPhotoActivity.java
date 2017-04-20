package com.beacon.projectdetect.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.Service.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by qiwhuang on 4/19/2017.
 */

public class AddPhotoActivity extends AppCompatActivity {

    private String userChoosenTask ;
    private int SELECT_FILE = 1;
    private ImageView ivImage;
    private Bitmap bm = null;
    private Button save ;
    private String beaconIdentifier;
    private String projectIdentifier;
    private String filePath;
    private Uri finalFile;
    private String beaconUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        save = (Button) findViewById(R.id.save_photo);
        beaconIdentifier = getIntent().getStringExtra("beaconIdentifierEdit");
        projectIdentifier = getIntent().getStringExtra("projectId");
        beaconUid = getIntent().getStringExtra("beaconUid");
        selectImage();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setEnabled(false);
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                OutputStream os = null;
                try {
                    File file = new File(dir, beaconIdentifier + ".png");
                    os = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, os);
                    bm.recycle();
                    filePath = file.getPath();
                    finalFile = Uri.fromFile(file);
                    UploadTask uploadTask = FirebaseManager.getInstance().getFirebaseStorage().getReference().child("BeaconData")
                            .child(beaconUid).child("ProjectPlug").child(projectIdentifier).child( beaconIdentifier + ".png")
                            .putFile(finalFile,new StorageMetadata.Builder()
                                    .setContentType("image/jpeg")
                                    .build());

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // TODO error handling when upload
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            Toast.makeText(AddPhotoActivity.this, "Upload complet", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPhotoActivity.this,MainActivity.class);
                            intent.putExtra("changePhoto", true);
                            startActivity(intent);
                        }
                    });
                } catch(IOException e) {
                    bm.recycle();
                    Log.e("combineImages", "problem combining images", e);
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Choisir dans les photos",
                "Annuler" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter des Photos!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddPhotoActivity.this);
                if (items[item].equals("Choisir dans les photos")) {
                    userChoosenTask="Choisir dans les photos";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Annuler")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bm = getResizedBitmap(bm,512,512);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
