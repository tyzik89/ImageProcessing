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
     * Транспонирование матрицы.
     * Для того чтобы транспонировать матрицу, нужно ее строки записать в столбцы транспонированной матрицы.
     * @param matrix исходная матрица
     * @return транспонированная матрица
     */
    public static double[][] transpose(double[][] matrix) {
        int m_rows = matrix.length;
        int m_cols = matrix[0].length;
        double[][] transposeMatrix = new double[m_cols][m_rows];

        for (int i = 0; i < m_rows; i++) {
            for (int j = 0; j < m_cols; j++) {
                transposeMatrix[j][i] = matrix[i][j];
            }
        }
        return transposeMatrix;
    }

    /**
     * Вычисление определителя матрицы.
     * Матрица должна быть квадратичной.
     * @param matrix исходная матрица
     * @return определитель матрицы
     * @throws MatrixMismatchException
     */
    public static double determinant(double[][] matrix) throws MatrixMismatchException {
        double calcResult = 0.0;
        int m_rows = matrix.length;
        int m_cols = matrix[0].length;

        if (m_rows != m_cols) throw new MatrixMismatchException("The determinant can only be calculated for a square matrix.");

        if (m_rows == 2) {
            calcResult = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            int koeff = 1;
            for(int i = 0; i < m_rows; i++) {
                if(i % 2 ==1) {
                    koeff = -1;
                }
                else {
                    koeff = 1;
                }
                //Разложение:
                calcResult += koeff * matrix[0][i] * determinant(getMinor(matrix, 0, i));
            }
        }
        return calcResult;
    }

    /**
     * Получение минора матрицы
     */
    private static double[][] getMinor(double[][] matrix, int row, int column) {
        int minorLength = matrix.length-1;
        double[][] minor = new double[minorLength][minorLength];
        //переменные для того, чтобы "пропускать" ненужные нам строку и столбец
        int dI = 0;
        int dJ = 0;
        for(int i = 0; i <= minorLength; i++) {
            dJ = 0;
            for(int j = 0; j <= minorLength; j++) {
                if(i == row){
                    dI = 1;
                } else {
                    if (j == column) {
                        dJ = 1;
                    }
                    else {
                        minor[i - dI][j - dJ] = matrix[i][j];
                    }
                }
            }
        }
        return minor;
    }

    /**
     * Умножение двух матриц.
     * Кол-во колонок первой матрицы должно быть равно кол-ву строк второй матрицы
     * @param m1 первая матрица L x M
     * @param m2 вторая матрица M x N
     * @return результирующая матрица L x N
     * @throws MatrixMismatchException
     */
    public static double[][] multiplication(double[][] m1, double[][] m2) throws MatrixMismatchException {
        int m1_cols = m1[0].length;
        int m1_rows = m1.length;
        int m2_cols = m2[0].length;
        int m2_rows = m2.length;

        if (m1_cols != m2_rows) throw new MatrixMismatchException("Column count of matrix 1 not equals rows count of matrix 2.");

        double[][] result = new double[m1_rows][m2_cols];

        for (int i = 0; i < m1_rows; i++) {
            for (int j = 0; j < m2_cols; j++) {
                double elem = 0;
                for (int k = 0; k < m1_cols; k++) {
                    elem += m1[i][k] * m2[k][j];
                }
                result[i][j] = elem;
            }
        }
        return result;
    }
}
