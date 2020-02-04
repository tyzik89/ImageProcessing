package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Marker;
import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

public class MarkersFormer {

    private static final double distanceOfMarkers = 1.0;
    private static final double ratioLength = 0.2;
    private Mat vectorOfLines;
    private Mat sourceMat;

    public MarkersFormer(Mat vectorOfLines, Mat sourceMat) {
        this.vectorOfLines = vectorOfLines;
        this.sourceMat = sourceMat;
    }

    public Mat prepareMaskOfMarkers() {
        // Готовые маркеры
        Mat maskOfMarkers = new Mat(sourceMat.size(), CvType.CV_32SC1, ImageUtils.COLOR_BLACK);
        Point startPointOfLine;
        Point endPointOfLine;

        //Бежим по всем найденным линиям
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                //получаем координаты начальной и конечной точек линии
                double[] line = vectorOfLines.get(i, j);
                startPointOfLine = new Point(line[0], line[1]);
                endPointOfLine = new Point(line[2], line[3]);
                //System.out.println(startPointOfLine.x + " " + startPointOfLine.y);

                Marker firstMarker = new Marker();
                Marker secondMarker = new Marker();
                //Находим параллельные маркеры для этой линии на определенном растоянии от линии
                findParallelMarkers(startPointOfLine, endPointOfLine, firstMarker, secondMarker, distanceOfMarkers);

                //Уменьшаем маркер, чтобы он был чуть меньше границы объекта
                reduceMarkerLength(firstMarker, ratioLength);
                reduceMarkerLength(secondMarker, ratioLength);
                //System.out.println(firstMarker.getStartPoint().x + " " + firstMarker.getStartPoint().y);

                //Определяем тип маркера, сравнивая фон оригинального изображения
                MarkersGradientComparator comparator = new MarkersGradientComparator(sourceMat);
                comparator.compare(firstMarker, secondMarker);

                createMaskWithMarker(firstMarker);
            }
            break;
        }
        return maskOfMarkers;
    }

    private Mat createMaskWithMarker(Marker m) {
        //Маска с маркером
        Mat maskWithMarker = new Mat(sourceMat.size(), CvType.CV_8UC1, ImageUtils.COLOR_BLACK);
        Imgproc.line(
                maskWithMarker,
                m.getStartPoint(),
                m.getEndPoint(),
                ImageUtils.COLOR_WHITE,
                1,
                4);
        ShowImage.show(ImageUtils.matToImageFX(maskWithMarker), "Mask with marker");
        return maskWithMarker;
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
