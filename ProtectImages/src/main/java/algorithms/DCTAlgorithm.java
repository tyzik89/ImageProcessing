package algorithms;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import utils.ColorScaleUtils;
import utils.ImageUtils;
import utils.SegmentsImageUtils;
import utils.ShowImage;

import java.util.ArrayList;

public class DCTAlgorithm extends Algorithm {

    private final static String PREFIX_NEW_FILE_NAME = "dct_";
    private final static String PREFIX_RESULT_FILE_NAME = "result_dct_";

    public DCTAlgorithm(String pathname, String filename) {
        setPathname(pathname);
        setFilename(filename);
        setSizeSegment(32);
    }

    @Override
    public void check() {
        loadImage(getPathname(), PREFIX_NEW_FILE_NAME + getFilename());
        ArrayList<Mat> segments =  SegmentsImageUtils.analyze(getSourceMat(), getSizeSegment());
        for (Mat segment : segments) {
            Mat graySegment = ColorScaleUtils.doGrayscale(segment);

            Mat floatSegment = new Mat();
            graySegment.convertTo(floatSegment, CvType.CV_32F, 1.0 / 255);
            //m2.convertTo(m3, CvType.CV_8U, 255);
            Core.dct(floatSegment, floatSegment);

            Mat coefficients = floatSegment.submat(0, 8, 0, 8);
            double avg = getAverageMatValue(coefficients);
            String hash = getHash(coefficients, avg);
            checkHashInSegments(hash, segment);
        }
        saveImage(getSourceMat(), getPathname(), PREFIX_RESULT_FILE_NAME + getFilename());
        ShowImage.show(ImageUtils.matToImageFX(getSourceMat()));
    }

    private String getHashFromSegments(Mat segment) {
        StringBuilder hash = new StringBuilder();
        //Пробегаемся по всем пикселям сегмента
        for (int i = 0, rows = segment.rows(), hashBit = 0; i < rows; i++) {
            for (int j = 0, cols = segment.cols(); j < cols; j++, hashBit++) {
                if (hashBit == 64) break;
                //берём пиксель
                double[] pixel = segment.get(i, j);
                //из пикселя берём синий канал
                long blueChannel = (long) pixel[0];
                //бинарное представление синего канала
                String blueChannelBin = toBitString(blueChannel, 8);
                //получаем младший бит синего канала
                String bit = blueChannelBin.substring(7, 8);
                hash.append(bit);
            }
        }
        return hash.toString();
    }

    private void checkHashInSegments(String hash, Mat segment) {
        boolean ok = true;
        int admission = 1;
        String[] hashArr = hash.split("");
        //Пробегаемся по всем пикселям оригинального сегмента
        for (int i = 0, rows = segment.rows(), hashBit = 0; i < rows; i++) {
            for (int j = 0, cols = segment.cols(); j < cols; j++, hashBit++) {
                if (hashBit == 64) break;
                //берём пиксель
                double[] pixel = segment.get(i, j);
                //из пикселя берём синий канал
                long blueChannel = (long) pixel[0];
                //бинарное представление синего канала
                String blueChannelBin = toBitString(blueChannel, 8);
                //младший бит синего канала сравниваем с битом хэша
                String bit = blueChannelBin.substring(7, 8);
                if (!hashArr[hashBit].equals(bit)) {
                    admission--;
                    if (admission <= 0) {
                        ok = false;
                    }
                }
            }
            if (hashBit == 64) break;
        }
        if (!ok) {
            for (int i = 0, rows = segment.rows(), hashBit = 0; i < rows; i++) {
                for (int j = 0, cols = segment.cols(); j < cols; j++, hashBit++) {
                    //берём пиксель
                    double[] pixel = segment.get(i, j);
                    //в массив каналов пикселя упаковываем обновленный синий канал
                    pixel[0] = 0.0;
                    pixel[1] = 0.0;
                    pixel[2] = 255.0;
                    //Засовываем пиксель обратно в сегмент
                    segment.put(i, j, pixel);
                }
            }
        }
    }

    /*================================================================================================================*/

    @Override
    public void run() {
        loadImage(getPathname(), getFilename());
        ArrayList<Mat> segments =  SegmentsImageUtils.analyze(getSourceMat(), getSizeSegment());
        for (Mat segment : segments) {
            //ShowImage.show(ImageUtils.matToImageFX(segment));
            Mat graySegment = ColorScaleUtils.doGrayscale(segment);

            //Преобразование матрицы в 32-битную для дальнейшего дискретно-косинусного преобразования
            Mat floatSegment = new Mat();
            graySegment.convertTo(floatSegment, CvType.CV_32F, 1.0 / 255);
            //m2.convertTo(m3, CvType.CV_8U, 255);
            Core.dct(floatSegment, floatSegment);

            //Извлечени значимой матрицы коэффициентов 8х8
            Mat coefficients = floatSegment.submat(0, 8, 0, 8);
            //Среднее значение коэффициентов
            double avg = getAverageMatValue(coefficients);
            //Получаем хэш
            String hash = getHash(coefficients, avg);
            putHashInSegments(hash, segment);
        }
        saveImage(getSourceMat(), getPathname(), PREFIX_NEW_FILE_NAME + getFilename());
        //ShowImage.show(ImageUtils.matToImageFX(getSourceMat()));
    }

    private void putHashInSegments(String hash, Mat segment) {
        String[] hashArr = hash.split("");
        //Пробегаемся по всем пикселям оригинального сегмента
        for (int i = 0, rows = segment.rows(), hashBit = 0; i < rows; i++) {
            for (int j = 0, cols = segment.cols(); j < cols; j++, hashBit++) {
                if (hashBit == 64) return;
                //берём пиксель
                double[] pixel = segment.get(i, j);
                //из пикселя берём синий канал
                long blueChannel = (long) pixel[0];
                //бинарное представление синего канала
                String blueChannelBin = toBitString(blueChannel, 8);
                //младший бит синего канала заменяем битом хэша
                String newBlueChannelBin = blueChannelBin.substring(0, 7) + hashArr[hashBit];
                long newBlueChannel = Long.parseLong(newBlueChannelBin, 2);
                //в массив каналов пикселя упаковываем обновленный синий канал
                pixel[0] = (double) newBlueChannel;
                //Засовываем пиксель обратно в сегмент
                segment.put(i, j, pixel);
            }
        }
    }

    //0-дополненное двоичное представление целого числа
    private String toBitString( long x, int bits ){
        String bitString = Long.toBinaryString(x);
        int size = bitString.length();
        StringBuilder sb = new StringBuilder( bits );
        if( bits > size ){
            for( int i=0; i<bits-size; i++ )
                sb.append('0');
            sb.append( bitString );
        }else
            sb = sb.append( bitString.substring(size-bits, size) );

        return sb.toString();
    }

    //Получение хэша из матрицы коэффициентов
    private String getHash(Mat coefficients, double avg) {
        StringBuilder hash = new StringBuilder();
        for (int y = 0, cols = coefficients.cols(); y < cols; y++) {
            for (int x = 0, rows = coefficients.rows(); x < rows; x++) {
                double cValue = coefficients.get(x, y)[0];
                hash.append(Double.compare(cValue, avg) == 1 ? 1 : 0);
            }
        }
        return hash.toString();
    }

    //Среднее значение по матрице коэффициентов
    private double getAverageMatValue(Mat coefficients) {
        int count = 0;
        double sum = 0.0;
        for (int y = 0, cols = coefficients.cols(); y < cols; y++) {
            for (int x = 0, rows = coefficients.rows(); x < rows; x++) {
                if (x == 0 && y == 0) continue;
                count++;
                sum += coefficients.get(x, y)[0];
            }
        }
        //fixme приведение типов, возможно, не правильное
        return (sum / count);
    }
}
