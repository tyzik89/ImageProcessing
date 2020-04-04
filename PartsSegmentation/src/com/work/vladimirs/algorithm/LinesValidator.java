package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import com.work.vladimirs.algorithm.methods.AnalyticGeometry;
import com.work.vladimirs.utils.ColorScaleUtils;
import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class LinesValidator {

    //Максимальная дистанция между отрезками, которые считаются близко расположенными
    private static final double MAXIMAL_DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES = 25.0;
    private Mat originalMat;

    public LinesValidator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    public ArrayList<Line> validateByBarChart(ArrayList<Line> rawLines) {
        ArrayList<Line> approvedLines = new ArrayList<>();

        Mat maskWithMarker = new Mat(originalMat.size(), CvType.CV_8UC1, ImageUtils.COLOR_BLACK);
        createMaskWithMarker(rawLines, maskWithMarker, ImageUtils.COLOR_WHITE);

        //Получаем оригинальную матрицу в градациях серого
        Mat grayOriginalMat = new Mat();
        Imgproc.cvtColor(originalMat, grayOriginalMat, Imgproc.COLOR_BGR2GRAY);
        // Вычисляем гистограммы по каналам
        ArrayList<Mat> images = new ArrayList<Mat>();
        images.add(grayOriginalMat);
        Mat histGray = new Mat();
        //Вычисляем гистограмму
        Imgproc.calcHist(images, new MatOfInt(0), maskWithMarker, histGray, new MatOfInt(256), new MatOfFloat(0, 256));
        // Нормализация диапазона
//        Core.normalize(histGray, histGray, 0, 128, Core.NORM_MINMAX);
        // Отрисовка гистограмм
        double v = 0;
        int h = 150;
        Mat imgHistGray = new Mat(h, 256, CvType.CV_8UC1, ImageUtils.COLOR_WHITE);
        for (int i = 0, j = histGray.rows(); i < j; i++) {
            v = Math.round(histGray.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistGray, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_GRAY);
            }
        }
        ShowImage.show(ImageUtils.matToImageFX(imgHistGray), "Gray");
        //System.out.println(histGray.dump());
        return approvedLines;
    }

    private void createMaskWithMarker(ArrayList<Line> lines, Mat maskWithMarker, Scalar color) {
        //fixme Тип матрицы поменял на 32-х битную
        // todo ВНИМАНИЕ! РЕ_ИНВЕРТИРУЕМ ОСИ КООРДИНАТ!
        // Рисуем маркеры
        for (Line line : lines) {
            Point invert_start = new Point(line.getStartPoint().y, line.getStartPoint().x);
            Point invert_end = new Point(line.getEndPoint().y, line.getEndPoint().x);
            Imgproc.line(
                    maskWithMarker,
                    invert_start,
                    invert_end,
                    color,
                    1,
                    4);
        }
    }

    public ArrayList<Line> validateByGradient(ArrayList<Line> rawLines) {
        ArrayList<Line> approvedLines = new ArrayList<>();

        HashSet<Line> setAllLines = new HashSet<>(rawLines);
        HashSet<Line> setApprovedNearbyLines = new HashSet<>();
        ArrayList<ArrayList<Line>> pairOfCollinearNearbyLines = getPairOfCollinearNearbyLines(rawLines);

        //Для каждой пары линий делаем сравнение их яркостей, отбрасываем линии, принадлежащие фону
        for (ArrayList<Line> pairOfCollinearNearbyLine : pairOfCollinearNearbyLines) {
            //Удаляем подозрительную пару близкорасположенных линий
            setAllLines.removeAll(pairOfCollinearNearbyLine);
            GradientComparator gradientComparator = new GradientComparator(originalMat);
            int compared = gradientComparator.compare(pairOfCollinearNearbyLine.get(0), pairOfCollinearNearbyLine.get(1));
            setApprovedNearbyLines.add(compared < 0 ? pairOfCollinearNearbyLine.get(1) : pairOfCollinearNearbyLine.get(0));
        }

        //Добавляем ранее удалённые, но теперь уже подтверждённые линии
        setAllLines.addAll(setApprovedNearbyLines);
        approvedLines.addAll(setAllLines);
        return approvedLines;
    }

    private ArrayList<ArrayList<Line>> getPairOfCollinearNearbyLines(ArrayList<Line> rawLines) {
        //Создаём двумерное хранилище пар близко расположенных параллельных линий
        ArrayList<ArrayList<Line>> arrayPairOfCollinearNearbyLines = new ArrayList<>();
        //Нам необходимо сравнить каждый отрезок с каждым
        ArrayList<Line> rl = new ArrayList<Line>(rawLines);
        ArrayList<Line> lr = new ArrayList<Line>(rl);
        Collections.reverse(lr);
        for (Line lineA : rl) {
            lr.remove(lr.size() - 1);
            for (Line lineZ : lr) {
                //Если два отрезка коллинеарны
                if (AnalyticGeometry.checkCollinearityOfTwoLinesByPseudoScalarProduct(lineA, lineZ)) {
                    //Проверяем, что два отрезка расположенны достаточно близко друг к другу
                    //Получаем расстояние от конца отрезка до прямой, на которой лежит другой отрезок
                    double distance = AnalyticGeometry.getDistanceFromPointToStraightLine(lineA.getStartPoint(), lineZ);
                    //System.out.println(distance);
                    //Если дистанция не превышает заданную
                    if (Double.compare(distance, MAXIMAL_DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES) <= 0) {
                        //И если линии действительно лежат близко друг от друга, т.е. проекция одной пересекает другую
                        //т.е. начальная или конечная точки первого лежат над вторым отрезком или наоборот, т.к. пробегаем по линиям единожды
                        if ((AnalyticGeometry.isPointOverSegment(lineA.getStartPoint(), lineZ) || AnalyticGeometry.isPointOverSegment(lineA.getEndPoint(), lineZ))
                                || (AnalyticGeometry.isPointOverSegment(lineZ.getStartPoint(), lineA) || AnalyticGeometry.isPointOverSegment(lineZ.getEndPoint(), lineA))) {

                            arrayPairOfCollinearNearbyLines.add(new ArrayList<Line>() {{add(lineA); add(lineZ);}} );
                        }
                    }
                }
            }
        }

        return arrayPairOfCollinearNearbyLines;
    }
}
