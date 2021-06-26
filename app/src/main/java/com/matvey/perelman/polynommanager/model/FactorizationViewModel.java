package com.matvey.perelman.polynommanager.model;

import android.app.Application;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.matvey.perelman.polynommanager.R;
import com.matvey.perelman.polynommanager.controller.Polynom;
import com.matvey.perelman.polynommanager.controller.Utils;

public class FactorizationViewModel extends AndroidViewModel {
    private final MutableLiveData<String> polynom;
    private final MutableLiveData<Integer> field;
    private final MutableLiveData<String> result;
    private Runnable update;

    public FactorizationViewModel(@NonNull Application application) {
        super(application);
        polynom = new MutableLiveData<>();
        field = new MutableLiveData<>();
        result = new MutableLiveData<>();
    }

    public void initVals(int field, String polynom) {
        this.field.setValue(field);
        this.polynom.setValue(polynom);
    }

    public void onStart(EditText text_field, EditText text_polynom, TextView result_tv, Runnable update, LifecycleOwner lo) {
        text_field.setText(String.valueOf(field.getValue()));
        text_polynom.setText(polynom.getValue());
        text_field.setOnEditorActionListener((v, actionId, event) -> update(text_field, text_polynom));
        text_polynom.setOnEditorActionListener((v, actionId, event) -> update(text_field, text_polynom));
        result.observe(lo, result_tv::setText);
        this.update = update;
    }

    private boolean update(EditText text_field, EditText text_polynom) {
        int i1 = fieldListener(text_field);
        int i2 = textPolynomListener(text_polynom);
        int m = i1 * i2;
        if (m > 0 && m < 4) {
            update.run();
        } else if (m == 8) {
            result.postValue(result.getValue());
        }
        return m == 0;
    }

    private int fieldListener(EditText text_field) {
        int i_field;
        try {
            i_field = Integer.parseInt(text_field.getText().toString());
            if(i_field > Integer.MAX_VALUE / 2){
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            text_field.setError(getApplication().getString(R.string.number_too_big));
            return 0;
        }
        if (i_field < 2) {
            text_field.setError(getApplication().getString(R.string.number_too_little));
            return 0;
        }
        Integer last = field.getValue();
        last = (last == null) ? 0 : last;
        if (i_field == last)
            return 2;
        String s = Utils.factorizeNumber(i_field);
        if (s.isEmpty()) {
            field.setValue(i_field);
            return 1;
        } else {
            text_field.setError(getApplication().getString(R.string.number_not_primary));
            result.postValue(i_field + " = " + s);
            return 0;
        }
    }

    public int textPolynomListener(EditText text_p) {
        String newText = text_p.getText().toString();
        String last;
        last = polynom.getValue();
        if (newText.equals(last))
            return 2;
        try {
            Polynom.read(newText);
            polynom.setValue(newText);
            return 1;
        } catch (RuntimeException e) {
            text_p.setError(getApplication().getString(R.string.error_reading));
            return 0;
        }
    }

    public MutableLiveData<String> getPolynom() {
        return polynom;
    }

    public MutableLiveData<Integer> getField() {
        return field;
    }

    public MutableLiveData<String> getResult() {
        return result;
    }
}