package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import com.work.vladimirs.algorithm.methods.AnalyticGeometry;
import org.opencv.core.Mat;

import java.util.*;

public class LinesValidator {

    private static final double MAXIMAL_DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES = 25.0;
    private static final int MINIMAL_LINE_LENGTH = 40;
    private Mat originalMat;

    public LinesValidator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    public ArrayList<Line> validate(ArrayList<Line> lines) {
        ArrayList<Line> approvedLines = new ArrayList<Line>();

        ArrayList<ArrayList<Line>> pairOfCollinearNearbyLines = getPairOfCollinearNearbyLines(lines);
        for (ArrayList<Line> pairOfCollinearNearbyLine : pairOfCollinearNearbyLines) {
            approvedLines.addAll(pairOfCollinearNearbyLine);
        }

        return approvedLines;
    }

    private ArrayList<ArrayList<Line>> getPairOfCollinearNearbyLines(ArrayList<Line> lines) {
        //Создаём двумерное хранилище пар близко расположенных параллельных линий
        ArrayList<ArrayList<Line>> arrayPairOfCollinearNearbyLines = new ArrayList<>();
        //Нам необходимо сравнить каждый отрезок с каждым
        ArrayList<Line> rl = new ArrayList<Line>(lines);
        ArrayList<Line> lr = new ArrayList<Line>(rl);
        Collections.reverse(lr);
        for (Line lineA : rl) {
            lr.remove(lr.size() - 1);
            for (Line lineZ : lr) {

                //Если два отрезка коллинеарны
                if (AnalyticGeometry.checkCollinearityOfTwoLinesByPseudoScalarProduct(lineA, lineZ)) {
                    //Если два отрезка расположенные достаточно близко друг к другу
                    //Получаем расстояние от точки отрезка до прямой, на которой лежит другой отрезок
                    double distance = AnalyticGeometry.getDistanceFromPointToStraightLine(lineA.getStartPoint(), lineZ);
                    System.out.println("distance = " + distance);
                    //Если дистанция не превышает заданную
                    if (Double.compare(distance, MAXIMAL_DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES) <= 0) {
                        //И если линии действительно лежат близко друг от друга, т.е. проекция одной пересекает другую
                        //т.е. начальная или конечная точки первого лежат над вторым отрезком
                        if (AnalyticGeometry.isPointOverSegment(lineA.getStartPoint(), lineZ) || AnalyticGeometry.isPointOverSegment(lineA.getEndPoint(), lineZ)) {
                            arrayPairOfCollinearNearbyLines.add(new ArrayList<Line>() {{add(lineA); add(lineZ);}} );
                        }
                    }
                }

            }
        }
        System.out.println("lines.size()= " + lines.size());
        System.out.println("arrayPairOfCollinearNearbyLines.size()= " + arrayPairOfCollinearNearbyLines.size());
        return arrayPairOfCollinearNearbyLines;
    }
}
