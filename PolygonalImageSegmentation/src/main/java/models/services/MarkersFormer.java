package models.services;

import models.entities.Line;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import utils.ImageUtils;

public class MarkersFormer {

    private double distanceOfMarkers;
    private double ratioLength;
    private Mat vectorOfLines;
    private Mat sourceMat;

    public MarkersFormer(Mat vectorOfLines, Mat sourceMat, double distanceOfMarkers, double ratioLength) {
        this.vectorOfLines = vectorOfLines;
        this.sourceMat = sourceMat;
        this.distanceOfMarkers = distanceOfMarkers;
        this.ratioLength = ratioLength;
    }

    public Mat prepareMaskOfMarkers() {
        // Создание маркерного изображения для алгоритма водоразделов
        Mat maskWithMarker = new Mat(sourceMat.size(), CvType.CV_32S, ImageUtils.COLOR_BLACK);
        Line currentLine;

        //Бежим по всем найденным линиям
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                //получаем координаты начальной и конечной точек линии
                double[] line = vectorOfLines.get(i, j);
                //todo ВНИМАНИЕ! ИНВЕРТИРУЕМ ОСИ КООРДИНАТ!
                currentLine = new Line(new Point(line[1], line[0]), new Point(line[3], line[2]));

                //Проверяем линию на "надёжность" по длинне
                if (!LineValidator.validateLineLength(currentLine)) continue;

                Line firstMarker = new Line();
                Line secondMarker = new Line();
                //Находим параллельные маркеры для этой линии, лежащие на определенном растоянии от линии
                findParallelMarkers(currentLine, firstMarker, secondMarker, distanceOfMarkers);

//                System.out.println("Исходная линия: " + "start:{" + currentLine.getStartPoint().x + "," + currentLine.getStartPoint().y + "}," + " end:{" + currentLine.getEndPoint().x + "," + currentLine.getEndPoint().y + "}");
//                System.out.println("Маркер_1: " + "start:{" + firstMarker.getStartPoint().x + "," + firstMarker.getStartPoint().y + "}," + " end:{" + firstMarker.getEndPoint().x + "," + firstMarker.getEndPoint().y + "}");
//                System.out.println("Маркер_2: " + "start:{" + secondMarker.getStartPoint().x + "," + secondMarker.getStartPoint().y + "}," + " end:{" + secondMarker.getEndPoint().x + "," + secondMarker.getEndPoint().y + "}");

                //Уменьшаем маркер, чтобы он был чуть меньше границы объекта
                reduceMarkerLength(firstMarker, ratioLength);
                reduceMarkerLength(secondMarker, ratioLength);

//                System.out.println("Маркер_1_reduce: " + "start:{" + firstMarker.getStartPoint().x + "," + firstMarker.getStartPoint().y + "}," + " end:{" + firstMarker.getEndPoint().x + "," + firstMarker.getEndPoint().y + "}");
//                System.out.println("Маркер_2_reduce: " + "start:{" + secondMarker.getStartPoint().x + "," + secondMarker.getStartPoint().y + "}," + " end:{" + secondMarker.getEndPoint().x + "," + secondMarker.getEndPoint().y + "}");

//                ShowImage.drawPointsBetweenTwoPoints(sourceMat, currentLine.getStartPoint().x, currentLine.getStartPoint().y, currentLine.getEndPoint().x, currentLine.getEndPoint().y, new double[]{255, 0, 0});
//                ShowImage.drawPointsBetweenTwoPoints(sourceMat, firstMarker.getStartPoint().x, firstMarker.getStartPoint().y, firstMarker.getEndPoint().x, firstMarker.getEndPoint().y, new double[]{0, 0, 255});
//                ShowImage.drawPointsBetweenTwoPoints(sourceMat, secondMarker.getStartPoint().x, secondMarker.getStartPoint().y, secondMarker.getEndPoint().x, secondMarker.getEndPoint().y, new double[]{0, 0, 255});
//                ShowImage.show(ImageUtils.matToImageFX(sourceMat), "LINES");

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

/*        // Находим контуры маркеров
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageWithMarker, contours, new Mat(),
                Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // Отрисовываем контуры нужным цветом
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(maskWithMarker, contours, i, color, 1);
        }*/

         //Отрисовка отдельного маркера
//        Mat markers = new Mat();
//        maskWithMarker.convertTo(markers, CvType.CV_8U);
//        ShowImage.show(ImageUtils.matToImageFX(markers), "Mask with marker");
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