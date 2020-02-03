package com.work.vladimirs.utils;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ColorScaleUtils {

    public static Mat doGrayscale(Mat rgbSegment) {
        //Конвертируем изображение в одноканальное
        Mat matGray = new Mat();
        Imgproc.cvtColor(rgbSegment, matGray, Imgproc.COLOR_BGR2GRAY);
        return matGray;
    }

    public static Mat doBinary (Mat graySegment) {
        //Перевод в бинарное изображение
        Mat matBinary = new Mat();
        Imgproc.threshold(graySegment, matBinary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        return matBinary;
    }
}
