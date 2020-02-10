package models.algorithms;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Алгоритм водоразделов - алгоритм наращивания областей, рекурсивно выполняющих процедуру группировки пикселей в подобласти по заранее заданным критериям.
 */
public class WatershedSegmentation implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(WatershedSegmentation.class);

    //Матрица с маркерами
    private Mat markers;
    private Mat currentMat;

    public WatershedSegmentation(Mat markers, Mat currentMat) {
        this.markers = markers;
        this.currentMat = currentMat;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        //Преобразовываем матрицу в 3-х канальную, 8-битовую
        Mat frame8SC3 = new Mat();
        Imgproc.cvtColor(currentMat, frame8SC3, Imgproc.COLOR_BGRA2BGR);

        //Применяем алгоритм водоразделов
        Imgproc.watershed(frame8SC3, markers);

        // Отображаем результат
        Mat result = new Mat();
        markers.convertTo(result, CvType.CV_8U);

        return result;
    }
}
