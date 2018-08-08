package id.web.jagungbakar.groupedpicture.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import id.web.jagungbakar.groupedpicture.R;
import id.web.jagungbakar.groupedpicture.ImageListAdapter;

public class CameraFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Button button;
    Button button_done;
    Button button_remove;
    ImageView imageView;
    ListView image_list_view;
    LinearLayout list_container;

    private ArrayList<HashMap<String,Bitmap>> imageList = new ArrayList<>();
    private int current_preview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_camera,
                container, false);

        button = (Button) rootView.findViewById(R.id.btn_take_picture);
        button_done = (Button) rootView.findViewById(R.id.btn_done);
        button_remove = (Button) rootView.findViewById(R.id.btn_remove);
        imageView = (ImageView) rootView.findViewById(R.id.result_image);
        image_list_view = (ListView) rootView.findViewById(R.id.image_list_view);
        list_container = (LinearLayout) rootView.findViewById(R.id.list_container);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

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

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                HashMap<String,Bitmap> map = new HashMap<>();
                map.put("img", bitmap);
                imageList.add(map);

                current_preview = imageList.size() - 1;

                imageView.setImageBitmap(bitmap);
                button_done.setVisibility(View.VISIBLE);
                button_remove.setVisibility(View.VISIBLE);
                list_container.setVisibility(View.VISIBLE);

                rebuildTheImageList();
            }
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
        int size = imageList.size();
        if (size == 0) {
            button_done.setVisibility(View.GONE);
            button_remove.setVisibility(View.GONE);
            list_container.setVisibility(View.GONE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_holder));
        } else {
            rebuildTheImageList();
            imageView.setImageBitmap(imageList.get(size-1).get("img"));
        }
    }
}
