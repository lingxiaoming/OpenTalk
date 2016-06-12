package com.azfn.opentalk.ui;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.azfn.opentalk.R;
import com.azfn.opentalk.base.BaseRecyclerAdapter;
import com.azfn.opentalk.base.RecyclerViewHolder;
import com.azfn.opentalk.model.Group;
import com.azfn.opentalk.tools.NetworkUtil;
import com.azfn.opentalk.tools.ToastUtils;
import com.azfn.opentalk.tools.loadImageView.ImageLoadHelper;

import java.util.List;


public class GroupAdapter extends BaseRecyclerAdapter<Group> {

    public GroupAdapter(Context context, List<Group> datas) {
        super(context, datas, R.layout.item_group);
    }

    @Override
    protected void bindViewHolder(int position, RecyclerViewHolder holder) {
        ImageView ivAvatar = holder.getView(R.id.img_item_group_avatar, false);
        TextView tvName = holder.getView(R.id.tv_item_group_name, false);
        TextView tvCode = holder.getView(R.id.tv_item_group_code, false);
        CheckBox cbChoice = holder.getView(R.id.cb_item_group, false);

        final Group group = datas.get(position);
        if (group != null) {
            tvName.setText(group.name);
            tvCode.setText(group.code+"");
            cbChoice.setChecked(group.isChecked==1?true:false);
            ImageLoadHelper.getImageLoader().loadCircleImage(context, ivAvatar, group.avatar);
        }
    }

    @Override
    protected void loadData(int start, int rows, ITaskFinishListener iTaskFinishListener) {
        if (NetworkUtil.isNetworkAvailable(context)) {
//            new GetHomeClassListTask(iTaskFinishListener, start, rows).request();
        } else {
            iTaskFinishListener.onFail(null);
            ToastUtils.show(context, "网络异常");
        }
    }
}
