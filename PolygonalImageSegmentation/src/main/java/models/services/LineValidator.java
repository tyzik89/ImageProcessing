package models.services;

import models.entities.Line;
import org.opencv.core.Point;

import java.util.ArrayList;

public class LineValidator {

    public static boolean validateLineLength(Line currentLine) {
        Point p1 = currentLine.getStartPoint();
        Point p2 = currentLine.getEndPoint();
        ArrayList<Point> pointArrayList = generateSetPointsBetweenTwoPoints(p1.x, p1.y, p2.x, p2.y);
//        if (pointArrayList.size() < 40) {
//            System.out.println("FALSE!!!"); return false;}

        return true;
    }

    /**
     * реализация алгоритма Брезенхема
     */
    private static ArrayList<Point> generateSetPointsBetweenTwoPoints(double x, double y, double x2, double y2) {
        ArrayList<Point> pointArray = new ArrayList<>();
        double w = x2 - x ;
        double h = y2 - y ;
        double dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = (int) Math.abs(w) ;
        int shortest = (int) Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = (int) Math.abs(h) ;
            shortest = (int) Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
            pointArray.add(new Point(x, y));
            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }

        return pointArray;
    }
}
