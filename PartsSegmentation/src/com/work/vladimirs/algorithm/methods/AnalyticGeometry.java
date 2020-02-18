package com.work.vladimirs.algorithm.methods;

import com.work.vladimirs.algorithm.entities.Line;
import org.opencv.core.Point;

public final class AnalyticGeometry {

    /**
     * Расстояние от точки до отрезка.
     *
     * Если перпендикуляр не падает на отрезок,
     * то ответом будет минимальное из расстояний от данной точки до концов отрезка.
     * @return
     */
    public static double getDistanceFromPointToLineSegment(Point m, Line lineSegment) {
        Point vec_p1m = getVectorByTwoPoints(lineSegment.getStartPoint(), m);
        Point vec_p1p2 = getVectorByTwoPoints(lineSegment.getStartPoint(), lineSegment.getEndPoint());
        Point vec_p2m = getVectorByTwoPoints(lineSegment.getEndPoint(), m);
        Point vec_p2p1 = getVectorByTwoPoints(lineSegment.getEndPoint(), lineSegment.getStartPoint());

        boolean isPointOverSegment = !((getScalarProductOfVectors(vec_p1m, vec_p1p2) < 0)
                || (getScalarProductOfVectors(vec_p2m, vec_p2p1) < 0));

        if (isPointOverSegment) {
            return  getDistanceFromPointToStraightLine(m, lineSegment);
        } else {
            double d1 = getDistanceBetweenPoints(m, lineSegment.getStartPoint());
            double d2 = getDistanceBetweenPoints(m, lineSegment.getEndPoint());
            return Math.min(d1, d2);
        }
    }

    /**
     * Расстояние от точки до прямой.
     * @param m
     * @param straightLine
     * @return
     */
    public static double getDistanceFromPointToStraightLine(Point m, Line straightLine) {
        Point p1 = straightLine.getStartPoint();
        Point p2 = straightLine.getEndPoint();
        Point vec_p1p2 = getVectorByTwoPoints(p1, p2);
        Point vec_p1m = getVectorByTwoPoints(p1, m);
        double dist = getDistanceBetweenPoints(p1, p2);
        double spv = getPseudoScalarProductOfVectors(vec_p1p2, vec_p1m);
        return spv / dist;
    }

    /**
     * Если же прямые заданы точками P1(x1, y1), P2(x2, y2), M1(x3, y3), M2(x4, y4),
     * то условие их параллельности заключается в проверки косого произведения векторов P1P2 и M1M2:
     * если оно равно нулю, то прямые параллельны.
     * @param l1
     * @param l2
     * @return
     */
    public static double checkCollinearityOfTwoLines(Line l1, Line l2) {
        Point vec_l1 = getVectorByTwoPoints(l1.getStartPoint(), l1.getEndPoint());
        Point vec_l2 = getVectorByTwoPoints(l2.getStartPoint(), l2.getEndPoint());
        return getPseudoScalarProductOfVectors(vec_l1, vec_l2);
    }

    public static Point getLineCenter(Line line) {
        Point p1 = line.getStartPoint();
        Point p2 = line.getEndPoint();
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public static double getDistanceBetweenPoints(double x, double y) {
        return Math.sqrt(x*x + y*y);
    }

    public static double getDistanceBetweenPoints(Point p1, Point p2) {
        return Math.sqrt((p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y));
    }

    public static Point getVectorByTwoPoints(Point p1, Point p2) {
        return new Point(p2.x - p1.x, p2.y - p1.y);
    }

    /**
     * Скалярное произведение векторов
     * 1) Если result > 0, то угол между данными векторами острый. Как вариант, векторы сонаправлены.
     * 2) Если result < 0, то угол между данными векторами тупой. Как вариант, векторы направлены противоположно.
     * 3) Если result = 0, то векторы ортогональны (перпендиколярны)
     * @param v1
     * @param v2
     * @return
     */
    public static double getScalarProductOfVectors(Point v1, Point v2) {
        return ((v1.x * v2.x) + (v1.y * v2.y));
    }

    /**
     * Косое (псевдо-скалярное) произведение векторов
     * @param v1
     * @param v2
     * @return
     */
    public static double getPseudoScalarProductOfVectors(Point v1, Point v2) {
        return ((v1.x * v2.y) - (v2.x * v1.y));
    }
}
