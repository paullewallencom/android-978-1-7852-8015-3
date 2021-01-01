package com.geniesoftstudios.androidtvinterfaces;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Collections;
import java.util.List;

/**
 * Created by Steven Daniel on 12/06/2015.
 */
public class CustomRowsFragment extends RowsFragment {

    private final int NUM_ROWS = 5;
    private final int NUM_COLS = 15;

    private ArrayObjectAdapter rowsAdapter;
    private CardPresenter cardPresenter;

    private static final int HEADERS_FRAGMENT_SCALE_SIZE = 300;
    private static final String TAG = "CustomRowsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        int marginOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADERS_FRAGMENT_SCALE_SIZE, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.rightMargin -= marginOffset;
        v.setLayoutParams(params);
        v.setBackgroundColor(Color.DKGRAY);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        loadRows();
        setCustomPadding();
    }

    private void loadRows() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        cardPresenter = new CardPresenter();
        List<Movie> list = MovieList.setupMovies();

        int i;
        for (i = 0; i < NUM_ROWS; i++) {
            if (i != 0) Collections.shuffle(list);
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < NUM_COLS; j++) {
                listRowAdapter.add(list.get(j % 5));
            }
            HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i]);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }
        setAdapter(rowsAdapter);
    }

    private void setCustomPadding() {
        getView().setPadding(Utils.convertDpToPixel(getActivity(), -24),
                Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 48), 0);
    }

    public void refresh() {
        getView().setPadding(Utils.convertDpToPixel(getActivity(), -24),
                Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 300), 0);
    }
}
