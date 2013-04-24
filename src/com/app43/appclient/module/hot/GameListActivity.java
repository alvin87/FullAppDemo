package com.app43.appclient.module.hot;

import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListActivity extends Activity {

    int iconID[] = { R.drawable.game1, R.drawable.game2, R.drawable.game3,
            R.drawable.game4, R.drawable.game5, R.drawable.game6,
            R.drawable.game7, R.drawable.game8, R.drawable.game9 };
    int textId[] = { R.string.game1, R.string.game2, R.string.game3,
            R.string.game4, R.string.game5, R.string.game6, R.string.game7,
            R.string.game8, R.string.game9 };
    String urlString[] = { SettingsUtils.URL_GAME_1, SettingsUtils.URL_GAME_2,
            SettingsUtils.URL_GAME_3, SettingsUtils.URL_GAME_4, SettingsUtils.URL_GAME_5,
            SettingsUtils.URL_GAME_6, SettingsUtils.URL_GAME_7, SettingsUtils.URL_GAME_8,
            SettingsUtils.URL_GAME_9 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_introduce_list);
        GridView gridView = (GridView) findViewById(R.id.game_gridview);
        GameAdapter gameAdapter = new GameAdapter(this);

        gridView.setAdapter(gameAdapter);
    }

    private class GameAdapter extends BaseAdapter {
        Activity activity;

        public GameAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return iconID.length;
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GameHolder gameHolder;
            final int index = position;
            if (convertView == null) {
                gameHolder = new GameHolder();
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.hot_grid_item, null);
                gameHolder.icon = (ImageView) convertView
                        .findViewById(R.id.hot_grid_item_image);
                gameHolder.name = (TextView) convertView
                        .findViewById(R.id.hot_grid_item_text);
                convertView.setTag(gameHolder);
            } else {
                gameHolder = (GameHolder) convertView.getTag();
            }
            gameHolder.icon.setImageResource(iconID[position]);
            gameHolder.name.setText(textId[position]);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, HotListActivity.class);
                    intent.putExtra(SettingsUtils.PAGEURL, urlString[index]);
                    intent.putExtra(SettingsUtils.CATEGORY_NAME, textId[index]);
                    activity.startActivity(intent);
                }
            });
            return convertView;
        }

    }

    private static class GameHolder {
        public ImageView icon = null;
        public TextView name = null, size = null, summary = null;
    }
}
