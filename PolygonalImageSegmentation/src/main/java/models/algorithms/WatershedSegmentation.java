package models.algorithms;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

import java.util.*;

/**
 * Алгоритм водоразделов - алгоритм наращивания областей, рекурсивно выполняющих процедуру группировки пикселей в подобласти по заранее заданным критериям.
 */
public class WatershedSegmentation implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(WatershedSegmentation.class);

    //Матрица с маркерами
    private Mat markers;

    //Цветная контурная карта
    private Map<Scalar, ArrayList<MatOfPoint>> scalarContoursMap;

    public WatershedSegmentation(Mat markers) {
        this.markers = markers;
    }

    public WatershedSegmentation(Mat markers, Map<Scalar, ArrayList<MatOfPoint>> scalarContoursMap) {
        this.markers = markers;
        this.scalarContoursMap = scalarContoursMap;
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
        Mat result = Mat.zeros(markers.size(), CvType.CV_8UC3);
        //markers.convertTo(result, CvType.CV_8UC1);

        //TODO Рисуем цветные сегменты
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        for (Map.Entry<Scalar, ArrayList<MatOfPoint>> scalarArrayListEntry : scalarContoursMap.entrySet()) {
            contours.addAll(scalarArrayListEntry.getValue());
        }

        // Generate random colors
        Random rng = new Random(12345);
        List<Scalar> colors = new ArrayList<>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            int b = rng.nextInt(256);
            int g = rng.nextInt(256);
            int r = rng.nextInt(256);
            colors.add(new Scalar(b, g, r));
        }
        // Заполнение помеченных объектов цветами
        byte[] dstData = new byte[(int) (result.total() * result.channels())];
        result.get(0, 0, dstData);
        // Fill labeled objects with random colors
        int[] markersData = new int[(int) (markers.total() * markers.channels())];
        markers.get(0, 0, markersData);
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                int index = markersData[i * markers.cols() + j];
                if (index > 0 && index <= contours.size()) {
                    dstData[(i * result.cols() + j) * 3 + 0] = (byte) colors.get(index - 1).val[0];
                    dstData[(i * result.cols() + j) * 3 + 1] = (byte) colors.get(index - 1).val[1];
                    dstData[(i * result.cols() + j) * 3 + 2] = (byte) colors.get(index - 1).val[2];
                }
               /* else {
                    dstData[(i * result.cols() + j) * 3 + 0] = 0;
                    dstData[(i * result.cols() + j) * 3 + 1] = 0;
                    dstData[(i * result.cols() + j) * 3 + 2] = 0;
                }*/
            }
        }
        result.put(0, 0, dstData);
        //TODO Рисуем цветные сегменты

        return result;
    }
}
