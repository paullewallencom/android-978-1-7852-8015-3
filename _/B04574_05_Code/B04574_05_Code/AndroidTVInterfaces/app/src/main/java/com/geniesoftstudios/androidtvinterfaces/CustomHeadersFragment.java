package com.geniesoftstudios.androidtvinterfaces;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import java.util.LinkedHashMap;

/**
 * Created by Steven Daniel on 12/06/2015.
 */
public class CustomHeadersFragment extends HeadersFragment {

    private static final String TAG = "CustomHeadersFragment";
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        setHeaderAdapter();
        setCustomPadding();
    }

    private void setHeaderAdapter() {
        mAdapter = new ArrayObjectAdapter();

        LinkedHashMap<Integer, CustomRowsFragment> fragments = ((MainActivity) getActivity()).getFragments();

        int id = 0;
        for (int i = 0; i < fragments.size(); i++) {
            HeaderItem header = new HeaderItem(id, "Category " + i);
            ArrayObjectAdapter innerAdapter = new ArrayObjectAdapter();
            innerAdapter.add(fragments.get(i));
            mAdapter.add(id, new ListRow(header, innerAdapter));
            id++;
        }

        setAdapter(mAdapter);
    }

    private void setCustomPadding() {
        getView().setPadding(0, Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 48), 0);
    }

    private AdapterView.OnItemSelectedListener getDefaultItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = ((ListRow) adapterView.getItemAtPosition(i)).getAdapter().get(0);
                getFragmentManager().beginTransaction().replace(R.id.rows_container, (Fragment) obj).commit();
                ((MainActivity) getActivity()).updateCurrentRowsFragment((CustomRowsFragment) obj);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG,"Nothing has been selected");
            }
        };
    }
}
