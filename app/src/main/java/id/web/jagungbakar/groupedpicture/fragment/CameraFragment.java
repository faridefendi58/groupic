package id.web.jagungbakar.groupedpicture.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import id.web.jagungbakar.groupedpicture.R;
import id.web.jagungbakar.groupedpicture.ImageListAdapter;

public class CameraFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final String LOG_TAG = "MainActivity";

    Button button;
    Button button_done;
    Button button_remove;
    Button btn_minimize;
    ImageView imageView;
    ListView image_list_view;
    LinearLayout list_container;

    private ArrayList<HashMap<String,Bitmap>> imageList = new ArrayList<>();
    private ArrayList<HashMap<String,String>> imageListAttributes = new ArrayList<>();
    private int current_preview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_camera,
                container, false);

        verifyStoragePermissions(getActivity());

        button = (Button) rootView.findViewById(R.id.btn_take_picture);
        button_done = (Button) rootView.findViewById(R.id.btn_done);
        button_remove = (Button) rootView.findViewById(R.id.btn_remove);
        btn_minimize = (Button) rootView.findViewById(R.id.btn_minimize);
        imageView = (ImageView) rootView.findViewById(R.id.result_image);
        image_list_view = (ListView) rootView.findViewById(R.id.image_list_view);
        list_container = (LinearLayout) rootView.findViewById(R.id.list_container);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/

            }
        });

        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage(getResources().getString(R.string.confirm_delete))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                _remove_item();
                                Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.delete_success_message),
                                        Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_container.setVisibility(View.GONE);
                btn_minimize.setVisibility(View.VISIBLE);
            }
        });

        btn_minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_container.setVisibility(View.VISIBLE);
                btn_minimize.setVisibility(View.GONE);
            }
        });

        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            HashMap<String,Bitmap> map = new HashMap<>();
            map.put("img", imageBitmap);
            imageList.add(map);

            HashMap<String,String> mapAttr = new HashMap<>();
            mapAttr.put("mCurrentFileName", mCurrentFileName);
            mapAttr.put("current_path", mCurrentPhotoPath);
            imageListAttributes.add(mapAttr);

            current_preview = imageList.size() - 1;

            button_done.setVisibility(View.VISIBLE);
            button_remove.setVisibility(View.VISIBLE);
            list_container.setVisibility(View.VISIBLE);

            rebuildTheImageList();
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imageView.setImageBitmap(bitmap);

            HashMap<String,Bitmap> map = new HashMap<>();
            map.put("img", bitmap);
            imageList.add(map);

            HashMap<String,String> mapAttr = new HashMap<>();
            mapAttr.put("file_name", mCurrentFileName);
            mapAttr.put("file_path", mCurrentPhotoPath);
            imageListAttributes.add(mapAttr);

            current_preview = imageList.size() - 1;

            button_done.setVisibility(View.VISIBLE);
            button_remove.setVisibility(View.VISIBLE);
            list_container.setVisibility(View.VISIBLE);

            rebuildTheImageList();
        }
    }

    private void rebuildTheImageList() {
        ImageListAdapter adapter = new ImageListAdapter(getActivity(), imageList);
        image_list_view.setAdapter(adapter);
        imageListViewListener();
    }

    private void imageListViewListener() {
        image_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageView.setImageBitmap(imageList.get(position).get("img"));
                current_preview = position;
            }
        });
    }

    private void _remove_item() {
        imageList.remove(current_preview);
        imageListAttributes.remove(current_preview);
        int size = imageList.size();
        if (size == 0) {
            button_done.setVisibility(View.GONE);
            button_remove.setVisibility(View.GONE);
            list_container.setVisibility(View.GONE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_holder_256));
        } else {
            rebuildTheImageList();
            imageView.setImageBitmap(imageList.get(size-1).get("img"));
        }
    }

    public void saveImage() {
        imageList.clear();
        imageListAttributes.clear();

        button_done.setVisibility(View.GONE);
        button_remove.setVisibility(View.GONE);
        list_container.setVisibility(View.GONE);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_holder_256));

        //remove the backup
        /*for (int i = 0; i < imageListAttributes.size(); i++) {
            try {
                File file = new File(imageListAttributes.get(i).get("file_path"));
                boolean deleted = file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        Toast.makeText(
                getContext(),
                getResources().getString(R.string.save_success_message),
                Toast.LENGTH_SHORT).show();
    }

    String mCurrentPhotoPath;
    String mCurrentFileName;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp + "_";
        // cachec storage dir, deleted on uninstalled app
        //File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "GroupedPicture" + File.separator);

        if (!storageDir.mkdirs()) {
            Log.e(getActivity().getClass().getSimpleName(), "Directory not created");
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentFileName = image.getName();
        //Log.e(getActivity().getClass().getSimpleName(),"mCurrentPhotoPath : "+ mCurrentPhotoPath);
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    private Intent takePictureIntent;
    private File photoFile = null;

    private void dispatchTakePictureIntent() {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                //Log.e(LOG_TAG, "photoUri : "+ photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
