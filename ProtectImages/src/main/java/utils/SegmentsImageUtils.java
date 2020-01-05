package utils;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class SegmentsImageUtils {

    /**
     * Разбиение изображения на заданные сегменты
     */
    public static ArrayList<Mat> analyze(Mat source, int sizeSegment) {
        ArrayList<Mat> matArrayList = new ArrayList<>();

        int width = source.cols();
        int height = source.rows();

        int rowStart = 0, colStart = 0;
        int rowEnd = sizeSegment, colEnd = sizeSegment;

        while (rowEnd < height) {
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + sizeSegment + 1;
            }

            colEnd = width;
            Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
            //System.out.println(resultMat.size().toString());
            matArrayList.add(resultMat);

            rowStart = rowEnd + 1;
            rowEnd = rowEnd + sizeSegment + 1;

            colStart = 0;
            colEnd = sizeSegment;
        }

        if (rowEnd >= height) {
            rowEnd = height;
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + sizeSegment + 1;
            }

            colEnd = width;
            Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
            //System.out.println(resultMat.size().toString());
            matArrayList.add(resultMat);
        }
        return matArrayList;
    }


    /**
     * Соединение изображения из сегментов
     */
    public static void synthesis(ArrayList<Mat> segments, Mat originalMat) {
        //извлекаем массив байт из оригинального изображения
        byte[] byteBufferOriginalMat = new byte[originalMat.cols() * originalMat.rows() * originalMat.channels()];
        originalMat.get(0, 0, byteBufferOriginalMat);

        //текущая позиция в оригинальном массиве
        int position = 0;
        //бежим по каждому сегменту
        for (Mat segment : segments) {
            //извлекаем массив байт из каждого сегмента
            byte[] byteBufferSegment = new byte[segment.cols() * segment.rows() * segment.channels()];
            segment.get(0, 0, byteBufferSegment);
            //каждый байт сегмента помещаем в позицию байта оригинального изображения, с запоминанием позиции байта оригинала
            for (int i = 0; i < byteBufferSegment.length; i++, position++) {
                byteBufferOriginalMat[position] = byteBufferSegment[i];
            }
        }
        //заполненный массив байт помещаем обратно в оригинальную матрицу
        originalMat.put(0, 0, byteBufferOriginalMat);
    }
}
