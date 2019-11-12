package app;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class Algorithm {

    public ArrayList<Mat> doSegmentation(Mat source, int sizeSegment) {
        ArrayList<Mat> matArrayList = new ArrayList<>();

        int width = source.cols();
        int height = source.rows();

        int rowStart = 0, colStart = 0;
        int rowEnd = sizeSegment, colEnd = sizeSegment;

        while (rowEnd < height) {
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + sizeSegment + 1;
            }

            colEnd = width;
            Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
            //System.out.println(resultMat.size().toString());
            matArrayList.add(resultMat);

            rowStart = rowEnd + 1;
            rowEnd = rowEnd + sizeSegment + 1;

            colStart = 0;
            colEnd = sizeSegment;
        }

        if (rowEnd >= height) {
            rowEnd = height;
            while (colEnd < width) {
                Mat resultMat = source.submat(rowStart, rowEnd, colStart, colEnd);
                //System.out.println(resultMat.size().toString());
                matArrayList.add(resultMat);

                colStart = colEnd + 1;
                colEnd = colEnd + sizeSegment + 1;
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

    public void doSteganography(Mat segmentBinary, Mat originalSegment) {
        long hash = getHash(segmentBinary);

        /*int red = 0, green = 0, blue = 0, r = 0, g = 0, b = 0;
        byte[] bytes = new byte[originalSegment.cols() * originalSegment.rows() * originalSegment.channels()];
        originalSegment.get(0, 0, bytes);
        for (int i = 0, j = bytes.length; i < j; i+=originalSegment.channels()) {
            //Шеснадцатиричный литерал 0xFF равен 255
            //Конструкция обращает все биты, кроме младших в 0
            b = bytes[i] & 0xFF;
            g = bytes[i + 1] & 0xFF;
            r = bytes[i + 2] & 0xFF;
        }*/

        System.out.println(originalSegment.channels());
        System.out.println(originalSegment.size());
        System.out.println(originalSegment.dump());
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
