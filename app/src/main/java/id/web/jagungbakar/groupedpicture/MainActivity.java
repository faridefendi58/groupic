package id.web.jagungbakar.groupedpicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import id.web.jagungbakar.groupedpicture.fragment.CameraFragment;
import id.web.jagungbakar.groupedpicture.fragment.LibraryFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private android.support.v7.app.ActionBar actionBar;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_library:
                fragment = new LibraryFragment();
                actionBar.setTitle(getResources().getString(R.string.title_library));
                break;

            case R.id.navigation_camera:
                fragment = new CameraFragment();
                actionBar.hide();
                break;

            case R.id.navigation_notifications:
                break;
        }

        return loadFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new CameraFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        actionBar = getSupportActionBar();

        navigation.setSelectedItemId(R.id.navigation_camera);
        navigation.getMenu().getItem(1).setChecked(true);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
