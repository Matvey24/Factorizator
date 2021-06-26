package com.matvey.perelman.polynommanager.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PolynomContainer {
    public final ArrayList<Pair> list;
    public int scale;

    public PolynomContainer() {
        list = new ArrayList<>();
        scale = 1;
    }

    public static class Pair {
        Polynom p;
        int pow;
    }

    public void append(Polynom p, int pow) {
        if (pow == 0)
            return;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).p.equals(p)) {
                list.get(i).pow += pow;
                return;
            }
        }
        Pair pair = new Pair();
        pair.p = p.cp();
        pair.pow = pow;
        list.add(pair);
    }

    public void multiply(PolynomContainer other) {
        for (Pair p : other.list)
            append(p.p, p.pow);
        scale *= other.scale;
    }

    public void power(int pow) {
        for (Pair p : list)
            p.pow *= pow;
    }

    @Override
    public String toString() {
        //sorting
        Collections.sort(list, (o1, o2) -> {
            int p = o1.p.size - o2.p.size;
            if(p != 0)
                return p;
            for(int i = o1.p.size - 1; i >= 0; --i){
                p = o1.p.data[i] - o2.p.data[i];
                if(p != 0)
                    return p;
            }
            return 0;
        });
        StringBuilder sb = new StringBuilder();
        if (scale != 1)
            sb.append(scale);
        boolean needRemoveDot = false;
        for (Pair pair : list) {
            needRemoveDot = false;
            Polynom p = pair.p;
            int pow = pair.pow;
            if (pow == 0)
                continue;
            if (list.size() == 1 && pow == 1 && scale == 1)
                sb.append(p);
            else
                sb.append(p.toStringWithBrackets());
            if (pow == 1)
                continue;
            sb.append('^').append(pow).append('*');
            needRemoveDot = true;
        }

        if (needRemoveDot)
            sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
