package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import com.work.vladimirs.utils.ImageUtils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class MarkersFormer {

    private static final double DISTANCE_BETWEEN_MARKERS_AND_LINE = 2.0;
    private static final double REDUCTION_RATIO_LENGTH = 0.2;
    private static final double DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES = 100.0;

    private Mat vectorOfLines;
    private Mat sourceMat;

    public MarkersFormer(Mat vectorOfLines, Mat sourceMat) {
        this.vectorOfLines = vectorOfLines;
        this.sourceMat = sourceMat;
    }

    public Mat prepareMaskOfMarkers() {
        // Создание маркерного изображения для алгоритма водоразделов
        Mat maskWithMarker = new Mat(sourceMat.size(), CvType.CV_32S, ImageUtils.COLOR_BLACK);
        //Получаем все вектора ввиде массива
        ArrayList<Line> lines = getArrayOfLines(vectorOfLines);

        lines = LineValidator.findCollinearNearby(lines, lines.get(0), DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES);

        for (Line currentLine : lines) {
            //Проверяем линию на "надёжность" по длине
            if (!LineValidator.validateLineLength(currentLine)) continue;

            Line firstMarker = new Line();
            Line secondMarker = new Line();
            //Находим параллельные маркеры для этой линии, лежащие на определенном растоянии от линии
            findParallelMarkers(currentLine, firstMarker, secondMarker, DISTANCE_BETWEEN_MARKERS_AND_LINE);

            //Уменьшаем маркер, чтобы он был чуть меньше границы объекта
            reduceMarkerLength(firstMarker, REDUCTION_RATIO_LENGTH);
            reduceMarkerLength(secondMarker, REDUCTION_RATIO_LENGTH);

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
        return maskWithMarker;
    }

    private ArrayList<Line> getArrayOfLines(Mat vectorOfLines) {
        ArrayList<Line> lineArrayList = new ArrayList<>();
        Line currentLine;
        //Бежим по всем найденным линиям
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                //получаем координаты начальной и конечной точек линии
                double[] line = vectorOfLines.get(i, j);
                //todo ВНИМАНИЕ! ИНВЕРТИРУЕМ ОСИ КООРДИНАТ!
                currentLine = new Line(new Point(line[1], line[0]), new Point(line[3], line[2]));
                lineArrayList.add(currentLine);
            }
        }
        return lineArrayList;
    }

    private void createMaskWithMarker(Line m, Mat maskWithMarker, Scalar color) {
        //fixme Тип матрицы поменял на 32-х битную
        // todo ВНИМАНИЕ! РЕ_ИНВЕРТИРУЕМ ОСИ КООРДИНАТ!
        // Рисуем маркеры
        Point invert_start = new Point(m.getStartPoint().y, m.getStartPoint().x);
        Point invert_end = new Point(m.getEndPoint().y, m.getEndPoint().x);

        //Mat imageWithMarker = new Mat(sourceMat.size(), CvType.CV_8UC1, ImageUtils.COLOR_BLACK);
        Imgproc.line(
                maskWithMarker,
                invert_start,
                invert_end,
                color,
                1,
                4);
    }

    private void reduceMarkerLength(Line m, double ratio) {
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

    private void findParallelMarkers(Line currentLine, Line m1, Line m2, double distance) {
        Point lineStart = currentLine.getStartPoint();
        Point lineEnd = currentLine.getEndPoint();

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
