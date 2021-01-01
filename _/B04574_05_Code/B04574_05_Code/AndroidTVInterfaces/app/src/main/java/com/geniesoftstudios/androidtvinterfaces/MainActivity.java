package com.geniesoftstudios.androidtvinterfaces;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v17.leanback.widget.SearchOrbView;
import android.view.View;
import java.util.LinkedHashMap;

/**
 * Created by Steven Daniel on 12/06/2015.
 */
public class MainActivity extends Activity {

    private SearchOrbView orbView;

    private CustomHeadersFragment headersFragment;
    private CustomRowsFragment rowsFragment;

    private final int CATEGORIES_NUMBER = 5;
    private LinkedHashMap<Integer, CustomRowsFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        // Check to see if we are using an Android TV Device
        if (isRunningOnTVDevice()) {
            orbView = (SearchOrbView) findViewById(R.id.custom_search_orb);
            orbView.setOrbColor(getResources().getColor(R.color.search_opaque));
            orbView.bringToFront();
            orbView.setOnOrbClickedListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), TVSearchActivity.class);
                    startActivity(intent);
                }
            });

            fragments = new LinkedHashMap<Integer, CustomRowsFragment>();

            for (int i = 0; i < CATEGORIES_NUMBER; i++) {
                CustomRowsFragment fragment = new CustomRowsFragment();
                fragments.put(i, fragment);
            }

            headersFragment = new CustomHeadersFragment();
            rowsFragment = fragments.get(0);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .replace(R.id.header_container, headersFragment, "CustomHeadersFragment")
                    .replace(R.id.rows_container, rowsFragment, "CustomRowsFragment");
            transaction.commit();
        }
    }

    // method to check if we are using an Android TV Device
    private boolean isRunningOnTVDevice() {
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)
            return true;
        else return false;
    }
    public LinkedHashMap<Integer, CustomRowsFragment> getFragments() {
        return fragments;
    }

    public void updateCurrentRowsFragment(CustomRowsFragment fragment) {
        rowsFragment = fragment;
    }
}
