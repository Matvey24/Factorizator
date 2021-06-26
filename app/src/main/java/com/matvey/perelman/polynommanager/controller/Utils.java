package com.matvey.perelman.polynommanager.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Utils {
    public static int mod(long a, int p) {
        a = a % p;
        if (a < 0)
            a += p;
        return (int)a;
    }
    //обратное к числу а в мультипликативной группе Zp.
    //расширенный алгоритм евклида
    //самый полезный алгоритм, про который я узнал за 1 курс
    public static long reverse(long a, long p) {
        //1 = (a * x) mod p => 1 = a * x + p * y
        long x1 = 1, v1 = a;
        long x2 = 0, v2 = p;
        while (v1 != 1) {
            long delta = v1 / v2;
            v1 -= v2 * delta;
            x1 -= x2 * delta;

            long t = x1;
            x1 = x2;
            x2 = t;

            t = v1;
            v1 = v2;
            v2 = t;
        }
        x1 %= p;
        if (x1 < 0)
            x1 += p;
        return x1;
    }
    public static Polynom reverse(Polynom a, Polynom p){
        int size = p.size;
        Polynom x1 = new Polynom(a.field, size);
        Polynom x2 = new Polynom(a.field, size);
        x1.data[0] = 1;
        x1.size = 1;
        Polynom v1 = a.cp();
        Polynom v2 = p.cp();
        Polynom tmp1 = new Polynom(a.field, size);
        while(!(v1.size <= 1)){
            Polynom tmp = v1.divWithRemainder(v2);
            tmp.multiply(x2, tmp1);
            x1.sub(tmp, 1, 0);

            Polynom t = x1;
            x1 = x2;
            x2 = t;

            t = v1;
            v1 = v2;
            v2 = t;
        }
        x1.mod(p);
        long v = v1.data[0];
        if(v != 0) {
            v = reverse(v, p.field);
            for (int i = 0; i < x1.size; ++i)
                x1.data[i] = mod(x1.data[i] * v, p.field);
        }
        return x1;
    }
    public static String factorizeNumber(int number) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 2; i * i <= number; ++i) {
            if (number % i == 0) {
                Integer v = map.get(i);
                if (v == null || v == 0) {
                    map.put(i, 1);
                } else {
                    map.put(i, v + 1);
                }
                number /= i;
                --i;
            }
        }
        if (!map.isEmpty()) {
            Integer v = map.get(number);
            if (v == null || v == 0) {
                map.put(number, 1);
            } else
                map.put(number, v + 1);
        }
        StringBuilder sb = new StringBuilder();
        boolean started = false;
        for (Map.Entry<Integer, Integer> v : map.entrySet()) {
            if (started)
                sb.append(" * ");
            else
                started = true;
            sb.append(v.getKey());
            if (v.getValue() != 1)
                sb.append('^').append(v.getValue());
        }
        return sb.toString();
    }

    public static int rank(int[][] matrix, int p) {
        for (int i = 0; i < matrix.length; ++i) {
            int j = 0;
            //находим первую строку, в которой элемент идет раньше всех (на месте j), но строка пуста до i символа
            for (; j < matrix.length; ++j) {
                mat:
                {
                    for (int l = 0; l < i; ++l) {
                        if (matrix[j][l] != 0) {
                            break mat;
                        }
                    }
                    if (matrix[j][i] != 0)
                        break;
                }
            }
            if (j == matrix.length) {
                continue;
            }
            long rev = reverse(matrix[j][i], p);
            //вычитаем найденную строку из всех остальных, обнуляя коэффициент на j месте
            for (int k = 0; k < matrix.length; ++k) {
                if (k == j || matrix[k][i] == 0)
                    continue;

                long scale = (matrix[k][i] * rev) % p;
                for (int l = i; l < matrix.length; ++l) {
                    matrix[k][l] = mod(matrix[k][l] - scale * matrix[j][l], p);
                }
            }
        }
        //в результате получим не единичную матрицу, но ту, в которой невозможно сделать меньше ненулевых строк
        //ранг такой матрицы - количество ненулевых строк
        //считаем их
        int rank = 0;
        for (int[] ints : matrix) {
            int j = 0;
            for (; j < matrix.length; ++j) {
                if (ints[j] != 0) {
                    break;
                }
            }
            if (j != matrix.length) {
                rank++;
            }
        }
        return rank;
    }

    public static void transpose(int[][] matrix) {
        int size = matrix.length;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < i; ++j) {
                int k = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = k;
            }
        }
    }

    public static void normArray(int[] array, int p) {
        long v = 0;
        int i = 0;
        for (; i < array.length; ++i) {
            if (array[i] != 0) {
                v = reverse(array[i], p);
                break;
            }
        }
        if (i == array.length)
            return;

        for (; i < array.length; ++i) {
            array[i] = mod(array[i] * v, p);
        }
    }
    public static int[][] solveLinearEquationSystem(int[][] s, int p) {
//        rank(s, p);
        for (int[] ints : s)
            normArray(ints, p);
        Arrays.sort(s, (o1, o2) -> {
            for (int i = 0; i < o1.length; ++i) {
                int c = ((o1[i] != 0) ? 1 : 0) - ((o2[i] != 0) ? 1 : 0);
                if (c != 0) {
                    return -c;
                }
            }
            return 0;
        });
        transpose(s);
        Stack<Transposition> stack = new Stack<>();
        int i = 0;
        for(; i < s.length; ++i) {
            int j = i;
            for (; j < s.length; ++j) {
                int k = 0;
                for (; k < s[j].length; ++k) {
                    if (s[j][k] != 1 && k == i || s[j][k] != 0 && k != i)
                        break;
                }
                if(k == s.length)
                    break;
            }
            if(j != s.length){
                if(i == j)
                    continue;
                Transposition t = new Transposition(i, j);
                stack.push(t);
                swap(s, t);
            }else{
                break;
            }
        }

        if (i == s.length) {
            return new int[s.length][0];
        }

        int[][] solution = new int[s.length][s.length - i];
        for (int b = i; b < s.length; ++b) {
            for (int c = 0; c < s[b].length; ++c)
                solution[c][b - i] = mod(-s[b][c], p);
        }
        for (int b = i; b < s.length; ++b)
            solution[b][b - i] = 1;

        while (!stack.empty())
            swap(solution, stack.pop());
        return solution;
    }

    private static class Transposition {
        int i, j;

        public Transposition(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static <T> void swap(T[] arr, Transposition t) {
        T tmp = arr[t.i];
        arr[t.i] = arr[t.j];
        arr[t.j] = tmp;
    }
}
