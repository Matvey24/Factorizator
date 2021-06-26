package com.matvey.perelman.polynommanager.model;

import android.app.Application;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.matvey.perelman.polynommanager.R;
import com.matvey.perelman.polynommanager.controller.Polynom;
import com.matvey.perelman.polynommanager.controller.Utils;

public class ReverseViewModel extends AndroidViewModel {
    private final MutableLiveData<String> targetName1;

    private final MutableLiveData<String> polynomField;
    private final MutableLiveData<String> polynomMain;

    private final MutableLiveData<Integer> field;
    private final MutableLiveData<String> result1;
    private final MutableLiveData<String> result2;
    private Runnable update;
    public ReverseViewModel(@NonNull Application application) {
        super(application);
        targetName1 = new MutableLiveData<>();
        polynomField = new MutableLiveData<>();
        polynomMain = new MutableLiveData<>();
        field = new MutableLiveData<>();
        result1 = new MutableLiveData<>("");
        result2 = new MutableLiveData<>("");
    }
    public void initVals(int field, String polynomA, String polynomB){
        this.field.setValue(field);
        this.polynomField.setValue(polynomA);
        this.polynomMain.setValue(polynomB);
    }
    public void onStart(EditText text_field,
                        EditText text_polynom_a, EditText text_polynom_b,
                        TextView target_name1, TextView tv_result1, TextView tv_result2,
                        Runnable update, LifecycleOwner lo){
        this.update = update;
        text_field.setText(String.valueOf(field.getValue()));
        text_polynom_a.setText(polynomField.getValue());
        text_polynom_b.setText(polynomMain.getValue());
        targetName1.observe(lo, target_name1::setText);
        result1.observe(lo, tv_result1::setText);
        result2.observe(lo, tv_result2::setText);

        text_field.setOnEditorActionListener((v, actionId, event) -> update(text_field, text_polynom_a, text_polynom_b));
        text_polynom_a.setOnEditorActionListener((v, actionId, event) -> update(text_field, text_polynom_a, text_polynom_b));
        text_polynom_b.setOnEditorActionListener((v, actionId, event) -> update(text_field, text_polynom_a, text_polynom_b));
    }
    private boolean update(EditText text_field, EditText text_polynom_a, EditText text_polynom_b){
        int i1 = fieldListener(text_field);
        int i2 = textPolynomListener(text_polynom_a, true);
        int i3 = textPolynomListener(text_polynom_b, false);
        int m = i1 * i2 * i3;
        if(m > 0 && m < 8) {
            update.run();
        }else if (m == 8){
            result1.postValue(result1.getValue());
        }
        return m == 0;
    }
    private int fieldListener(EditText text_field){
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
        if(i_field < 2){
            text_field.setError(getApplication().getString(R.string.number_too_little));
            return 0;
        }
        Integer last = field.getValue();
        last = (last == null) ? 0 : last;
        if (i_field == last)
            return 2;
        String s = Utils.factorizeNumber(i_field);
        if(s.isEmpty()){
            field.setValue(i_field);
            return 1;
        }else{
            text_field.setError(getApplication().getString(R.string.number_not_primary));
            targetName1.postValue(getApplication().getString(R.string.title_factor));
            result1.postValue(i_field + " = " + s);
            return 0;
        }
    }
    private int textPolynomListener(EditText text_p, boolean let){
        String newText = text_p.getText().toString();

        try {
            Polynom p = new Polynom(newText, field.getValue());
            if(let) {
                if(!p.isPrimary()){
                    text_p.setError(getApplication().getString(R.string.polynom_not_primary));
                    return 0;
                }
                if(p.size <= 1){
                    text_p.setError(getApplication().getString(R.string.polynom_is_number));
                    return 0;
                }
                polynomField.setValue(newText);
            }else
                polynomMain.setValue(newText);
            return 1;
        }catch (RuntimeException e){
            text_p.setError(getApplication().getString(R.string.error_reading));
            return 0;
        }
    }
    public MutableLiveData<String> getTargetName1() {
        return targetName1;
    }

    public MutableLiveData<String> getPolynomField() {
        return polynomField;
    }

    public MutableLiveData<String> getPolynomMain() {
        return polynomMain;
    }

    public MutableLiveData<Integer> getField() {
        return field;
    }

    public MutableLiveData<String> getResult1() {
        return result1;
    }
}