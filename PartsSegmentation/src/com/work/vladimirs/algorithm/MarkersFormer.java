package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Marker;
import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MarkersFormer {

    private static final double distanceOfMarkers = 5.0;
    private static final double ratioLength = 0.2;
    private Mat vectorOfLines;
    private Mat sourceMat;

    public MarkersFormer(Mat vectorOfLines, Mat sourceMat) {
        this.vectorOfLines = vectorOfLines;
        this.sourceMat = sourceMat;
    }

    public Mat prepareMaskOfMarkers() {
        // Готовые маркеры
        Mat maskWithMarker = new Mat(sourceMat.size(), CvType.CV_32SC1, ImageUtils.COLOR_BLACK);
        Point startPointOfLine;
        Point endPointOfLine;

        //Бежим по всем найденным линиям
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                //получаем координаты начальной и конечной точек линии
                double[] line = vectorOfLines.get(i, j);
                startPointOfLine = new Point(line[0], line[1]);
                endPointOfLine = new Point(line[2], line[3]);

                //Проверяем линию на "надёжность"
                if (!LineValidator.validateLine(startPointOfLine, endPointOfLine)) continue;

                Marker firstMarker = new Marker();
                Marker secondMarker = new Marker();
                //Находим параллельные маркеры для этой линии на определенном растоянии от линии
                findParallelMarkers(startPointOfLine, endPointOfLine, firstMarker, secondMarker, distanceOfMarkers);

                System.out.println("Граница: " + "start:{" + startPointOfLine.x + "," + startPointOfLine.y + "}," + " end:{" + endPointOfLine.x + "," + endPointOfLine.y + "}");
                System.out.println("Маркер_1: " + "start:{" + firstMarker.getStartPoint().x + "," + firstMarker.getStartPoint().y + "}," + " end:{" + firstMarker.getEndPoint().x + "," + firstMarker.getEndPoint().y + "}");
                System.out.println("Маркер_2: " + "start:{" + secondMarker.getStartPoint().x + "," + secondMarker.getStartPoint().y + "}," + " end:{" + secondMarker.getEndPoint().x + "," + secondMarker.getEndPoint().y + "}");

                //Уменьшаем маркер, чтобы он был чуть меньше границы объекта
                reduceMarkerLength(firstMarker, ratioLength);
                reduceMarkerLength(secondMarker, ratioLength);

                System.out.println("Маркер_1_reduce: " + "start:{" + firstMarker.getStartPoint().x + "," + firstMarker.getStartPoint().y + "}," + " end:{" + firstMarker.getEndPoint().x + "," + firstMarker.getEndPoint().y + "}");
                System.out.println("Маркер_2_reduce: " + "start:{" + secondMarker.getStartPoint().x + "," + secondMarker.getStartPoint().y + "}," + " end:{" + secondMarker.getEndPoint().x + "," + secondMarker.getEndPoint().y + "}");

                //Определяем тип маркера, сравнивая фон оригинального изображения
                //Фон ТЕМНЕЕ, это значит что это маркер фона.
                MarkersGradientComparator gradientComparator = new MarkersGradientComparator(sourceMat);
                int comp = gradientComparator.compare(firstMarker, secondMarker);
                if (comp < 0) {
                    createMaskWithMarker(firstMarker, maskWithMarker, ImageUtils.COLOR_GRAY);
                    createMaskWithMarker(secondMarker, maskWithMarker, ImageUtils.COLOR_WHITE);
                } else if (comp > 0) {
                    createMaskWithMarker(firstMarker, maskWithMarker, ImageUtils.COLOR_WHITE);
                    createMaskWithMarker(secondMarker, maskWithMarker, ImageUtils.COLOR_GRAY);
                }
            }
        }
        return maskWithMarker;
    }

    private void createMaskWithMarker(Marker m, Mat maskWithMarker, Scalar color) {
        //fixme Тип матрицы поменял на 32-х битную
        //Маска с маркером
        //Mat maskWithMarker = new Mat(sourceMat.size(), CvType.CV_32SC1, ImageUtils.COLOR_BLACK);
        Imgproc.line(
                maskWithMarker,
                m.getStartPoint(),
                m.getEndPoint(),
                color,
                1,
                4);

         //Отрисовка отдельного маркера
//        Mat markers = new Mat();
//        maskWithMarker.convertTo(markers, CvType.CV_8U);
//        ShowImage.show(ImageUtils.matToImageFX(markers), "Mask with marker");
    }

    private void reduceMarkerLength(Marker m, double ratio) {
        Point start = m.getStartPoint();
        Point end = m.getEndPoint();

        double newX = (start.x + ratio * end.x) / (1 + ratio);
        double newY = (start.y + ratio * end.y) / (1 + ratio);
        double[] newStart = {newX, newY};

        ratio = 1 / ratio;

        newX = (start.x + ratio * end.x) / (1 + ratio);
        newY = (start.y + ratio * end.y) / (1 + ratio);
        double[] newEnd = {newX, newY};

        m.setStartPoint(new Point(newStart));
        m.setEndPoint(new Point(newEnd));
    }

    private void findParallelMarkers(Point lineStart, Point lineEnd, Marker m1, Marker m2, double distance) {
        double delta_x = lineEnd.x - lineStart.x;
        double delta_y = lineEnd.y - lineStart.y;
        double lineLength = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        //Координаты направляющего вектора
        double udx = delta_x / lineLength;
        double udy = delta_y / lineLength;
        //Перпендикулярный вектор к направляющему
        double perpx = -udy * distance;
        double perpy = udx * distance;

        m1.setStartPoint(new Point(lineStart.x + perpx, lineStart.y + perpy));
        m1.setEndPoint(new Point(lineEnd.x + perpx, lineEnd.y + perpy));

        m2.setStartPoint(new Point(lineStart.x - perpx, lineStart.y - perpy));
        m2.setEndPoint(new Point(lineEnd.x - perpx, lineEnd.y - perpy));
    }
}
