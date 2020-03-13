package com.work.vladimirs.utils;

public final class MatrixOperations {

    /**
     * Исключение, при не соответствии матриц.
     * Кол-во колонок первой матрицы не равно кол-ву строк второй матрицы
     */
    public static class MatrixMismatchException extends Exception {
        public MatrixMismatchException() {
            super();
        }

        public MatrixMismatchException(String s) {
            super(s);
        }
    }

    /**
     * Умножение двух матриц.
     * Кол-во колонок первой матрицы должно быть равно кол-ву строк второй матрицы
     * @param m1 первая матрица L x M
     * @param m2 вторая матрица M x N
     * @return результирующая матрица L x N
     * @throws MatrixMismatchException
     */
    public static int[][] multiplication(int[][] m1, int[][] m2) throws MatrixMismatchException {
        int m1_cols = m1[0].length;
        int m1_rows = m1.length;
        int m2_cols = m2[0].length;
        int m2_rows = m2.length;

        if (m1_cols != m2_rows) throw new MatrixMismatchException("Column count of matrix 1 not equals rows count of matrix 2!");

        int[][] result = new int[m1_rows][m2_cols];

        for (int i = 0; i < m1_rows; i++) {
            for (int j = 0; j < m2_cols; j++) {
                int elem = 0;
                for (int k = 0; k < m1_cols; k++) {
                    elem += result[i][j] = m1[i][k] * m2[k][j];
                }
                result[i][j] = elem;
            }
        }

        return result;
    }
}
