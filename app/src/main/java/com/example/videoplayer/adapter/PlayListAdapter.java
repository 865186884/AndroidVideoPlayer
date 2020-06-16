package com.example.videoplayer.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.activity.MainActivity;
import com.example.videoplayer.activity.PlayActivity;
import com.example.videoplayer.R;
import com.example.videoplayer.entity.VideoItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 播放列表RecyclerViewAdapter
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    // 进度条旁边的当前进度文字，将毫秒化为mm:ss格式
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private MainActivity context;
    private int resourceId;
    private List<VideoItem> listData;
    private Intent playIntent;

    //定义一个提示框
    AlertDialog dialog, renameDialog;
    //获取自定义界面
    ConstraintLayout layout, renameLayout;

    // 初始化构造函数
    public PlayListAdapter(MainActivity context, int resourceId, List<VideoItem> listData) {
        this.context = context;
        this.resourceId = resourceId;
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view;
        view = LayoutInflater.from(context).inflate(resourceId, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final VideoItem item = listData.get(position);
        // 列表item内容填充
        holder.id = item.getId();
        holder.name.setText(item.getDisplay_name());
        holder.duration.setText(time.format(item.getDuration()));
        holder.path.setText(item.getData());
        // bitmap图片
        if (item.getImage_bitmap() != null) {
            holder.image.setImageBitmap(item.getImage_bitmap());
        }
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*对话框实现*/
                dialog = new AlertDialog.Builder(context).create();
                layout = (ConstraintLayout) View.inflate(context, R.layout.menu, null);
                dialog.setView(layout);//设置对话框显示内容
                Button rename_btn = layout.findViewById(R.id.menu_rename_btn);
                Button delete_btn = layout.findViewById(R.id.menu_delete_btn);
                // 绑定按钮事件
                rename_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 弹出重命名对话框
                        showRenameDialog(position, item.getData());
                    }
                });
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 弹出二次确认对话框
                        showDeleteDialog(position, item.getData());
                    }
                });
                dialog.show();
                // 修改对话框大小
                final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = 180;
                params.height = 135;
                dialog.getWindow().setAttributes(params);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转播放页
                playIntent = new Intent(context, PlayActivity.class);
                playIntent.putExtra("data", item.getData());
                context.startActivity(playIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        int id;
        ImageView image;
        TextView name, duration, path;
        Button more_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_video_image);
            name = itemView.findViewById(R.id.item_video_name);
            duration = itemView.findViewById(R.id.item_video_duration);
            more_btn = itemView.findViewById(R.id.item_video_more);
            path = itemView.findViewById(R.id.item_video_path);
        }
    }

    private void showRenameDialog(final int position, final String oldPath){
        renameDialog = new AlertDialog.Builder(context).create();
        renameLayout = (ConstraintLayout) View.inflate(context, R.layout.dialog_rename, null);
        renameDialog.setView(renameLayout);//设置对话框显示内容
        final EditText renameText = renameLayout.findViewById(R.id.dialog_rename_text);
        Button submit_btn = renameLayout.findViewById(R.id.dialog_submit);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder newPath = new StringBuilder();
                String[] split = oldPath.split("\\.");
                String[] split1 = oldPath.split("/");
                for (int i = 0; i < split1.length - 1; i++) {
                    if (!split1[i].equals("")) {
                        newPath.append("/");
                        newPath.append(split1[i]);
                    }
                }
                newPath.append("/");
                newPath.append(renameText.getText());
                newPath.append(".");
                newPath.append(split[split.length - 1]);
                renameFile(oldPath, newPath.toString());
                // 更新数据
                VideoItem videoItem = listData.get(position);
                videoItem.setDisplay_name(renameText.getText() + "." + split[split.length - 1]);
                videoItem.setData(newPath.toString());
                listData.set(position, videoItem);
                // 刷新item
                notifyItemChanged(position);
                // 刷新sd卡(刷新媒体库)
                MediaScannerConnection.scanFile(context, new String[]{oldPath, newPath.toString()}, null, null);
                dialog.cancel();
                renameDialog.cancel();
                Toast.makeText(context, "修改成功,已刷新列表", Toast.LENGTH_SHORT).show();
            }
        });
        renameDialog.show();
    }

    private void showDeleteDialog(final int position, final String path){
        final AlertDialog dialog1 = dialog;
        //创建退出对话框
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("系统提示");
        alertDialog.setMessage("确定要删除吗");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除文件
                deleteSingleFile(path);
                // 移出列表
                listData.remove(position);
                // 删除选择项
                notifyItemRemoved(position);
                // 刷新sd卡(刷新媒体库)
                MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
                dialog1.cancel();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /**
     * 删除单个文件
     */
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Toast.makeText(context, "删除成功,已刷新列表", Toast.LENGTH_SHORT).show();
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Toast.makeText(context, "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** 重命名文件 */
    private File renameFile(String oldPath, String newPath) {
        if (oldPath == null || oldPath.equals("")) {
            return null;
        }
        if (newPath == null || newPath.equals("")) {
            return null;
        }
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        boolean b = oldFile.renameTo(newFile);
        return new File(newPath);
    }
}
