package com.app43.appclient.module.install_introduce;

import com.alvin.api.utils.AsyncImageLoader;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.adapter.BaseOnClickListItemAdapter.ViewHolder;
import com.app43.appclient.module.adapter.ProgressListViewAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 先不用第三极餐单的.
 * 
 * 项目名称：mac_app43 类名称：NewSuperTreeViewAdapter 类描述： 创建人：pc 创建时间：2012-2-8
 * 下午3:10:40 修改人：pc 修改时间：2012-2-8 下午3:10:40 修改备注：
 * 
 * @version
 * 
 */
public class SuperTreeViewNewAdapter extends BaseExpandableListAdapter {
    Context context;
    List<InfoGroup> infoGroups = new ArrayList<InfoGroup>();
    Drawable defaultDrawable;
    CheckBoxSelectListener checkBoxSelect;

    public SuperTreeViewNewAdapter(Context context, List<InfoGroup> groups,
            CheckBoxSelectListener checkBoxSelect) {
        this.context = context;
        infoGroups = groups;
        this.checkBoxSelect = checkBoxSelect;
        defaultDrawable = context.getResources().getDrawable(
                R.drawable.app_thumb_default_80_60);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final int igroupPosition = groupPosition;
        final int ichildPosition = childPosition;
        final App app = infoGroups.get(igroupPosition).getAppList()
                .get(ichildPosition);
        final boolean isSel = app.isSelected() == 1 ? true : false;
        ViewHolder viewHold = null;
        String iconUrl = infoGroups.get(groupPosition).getAppList()
                .get(childPosition).getImage();
        convertView = LayoutInflater.from(context).inflate(
                R.layout.recommend_app_list_item, null);
        viewHold = new ViewHolder();
        viewHold.icon = (ImageView) convertView
                .findViewById(R.id.recommend_app_icon);
        viewHold.name = (TextView) convertView
                .findViewById(R.id.recommend_app_name);
        viewHold.version = (TextView) convertView
                .findViewById(R.id.recommend_app_version_name);
        viewHold.size = (TextView) convertView
                .findViewById(R.id.recommend_app_size);
        viewHold.summary = (TextView) convertView
                .findViewById(R.id.recommend_app_introduce);
        viewHold.imageButton = (Button) convertView
                .findViewById(R.id.recommend_app_button);
        viewHold.progressBar = (ProgressBar) convertView
                .findViewById(R.id.loadProgressBar);
        viewHold.checkBox = (CheckBox) convertView
                .findViewById(R.id.recommend_app_select_button);
        viewHold.state = (TextView) convertView
                .findViewById(R.id.download_state);
        viewHold.safeImageView = (ImageView) convertView
                .findViewById(R.id.recommend_app_safe);

        if (app.getSafe() == 1) {
            viewHold.safeImageView.setVisibility(View.VISIBLE);
        } else {
            viewHold.safeImageView.setVisibility(View.GONE);
        }
        viewHold.childPosition = ichildPosition;
        viewHold.progressBar.setVisibility(View.GONE);
        viewHold.version.setVisibility(View.GONE);
        viewHold.state.setVisibility(View.GONE);
        viewHold.imageButton.setVisibility(View.GONE);
        viewHold.checkBox.setVisibility(View.VISIBLE);
        viewHold.icon.setImageResource(R.drawable.app_thumb_default_80_60);
        if (iconUrl != null || !iconUrl.equals("")) {
            viewHold.iconurl = iconUrl;

            Drawable drawable = AsyncImageLoader.loadBitmap(viewHold.iconurl);
            if (drawable != null) {
                viewHold.icon.setImageDrawable(drawable);
            } else {
                ProgressListViewAdapter.AsyncImage(viewHold.iconurl, viewHold);
            }
        }

        viewHold.summary.setText(app.getSummary());// 介绍
        viewHold.name.setText(app.getTitle());// 名称
        viewHold.size.setText(app.getSize());// 大小
        viewHold.checkBox.setChecked(isSel);// checkbox

        viewHold.checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (infoGroups.get(igroupPosition).getAppList()
                        .get(ichildPosition).isSelected() == 1) {
                    infoGroups.get(igroupPosition).getAppList()
                            .get(ichildPosition).setSelected(0);
                } else {
                    infoGroups.get(igroupPosition).getAppList()
                            .get(ichildPosition).setSelected(1);
                }

                checkBoxSelect.checkBoxSelOnClick(infoGroups
                        .get(igroupPosition).getAppList().get(ichildPosition),
                        igroupPosition, ichildPosition);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return infoGroups.get(groupPosition).getAppList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return infoGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        FatherViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new FatherViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.install_recommend_list_group, null);
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.gropu_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FatherViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(infoGroups.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    protected class FatherViewHolder {
        TextView textView;
    }

    public void updateExpandList(List<InfoGroup> groups) {
        infoGroups = groups;
        // for (int i = 0; i < infoGroups.size(); i++) {
        // for (int j = 0; j < infoGroups.get(i).getAppList().size(); j++) {
        // infoGroups.get(i).getAppList().get(j).setSelected(0);
        // }
        // }

        notifyDataSetChanged();
    }

    /**
     * 只是装机推荐监听checkBox的点击
     */
    public interface CheckBoxSelectListener {
        public void checkBoxSelOnClick(App app, int fatherPosition,
                int childPosition);
    }
}
