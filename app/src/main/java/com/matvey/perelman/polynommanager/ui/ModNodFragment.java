package com.matvey.perelman.polynommanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.matvey.perelman.polynommanager.R;
import com.matvey.perelman.polynommanager.controller.Polynom;
import com.matvey.perelman.polynommanager.model.ModNodViewModel;

public class ModNodFragment extends Fragment {

    private ModNodViewModel modViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle sis) {
        modViewModel =
                new ViewModelProvider(this).get(ModNodViewModel.class);
        View root = inflater.inflate(R.layout.mod_nod_model, container, false);

        SharedPreferences p = getContext().getSharedPreferences("modnod_prefs", Context.MODE_PRIVATE);
        modViewModel.initVals(
                p.getInt("field", 11),
                p.getString("polynomA", "x^2+5x+6"),
                p.getString("polynomB", "x^2+3x+2"));

        EditText text_field = root.findViewById(R.id.et_field);
        EditText text_polynom_a = root.findViewById(R.id.polynom_a_et);
        EditText text_polynom_b = root.findViewById(R.id.polynom_b_et);
        TextView tv_targetName1 = root.findViewById(R.id.output_name1_tv);
        TextView tv_result1 = root.findViewById(R.id.output1_tv);
        TextView tv_targetName2 = root.findViewById(R.id.output_name2_tv);
        tv_targetName2.setText(R.string.str_nod);
        TextView tv_result2 = root.findViewById(R.id.output2_tv);

        modViewModel.onStart(text_field,
                text_polynom_a, text_polynom_b,
                tv_targetName1, tv_result1, tv_result2,
                this::update, getViewLifecycleOwner());
        update();
        return root;
    }

    public void update() {
        int field = modViewModel.getField().getValue();
        Polynom polynomA = new Polynom(modViewModel.getPolynomA().getValue(), field);
        Polynom polynomB = new Polynom(modViewModel.getPolynomB().getValue(), field);
        Polynom mod = polynomA.cp();
        mod.mod(polynomB);
        modViewModel.getTargetName1().postValue(getString(R.string.str_mod));
        modViewModel.getResult1().postValue(mod.toString());

        polynomA = polynomA.nod(polynomB);
        modViewModel.getResult2().postValue(polynomA.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences p = getContext().getSharedPreferences("modnod_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putInt("field", modViewModel.getField().getValue());
        e.putString("polynomA", modViewModel.getPolynomA().getValue());
        e.putString("polynomB", modViewModel.getPolynomB().getValue());
        e.apply();
    }
}