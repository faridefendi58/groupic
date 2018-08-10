package id.web.jagungbakar.groupedpicture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import id.web.jagungbakar.groupedpicture.R;
import id.web.jagungbakar.groupedpicture.controllers.PictureController;

public class LibraryFragment extends Fragment {

    private View rootView;
    private ListView library_list_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_library, null);

        initUi();

        Object pictures = PictureController.getInstance().getItems();
        Log.e(getActivity().getClass().getSimpleName(), "pictures : "+ pictures.toString());
        return rootView;
    }

    private void initUi() {
        library_list_view = (ListView) rootView.findViewById(R.id.library_list_view);
    }
}
