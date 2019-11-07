package models.algorithms;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Алгоритм водоразделов - алгоритм наращивания областей, рекурсивно выполняющих процедуру группировки пикселей в подобласти по заранее заданным критериям.
 */
public class WatershedSegmentation implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(WatershedSegmentation.class);

    //Матрица с маркерами
    private Mat markers;

    public WatershedSegmentation(Mat markers) {
        this.markers = markers;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        //Преобразовываем матрицу в 3-х канальную, 8-битовую
        Mat frame8SC3 = new Mat();
        Imgproc.cvtColor(frame, frame8SC3, Imgproc.COLOR_BGRA2BGR);

        //Применяем алгоритм водоразделов
        Imgproc.watershed(frame8SC3, markers);

        // Отображаем результат
        Mat result = Mat.zeros(markers.size(), CvType.CV_8U);
        markers.convertTo(result, CvType.CV_8UC1);

        return result;
    }
}
