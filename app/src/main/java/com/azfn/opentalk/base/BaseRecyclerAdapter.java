package com.azfn.opentalk.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azfn.opentalk.R;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener, RecyclerViewHolder.OnItemViewClickListener {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_HEADER = 3;

    private OnItemClickListener onItemClickListener;
    private OnViewClickListener onViewClickListener;

    private LayoutInflater inflater;

    protected View headerView;
    protected View footerView;

    protected List<T> datas;
    protected Context context;

    protected int headerViewCount;
    protected int footerViewCount;
    protected int itemRes;
    protected int defaultRadius = 12;//默认图片圆角大小

    // 分页加载
    private int currentPage;//当前显示第几页
    private int pages;//总共的页数
    private final int NUMS = 10;//每页显示数据条数
    private boolean noMore = false;//没有更多数据了
    private boolean isLoadingData = false;//正在加载数据，不要在请求了

    private MyTaskFinishListener myTaskFinishListener;

    public BaseRecyclerAdapter(Context context, List<T> datas, int itemLayoutResourse) {
        this.datas = datas;
        this.context = context;
        this.itemRes = itemLayoutResourse;
        inflater = LayoutInflater.from(context);
        myTaskFinishListener = new MyTaskFinishListener();
        footerView = inflater.inflate(R.layout.layout_footview, null);//这里默认就让每个recyclerview有footer
        setFooterView(footerView);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_NORMAL:
                view = inflater.inflate(itemRes, parent, false);
                break;
            case TYPE_FOOTER:
                view = footerView;
                break;
            case TYPE_HEADER:
                view = headerView;
                break;
            default:
                view = inflater.inflate(itemRes, parent, false);
                break;
        }
        view.setOnClickListener(this);
        return new RecyclerViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        if (isFooter(position)) {
            bindFooter(holder);
        } else if (isHeader(position)) {
            bindHeader(holder);
        } else {
            final int index = position - headerViewCount;
            if (index < datas.size()) {
                bindViewHolder(index, holder);
                View itemView = holder.getItemView();
                final T data = datas.get(index);
                itemView.setTag(data);
            }
        }
    }

    public String TAG = "BaseRecyclerAdapter";

    @Override
    public int getItemCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size() + headerViewCount + footerViewCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) {
            return TYPE_FOOTER;
        } else if (isHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public int getPosition(T data) {
        return datas.indexOf(data) + headerViewCount;
    }

    protected abstract void bindViewHolder(int position, RecyclerViewHolder holder);

    protected abstract void loadData(int start, int rows, ITaskFinishListener iTaskFinishListener);

    protected void bindHeader(RecyclerViewHolder holder) {
        holder.getItemView().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected void bindFooter(RecyclerViewHolder holder) {
        holder.getItemView().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (!noMore && !isLoadingData && currentPage != 0) {
            isLoadingData = true;
            loadData(currentPage * NUMS, NUMS, myTaskFinishListener);
        }
        if (noMore || datas.size() == 0) {
            holder.getItemView().setVisibility(View.GONE);//TODO 请将加载中改为没有更多数据了哦
        } else {
            holder.getItemView().setVisibility(View.VISIBLE);
        }
    }

    private class MyTaskFinishListener implements ITaskFinishListener {

        @Override
        public void onSuccess(String taskName, Object object) {
            if (object != null && object instanceof List) {
                List<T> list = (List<T>) object;
                if (list != null) {
                    if (list.size() < NUMS) {
                        noMore = true;
                    }

                    if (list.size() == 0 && currentPage == 0) {//TODO 第一页,没有数据时,清空Datas并刷新adapter
                        datas.clear();
                        notifyDataSetChanged();
                    } else if (list.size() > 0) {
                        currentPage++;

                        if (currentPage == 1) {
                            datas.clear();
                        }
                        datas.addAll(list);
                        notifyDataSetChanged();
                    }
                }
            }
            isLoadingData = false;
            loadDataFinish(true);
        }

        @Override
        public void onFail(String taskName) {
            isLoadingData = false;
            loadDataFinish(false);
        }
    }

    /**
     * 首次加载数据
     */
    public void loadDataFirstTime() {
        if (!isLoadingData) {
            noMore = false;
            currentPage = 0;
            loadData(0, NUMS, myTaskFinishListener);
            isLoadingData = true;
        }
    }

    protected boolean isFooter(int position) {
        if (footerView == null) {
            return false;
        }
        return position == getItemCount() - 1;
    }

    protected boolean isHeader(int position) {
        if (headerView == null) {
            return false;
        }
        return position == 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setHeaderView(View headerView) {
        if (this.headerView == null) {
            if (headerView != null) {
                this.headerView = headerView;
                this.headerViewCount++;
                notifyItemInserted(0);
            }
        }
    }

    public void setFooterView(View footerView) {
        if (footerView != null) {
            this.footerView = footerView;
            this.footerViewCount = 1;
        }
    }

    public void removeHeaderView() {
        if (headerViewCount > 0) {
            this.headerView = null;
            this.headerViewCount--;
            this.notifyItemRemoved(0);
        }
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, v.getTag());
        }
    }

    @Override
    public void onViewClick(View view) {
        if (onViewClickListener != null) {
            onViewClickListener.onViewClick(view, view.getTag());
        }
    }

    /**
     * 整个Item点击事件
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T data);
    }

    /**
     * 整个Item中的子view设置点击事件
     * 注意：必须在Adapter中setTag，否则这里data没有数据，目前还没有更好的解决办法，如有请告知
     *
     * @param <T>
     */
    public interface OnViewClickListener<T> {
        void onViewClick(View view, T data);
    }


    private OnDataLoadFinish onDataLoadFinish;

    public void setOnDataLoadFinish(OnDataLoadFinish onDataLoadFinish) {
        this.onDataLoadFinish = onDataLoadFinish;
    }

    public interface OnDataLoadFinish {
        void loadDataFinished(int datasLength);
    }

    private void loadDataFinish(boolean success) {
        if (onDataLoadFinish != null) {
            onDataLoadFinish.loadDataFinished(datas.size());
        }
    }

    public interface ITaskFinishListener{
        void onSuccess(String taskName, Object object);
        void onFail(String taskName);
    }
}
