package com.vladimirs.work;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class RecoveryImageBackup {
   // static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Imgcodecs.imread("src/main/resources/1.png");


        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Пороговая матрица
        Imgproc.threshold(grayMat, mat, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
//        new Window((BufferedImage) HighGui.toBufferedImage(mat), "Исходное изображение");

        //Инвертируем изображение
        Mat inverted = new Mat();
        Core.bitwise_not(mat, inverted);
        new Window((BufferedImage) HighGui.toBufferedImage(inverted), "Инвертированное изображение");

        //Необходимые ядра для преобразований
        Mat horizKernel = new Mat(new Size(31, 1), CvType.CV_8UC1, new Scalar(255));
        Mat verticKernel = new Mat(new Size(1, 31), CvType.CV_8UC1, new Scalar(255));
        int n = 15; //Размеры диагональных матриц
        Mat diagKernel =  Mat.eye(n, n, CvType.CV_8UC1);
        Mat obrDiagKernel = Mat.zeros(n, n, CvType.CV_8UC1);
        int x = 0;
        for (int y = n - 1; y > -1; y--) {
            obrDiagKernel.put(y, x, 1);
            x++;
        }

        Mat res = new Mat();
        Mat horizLines = new Mat();
        Mat verticLines = new Mat();
        Mat slashLines = new Mat();

        //Получаем матрицу горизонтальных линий размыканием
        Imgproc.morphologyEx(inverted, horizLines, Imgproc.MORPH_OPEN, horizKernel);
        //Убрали горизонтальные линии пересечением исходника и линий
        Core.bitwise_xor(inverted, horizLines, res);

        //Получаем матрицу вуртикальных линий размыканием
        Imgproc.morphologyEx(res, verticLines, Imgproc.MORPH_OPEN, verticKernel);
        //Убрали вертикальные линии пересечением исходника и линий
        Core.bitwise_xor(res, verticLines, res);

        //Получаем матрицу косых линий размыканием
        Imgproc.morphologyEx(res, slashLines, Imgproc.MORPH_OPEN, diagKernel);
        //Убрали косые линии пересечением исходника и линий
        Core.bitwise_xor(res, slashLines, res);
        //Получаем матрицу косых линий размыканием
        Imgproc.morphologyEx(res, slashLines, Imgproc.MORPH_OPEN, obrDiagKernel);
        //Убрали косые линии пересечением исходника и линий
        Core.bitwise_xor(res, slashLines, res);



        //Восстановление проререзей после всего
        Mat structureElementHoriz = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1, 3));
        Imgproc.dilate(res, res, structureElementHoriz);
        Imgproc.erode(res, res, structureElementHoriz);
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Без гориз. и вертик. линий");


        //Итоговое изображение
        Core.bitwise_not(res, res);
        new Window((BufferedImage) HighGui.toBufferedImage(res), "Итог");

        System.out.println("Статистика:");
        Mat notRes = new Mat();
        Mat notMat = new Mat();
        Mat stat = new Mat();

        Core.bitwise_not(mat, notMat);
        Core.bitwise_not(res, notRes);

        Core.bitwise_and(mat, notRes, stat);
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "LOST");
        System.out.printf("Lost: %d\n", Core.countNonZero(stat));

        Core.bitwise_and(res, notMat, stat);
        System.out.printf("False: %d\n", Core.countNonZero(stat));
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "FALSE");

        Core.bitwise_xor(res, mat, stat);
        System.out.printf("Error: %d\n", Core.countNonZero(stat));
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "ERROR");
    }
}
