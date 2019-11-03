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

    private Mat mask;

    public WatershedSegmentation(Mat mask) {
        this.mask = mask;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        //Преобраовываем матрицу в 3-х канальное, 8-битовое
        Mat frame8SC3 = new Mat();
        Imgproc.cvtColor(frame, frame8SC3, Imgproc.COLOR_BGRA2BGR);

        // Находим контуры маркеров
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mask, contours, new Mat(),
                Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // Отрисовываем контуры разными оттенками серого
        Mat markers = new Mat(frame8SC3.size(), CvType.CV_32SC1,  new Scalar(0));
        int color = 80;
        for (int i = 0, j = contours.size(); i < j; i++) {
            Imgproc.drawContours(markers, contours, i, Scalar.all(color), 1);
            color += 20;
        }

        Imgproc.watershed(frame8SC3, markers);

        // Отображаем результат
        Mat result = new Mat();
        markers.convertTo(result, CvType.CV_8U);

        return result;
    }
}
