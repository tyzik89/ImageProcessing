package algorithms;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.BitSet;

@Deprecated
public class AlgorithmTemp {

    public void doSteganography(Mat segmentBinary, Mat originalSegment) {
        //получаем хэш код бинарного сегмента
        long hash = getHash(segmentBinary);
        //Получаем набор бит хэшкода
        String bin = Long.toBinaryString(hash);
        System.out.println("length of binary string = " + bin.length());
        System.out.println("binary string = " + bin);

        byte[] byteBuffer = new byte[originalSegment.cols() * originalSegment.rows() * originalSegment.channels()];
        int segmentSize =  originalSegment.cols() * originalSegment.rows();
        System.out.println("SegmentSize, channels, depth = " + segmentSize + " " + originalSegment.channels() + " " + originalSegment.depth());
        System.out.println("originalSegmentBytesSize = " + byteBuffer.length);
        //извлекаем все байты из оригинального сегмента, для дальнейшей упаковки в них битов хэша
        originalSegment.get(0, 0, byteBuffer);
        //разбиваем массив байт каждого пикселя по каналам, для удобства работы с каждым каналом в отдельности
        byte[][] separateRGBArray = getSeparateRGBArray(byteBuffer, segmentSize, originalSegment.channels());

        /*
        * 1. бежим по массиву битов хэш строки
        * 2. бежим по каждму пикселю сегмента
        * 3. извлекаем из каждого пикселя синий канал
        * 4. в младщий бит синего канала записываем бит хэша
        * 5. обновлённый массив упаковываем оратно в сегмент
        * 6. получаем в итоге сегмент изображения, хранящей в себе уникальный хэш-код этого сегмента
        * 7. повторяем процедуру для каждого сегмента размером 8x8
        */

        //преобразуем строковый набор бит, в массив нулей и единиц
        int[] bitHashArray = convertStringToIntArray(bin);
        //перебираем все нули и единицы массива хэша сегмента
        for (int i = 0, j = bitHashArray.length; i < j; i++) {
            //извлекаем синий канал из пикселя
            byte blueChannel = separateRGBArray[i][0];
            //Записываем в младший бит синего канала каждого пикселя оригинльного сегмента новый бит из хэша
            BitSet bitSet = BitSet.valueOf(new byte[] {blueChannel});
            if (bitHashArray[i] == 1) {
                bitSet.set(0, true);
            } else {
                bitSet.set(0, false);
            }
            //изменённый синий канал упаковываем обратно в массив
            separateRGBArray[i][0] = (bitSet.toByteArray())[0];
        }
        //Разделённый по каналам двумерный массив упаковываем обратно в одномерный
        byteBuffer = getUnitedArray(separateRGBArray, segmentSize, originalSegment.channels());
        //обновлённый одномерный массив байт упаковываем обратно в цветной сегмент
        originalSegment.put(0,0, byteBuffer);
    }

    /**
     * Конвертация строки с битами в объект BitSet
     * @param s
     * @return
     */
    private static BitSet fromString(final String s) {
        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
    }

    /**
     * Конвертация объекта Bitset в строку с битами
     * @param bs
     * @return
     */
    private static String toString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }

    /**
     * @param str входная строка бит
     * @return массив с нулями и единицами типа int
     */
    private int[] convertStringToIntArray(String str) {
        String[] bitStringArray = str.split("");
        int[] bitIntArray = new int[bitStringArray.length];
        for (int i = 0; i < bitIntArray.length; i++) {
            bitIntArray[i] = Integer.parseInt(bitStringArray[i]);
        }
        return bitIntArray;
    }

    /**
     * Собираем двумерный массив, состоящий из пикселей и их каналов, в одномерный массив
     * @param separateRGBArray
     * @param frameSize
     * @param numChannels
     * @return
     */
    private byte[] getUnitedArray(byte[][] separateRGBArray, int frameSize, int numChannels) {
        byte[] out = new byte[frameSize * numChannels];
        for (int pix = 0, i = 0; pix < frameSize; pix++) {
            for (int ch = 0; ch < numChannels; ch++, i++) {
                out[i] = separateRGBArray[pix][ch];
            }
        }
        return out;
    }

    /**
     * Разбиваем одномерный массив на двумерный массив, состоящий из пикселей и их каналов
     * @param   byteBuffer массив байт всех пикселей
     * @param   frameSize размер сегмента
     * @param   numChannels кол-во каналов сегмента.
     * @return  разделённый по каналам массив байт,
     *          где первый индекс номер пикселя,
     *          второй индекс - канал
     */
    private byte[][] getSeparateRGBArray(byte[] byteBuffer, int frameSize, int numChannels) {
        byte[][] out = new byte[frameSize][numChannels];
        for (int pix = 0, i = 0; pix < frameSize; pix++) {
            for (int ch = 0; ch < numChannels; ch++, i++) {
                out[pix][ch] = byteBuffer[i];
            }
        }
        return out;
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
        System.out.println("hashcode = " + hash);
        return hash;
    }

    private int convertLowerBitToZero(int i) {
        //Шеснадцатиричный литерал 0xFF равен 255
        //Конструкция обращает все биты, кроме младших в 0
        return i & 0xFF;
        /*R.setRGB(x, y, rgb & 0xff0000);
        G.setRGB(x, y, rgb & 0xff00);
        B.setRGB(x, y, rgb & 0xff);*/
    }

    //Взять младщий бит числа
    private byte takeLowerBit(int i) {
        return  (byte) (i & 0x1);
    }

    //Инвертировать биты на определённой позиции, позиция младшего = (0)
    private static long flipBitWithBitset(int position, long value) {
        BitSet bs = BitSet.valueOf(new long[] { value });
        bs.flip(position);
        return bs.toLongArray()[0];
    }

    private static long flipBit(int position, long value) {
        return value ^ 1 << position;
    }
}
