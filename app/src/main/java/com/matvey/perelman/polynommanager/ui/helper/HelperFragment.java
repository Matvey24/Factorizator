package com.matvey.perelman.polynommanager.ui.helper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matvey.perelman.polynommanager.R;

public class HelperFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helper, container, false);
        RecyclerView rv = view.findViewById(R.id.helper_view);
        HelperAdapter adapter = new HelperAdapter();
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(adapter);
        String[] array = getResources().getStringArray(R.array.helpers);
        String[][] na = new String[array.length / 2][2];
        for(int i = 0; i < array.length; ++i){
            na[i / 2][i % 2] = array[i];
        }
        adapter.resetContent(na);
        return view;
    }
}