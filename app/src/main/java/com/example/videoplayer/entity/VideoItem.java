package com.example.videoplayer.entity;

import android.graphics.Bitmap;

public class VideoItem {
    /** ID */
    private Integer id = 0;

    /** 文件路径 */
    private String data = null;

    /** 视频名 */
    private String display_name = null;

    /** 分辨率 */
    private String resolution = null;

    /** 大小 */
    private Long size = 0L;

    /** 修改时间 */
    private Long date_modified = 0L;

    /** 时长 */
    private Long duration = 0L;

    /** bitmap图片 */
    private Bitmap image_bitmap;

    public VideoItem(Integer id, String data, String display_name, String resolution, Long size, Long date_modified, Long duration, Bitmap image_bitmap) {
        this.id = id;
        this.data = data;
        this.display_name = display_name;
        this.resolution = resolution;
        this.size = size;
        this.date_modified = date_modified;
        this.duration = duration;
        this.image_bitmap = image_bitmap;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(Long date_modified) {
        this.date_modified = date_modified;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }

    public void setImage_bitmap(Bitmap image_bitmap) {
        this.image_bitmap = image_bitmap;
    }
}
