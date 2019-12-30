package algorithms;

import utils.ColorScaleUtils;
import utils.SegmentsImageUtils;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class BinaryAlgorithm extends Algorithm {

    private String pathname;
    private int sizeSegment = 8;

    public BinaryAlgorithm(String pathname) {
        this.pathname = pathname;
    }

    @Override
    public void run() {
        loadImage(pathname);
        ArrayList<Mat> segments = SegmentsImageUtils.analyze(getSourceMat(), sizeSegment);
        System.out.println(segments.size());

        for (Mat segment : segments) {
            //Бинаризируем сегмент
            Mat graySegment = ColorScaleUtils.doGrayscale(segment);
            Mat binarySegment = ColorScaleUtils.doBinary(graySegment);
            //Получаем хэш сегмента
            String hash = getSimpleBinaryHash(binarySegment);

        }
    }

    private void putHashInSegments(String hash, Mat mat) {

    }

    private long getCompositeBinaryHash(Mat m) {
        long hash = 1125899906842597L;

        byte[] arr = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, arr);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == -1) arr[i] = 1;
            hash = 31 * hash + arr[i];
        }

        if (hash < 0) hash = 0 - hash;
        System.out.println("hashcode = " + hash);
        return hash;
    }

    private String getSimpleBinaryHash(Mat m) {
        StringBuilder hash = new StringBuilder();

        for (int i = 0, rows = m.rows(); i < rows; i++) {
            for (int j = 0, cols = m.cols(); j < cols; j++) {
                double e = m.get(i, j)[0];
                hash.append(e > 0 ? 1 : 0);
            }
        }

        return hash.toString();
    }
}
