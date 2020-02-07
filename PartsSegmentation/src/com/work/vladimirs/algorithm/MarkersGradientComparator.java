package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MarkersGradientComparator implements Comparator<Line> {

    private static final double EPSILON = 0.0001;
    private Mat originalMat;

    public MarkersGradientComparator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    @Override
    public int compare(Line m1, Line m2) {
        ArrayList<Point> pointListOfMarkerM1 = generateSetPointsBetweenTwoPoints(m1.getStartPoint().x, m1.getStartPoint().y, m1.getEndPoint().x, m1.getEndPoint().y);
        ArrayList<Point> pointListOfMarkerM2 = generateSetPointsBetweenTwoPoints(m2.getStartPoint().x, m2.getStartPoint().y, m2.getEndPoint().x, m2.getEndPoint().y);

        System.out.println("pointListOfMarkerM1: " + pointListOfMarkerM1.toString());
        System.out.println("pointListOfMarkerM2: " + pointListOfMarkerM2.toString());

        double gradientValueM1 = findMarkerGradient(pointListOfMarkerM1);
        double gradientValueM2 = findMarkerGradient(pointListOfMarkerM2);

        System.out.println("gradientValueM1: " + gradientValueM1 + "\ngradientValueM2: " + gradientValueM2 + "\n");

        return Double.compare(gradientValueM1, gradientValueM2);
    }

    private double findMarkerGradient(ArrayList<Point> pointListOfMarker) {
        double value = 0;
        for (Point point : pointListOfMarker) {
            double[] pixelChannels = originalMat.get((int) point.x, (int) point.y);

//            System.out.println("{" + (int) point.x + ", " + (int) point.y + "}: " + Arrays.toString(pixelChannels));

//            if (pixelChannels == null) break;
            double brightnessPixel = (pixelChannels[0] + pixelChannels[1] + pixelChannels[2]) / 3;
            value += brightnessPixel;
        }
        return value / pointListOfMarker.size();
    }

    private boolean isEqual(double x, double y){
        return Math.abs(x-y) < EPSILON;
    }


    /**
     * реализация алгоритма Брезенхема
     */
    //fixme поправить этот алгоритм тут и в #ShowImage
    private ArrayList<Point> generateSetPointsBetweenTwoPoints(double x, double y, double x2, double y2) {
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





/*
    public void line(int x,int y,int x2, int y2, int color) {
        int w = x2 - x ;
        int h = y2 - y ;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
            putpixel(x,y,color) ;
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
    }
*/
