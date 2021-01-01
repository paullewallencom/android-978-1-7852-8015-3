package com.geniesoftstudios.androidtvinterfaces;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Steven Daniel on 12/06/2015.
 */
public class TVSearchFragment extends SearchFragment implements SearchFragment.SearchResultProvider {

    private static final String TAG = "TVSearchFragment";
    private static final int SEARCH_DELAY_MS = 300;
    private ArrayObjectAdapter mRowsAdapter;
    private Handler mHandler = new Handler();
    private SearchRunnable mDelayedLoad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        mDelayedLoad = new SearchRunnable();
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        mRowsAdapter.clear();
        if (!TextUtils.isEmpty(newQuery)) {
            mDelayedLoad.setSearchQuery(newQuery);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mRowsAdapter.clear();
        if (!TextUtils.isEmpty(query)) {
            mDelayedLoad.setSearchQuery(query);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    private class SearchRunnable implements Runnable {

        private String query;
        public void setSearchQuery(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            mRowsAdapter.clear();
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(new CardPresenter());
            adapter.addAll(0, MovieList.list);
            HeaderItem header = new HeaderItem(0, getResources().getString(R.string.search_results));
            mRowsAdapter.add(new ListRow(header, adapter));
        }
    }
}
