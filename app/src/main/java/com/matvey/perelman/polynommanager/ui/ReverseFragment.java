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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.matvey.perelman.polynommanager.R;
import com.matvey.perelman.polynommanager.controller.Polynom;
import com.matvey.perelman.polynommanager.controller.Utils;
import com.matvey.perelman.polynommanager.model.ReverseViewModel;

public class ReverseFragment extends Fragment {

    private ReverseViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle sis) {
        viewModel =
                new ViewModelProvider(this).get(ReverseViewModel.class);
        View root = inflater.inflate(R.layout.mod_nod_model, container, false);

        SharedPreferences p = getContext().getSharedPreferences("reverse_prefs", Context.MODE_PRIVATE);
        viewModel.initVals(
                p.getInt("field", 11),
                p.getString("polynomA", "x+1"),
                p.getString("polynomB", "2"));

        EditText text_field = root.findViewById(R.id.et_field);
        TextInputEditText text_polynom_field = root.findViewById(R.id.polynom_a_et);
        TextInputEditText text_polynom_main = root.findViewById(R.id.polynom_b_et);
        TextView tv_targetName = root.findViewById(R.id.output_name1_tv);
        TextView tv_result = root.findViewById(R.id.output1_tv);
        TextView tv_result2 = root.findViewById(R.id.output2_tv);

        TextInputLayout til = root.findViewById(R.id.textInputLayout2);
        til.setHint(R.string.str_ideal);
        til = root.findViewById(R.id.textInputLayout3);
        til.setHint(R.string.str_main);
        viewModel.onStart(text_field, text_polynom_field, text_polynom_main, tv_targetName, tv_result, tv_result2, this::update, getViewLifecycleOwner());
        update();
        return root;
    }

    public void update() {
        int field = viewModel.getField().getValue();
        try {
            Polynom p = new Polynom(viewModel.getPolynomField().getValue(), field);
            Polynom a = new Polynom(viewModel.getPolynomMain().getValue(), field);
            StringBuilder sb = new StringBuilder();
            String s = a.toStringWithBrackets();
            boolean b = a.isOneElement();
            a = Utils.reverse(a, p);
            sb.append(a.toStringWithBrackets());
            if(b && a.isOneElement())
                sb.append(" * ");
            sb.append(s);
            sb.append(" = 1 mod ").append(p.toStringWithBrackets()).append("\n\n").append(a);
            viewModel.getTargetName1().postValue(getString(R.string.reverse_element));
            viewModel.getResult1().postValue(sb.toString());
        } catch (RuntimeException e) {
            viewModel.getResult1().postValue(e.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences p = getContext().getSharedPreferences("reverse_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putInt("field", viewModel.getField().getValue());
        e.putString("polynomA", viewModel.getPolynomField().getValue());
        e.putString("polynomB", viewModel.getPolynomMain().getValue());
        e.apply();
    }
}