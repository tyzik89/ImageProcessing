package com.work.vladimirs.algorithm.methods;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Фильтр Гаусса используется для снижения уровня шумов и даёт размытие
 */
public class GaussBlurAlgorithm implements Algorithm{

    /**
     * Размер ядра фильтра
     */
    private int sizeGaussFilter;

    public GaussBlurAlgorithm(int sizeGaussFilter) {
        this.sizeGaussFilter = sizeGaussFilter;
    }

    /**
     * @param frame исходная матрица изображения
     * @return размытую матрицу
     */
    public Mat doAlgorithm(Mat frame) {

        if (sizeGaussFilter > 0)
            Imgproc.blur(frame, frame, new Size(sizeGaussFilter, sizeGaussFilter));
        return frame;
    }
}
