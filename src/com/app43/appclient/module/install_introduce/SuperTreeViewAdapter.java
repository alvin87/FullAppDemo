package com.app43.appclient.module.install_introduce;

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.app43.appclient.module.install_introduce.TreeViewAdapter.TreeNode;
import com.app43.appclient.module.launch.InstallRecommendActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SuperTreeViewAdapter extends BaseExpandableListAdapter {

    Activity activity;

    public static class SuperTreeNode {
        public Object parent;
        // 二级树形菜单的结构体
        public List<TreeViewAdapter.TreeNode> childs = new ArrayList<TreeViewAdapter.TreeNode>();
    }

    private List<SuperTreeNode> superTreeNodes = new ArrayList<SuperTreeNode>();

    // private OnChildClickListener stvClickEvent;// 外部回调函数

    public SuperTreeViewAdapter(Activity activity
    // ,OnChildClickListener stvClickEvent
    ) {
        this.activity = activity;
        // this.stvClickEvent = stvClickEvent;
    }

    public List<SuperTreeNode> GetTreeNode() {
        return superTreeNodes;
    }

    public void UpdateTreeNode(List<SuperTreeNode> node) {
        superTreeNodes = node;
    }

    public void RemoveAll() {
        superTreeNodes.clear();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return superTreeNodes.get(groupPosition).childs.get(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return superTreeNodes.get(groupPosition).childs.size();
    }

    public ExpandableListView getExpandableListView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                InstallRecommendActivity.GroupHeight);
        ExpandableListView superTreeView = new ExpandableListView(activity);
        superTreeView.setLayoutParams(lp);
        superTreeView.setDividerHeight(DisplayUtils.convertDIP2PX(activity, 1));
        int color = 0xc0c0c0;
        ColorDrawable colorDrawable = new ColorDrawable(color);
        superTreeView.setDivider(colorDrawable);
        superTreeView.setCacheColorHint(0x000000);
        return superTreeView;
    }

    /**
     * 三层树结构中的第二层是一个ExpandableListView
     */
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO 三级item点击监听
        final ExpandableListView treeView = getExpandableListView();
        final TreeViewAdapter treeViewAdapter = new TreeViewAdapter(activity,
                0, treeView, childPosition);
        List<TreeNode> tmp = treeViewAdapter.GetTreeNode();// 临时变量取得TreeViewAdapter的TreeNode集合，可为空
        final TreeNode treeNode = (TreeNode) getChild(groupPosition,
                childPosition);
        tmp.add(treeNode);
        treeViewAdapter.UpdateTreeNode(tmp);
        treeView.setScrollbarFadingEnabled(true);
        treeView.setAdapter(treeViewAdapter);
        treeView.setGroupIndicator(null);

        // 关键点：取得选中的二级树形菜单的父子节点,结果返回给外部回调函数
        // treeView.setOnChildClickListener(this.stvClickEvent);
        /**
         * 关键点：第二级菜单展开时通过取得节点数来设置第三级菜单的大小
         */
        treeView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, (treeNode.childs
                                .size())
                                * InstallRecommendActivity.ChildHeight
                                + InstallRecommendActivity.GroupHeight);
                treeView.setLayoutParams(lp);
            }
        });

        /**
         * 第二级菜单回收时设置为标准Item大小
         */
        treeView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        InstallRecommendActivity.GroupHeight);
                treeView.setLayoutParams(lp);
            }
        });

        return treeView;
    }

    /**
     * 三级树结构中的首层是TextView,用于作为title
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        TextView textView = TreeViewAdapter.getTextView(activity);
        textView.setText(getGroup(groupPosition).toString());
        textView.setPadding(InstallRecommendActivity.PaddingLeft, 0, 0, 0);
        textView.setBackgroundColor(Color.WHITE);
        return textView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getGroup(int groupPosition) {
        return superTreeNodes.get(groupPosition).parent;
    }

    public int getGroupCount() {
        return superTreeNodes.size();
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
