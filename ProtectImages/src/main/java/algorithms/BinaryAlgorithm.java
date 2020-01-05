package algorithms;

import utils.ColorScaleUtils;
import utils.ImageUtils;
import utils.SegmentsImageUtils;
import org.opencv.core.Mat;
import utils.ShowImage;

import java.util.ArrayList;
import java.util.Arrays;

public class BinaryAlgorithm extends Algorithm {

    private String pathname;
    private int sizeSegment = 151;

    public BinaryAlgorithm(String pathname) {
        this.pathname = pathname;
    }

    @Override
    public void run() {
        loadImage(pathname);
        ArrayList<Mat> segments = SegmentsImageUtils.analyze(getSourceMat(), sizeSegment);

        for (Mat segment : segments) {
            //переводим в градации серого и бинаризируем сегмент
            Mat graySegment = ColorScaleUtils.doGrayscale(segment);
            Mat binarySegment = ColorScaleUtils.doBinary(graySegment);
            //Получаем хэш сегмента
            String hash = getSimpleBinaryHash(binarySegment);
            //Упаковываем хэш в сегмент
            putHashInSegments(hash, segment);
        }

        SegmentsImageUtils.synthesis(segments, getSourceMat());
        ShowImage.show(ImageUtils.matToImageFX(getSourceMat()));
        //saveImage(getSourceMat(), pathname);
    }

    private void putHashInSegments(String hash, Mat segment) {
        String[] hashArr = hash.split("");
        //Пробегаемся по всем пикселям оригинального сегмента
        for (int i = 0, rows = segment.rows(), hashBit = 0; i < rows; i++) {
            for (int j = 0, cols = segment.cols(); j < cols; j++, hashBit++) {
                //берём пиксель
                double[] pixel = segment.get(i, j);
                //из пикселя берём синий канал
                long blueChannel = (long) pixel[0];
                //бинарное представление синего канала
                String blueChannelBin = toBitString(blueChannel, sizeSegment);
                //младший бит синего канала заменяем битом хэша
                String newBlueChannel = blueChannelBin.substring(0, 7) + hashArr[hashBit];
                blueChannel = Long.parseLong(newBlueChannel, 2);
                //в массив каналов пикселя упаковываем обновленный синий канал
                pixel[0] = blueChannel;
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
