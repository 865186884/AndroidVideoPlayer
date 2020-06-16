package com.example.videoplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.videoplayer.R;
import com.example.videoplayer.adapter.PlayListAdapter;
import com.example.videoplayer.entity.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContentResolver resolver = null;
    private RecyclerView recyclerView;
    private List<VideoItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolver = getContentResolver();
        recyclerView = findViewById(R.id.main_recylerView);
        // 获取本地视频列表
        list = queryVideo();
        PlayListAdapter adapter = new PlayListAdapter(MainActivity.this, R.layout.item_video, list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    private List<VideoItem> queryVideo(){
        List<VideoItem> list = new ArrayList<>();
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                // 获取视频信息
                Integer id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                Long date_modified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                // 获取视频缩略图
                Bitmap bitmap = null;
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                VideoItem video = new VideoItem(id, data, display_name, resolution, size, date_modified, duration, bitmap);
                list.add(video);
            }
            cursor.close();
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
