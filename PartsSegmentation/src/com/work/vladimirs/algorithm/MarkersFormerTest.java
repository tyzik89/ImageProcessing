package com.work.vladimirs.algorithm;

import com.work.vladimirs.utils.ImageUtils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class MarkersFormerTest {

    private Mat vectorOfLines;
    private Mat sourceMat;

    public MarkersFormerTest(Mat vectorOfLines, Mat sourceMat) {
        this.vectorOfLines = vectorOfLines;
        this.sourceMat = sourceMat;
    }

    public Mat prepareMaskOfMarkers() {
        // Готовые маркеры
        Mat maskOfMarkers = new Mat(sourceMat.size(), CvType.CV_32SC1, ImageUtils.COLOR_BLACK);

        //Бежим по всем найденным линиям
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                //получаем линию ввиде координат начальной и конечной точек
                double[] line = vectorOfLines.get(i, j);
                //System.out.println("(X1;Y1) = " + "(" + line[0] + ";" + line[1] + ")" + "\n" + "(X2;Y2) = "  + "(" + line[2] + ";" + line[3] + ")" + "\n");
                //У линии контура находим координаты точек для двух параллельных линий-маркеров
                //Рисуем линии будущих маркеров на расстоянии distance от найденной линии
                findAndDrawParallelMarkers(line, maskOfMarkers, 2.0);
            }
            //break;
        }
        return maskOfMarkers;
    }

    private void findAndDrawParallelMarkers(double[] line, Mat maskOfMarkers, double distance) {
        Point lineStart = new Point(line[0], line[1]);
        Point lineEnd = new Point(line[2], line[3]);

        double dx = lineEnd.x - lineStart.x;
        double dy = lineEnd.y - lineStart.y;

        double len = Math.sqrt(dx*dx+dy*dy);
        //Координаты направляющего вектора
        double udx = dx / len;
        double udy = dy / len;
        //Перпендикулярный вектор к направляющему
        double perpx = -udy * distance;
        double perpy = udx * distance;

        // "left" line start
        Point leftMarkerStart = new Point(lineStart.x + perpx, lineStart.y + perpy);
        // "left" line end
        Point leftMarkerEnd = new Point(lineEnd.x + perpx, lineEnd.y + perpy);
        // "right" line start
        Point rightMarkerStart = new Point(lineStart.x - perpx, lineStart.y - perpy);
        // "right" line end
        Point rightMarkerEnd = new Point(lineEnd.x - perpx, lineEnd.y - perpy);

        //уменьшаем линию маркера, чтобы она вписывалась в объект
        reduceLineLength(leftMarkerStart, leftMarkerEnd, 0.2);
        reduceLineLength(rightMarkerStart, rightMarkerEnd, 0.2);

        //Находим координаты центра каждого маркера.
        Point leftMarkerCenter = new Point((leftMarkerStart.x + leftMarkerEnd.x) / 2, (leftMarkerStart.y + leftMarkerEnd.y) / 2 );
        Point rightMarkerCenter = new Point((rightMarkerStart.x + rightMarkerEnd.x) / 2, (rightMarkerStart.y + rightMarkerEnd.y) / 2 );
        //По координатам центров маркеров получаем пиксели оригинального изображения
        Mat originalMat = sourceMat;
        double[] leftMarkerCenterPixel = originalMat.get( (int) leftMarkerCenter.x, (int) leftMarkerCenter.y);
        double[] rightMarkerCenterPixel = originalMat.get( (int) rightMarkerCenter.x, (int) rightMarkerCenter.y);
        System.out.println(Arrays.toString(leftMarkerCenterPixel));
        System.out.println(Arrays.toString(rightMarkerCenterPixel));
        //Определяем яркость этих пикселей. Фон ТЕМНЕЕ, это значит что это маркер фона.
        double brightnessLeft = (leftMarkerCenterPixel[0] + leftMarkerCenterPixel[1] + leftMarkerCenterPixel[2]) / 3;
        double brightnessRight = (rightMarkerCenterPixel[0] + rightMarkerCenterPixel[1] + rightMarkerCenterPixel[2]) / 3;

        //fixme сравнение double сделано плохо + плохо сделано условие отрисовки - изьбыточный код
        if (brightnessLeft < brightnessRight) {
            drawLines(maskOfMarkers, leftMarkerStart, leftMarkerEnd, 80);
            drawLines(maskOfMarkers, rightMarkerStart, rightMarkerEnd, 200);
        } else if (brightnessLeft > brightnessRight) {
            drawLines(maskOfMarkers, leftMarkerStart, leftMarkerEnd, 200);
            drawLines(maskOfMarkers, rightMarkerStart, rightMarkerEnd, 80);
        }
    }

    private void reduceLineLength(Point start, Point end, double ratio) {
        double newX = (start.x + ratio * end.x) / (1 + ratio);
        double newY = (start.y + ratio * end.y) / (1 + ratio);
        //System.out.println(newX + " " + newY);
        double[] newStart = {newX, newY};

        ratio = 1 / ratio;

        newX = (start.x + ratio * end.x) / (1 + ratio);
        newY = (start.y + ratio * end.y) / (1 + ratio);
        double[] newEnd = {newX, newY};

        start.set(newStart);
        end.set(newEnd);
    }

    private void drawLines(Mat maskOfMarkers, Point start, Point end, int color) {
        //Маска с маркерами для одной линии
        Mat maskOfLines = new Mat(sourceMat.size(), CvType.CV_8U, ImageUtils.COLOR_BLACK);
        Imgproc.line(
                maskOfLines,
                start, end,
                ImageUtils.COLOR_GRAY,
                1,
                4);
        drawMarkers(maskOfMarkers, maskOfLines, color);
        //ShowImage.show(ImageUtils.matToImageFX(maskOfLines));
    }

    /**
     * Отрисовка маркеров разным цветом (по уровням сегментации) используя маску
     */
    private Mat drawMarkers(Mat maskOfMarkers, Mat maskOfLines, int color) {
        //Подготовка маркеров для отрисовки
        // Находим контуры маркеров
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(maskOfLines, contours, new Mat(),
                Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE);
        // Отрисовываем контуры разными оттенками серого
        for (int i = 0, j = contours.size(); i < j; i++) {
            Imgproc.drawContours(maskOfMarkers, contours, i, Scalar.all(color), 1);
        }

        return maskOfMarkers;
    }
}
