package com.tsinghua.tsinghelper.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tsinghua.tsinghelper.R;
import com.tsinghua.tsinghelper.adapters.TaskAdapter;
import com.tsinghua.tsinghelper.dtos.TaskDTO;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);
        initRecyclerView();
        return root;
    }

    private void initRecyclerView() {
        mAdapter = new TaskAdapter(genTasks());
        mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private ArrayList<TaskDTO> genTasks() {
        ArrayList<TaskDTO> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String s = String.valueOf(i + 1);
            tasks.add(new TaskDTO("任务" + s,
                    "报酬" + s, "截止日期" + s ));
        }
        return tasks;
    }


}
