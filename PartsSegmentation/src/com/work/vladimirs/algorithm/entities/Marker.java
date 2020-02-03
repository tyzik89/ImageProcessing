package com.work.vladimirs.algorithm.entities;

import org.opencv.core.Point;

public class Marker {

    private Point startPoint;
    private Point endPoint;

    public Marker() {
    }

    public Marker(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
}
