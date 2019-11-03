package models.algorithms;

import models.image.StorageMatrix;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

/**
 * Преобразование Хафа (Hough Transform) — это метод для поиска линий, кругов и других простых форм на изображении.
 *
 * Преобразование Хафа предназначено для поиска объектов, принадлежащих определённому классу фигур с использованием процедуры голосования.
 * Процедура голосования применяется к пространству параметров,
 * из которого и получаются объекты определённого класса фигур по локальному максимуму в так называемом,
 * накопительном пространстве (accumulator space) которое строится при вычислении трансформации Хафа.
 */
public class HoughConversionAlgorithm implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(HoughConversionAlgorithm.class);

    //Тип метода Хафа
    private boolean typeHoughMethodClassic;
    // Разрешение параметра r в пикселях
    private double distance;
    // Разрешение параметра θ в радианах
    private double angle;
    // Минимальное количество пересечений(голосов), чтобы "обнаружить" линию
    private int threshold;

    private double srn = 0.0;
    private double stn = 0.0;
    private double minTheta = 0.0;
    private double maxTheta = Math.PI;

    private double minLineLength = 0.0;
    private double maxLineGap = 0.0;

    public HoughConversionAlgorithm(boolean typeHoughMethod, double distance,  double angle, int threshold, double ... params) {
        this.typeHoughMethodClassic = typeHoughMethod;
        this.distance = distance;
        this.angle = angle;
        this.threshold = threshold;

        if (typeHoughMethodClassic) {
            this.srn = params[0];
            this.stn = params[1];
            this.minTheta = params[2];
            this.maxTheta = params[3];
        } else {
            this.minLineLength = params[0];
            this.maxLineGap = params[1];
        }
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        //Конвертируем изображение в одноканальное
        Mat matGray = new Mat();
        Imgproc.cvtColor(frame, matGray, Imgproc.COLOR_BGR2GRAY);

        //Вектор, который будет хранить параметры (r, θ) обнаруженных линий
        Mat lines = new Mat();
        //Результирующая матрица
        Mat result = new Mat(frame.size(), CvType.CV_8UC3, ImageUtils.COLOR_WHITE);

        if (typeHoughMethodClassic) {
            LOGGER.debug(
                    "Классический метод Хафа с параметрами: distance={}, angle(rad.)={}, threshold={}, srn={}, stn={}, minTheta={}, maxTheta={}",
                    distance, Math.toRadians(angle), threshold, srn, stn, minTheta, maxTheta);

            Imgproc.HoughLines(matGray, lines, distance, Math.toRadians(angle), threshold, srn, stn, minTheta, maxTheta);

            // Отрисовка линий
            for (int i = 0; i < lines.rows(); i++) {
                double data[] = lines.get(i, 0);
                double rho = data[0];
                double theta = data[1];

                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);

                double x0 = cosTheta * rho;
                double y0 = sinTheta * rho;

                Point pt1 = new Point(x0 + 1000 * (-sinTheta), y0 + 1000 * cosTheta);
                Point pt2 = new Point(x0 - 1000 * (-sinTheta), y0 - 1000 * cosTheta);
                Imgproc.line(
                        result,
                        pt1,
                        pt2,
                        ImageUtils.COLOR_RED,
                        1,
                        4);
            }

        } else {
            LOGGER.debug(
                    "Вероятностный метод Хафа с параметрами: distance={}, angle(rad.)={}, threshold={}, minLineLength={}, maxLineGap={}",
                    distance, Math.toRadians(angle), threshold, minLineLength, maxLineGap);

            Imgproc.HoughLinesP(matGray, lines, distance, Math.toRadians(angle), threshold, minLineLength, maxLineGap);
            for (int i = 0, r = lines.rows(); i < r; i++) {
                for (int j = 0, c = lines.cols(); j < c; j++) {
                    double[] line = lines.get(i, j);
                    Imgproc.line(
                            result,
                            new Point(line[0], line[1]),
                            new Point(line[2], line[3]),
                            ImageUtils.COLOR_RED,
                            1,
                            4);
                }
            }
        }

        //Сохраняем линии отдельно, для последующего использования
        StorageMatrix.getInstance().setMatrixOfLines(lines);

        return result;
    }
}
