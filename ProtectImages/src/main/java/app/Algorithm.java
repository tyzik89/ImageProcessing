package app;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class Algorithm {

    private static int SIZE_SEGMENT = 500;

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

    public ArrayList<Mat> doGrayscale(ArrayList<Mat> rgbSegments) {
        ArrayList<Mat> grayscaleSegments = new ArrayList<>();

        for (Mat rgbSegment : rgbSegments) {
            //Конвертируем изображение в одноканальное
            Mat matGray = new Mat();
            Imgproc.cvtColor(rgbSegment, matGray, Imgproc.COLOR_BGR2GRAY);

            //Перевод в бинарное изображение
            //Mat matBinary = new Mat();
            //Imgproc.threshold(matGray, matBinary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

            //Добавляем результат в массив
            grayscaleSegments.add(matGray);
        }
        return grayscaleSegments;
    }

    public ArrayList<Integer> getHashCodeForEachSegment(ArrayList<Mat> segments) {
        ArrayList<Integer> hashCodes = new ArrayList<>();
        for (Mat segment : segments) {
            Core.dct();
        }
        return hashCodes;
    }
}
