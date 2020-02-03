package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Marker;
import org.opencv.core.Mat;

import java.util.Comparator;

public class MarkersGradientComparator implements Comparator<Marker> {

    private Mat originalMat;

    public MarkersGradientComparator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    @Override
    public int compare(Marker m1, Marker m2) {
        double[] buffer = new double[originalMat.channels() * originalMat.rows() * originalMat.cols()];
        originalMat.get(0, 0, buffer);

        return 0;
    }
}
