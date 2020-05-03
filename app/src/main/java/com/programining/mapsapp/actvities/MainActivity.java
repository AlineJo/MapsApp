package com.programining.mapsapp.actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.programining.mapsapp.R;
import com.programining.mapsapp.fragments.DisplayLocationFragment;
import com.programining.mapsapp.fragments.GetCurrentUserLocationFragment;
import com.programining.mapsapp.fragments.SelectLocationFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeFragmentTo(new GetCurrentUserLocationFragment(), GetCurrentUserLocationFragment.class.getSimpleName());
    }

    private void changeFragmentTo(Fragment fragmentToDisplay, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (fm.findFragmentByTag(fragmentTag) == null) {
            ft.addToBackStack(fragmentTag);
        }
        ft.replace(R.id.fl_host, fragmentToDisplay, fragmentTag);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragments, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_display:
                changeFragmentTo(new DisplayLocationFragment(), DisplayLocationFragment.class.getSimpleName());
                break;
            case R.id.menu_select:
                changeFragmentTo(new SelectLocationFragment(), SelectLocationFragment.class.getSimpleName());
                break;
            case R.id.menu_current:
                changeFragmentTo(new GetCurrentUserLocationFragment(), GetCurrentUserLocationFragment.class.getSimpleName());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
