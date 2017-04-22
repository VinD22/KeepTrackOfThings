package things.track.keep.chain.key.app.v.keeptrackofthings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import model.Thing;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Edit an Item to its new place!
 */

public class EditItem extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_FROM_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    String[] cameraPermissionArray = {"android.permission.CAMERA"};
    String[] storagePermissionArray = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    Realm realm;

    private ImageView mItemImage;
    private ImageButton mDeleteItem;
    private Button mUpdateItem, mAddPhoto;
    private EditText mName, mWhere, mAdditionalInfo;

    File file;
    byte[] imageData;

    int itemId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);

        realm = Realm.getDefaultInstance();

        mItemImage = (ImageView) findViewById(R.id.item_image);
        mDeleteItem = (ImageButton) findViewById(R.id.delete_item);

        mUpdateItem = (Button) findViewById(R.id.update_item);
        mAddPhoto = (Button) findViewById(R.id.add_photo);

        mName = (EditText) findViewById(R.id.name);
        mWhere = (EditText) findViewById(R.id.where);
        mAdditionalInfo = (EditText) findViewById(R.id.additional_info);

//        Toast.makeText(this, "Byte array is " + imageData + ".", Toast.LENGTH_SHORT).show();
//        String s = new String(imageData);
//        Toast.makeText(this, "Byte to String is " + s + ".", Toast.LENGTH_SHORT).show();

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {
            itemId = extras.getInt("id");

            Thing tempThing = realm.where(Thing.class).equalTo("id", itemId).findFirst();
            realm.beginTransaction();

            mName.setText(tempThing.getName());
            mWhere.setText(tempThing.getWhere() + "");
            mAdditionalInfo.setText(tempThing.getAddtionalData() + "");
            imageData = tempThing.getImage();

            Bitmap bmp = BitmapFactory.decodeByteArray(tempThing.getImage(), 0, tempThing.getImage().length);
            mItemImage.setImageBitmap(bmp);

            realm.commitTransaction();

        }


        mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemId == -1) {
                    Toast.makeText(EditItem.this, "Error in Item Id", Toast.LENGTH_SHORT).show();
                } else {

                    // Deleting Item from database


                    new AlertDialog.Builder(EditItem.this, R.style.MyDialogTheme)
                            .setTitle(R.string.delete_item)
                            .setMessage(R.string.confirm_delete)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                    realm.beginTransaction();
                                    Thing thing = realm.where(Thing.class).equalTo("id", itemId).findFirst();
                                    thing.deleteFromRealm();
                                    realm.commitTransaction();

                                    Intent intent = new Intent(EditItem.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();



                }

                
            }
        });

        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });

        mUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString();
                String where = mWhere.getText().toString();
                String additionalInfo = mAdditionalInfo.getText().toString();

                if (name.isEmpty() || where.isEmpty()) {
                    Toast.makeText(EditItem.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else {

                    // Save in Realm Database!

                    realm.beginTransaction();

                    Thing editItem = realm.where(Thing.class).equalTo("id", itemId).findFirst();
                    editItem.setId(itemId);
                    editItem.setName(capitalizeFirstLetter(name));
                    editItem.setWhere(where);
                    editItem.setImage(imageData);
                    editItem.setAddtionalData(additionalInfo + "");

                    realm.commitTransaction();

                    // Toast.makeText(EditItem.this, R.string.product_added, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditItem.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }


            }
        });

    }

    private void selectImage() {

        final CharSequence takePhotoText = getString(R.string.take_photo);
        final CharSequence chooseFromLibraryText = getString(R.string.choose_from_library);
        final CharSequence cancelText = getString(R.string.cancel);
        final CharSequence[] items = {takePhotoText, chooseFromLibraryText, cancelText};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditItem.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                // boolean result = Utility.checkPermission(EditItem.this);
                boolean result = true;


                if (items[item].equals(takePhotoText)) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals(chooseFromLibraryText)) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals(cancelText)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        // askForPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean cameraPermissiongiven = checkCameraPermission();

            if (cameraPermissiongiven) {
                takePicture();
            } else {
                requestCameraPermission();
            }

        } else {

            takePicture();

        }

    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "keep_track.jpg");
        // put Uri as extra in intent object
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        Uri photoURI = FileProvider.getUriForFile(EditItem.this, getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(intent, REQUEST_CAPTURE_FROM_CAMERA);

    }


    private void galleryIntent() {

//        Intent intent = new Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean storagePermissionGiven = checkStoragePermission();

            if (storagePermissionGiven) {
                selectFile();
            } else {
                requestStoragePermission();
            }

        } else {

            selectFile();

        }


    }

    private void selectFile() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            //   Toast.makeText(getActivity(), "Called RESULT_ON_ACTIVITY", Toast.LENGTH_SHORT).show();

            ProgressBar progressBar2 = new ProgressBar(EditItem.this, null, android.R.attr.progressBarStyleSmall);
            progressBar2.setVisibility(View.VISIBLE);

            Bitmap mBitmap;
            if (requestCode == REQUEST_CAPTURE_FROM_CAMERA) {

                progressBar2.setVisibility(View.GONE);

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "keep_track.jpg");
                Bitmap imageBitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500);
                // mProfilePic.setImageBitmap(imageBitmap);

                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(file.getAbsolutePath());
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            imageBitmap = rotateImage(imageBitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            imageBitmap = rotateImage(imageBitmap, 180);
                            break;
                        // etc.
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //   Toast.makeText(getActivity(), "Called RESULT_ON_ACTIVITY", Toast.LENGTH_SHORT).show();

                mItemImage.setImageBitmap(imageBitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                imageData = stream.toByteArray();


            } else if (requestCode == SELECT_FILE) {


                Uri uri = data.getData();

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    imageData = stream.toByteArray();

                    mItemImage.setImageBitmap(mBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


//                String tempPath = getPath(selectedImageUri, EditItem.this);
//                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
//                mBitmap = BitmapFactory.decodeFile(tempPath, btmapOptions);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
//                imageData = stream.toByteArray();


            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), cameraPermissionArray[0]);

        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(EditItem.this, cameraPermissionArray, CAMERA_PERMISSION_REQUEST_CODE);

    }

    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), storagePermissionArray[0]);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), storagePermissionArray[1]);

        return (result1 == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(EditItem.this, storagePermissionArray, STORAGE_PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    // boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = checkCameraPermission();

                    if (cameraAccepted) {
                        takePicture();
                        Toast.makeText(this, R.string.camera_permission_granted, Toast.LENGTH_SHORT).show();
                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel(getString(R.string.allow_camera_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(cameraPermissionArray,
                                                            CAMERA_PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            } else {
                                Toast.makeText(this, R.string.allow_camera_permission, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                EditItem.this.startActivity(intent);

                            }
                        }

                    }
                }
                break;

            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    // boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storagePermissionAccepted = checkStoragePermission();

                    if (storagePermissionAccepted) {
                        selectFile();
                        Toast.makeText(this, R.string.storage_permission_granted, Toast.LENGTH_SHORT).show();
                    } else {

                        // http://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel(getString(R.string.allow_storage_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(storagePermissionArray,
                                                            STORAGE_PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            } else {
                                Toast.makeText(this, R.string.allow_storage_permission, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                EditItem.this.startActivity(intent);

                            }
                        }

                    }
                }
                break;

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditItem.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }



}
