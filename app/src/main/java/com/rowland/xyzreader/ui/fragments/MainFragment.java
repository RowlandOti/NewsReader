package com.rowland.xyzreader.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rowland.xyzreader.R;
import com.rowland.xyzreader.data.ArticleLoader;
import com.rowland.xyzreader.data.UpdaterService;
import com.rowland.xyzreader.ui.activities.MainActivity;
import com.rowland.xyzreader.ui.adapters.ArticlesAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    // Logging tracker for this class
    private final String LOG_TAG = MainFragment.class.getSimpleName();

    // ButterKnife injected Views
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    protected RecyclerView mArticleRecycleView;

    // The grid adapter
    protected ArticlesAdapter mArticleAdapter;
    // Refresh state
    private boolean mIsRefreshing = false;
    // Refresh broadcastreceiver
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    public MainFragment() {

    }

    public static MainFragment newInstance(Bundle args) {
        MainFragment fragment = new MainFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Initialize the ViewPager and TabStripLayout
        ButterKnife.bind(this, rootView);
        // Return the view for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Configure the refresh layout look
        mSwipeRefreshLayout.setColorSchemeResources(R.color.apptheme_accent_teal);
        mSwipeRefreshLayout.setProgressViewOffset(true, 100, 400);
        // Get column count
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        // Create new instance of layout manager
        final StaggeredGridLayoutManager mStaggeredLayoutManger = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        // Set the layout manger
        mArticleRecycleView.setLayoutManager(mStaggeredLayoutManger);
        mArticleRecycleView.setHasFixedSize(false);
        // Call is actually only necessary with custom ItemAnimators
        mArticleRecycleView.setItemAnimator(new DefaultItemAnimator());
        // Create new adapter
        mArticleAdapter = new ArticlesAdapter(null, getActivity());
        // Set stable id's
        mArticleAdapter.setHasStableIds(true);
        // Associate RecycleView with adapter
        mArticleRecycleView.setAdapter(mArticleAdapter);
        // Set the refreshlayout's listener
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check which instance we are dealing with
        if (getActivity() instanceof MainActivity) {
            // Set the ToolBar
            ((MainActivity) getActivity()).setToolbar(mToolbar, false, false, R.drawable.logo);
        }
        // Restore any previous states
        if (savedInstanceState != null) {

        } else {
            onRefresh();
        }
        // Initialize the Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mRefreshingReceiver, new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mRefreshingReceiver);
    }

    @Override
    public void onRefresh() {
        getActivity().startService(new Intent(getActivity(), UpdaterService.class));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Create new adapter
        mArticleAdapter.setCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleRecycleView.setAdapter(null);
    }

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }
}
