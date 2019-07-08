package com.readboy.wearlauncher.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readboy.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.readboy.wearlauncher.LauncherApplication;
import com.readboy.wearlauncher.R;
import com.readboy.wearlauncher.application.AppInfo;
import com.readboy.wearlauncher.application.AppsLoader;
import com.readboy.wearlauncher.utils.Utils;
import com.readboy.wearlauncher.view.IconCache;
import com.readboy.wearlauncher.view.IconTextView;
import com.readboy.wearlauncher.view.MyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


/**
 * @author lxx
 * @date 2019/2/17
 */
public class WatchAppFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<AppInfo>> {
    private static final int LOADER_ID = 0x10;
    private RecyclerView mRecyclerView;
    private AppRecyclerAdapter mAppRecyclerAdapter;
    private MyGridLayoutManager mLayoutManager;
    private HeaderAndFooterWrapper mWrapperAdapter;
    private boolean isViewCreated;  //防止空指针，setUserVisibleHint()比onCreateView()快
    List<AppInfo> mAppList = new ArrayList<AppInfo>();
    private View view;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isViewCreated) {
            return;
        }
        if (!isVisibleToUser) {
            if (mRecyclerView != null) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.watch_app_recyclerview, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_vid);
        mLayoutManager = new MyGridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, 0);
        mAppRecyclerAdapter = new AppRecyclerAdapter();
        mRecyclerView.setAdapter(mAppRecyclerAdapter);
        final float mZoom = getContext().getResources().getDisplayMetrics().widthPixels / 240.0f;
        final int oddMagrinLeft = (int) (28 * mZoom);
        final int evenMarginLeft = (int) (16 * mZoom);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    if (parent.getChildAdapterPosition(view) % 2 == 1) {
                        outRect.set(oddMagrinLeft, 0, 0, 0);
                    } else if (parent.getChildAdapterPosition(view) % 2 == 0) {
                        outRect.set(evenMarginLeft, 0, 0, 0);
                    }
                }
            }
        };
        mRecyclerView.addItemDecoration(itemDecoration);
        addHeader();
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadApps(false);
        isViewCreated = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void addHeader() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.title_common, null, false);
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(getString(R.string.apps_title));
        mWrapperAdapter = new HeaderAndFooterWrapper(mAppRecyclerAdapter);
        mWrapperAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mWrapperAdapter);
    }

    public void refreshData(ArrayList<AppInfo> data) {
        mAppList.clear();
        mAppList.addAll(data);
        mAppRecyclerAdapter.notifyDataSetChanged();
    }

    public AppInfo getAppInfo(int position) {
        return mRecyclerView != null ? mAppList.get(position) : null;
    }

    public void loadApps(boolean reLoad) {
        if (!reLoad) {
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else if (isViewCreated) {
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<ArrayList<AppInfo>> onCreateLoader(int id, Bundle args) {
        if (id != LOADER_ID) {
            return null;
        }
        return new AppsLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppInfo>> loader, ArrayList<AppInfo> appInfos) {
        if (loader.getId() == LOADER_ID) {
            refreshData(appInfos);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppInfo>> loader) {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.view_app_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final AppInfo appInfo = mAppList.get(position);
            if (holder.mIconTextView == null) {
                holder.mIconTextView = (IconTextView) holder.itemView.findViewById(R.id.app_icon_tvid);
            }
            if (holder.mIconTextView != null) {
                holder.mIconTextView.applyFromShortcutInfo(appInfo, mTconCache);
            }
            holder.mIconTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.startActivity(getContext(), appInfo.mPackageName, appInfo.mClassName);
                }
            });
        }


        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
