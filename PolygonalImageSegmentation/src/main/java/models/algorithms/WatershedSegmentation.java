package models.algorithms;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 */
public class WatershedSegmentation implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(WatershedSegmentation.class);

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
        Mat result = new Mat();
        markers.convertTo(result, CvType.CV_8U);

        return result;
    }
}
