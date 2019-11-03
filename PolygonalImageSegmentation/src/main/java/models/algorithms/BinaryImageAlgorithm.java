package models.algorithms;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Алгоритм бинаризации изображения, т.е. конвертация всех пикселей либо в 0 либо в 1 (чёрно/белая матрица)
 */
public class BinaryImageAlgorithm implements Algorithm{

    private final static Logger LOGGER = LoggerFactory.getLogger(BinaryImageAlgorithm.class);

    private int threshold;
    private boolean isOtsu;

    public BinaryImageAlgorithm(int threshold, boolean isOtsu) {
        this.threshold = threshold;
        this.isOtsu = isOtsu;
    }

    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");
        //Конвертируем изображение в одноканальное
        Mat matGray = new Mat();
        Imgproc.cvtColor(frame, matGray, Imgproc.COLOR_BGR2GRAY);

        //Перевод в бинарное изображение
        Mat matBinary = new Mat();
        if (isOtsu) {
            Imgproc.threshold(matGray, matBinary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        } else {
            Imgproc.threshold(matGray, matBinary, threshold, 255, Imgproc.THRESH_BINARY);
        }

        return matBinary;
    }
}
