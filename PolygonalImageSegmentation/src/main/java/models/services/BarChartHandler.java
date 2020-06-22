package models.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageUtils;
import utils.ShowImage;

import java.util.ArrayList;
import java.util.Arrays;

public class BarChartHandler {

    private Mat originalMat;

    public BarChartHandler(Mat originalMat) {
        this.originalMat = originalMat;
    }

    public Mat createBarChart(Mat maskWithMarker) {
        //Получаем оригинальную матрицу в градациях серого
        Mat grayOriginalMat = new Mat();
        if (originalMat.channels() != 1 ) {
            Imgproc.cvtColor(originalMat, grayOriginalMat, Imgproc.COLOR_BGR2GRAY);
        } else {
            originalMat.copyTo(grayOriginalMat);
        }
        //В массив изображений добавляем оригинальную матрицу в градациях серого
        ArrayList<Mat> images = new ArrayList<Mat>();
        images.add(grayOriginalMat);
        // Вычисляем гистограммы по единственному серому каналу
        Mat histGray = new Mat();
        //Вычисляем гистограмму
        Imgproc.calcHist(images, new MatOfInt(0), maskWithMarker, histGray, new MatOfInt(256), new MatOfFloat(0, 256));

       /* // Нормализация диапазона
        Core.normalize(histGray, histGray, 0, 128, Core.NORM_MINMAX);


        // Отрисовка гистограмм
        double v = 0;
        int h = 150;
        //Палитра белого цвета для отображения гистограммы
        Mat imgHistGray = new Mat(h, 256, CvType.CV_8UC1, ImageUtils.COLOR_WHITE);
        for (int i = 0, j = histGray.rows(); i < j; i++) {
            v = Math.round(histGray.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistGray, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_BLACK);
            }
        }
        ShowImage.show(ImageUtils.matToImageFX(imgHistGray), "BarChart");

//        System.out.println(histGray.dump());*/
        return histGray;
    }

    public double[] multiOtsu2Thresholds(Mat matHistogram) {
        int[] histogram = new int[256];
        int N = 0;  //total amount of pixels
        for (int i = 0; i < matHistogram.cols(); i++) {
            int val = (int) matHistogram.get(0, i)[0];
            if (val != 0) {
                histogram[i] = val;
                N = N + val;
            }
        }
        System.out.println(Arrays.toString(histogram));
        System.out.println("N=" + N);

        double W0K, W1K, W2K, M0, M1, M2, currVarB, optimalThresh1, optimalThresh2, maxBetweenVar, M0K, M1K, M2K, MT;

        optimalThresh1 = 0;
        optimalThresh2 = 0;

        W0K = 0;
        W1K = 0;
        M0K = 0;
        M1K = 0;

        MT = 0;

        maxBetweenVar = 0;

        for (int k = 0; k <= 255; k++) {
            MT += k * (histogram[k] / (double) N);
        }


        for (int t1 = 0; t1 <= 255; t1++) {
            W0K += histogram[t1] / (double) N; //Pi
            M0K += t1 * (histogram[t1] / (double) N); //i * Pi
            M0 = M0K / W0K; //(i * Pi)/Pi

            W1K = 0;
            M1K = 0;

            for (int t2 = t1 + 1; t2 <= 255; t2++) {
                W1K += histogram[t2] / (double) N; //Pi
                M1K += t2 * (histogram[t2] / (double) N); //i * Pi
                M1 = M1K / W1K; //(i * Pi)/Pi

                W2K = 1 - (W0K + W1K);
                M2K = MT - (M0K + M1K);

                if (W2K <= 0) break;

                M2 = M2K / W2K;

                currVarB = W0K * (M0 - MT) * (M0 - MT) + W1K * (M1 - MT) * (M1 - MT) + W2K * (M2 - MT) * (M2 - MT);

                if (maxBetweenVar < currVarB) {
                    maxBetweenVar = currVarB;
                    optimalThresh1 = t1;
                    optimalThresh2 = t2;
                }
            }
        }

        return new double[]{optimalThresh1, optimalThresh2};
    }

    public double[] multiOtsu3Thresholds(Mat matHistogram) {
        int[] histogram = new int[256];
        int N = 0;  //total amount of pixels
        for (int i = 0; i < matHistogram.cols(); i++) {
            int val = (int) matHistogram.get(0, i)[0];
            if (val != 0) {
                histogram[i] = val;
                N = N + val;
            }
        }
        System.out.println(Arrays.toString(histogram));
        System.out.println("N=" + N);

        double W0K, W1K, W2K, W3K, M0, M1, M2, M3, optimalThresh1, optimalThresh2, optimalThresh3, currVarB, maxBetweenVar, M0K, M1K, M2K, M3K, MT;

        optimalThresh1 = 0;
        optimalThresh2 = 0;
        optimalThresh3 = 0;

        W0K = 0;
        W1K = 0;
        M0K = 0;
        M1K = 0;
        MT = 0;
        maxBetweenVar = 0;

        for (int k = 0; k <= 255; k++) {
            MT += k * (histogram[k] / (double) N);
        }

        for (int t1 = 0; t1 <= 255; t1++)
        {
            W0K += histogram[t1] / (double) N; //Pi
            M0K += t1 * (histogram[t1] / (double) N); //i * Pi
            M0 = M0K / W0K; //(i * Pi)/Pi

            W1K = 0;
            M1K = 0;

            for (int t2 = t1 + 1; t2 <= 255; t2++)
            {
                W1K += histogram[t2] / (double) N; //Pi
                M1K += t2 * (histogram[t2] / (double) N); //i * Pi
                M1 = M1K / W1K; //(i * Pi)/Pi
                W2K = 1 - (W0K + W1K);
                M2K = MT - (M0K + M1K);

                if (W2K <= 0) break;

                M2 = M2K / W2K;

//                W3K = 0;
//                M3K = 0;

                for (int t3 = t2 + 1; t3 <= 255; t3++)
                {
                    W2K += histogram[t3] / (double) N; //Pi
                    M2K += t3 * (histogram[t3] / (double) N); // i*Pi
                    M2 = M2K / W2K; //(i*Pi)/Pi
                    W3K = 1 - (W0K + W1K + W2K);
                    M3K = MT - (M0K + M1K + M2K);
//                    W3K = 1 - (W1K + W2K);
//                    M3K = MT - (M1K + M2K);

                    M3 = M3K / W3K;
                    currVarB = W0K * (M0 - MT) * (M0 - MT) + W1K * (M1 - MT) * (M1 - MT) + W2K * (M2 - MT) * (M2 - MT) + W3K * (M3 - MT) * (M3 - MT);


//                    W3K = 0;
//                    M3K = 0;
//                W2K = 0;
//                M2K = 0;
                    if (maxBetweenVar < currVarB)
                    {
                        maxBetweenVar = currVarB;
                        optimalThresh1 = t1;
                        optimalThresh2 = t2;
                        optimalThresh3 = t3;
                    }
                }
            }
        }
        return new double[]{optimalThresh1, optimalThresh2, optimalThresh3};
    }

}
