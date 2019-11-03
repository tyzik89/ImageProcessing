package models.algorithms;

import org.opencv.core.Mat;

public interface Algorithm {

    Mat  doAlgorithm(Mat frame);
}
