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
     * Нахождение обратной матрицы {A(-1)}
     * Матрица должна быть квадратная.
     * 1. Найти определитель матрицы {det(A)}
     * 2. Найти матрицу миноров
     * 3. Находим матрицу алгебраических дополнений {A(*)}
     * 4. Находим транспонированную матрицу алгебраических дополнений {A(T*)}
     * 5. Вычисляем по формуле A(-1) = (1/det(A))*A(T*)
     * @param matrix исходная матрица
     * @return обратную матрицу
     * @throws MatrixMismatchException
     */
    public static double[][] inverse(double[][] matrix) throws MatrixMismatchException {
        int size = matrix.length;
        //Найдём определитель матрицы
        double det_matrix = determinant(matrix);
        if (Double.compare(det_matrix, 0.0) == 0) throw new MatrixMismatchException("Inverse matrix not exists.");
        //Находим транспонированную матрицу алгебраических дополнений соответствующих элементов матрицы matrix
        //Находим матрицу миноров - соответствует размеру исходной матрице
        double[][] minorMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double minorOfElementMatrix = determinant(getMinor(matrix, i, j));
                minorMatrix[i][j] = minorOfElementMatrix;
            }
        }
        //Находим матрицу алгебраических дополнений
        double[][] matrixOfAlgebraicComplements = getmatrixOfAlgebraicComplements(minorMatrix);
        //Транспонируем матрицу алгебраических дополнений
        double[][] transposeMatrixOfAlgebraicComplements = transpose(matrixOfAlgebraicComplements);
        //Вычисляем обратную матрицу по формуле
        double[][] result = multiplication(1 / det_matrix, transposeMatrixOfAlgebraicComplements);
        return result;
    }

    /**
     * Получение матрицы алгебраических дополнений
     * @param matrix исходная матрица
     * @return матрица алгебраических дополнений
     */
    private static double[][] getmatrixOfAlgebraicComplements(double[][] matrix) {
        int coefficient;
        int size = matrix.length;
        double[][] matrixOfAlgebraicComplements = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                coefficient = (i % 2 == 1) ? -1 : 1;
                matrixOfAlgebraicComplements[i][j] = coefficient * matrix[i][j];
            }
        }
        return matrixOfAlgebraicComplements;
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

        if (m_rows != m_cols) throw new MatrixMismatchException("Can only be calculated for a square matrix.");

        if (m_rows == 2) {
            calcResult = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            int koeff = 1;
            for(int i = 0; i < m_rows; i++) {
                if(i % 2 == 1) {
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
     * Получение минора (matrix.length-1)-го порядка матрицы
     */
    private static double[][] getMinor(double[][] matrix, int row, int column) {
        int minorLength = matrix.length - 1;
        double[][] minor = new double[minorLength][minorLength];
        //переменные для того, чтобы "пропускать" ненужные нам строку и столбец
        int dI = 0;
        int dJ = 0;
        for (int i = 0; i <= minorLength; i++) {
            dJ = 0;
            for (int j = 0; j <= minorLength; j++) {
                if (i == row) {
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

    /**
     * Умножение матрицы на число.
     * @param value число
     * @param m матрица
     * @return матрица, умноженная на число
     */
    public static double[][]  multiplication(double value, double[][] m){
        int size = m.length;
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = value * m[i][j];
            }
        }
        return result;
    }
}
