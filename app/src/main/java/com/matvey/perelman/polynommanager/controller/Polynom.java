package com.matvey.perelman.polynommanager.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Polynom {
    public static String algorithm_error = "error";
    public static String iterations_error = "error";
    public int size;
    public int[] data;
    public int field;

    public Polynom(int field, int capacity) {
        size = 0;
        data = new int[capacity];
        this.field = field;
    }

    public Polynom(String s, int field) {
        this.data = read(s);
        this.field = field;
        simple();
    }

    public Polynom(Polynom other) {
        this.data = Arrays.copyOf(other.data, other.size + 1);
        this.size = other.size;
        this.field = other.field;
    }

    public Polynom cp() {
        return new Polynom(this);
    }

    public void copyFrom(Polynom other) {
        ensureCapacity(other.size);
        System.arraycopy(other.data, 0, data, 0, other.size);
        if (other.size < size)
            Arrays.fill(data, other.size, size, 0);
        this.size = other.size;
    }

    public boolean isZero() {
        return size == 0;
    }

    public boolean isNumber() {
        return size == 1;
    }

    public boolean isOneElement() {
        for (int i = 0; i < size - 1; ++i) {
            if (data[i] != 0)
                return false;
        }
        return true;
    }

    public static int[] read(String s) {
        //определение переменной многочлена
        int idxx = -1;
        int idxn = -1;
        int idxt = -1;
        //проверка валидности строки
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case 'x':
                    idxx = i;
                    break;
                case 'n':
                    idxn = i;
                    break;
                case 't':
                    idxt = i;
                    break;
                case '-':
                case '+':
                case '^':
                case '*':
                case ' ':
                    break;
                default:
                    char c = s.charAt(i);
                    if (c <= '9' && c >= '0') {
                        break;
                    }
                    //неизвестный символ
                    throw new RuntimeException();
            }
        }
        int n = (idxx > -1 ? 1 : 0) + (idxn > -1 ? 1 : 0) + (idxt > -1 ? 1 : 0);
        if (n > 1) {
            //больше одной переменной
            throw new RuntimeException();
        }
        if (n == 1) {
            StringBuilder sb = new StringBuilder();
            //одна переменная
            char let = idxx > -1 ? 'x' : (idxn > -1 ? 'n' : 't');
            s = s.replaceAll("[ ^*]", "");

            class Incl {
                int pow;
                int scale;
            }

            ArrayList<Incl> incls = new ArrayList<>();
            for (int i = 0; i < s.length(); ) {
                char c = s.charAt(i);
                int sign = 1;
                //считываем знак, если есть
                if (c == '+')
                    ++i;
                else if (c == '-') {
                    sign = -1;
                    ++i;
                }
                //считываем коэффициент
                for (; i != s.length() && (s.charAt(i) >= '0' && s.charAt(i) <= '9'); ++i)
                    sb.append(s.charAt(i));
                if (sb.length() != 0)
                    sign *= Integer.parseInt(sb.toString());
                sb.setLength(0);
                Incl incl = new Incl();
                incls.add(incl);
                incl.scale = sign;
                if (i == s.length())
                    break;
                //считывание переменной, если есть
                if (s.charAt(i) == let) {
                    incl.pow = 1;
                    ++i;
                }
                if (i == s.length())
                    break;
                //считывание степени переменной
                for (; i != s.length() && (s.charAt(i) >= '0' && s.charAt(i) <= '9'); ++i)
                    sb.append(s.charAt(i));
                if (sb.length() != 0)
                    incl.pow = Integer.parseInt(sb.toString());
                sb.setLength(0);
            }
            //сортируем по возрастанию степени переменной
            Collections.sort(incls, (o1, o2) -> o1.pow - o2.pow);
            int[] mnog = new int[incls.get(incls.size() - 1).pow + 1];
            //записываем результат в массив
            for (Incl incl : incls) {
                mnog[incl.pow] += incl.scale;
            }
            return mnog;
        } else {
            //переменных нет, значит тип записи - коэффициенты через пробел или подряд в один символ
            String[] arr;
            if (s.length() != 1 && s.indexOf(' ') == -1 && s.indexOf('-') == -1) {
                //если нет пробелов и минусов, то тип записи - подряд
                arr = new String[s.length()];
                for (int i = 0; i < s.length(); ++i) {
                    arr[i] = "" + s.charAt(i);
                }
            } else {
                //иначе - через пробел
                arr = s.split(" ");
            }
            //коэффициенты идут по убыванию степени
            int[] mnog = new int[arr.length];
            for (int i = 0; i < arr.length; ++i) {
                mnog[i] = Integer.parseInt(arr[arr.length - i - 1]);
            }
            return mnog;
        }

    }

    private void simple() {
        size = 0;
        for (int i = 0; i < data.length; ++i) {
            //взятие модуля коэффициента
            data[i] = Utils.mod(data[i], field);
            if (data[i] != 0) {
                size = i + 1;
            }
        }
    }

    public void ensureCapacity(int need) {
        if (data.length < need) {
            data = Arrays.copyOf(data, need * 2);
        }
    }

    public void sub(final Polynom other, int scale, int scalePow) {
        if (other.size + scalePow > this.data.length) {
            ensureCapacity(other.size + scalePow);
        }
        for (int i = 0; i < other.size; ++i)
            data[i + scalePow] -= (scale * (long)other.data[i]) % field;
        simple();
    }

    public void deriv() {
        if (isZero())
            return;
        for (int i = 1; i < size; ++i)
            data[i - 1] = (int)((data[i] * (long)i) % field);
        data[size - 1] = 0;
        size = Math.max(size - 1, 0);
        simple();
    }

    public int norm() {
        int scale = data[size - 1];
        if (scale == 1)
            return 1;
        long v = Utils.reverse(scale, field);
        for (int i = 0; i < data.length; ++i) {
            data[i] = (int)((data[i] * v) % field);
        }
        simple();
        return scale;
    }

    public Polynom divWithRemainder(Polynom other) {
        int nsize = size - other.size;
        if (nsize < 0 || size == 0 || other.size == 0)
            return new Polynom(field, 1);
        Polynom p = new Polynom(field, nsize + 1);
        while (size >= other.size) {
            int c1 = data[size - 1];
            int c2 = other.data[other.size - 1];
            int scale = (int)((c1 * Utils.reverse(c2, field)) % field);
            p.data[size - other.size] = scale;
            sub(other, scale, size - other.size);
        }
        p.simple();
        return p;
    }

    public void mod(Polynom other) {
        while (size >= other.size && other.size != 0) {
            int v = data[size - 1];
            int ov = other.data[other.size - 1];
            int scale = (int)((v * Utils.reverse(ov, field)) % field);
            sub(other, scale, size - other.size);
        }
    }

    public Polynom nod(Polynom other) {
        Polynom min, max;
        if (other.size < size) {
            min = other;
            max = this;
        } else {
            min = this;
            max = other;
        }
        if (min.isZero())
            return min;
        max.mod(min);
        if (max.isZero()) {
            if (!min.isZero())
                min.norm();
            return min;
        }
        return min.nod(max);
    }

    @Override
    public String toString() {
        if (size == 0) {
            //если многочлен - 0
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = size - 1; i >= 0; --i) {
            //если коэффициент - 0, ничего не пишем
            if (data[i] == 0)
                continue;
            //проверка на знак минус
            if (i != size - 1 || data[i] < 0) {
                if (data[i] < 0) {
                    sb.append('-');
                } else {
                    sb.append('+');
                }
            }
            //проверка на единицу
            if (Math.abs(data[i]) != 1 || i == 0)
                sb.append(Math.abs(data[i]));
            //нулевая степень, первая или другая
            if (i != 0) {
                sb.append('x');
                if (i != 1) {
                    sb.append('^').append(i);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polynom polynom = (Polynom) o;
        return size == polynom.size &&
                field == polynom.field &&
                Arrays.equals(data, polynom.data);
    }

    public void rootForEach(int pow) {
        if (size == 0)
            return;
        int newSize = (size - 1) / pow + 1;
        for (int i = 0; i < newSize; ++i)
            data[i] = data[i * pow];
        for (int i = newSize; i < size; ++i)
            data[i] = 0;
        size = newSize;
    }

    public void multiply(Polynom other, Polynom tmp) {
        tmp.copyFrom(this);
        Arrays.fill(data, 0);
        size = 0;
        for (int i = 0; i < other.size; ++i) {
            int v = other.data[i];
            if (v == 0)
                continue;
            sub(tmp, Utils.mod(-v, field), i);
        }
    }

    public void pow(int pow, Polynom mod, Polynom tmp, Polynom tmp2) {
        if (pow == 1) {
            mod(mod);
            return;
        }
        if (pow % 2 == 0) {
            pow(pow / 2, mod, tmp, tmp2);
            tmp2.copyFrom(this);
            multiply(tmp2, tmp);
        } else {
            Polynom p = cp();
            pow(pow - 1, mod, tmp, tmp2);
            multiply(p, tmp);
        }
        mod(mod);
    }

    public boolean isPrimary() {
        if (size <= 2)
            return true;
        Polynom deriv = cp();
        deriv.deriv();
        if (deriv.isZero()) {
            //polynom looks like (...)^field
            return false;
        }

        deriv = cp().nod(deriv);
        if (!deriv.isNumber()) {
            //polynom has multiple roots
            return false;
        }
        int n = size - 1;
        int[][] matrix = new int[n][n];

        Polynom tmp = new Polynom(field, size * 2);
        Polynom tmp1 = new Polynom(field, size * 2);
        Polynom tmp2 = new Polynom(field, size * 2);
        Polynom tmp3 = new Polynom(field, size * 2);
        tmp3.data[1] = 1;
        tmp3.size = 2;
        tmp3.pow(field, this, tmp1, tmp2);
        tmp.data[0] = 1;
        tmp.size = 1;
        matrix[0][0] = 1;
        for (int i = 1; i < n; ++i) {
            tmp.multiply(tmp3, tmp1);
            tmp.mod(this);
            System.arraycopy(tmp.data, 0, matrix[i], 0, n);
        }

        Utils.transpose(matrix);

        for (int i = 0; i < n; ++i) {
            matrix[i][i] -= 1;
            if (matrix[i][i] < 0)
                matrix[i][i] += field;
        }
        int rank = Utils.rank(matrix, field);
        return rank == n - 1;
    }

    //there is no previous polynom after calling this function
    public PolynomContainer factorize() {
        //is number
        if (size <= 1) {
            PolynomContainer pc = new PolynomContainer();
            if (size == 0) {
                pc.scale = 0;
            } else {
                pc.scale = data[0];
            }
            return pc;
        }
        //is linear polynom
        if (size == 2) {
            PolynomContainer pc = new PolynomContainer();
            pc.scale = norm();
            pc.append(this, 1);
            return pc;
        }

        Polynom deriv = cp();
        deriv.deriv();
        if (deriv.isZero()) {
            //polynom looks like (...)^field
            rootForEach(field);
            PolynomContainer pc = factorize();
            pc.power(field);
            return pc;
        }

        deriv = cp().nod(deriv);
        if (!deriv.isNumber()) {
            //polynom has multiple roots
            Polynom p = divWithRemainder(deriv);
            PolynomContainer pc = p.factorize();
            PolynomContainer pc2 = deriv.factorize();
            pc.multiply(pc2);
            return pc;
        }
        // in this stage there is no multiple roots in polynom
        int n = size - 1;
        int[][] matrix = new int[n][n];

        Polynom x = new Polynom(field, size * 2);
        Polynom tmp1 = new Polynom(field, size * 2);
        Polynom tmp2 = new Polynom(field, size * 2);
        Polynom tmp3 = new Polynom(field, size * 2);
        tmp3.data[1] = 1;
        tmp3.size = 2;
        tmp3.pow(field, this, tmp1, tmp2);

        x.data[0] = 1;
        x.size = 1;
        for (int i = 1; i < n; ++i) {
            x.multiply(tmp3, tmp1);
            x.mod(this);
            System.arraycopy(x.data, 0, matrix[i], 0, n);
            matrix[i][i] = Utils.mod(matrix[i][i] - 1, field);
        }

        Utils.transpose(matrix);

        int rank = Utils.rank(matrix, field);
        if (rank == n - 1) {// Многочлен неприводим
            PolynomContainer pc = new PolynomContainer();
            pc.scale = norm();
            pc.append(this, 1);
            return pc;
        }
        Polynom th = new Polynom(field, size);
        int[][] arr2 = Utils.solveLinearEquationSystem(matrix, field);
        if (field <= 20) {
            int[] arr = new int[arr2.length];
            {
                int[] coefs = new int[arr2[0].length];
                coefs[0] = 1;
                while (coefs[coefs.length - 1] != field) {
                    boolean good = false;
                    for (int i = 0; i < arr.length; ++i) {
                        arr[i] = 0;
                        for (int j = 0; j < arr2[0].length; ++j)
                            arr[i] += (arr2[i][j] * (long)coefs[j]) % field;
                            arr[i] %= field;
                        if (arr[i] != 0 && i != 0)
                            good = true;
                    }
                    if (good)
                        break;
                    coefs[0]++;
                    for (int i = 0; i < coefs.length - 1; ++i) {
                        if (coefs[i] >= field) {
                            coefs[i] -= field;
                            coefs[i + 1]++;
                        } else
                            break;
                    }
                }
            }
            for (int i = 0; i < field; ++i) {
                System.arraycopy(arr, 0, x.data, 0, arr.length);
                x.simple();
                x.data[0] = i;
                th.copyFrom(this);
                Polynom nod = th.nod(x);
                if (!nod.isNumber()) {
                    PolynomContainer pc = divWithRemainder(nod).factorize();
                    pc.multiply(nod.factorize());
                    return pc;
                }
            }
            throw new RuntimeException(algorithm_error + ": " + this);
        } else {
            int pow = (field - 1) / 2;
            int[] coefs = new int[arr2[0].length];
            int iters = 0;
            while (iters < 10000) {
                ++iters;
                for(int i = 0; i < coefs.length; ++i)
                    coefs[i] = (int)(Math.random() * field);
                for (int i = 0; i < arr2.length; ++i) {
                    x.data[i] = 0;
                    for (int j = 0; j < arr2[0].length; ++j) {
                        x.data[i] += (arr2[i][j] * (long)coefs[j]) % field;
                        x.data[i] %= field;
                    }
                }
                x.simple();
                if (x.size > 1)
                    x.pow(pow, this, tmp1, tmp2);
                if (x.size > 1) {
                    for (int i = -1; i <= 1; ++i) {
                        tmp3.copyFrom(x);
                        tmp3.data[0] = Utils.mod(x.data[0] + i, field);
                        th.copyFrom(this);
                        Polynom nod = th.nod(tmp3);
                        if (nod.size > 1) {
                            PolynomContainer pc = divWithRemainder(nod).factorize();
                            pc.multiply(nod.factorize());
                            return pc;
                        }
                    }
                }
            }
            throw new RuntimeException(iterations_error + ": " + this);
        }
    }

    public String toStringWithBrackets() {
        if (isOneElement())
            return toString();
        return '(' + toString() + ')';
    }
}