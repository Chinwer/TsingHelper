package com.tsinghua.tsinghelper.ui.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tsinghua.tsinghelper.R;
import com.tsinghua.tsinghelper.adapters.GridImageAdapter;
import com.tsinghua.tsinghelper.dtos.TaskDTO;
import com.tsinghua.tsinghelper.engines.GlideEngine;
import com.tsinghua.tsinghelper.managers.ImageGridLayoutManager;
import com.tsinghua.tsinghelper.util.ErrorHandlingUtil;
import com.tsinghua.tsinghelper.util.HttpUtil;
import com.tsinghua.tsinghelper.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("Registered")
public class BaseTaskActivity extends AppCompatActivity {
    protected int maxSelectNum = 4;
    protected boolean isNewTask = true;
    protected TaskDTO mTask;

    @BindView(R.id.page_title)
    protected TextView mPageTitle;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.scroll_view)
    protected ScrollView mScrollView;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.title)
    protected EditText mTitle;
    @BindView(R.id.description)
    protected EditText mDescription;
    @BindView(R.id.reward)
    protected EditText mReward;
    @BindView(R.id.review_time)
    protected EditText mReviewTime;

    protected GridImageAdapter mAdapter;
    protected List<LocalMedia> mSelectList = new ArrayList<>();

    protected GridImageAdapter.onAddPicClickListener onAddPicClickListener = this::showGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setFocusChangeListeners() {
        mScrollView.getViewTreeObserver().addOnGlobalFocusChangeListener(
                (oldFocus, newFocus) -> {
                    mScrollView.smoothScrollTo(0, newFocus.getBottom());
                    mScrollView.post(newFocus::requestFocus);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initWidgets(Activity activity) {
        ButterKnife.bind(activity);

        setFocusChangeListeners();

        // initiate toolbar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ImageGridLayoutManager manager = new ImageGridLayoutManager(
                this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GridImageAdapter(activity, onAddPicClickListener, mSelectList);
        mAdapter.setData(mSelectList);
        mAdapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener((pos, v) -> {
            if (mSelectList.size() > 0) {
                LocalMedia media = mSelectList.get(pos);
                String pictureType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(pictureType);
                if (mediaType == 1) { // image preview
                    PictureSelector.create(activity).externalPicturePreview(
                            pos, mSelectList, AnimationType.DEFAULT_ANIMATION);
                }
            }
        });
    }

    protected void showGallery(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(maxSelectNum)
                .minSelectNum(0)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isPreviewImage(true)
                .isZoomAnim(true)
                .isEnableCrop(true)
                .isCamera(false)
                .isCompress(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            images = PictureSelector.obtainMultipleResult(data);
            mSelectList.addAll(images);
            mAdapter.setData(mSelectList);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected HashMap<String, String> checkFields(Activity activity) {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String reward = mReward.getText().toString();
        String reviewTime = mReviewTime.getText().toString();

        if (title.length() < 2 || title.length() > 20) {
            ToastUtil.showToast(activity, "任务标题必须为2到20位");
            return null;
        }
        if (description.isEmpty()) {
            ToastUtil.showToast(activity, "任务描述不能为空");
            return null;
        }
        if (reward.isEmpty()) {
            ToastUtil.showToast(activity, "任务报酬不能为空");
            return null;
        }
        if (Double.parseDouble(reward) < 0.2) {
            ToastUtil.showToast(activity, "报酬最少为0.2元");
            return null;
        }
        if (reviewTime.isEmpty()) {
            ToastUtil.showToast(activity, "审核时间不能为空");
            return null;
        }

        HashMap<String, String> res = new HashMap<>();
        res.put("title", title);
        res.put("reward", reward);
        res.put("description", description);
        res.put("review_time", reviewTime);
        return res;
    }

    protected void createTask(HashMap<String, String> params, Activity activity) {
        String url = isNewTask ? HttpUtil.TASK_ADD : HttpUtil.TASK_MODIFY;
        if (!isNewTask) {
            params.put("taskId", String.valueOf(mTask.id));
        }
        HttpUtil.post(url, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ErrorHandlingUtil.handleNetworkError(
                        BaseTaskActivity.this, "任务创建失败，请稍后重试", e);
                activity.finish();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                switch (response.code()) {
                    case 201:
                        String prompt = isNewTask ? "任务创建成功" : "任务信息修改成功";
                        ToastUtil.showToastOnUIThread(activity, prompt);
                        break;
                    case 400:
                        ToastUtil.showToastOnUIThread(activity, "请求参数不合法");
                        break;
                    default:
                        Log.i("info", resStr);
                        break;
                }
                activity.finish();
            }
        });
    }

    protected void setTaskInfo(TaskDTO task) {
        mTitle.setText(task.title);
        mDescription.setText(task.description);
        mReward.setText(String.valueOf(task.reward));
        mReviewTime.setText(String.valueOf(task.reviewTime));
    }
}
