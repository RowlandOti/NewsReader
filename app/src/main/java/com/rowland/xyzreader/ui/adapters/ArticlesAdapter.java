package com.rowland.xyzreader.ui.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rowland.xyzreader.R;
import com.rowland.xyzreader.data.ArticleLoader;
import com.rowland.xyzreader.data.ItemsContract;
import com.rowland.xyzreader.ui.ImageLoaderHelper;
import com.rowland.xyzreader.ui.activities.MainActivity;
import com.rowland.xyzreader.ui.widgets.DynamicHeightNetworkImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Oti Rowland on 1/22/2016.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    // Logging tracker for this class
    private final String LOG_TAG = ArticlesAdapter.class.getSimpleName();

    // Cursor containing data
    private Cursor mCursor;
    // The container Activity
    private FragmentActivity mActivity;

    public ArticlesAdapter(Cursor cursor, FragmentActivity activity) {
        // Acquire the cursor
        this.mCursor = cursor;
        // Acquire the containing activity
        this.mActivity = activity;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Layout to inflate for CustomViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        // Create a viewholder object
        final ViewHolder viewHolder = new ViewHolder(v);
        // Return new new ViewHolder
        return new ViewHolder(v);
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Bind the data to the view holder
        holder.bindTo(position);
    }

    @Override
    public int getItemCount() {
        // Check size of List first
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // ButterKnife injected views
        @Bind(R.id.card_view)
        CardView cardView;
        @Bind(R.id.thumbnail)
        DynamicHeightNetworkImageView thumbnailView;
        @Bind(R.id.article_title)
        TextView titleView;
        @Bind(R.id.article_subtitle)
        TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindTo(final int position) {
            mCursor.moveToPosition(position);
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            subtitleView.setText(DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR));
            thumbnailView.setImageUrl(mCursor.getString(ArticleLoader.Query.THUMB_URL), ImageLoaderHelper.getInstance(mActivity).getImageLoader());
            thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
            cardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Check which instance we are dealing with
                    if (mActivity instanceof MainActivity) {
                        // Execute Callback
                        Log.d(LOG_TAG, "SELECTED_POSITION: " + position);
                        mActivity.startActivity(new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(position)));
                    }
                }
            });
        }
    }

}
