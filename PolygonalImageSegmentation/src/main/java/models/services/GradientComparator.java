package models.services;

import models.entities.Line;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Comparator;

public class GradientComparator implements Comparator<Line> {

    private Mat originalMat;

    public GradientComparator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    @Override
    public int compare(Line m1, Line m2) {
        ArrayList<Point> pointListOfLineL1 = m1.getInnerPoints();
        ArrayList<Point> pointListOfLineL2 = m2.getInnerPoints();

//        System.out.println("pointListOfLineL1: " + pointListOfLineL1.toString());
//        System.out.println("pointListOfLineL2: " + pointListOfLineL2.toString());

        double gradientValueM1 = findLineGradient(pointListOfLineL1);
        double gradientValueM2 = findLineGradient(pointListOfLineL2);

//        System.out.println("gradientValueM1: " + gradientValueM1 + "\ngradientValueM2: " + gradientValueM2 + "\n");

        return Double.compare(gradientValueM1, gradientValueM2);
    }

    public double findLineGradient(ArrayList<Point> pointListOfLine) {
        double value = 0;
        for (Point point : pointListOfLine) {
            double[] pixelChannels = originalMat.get((int) point.x, (int) point.y);
            double brightnessPixel = (pixelChannels[0] + pixelChannels[1] + pixelChannels[2]) / 3;
            value += brightnessPixel;
        }
        return value / pointListOfLine.size();
    }
}