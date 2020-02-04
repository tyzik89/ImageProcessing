package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Marker;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Comparator;

public class MarkersGradientComparator implements Comparator<Marker> {

    private static final double EPSILON = 0.0001;
    private Mat originalMat;

    public MarkersGradientComparator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    @Override
    public int compare(Marker m1, Marker m2) {
        ArrayList<Point> pointListOfMarkerM1 = line(m1.getStartPoint().x, m1.getStartPoint().y, m1.getEndPoint().x, m1.getEndPoint().y);
        ArrayList<Point> pointListOfMarkerM2 = line(m2.getStartPoint().x, m2.getStartPoint().y, m2.getEndPoint().x, m2.getEndPoint().y);
        double gradientValueM1 = findMarkerGradient(pointListOfMarkerM1);
        double gradientValueM2 = findMarkerGradient(pointListOfMarkerM2);
        System.out.println("gradientValueM1: " + gradientValueM1 + "\ngradientValueM2: " + gradientValueM2 + "\n");
        return Double.compare(gradientValueM1, gradientValueM2);
    }

    private double findMarkerGradient(ArrayList<Point> pointListOfMarker) {
        double value = 0;
        for (Point point : pointListOfMarker) {
            double[] pixelChannels = originalMat.get((int) point.x, (int) point.y);
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
    private ArrayList<Point> line(double x, double y, double x2, double y2) {
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
