package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import com.work.vladimirs.algorithm.methods.AnalyticGeometry;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class LinesValidator {

    //Максимальная дистанция между отрезками, которые считаются близко расположенными
    private static final double MAXIMAL_DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES = 25.0;
    private Mat originalMat;

    public LinesValidator(Mat originalMat) {
        this.originalMat = originalMat;
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
