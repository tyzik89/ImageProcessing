package app;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class Algorithm {

    private static int SIZE_SEGMENT = 8;

    public ArrayList<Mat> doSegmentation(Mat source) {
        ArrayList<Mat> matArrayList = new ArrayList<>();

        int width = source.cols();
        int height = source.rows();

        int rowStart = 0, colStart = 0;
        int rowEnd = SIZE_SEGMENT, colEnd = SIZE_SEGMENT;

        while (rowEnd < height) {
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + SIZE_SEGMENT + 1;
            }

            colEnd = width;
            Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
            //System.out.println(resultMat.size().toString());
            matArrayList.add(resultMat);

            rowStart = rowEnd + 1;
            rowEnd = rowEnd + SIZE_SEGMENT + 1;

            colStart = 0;
            colEnd = SIZE_SEGMENT;
        }

        if (rowEnd >= height) {
            rowEnd = height;
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + SIZE_SEGMENT + 1;
            }

            colEnd = width;
            Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
            //System.out.println(resultMat.size().toString());
            matArrayList.add(resultMat);
        }
        return matArrayList;
    }

    public Mat doGrayscale(Mat rgbSegment) {
        //Конвертируем изображение в одноканальное
        Mat matGray = new Mat();
        Imgproc.cvtColor(rgbSegment, matGray, Imgproc.COLOR_BGR2GRAY);
        return matGray;
    }

    public Mat doBinary (Mat graySegment) {
        //Перевод в бинарное изображение
        Mat matBinary = new Mat();
        Imgproc.threshold(graySegment, matBinary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        return matBinary;
    }

    public void doSteganography(Mat segmentBinary, Mat originalsegment) {
        long hash = getHash(segmentBinary);

    }

    private long getHash(Mat m) {
        long hash = 1125899906842597L;

        byte[] arr = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, arr);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == -1) arr[i] = 1;
            hash = 31 * hash + arr[i];
        }
        if (hash < 0) hash = 0 - hash;
        return hash;
    }


}
