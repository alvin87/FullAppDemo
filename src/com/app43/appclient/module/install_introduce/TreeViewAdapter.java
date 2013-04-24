package com.app43.appclient.module.install_introduce;

/**
 * 第三级的高度是可以通过图片的高度来调整
 */
import com.alvin.api.utils.LogOutputUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.launch.InstallRecommendActivity;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TreeViewAdapter extends BaseExpandableListAdapter {

    Activity activity;
    int groupIndex;
    ExpandableListView expandListView;

    static public class TreeNode {
        public Object parent;
        public List<Object> childs = new ArrayList<Object>();
    }

    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
    Context parentContext;

    public TreeViewAdapter(Activity activity, int myPaddingLeft,
            ExpandableListView listView, int index) {
        groupIndex = index;
        this.activity = activity;
        expandListView = listView;
    }

    public List<TreeNode> GetTreeNode() {
        return treeNodes;
    }

    public void UpdateTreeNode(List<TreeNode> nodes) {
        treeNodes = nodes;
    }

    public void RemoveAll() {
        treeNodes.clear();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return treeNodes.get(groupPosition).childs.get(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return treeNodes.get(groupPosition).childs.size();
    }

    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final int group = groupPosition;
        final int child = childPosition;
        final String ss = getChild(groupPosition, childPosition).toString();
        convertView = activity.getLayoutInflater().inflate(
                R.layout.install_recommend_list_item_parent, null);
        TextView textView = (TextView) convertView
                .findViewById(R.id.install_introduce_appname);
        textView.setText(getChild(groupPosition, childPosition).toString());
        LinearLayout layout = (LinearLayout) convertView
                .findViewById(R.id.install_introduce_bottom);
        layout.setVisibility(View.GONE);
        CheckBox checkBox = (CheckBox) convertView
                .findViewById(R.id.install_introduce_select);
        checkBox.setChecked(false);

        // TODO 点击第三级菜单触发没写
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    LogOutputUtils.i(ss, "" + isChecked);
                } else {
                    LogOutputUtils.i(ss, "" + isChecked);
                }
            }
        });
        return convertView;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        convertView = activity.getLayoutInflater().inflate(
                R.layout.install_recommend_list_item_parent, null);
        TextView textView = (TextView) convertView
                .findViewById(R.id.install_introduce_appname);
        textView.setText(getGroup(groupPosition).toString());
        ImageButton imageButton = (ImageButton) convertView
                .findViewById(R.id.install_introduce_more);

        // TODO 点击第二级菜单触发没写
        final int index = groupPosition;
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (expandListView.isGroupExpanded(index)) {
                    expandListView.collapseGroup(index);
                } else {
                    expandListView.expandGroup(index);
                }
            }
        });
        final String ss = getGroup(groupPosition).toString();
        CheckBox checkBox = (CheckBox) convertView
                .findViewById(R.id.install_introduce_select);
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    LogOutputUtils.i(ss, "" + isChecked);
                } else {
                    LogOutputUtils.i(ss, "" + isChecked);
                }
            }
        });
        return convertView;
    }

    static public TextView getTextView(Context context) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                InstallRecommendActivity.TitleHeight);

        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        return textView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getGroup(int groupPosition) {
        return treeNodes.get(groupPosition).parent;
    }

    public int getGroupCount() {
        return treeNodes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
