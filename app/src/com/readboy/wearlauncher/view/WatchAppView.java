package com.readboy.wearlauncher.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.readboy.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.readboy.wearlauncher.LauncherApplication;
import com.readboy.wearlauncher.R;
import com.readboy.wearlauncher.application.AppInfo;
import com.readboy.wearlauncher.utils.WatchController;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by 1 on 2017/5/6.
 */

public class WatchAppView extends RelativeLayout implements WatchController.ClassDisableChangedCallback {

    Context mContext;
    private LauncherApplication mApplication;
    RecyclerView mRecyclerView;
    AppRecyclerAdapter mAppRecyclerAdapter;
    ImageView mImageView;
    private LayoutInflater mInflater;
    WatchController mWatchController;
    private MyGridLayoutManager mLayoutManager;
    private HeaderAndFooterWrapper mWrapperAdapter;
    List<AppInfo> mAppList = new ArrayList<AppInfo>();
    private OnItemClickListener mOnItemClickListener;

    public WatchAppView(Context context) {
        this(context, null);
    }

    public WatchAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mApplication = (LauncherApplication) context.getApplicationContext();
        mInflater = LayoutInflater.from(context);
        mWatchController = mApplication.getWatchController();
    }

    public void moveToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void refreshData(ArrayList<AppInfo> data) {
        mAppList.clear();
        mAppList = data;
        mAppRecyclerAdapter.notifyDataSetChanged();
    }

    public AppInfo getAppInfo(int position) {
        return mRecyclerView != null ? mAppList.get(position) : null;
    }

    public void setClassDisableShow(boolean show) {
        if (show) {
            mImageView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mImageView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.imageView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_vid);
        mLayoutManager = new MyGridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, 0);
        mAppRecyclerAdapter = new AppRecyclerAdapter();
        mRecyclerView.setAdapter(mAppRecyclerAdapter);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    if (parent.getChildAdapterPosition(view) % 2 == 1) {
                            outRect.set(28,0,0,0);
                    }else if(parent.getChildAdapterPosition(view) % 2 == 0){
                            outRect.set(16,0,0,0);
                    }
                }
            }
        };
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mWatchController.addClassDisableChangedCallback(this);
        mRecyclerView.requestFocus();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mWatchController.removeClassDisableChangedCallback(this);
    }

    @Override
    public void onClassDisableChange(boolean show) {
        setClassDisableShow(show);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void addHeader(int resId) {
        View view = LayoutInflater.from(mContext).inflate(resId, null, false);
        addHeader(view);
    }

    public void addHeader(View view) {
        if (mWrapperAdapter == null) {
            mWrapperAdapter = new HeaderAndFooterWrapper(mAppRecyclerAdapter);
        }
        mWrapperAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mWrapperAdapter);
    }


    class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.ViewHolder> {
        class ViewHolder extends RecyclerView.ViewHolder {
            protected IconTextView mIconTextView;

            ViewHolder(View itemView) {
                super(itemView);
                mIconTextView = (IconTextView) itemView.findViewById(R.id.app_icon_tvid);
            }
        }

        IconCache mTconCache;

        public AppRecyclerAdapter() {
            mTconCache = ((LauncherApplication) LauncherApplication.getApplication()).getIconCache();
        }

        @Override
        public int getItemCount() {
            return mAppList == null ? 0 : mAppList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            AppInfo appInfo = mAppList.get(position);
            if (holder.mIconTextView == null) {
                holder.mIconTextView = (IconTextView) findViewById(R.id.app_icon_tvid);
            }
            if (holder.mIconTextView != null) {
                holder.mIconTextView.applyFromShortcutInfo(appInfo, mTconCache);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.view_app_grid_item, parent, false));
            viewHolder.mIconTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.getAdapterPosition() - mWrapperAdapter.getHeadersCount());
                }
            });
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
