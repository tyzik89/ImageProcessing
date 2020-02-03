package com.work.vladimirs.algorithm.methods;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
/**
 * Алгоритм водоразделов - алгоритм наращивания областей, рекурсивно выполняющих процедуру группировки пикселей в подобласти по заранее заданным критериям.
 */
public class WatershedSegmentation implements Algorithm {

    //Матрица с маркерами
    private Mat markers;

    public WatershedSegmentation(Mat markers) {
        this.markers = markers;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        //Преобразовываем матрицу в 3-х канальную, 8-битовую
        Mat frame8SC3 = new Mat();
        Imgproc.cvtColor(frame, frame8SC3, Imgproc.COLOR_BGRA2BGR);

        //Применяем алгоритм водоразделов
        Imgproc.watershed(frame8SC3, markers);

        // Отображаем результат
        Mat result = new Mat();
        markers.convertTo(result, CvType.CV_8UC3);

        return result;
    }
}
