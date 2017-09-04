package com.frkn.fullfizik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by wahyu on 15/11/16.
 */

@SuppressLint("ValidFragment")
public class ActivityStyle13Fragment extends Fragment implements ActivityStyle13ClickListener{
    int wizard_page_position;
    ArrayList<ActivityStyle13Model> dataList;

    public ActivityStyle13Fragment(int position, ArrayList<ActivityStyle13Model> dataList) {
        this.wizard_page_position = position;
        this.dataList = dataList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = R.layout.activity13_fragment;
        View view = inflater.inflate(layout_id, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        RecyclerView rView = (RecyclerView) view.findViewById(R.id.recyclerView);
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(false);

        ActivityStyle13Adapter rcAdapter = new ActivityStyle13Adapter(getActivity(), dataList);
        rView.setAdapter(rcAdapter);
        rcAdapter.setClickListener(this);
        return view;
    }

    public final static String EXTRA_MESSAGE = "com.frkn.physbasic.MESSAGE";

    private void openSelectedItem(int type, int id, int imgCount, int fileLength) {
        Log.d("openSelectedItem", "type: " + type + ", id: " + id + ", imgCount: " + imgCount + ", fileLength: " + fileLength);
        Intent intent = new Intent(getActivity(), GalleryStyle17Activity.class);
        intent.putExtra(EXTRA_MESSAGE + "_type", String.valueOf(type));
        intent.putExtra(EXTRA_MESSAGE + "_id", String.valueOf(id));
        intent.putExtra(EXTRA_MESSAGE + "_imageCount", String.valueOf(imgCount));
        intent.putExtra(EXTRA_MESSAGE + "_fileLength", String.valueOf(fileLength));
        startActivity(intent);
    }

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        Toast.makeText(getActivity(), "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();
        openSelectedItem();
    }
}
