package com.matvey.perelman.polynommanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.matvey.perelman.polynommanager.R;
import com.matvey.perelman.polynommanager.controller.Polynom;
import com.matvey.perelman.polynommanager.controller.PolynomContainer;
import com.matvey.perelman.polynommanager.controller.threads.Tasks;
import com.matvey.perelman.polynommanager.model.FactorizationViewModel;

import java.util.Arrays;

public class FactorizationFragment extends Fragment {

    private FactorizationViewModel factViewModel;
    private Tasks tasks;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        factViewModel =
                new ViewModelProvider(this).get(FactorizationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_factor, container, false);

        SharedPreferences p = getContext().getSharedPreferences("factor_prefs", Context.MODE_PRIVATE);
        factViewModel.initVals(
                p.getInt("field", 3),
                p.getString("polynomA", "x^9-x"));

        EditText text_field = root.findViewById(R.id.et_field);
        EditText text_polynom = root.findViewById(R.id.polynom_et);
        TextView result_tv = root.findViewById(R.id.out_tv);
        factViewModel.onStart(text_field, text_polynom, result_tv, this::update, getViewLifecycleOwner());
        tasks = new Tasks();
        update();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences p = getContext().getSharedPreferences("factor_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putInt("field", factViewModel.getField().getValue());
        e.putString("polynomA", factViewModel.getPolynom().getValue());
        e.apply();
        tasks.disposeOnFinish();
    }

    public void update() {
        if(!tasks.isFinished())
            return;
        String s1 = factViewModel.getPolynom().getValue();
        Polynom p = new Polynom(s1, factViewModel.getField().getValue());
        tasks.runTask(() -> {
            factViewModel.getResult().postValue(getString(R.string.calculating));
            String s = p.toString();
            try {
                PolynomContainer pc = p.factorize();
                s = s + " = " + pc.toString();
            } catch (Throwable e) {
                s = s + " - " + e.getMessage();
            }
            factViewModel.getResult().postValue(s);
        });
    }
}