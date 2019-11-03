package com.vladimirs.work;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class RecoveryImage {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    //Необходимые ядра для преобразований
    private static Mat horizKernel = new Mat(new Size(31, 1), CvType.CV_8UC1, new Scalar(255));
    private static Mat verticKernel = new Mat(new Size(1, 31), CvType.CV_8UC1, new Scalar(255));

    public static void main(String[] args) {
        Mat mat = Imgcodecs.imread("src/main/resources/1.png");
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //Пороговая матрица
        Imgproc.threshold(grayMat, mat, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        new Window((BufferedImage) HighGui.toBufferedImage(mat), "Исходное изображение");
        //Инвертируем изображение
        Mat inverted = new Mat();
        Core.bitwise_not(mat, inverted);
//        new Window((BufferedImage) HighGui.toBufferedImage(inverted), "Инвертированное изображение");

//        ================================================ГОРИЗОНТАЛЬНЫЕ================================================

        Mat horizLines = new Mat();
        Mat res = new Mat();
        //Получаем матрицу горизонтальных линий размыканием
        Imgproc.morphologyEx(inverted, horizLines, Imgproc.MORPH_OPEN, horizKernel);
        //Убрали горизонтальные линии пересечением исходника и линий
        Core.bitwise_xor(inverted, horizLines, res);
        //Восстановление проререзей
        Mat structureElementVertic = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1, 3));
        //Залепливаем дырки от полосок
        Imgproc.dilate(res, res, structureElementVertic);
        Imgproc.dilate(res, res, structureElementVertic);
        //Инвертируем
        Core.bitwise_not(res, res);
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Залепленные дырки");
        //Получаем горизонтальные полоски с прорезями
        Core.bitwise_and(res, inverted, horizLines);
//        new Window((BufferedImage) HighGui.toBufferedImage(horizLines), "Горизонтальные полоски с прорезями");
        //Убрали горизонтальные линии с дырками пересечением исходника и линий
        Core.bitwise_xor(inverted, horizLines, res);
        //Наводим красоту
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Нормльно вырезанные горизонтальные полоски");


//        ==================================================ВЕРИТИКАЛЬНЫЕ===============================================

        Mat verticLines = new Mat();
        Mat temp = new Mat();
        res.copyTo(temp);
        //Получаем матрицу вертикальных линий размыканием
        Imgproc.morphologyEx(res, verticLines, Imgproc.MORPH_OPEN, verticKernel);
        //Убрали вертикальные линии пересечением исходника и линий
        Core.bitwise_xor(res, verticLines, res);
        //Восстановление проререзей
        Mat structuringElementHoriz = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 1));
        //Залепливаем дырки от полосок
        Imgproc.dilate(res, res, structuringElementHoriz);
        Imgproc.dilate(res, res, structuringElementHoriz);
        //Инвертируем
        Core.bitwise_not(res, res);
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Залепленные дырки");
        //Получаем горизонтальные полоски с прорезями
        Core.bitwise_and(res, temp, verticLines);
//        new Window((BufferedImage) HighGui.toBufferedImage(verticLines), "Вертикальные полоски с прорезями");
        //Убрали горизонтальные линии с дырками пересечением исходника и линий
        Core.bitwise_xor(temp, verticLines, res);
        //Наводим красоту
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Нормльно вырезанные вертикальные полоски");

//        ==================================================НАКЛОННЫЕ===================================================

        int n = 15; //Размеры диагональных матриц
        Mat diagKernel = Mat.eye(n, n, CvType.CV_8UC1);
        Mat obrDiagKernel = Mat.zeros(n, n, CvType.CV_8UC1);
        int x = 0;
        for (int y = n - 1; y > -1; y--) {
            obrDiagKernel.put(y, x, 1);
            x++;
        }
        Mat slashLines = new Mat();
        temp = new Mat();
        res.copyTo(temp);
        //Получаем матрицу прямодиагональных линий размыканием
        Imgproc.morphologyEx(res, slashLines, Imgproc.MORPH_OPEN, diagKernel);
        //Убрали прямодиагональныее линии пересечением исходника и линий
        Core.bitwise_xor(res, slashLines, res);
        //Восстановление проререзей
        Mat structuringElementSlash = Mat.zeros(3, 3, CvType.CV_8UC1);
        x = 0;
        for (int y = 3 - 1; y > -1; y--) {
            structuringElementSlash.put(y, x, 1);
            x++;
        }
        //Залепливаем дырки от полосок
        Imgproc.dilate(res, res, structuringElementSlash);
        Imgproc.dilate(res, res, structuringElementSlash);
        //Инвертируем
        Core.bitwise_not(res, res);
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Залепленные дырки");
        //Получаем прямодиагональные полоски с прорезями
        Core.bitwise_and(res, temp, slashLines);
//        new Window((BufferedImage) HighGui.toBufferedImage(slashLines), "Диагональные полоски с прорезями");
        //Убрали горизонтальные линии с дырками пересечением исходника и линий
        Core.bitwise_xor(temp, slashLines, res);
        //Наводим красоту
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Нормльно вырезанные диагональные полоски");

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        slashLines = new Mat();
        temp = new Mat();
        res.copyTo(temp);
        //Получаем матрицу обратнодиагональных линий размыканием
        Imgproc.morphologyEx(res, slashLines, Imgproc.MORPH_OPEN, obrDiagKernel);
        //Убрали обратнодиагональныее линии пересечением исходника и линий
        Core.bitwise_xor(res, slashLines, res);
        //Восстановление проререзей
        structuringElementSlash = Mat.eye(3, 3, CvType.CV_8UC1);
        //Залепливаем дырки от полосок
        Imgproc.dilate(res, res, structuringElementSlash);
        Imgproc.dilate(res, res, structuringElementSlash);
        //Инвертируем
        Core.bitwise_not(res, res);
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Залепленные дырки");
        //Получаем прямодиагональные полоски с прорезями
        Core.bitwise_and(res, temp, slashLines);
//        new Window((BufferedImage) HighGui.toBufferedImage(slashLines), "ОбратноДиагональные полоски с прорезями");
        //Убрали горизонтальные линии с дырками пересечением исходника и линий
        Core.bitwise_xor(temp, slashLines, res);
        //Наводим красоту
//        new Window((BufferedImage) HighGui.toBufferedImage(res), "Нормльно вырезанные обратнодиагональные полоски");


//        ========================================ВСЯКИЙ МУСОР==========================================================
        Mat rubbishKernel = Mat.zeros(4 , 4, CvType.CV_8UC1);
        rubbishKernel.put(1, 1, 1);
        rubbishKernel.put(1, 2, 1);
        rubbishKernel.put(2, 1, 1);
        rubbishKernel.put(2, 2, 1);

        //rubbishKernel = Mat.ones(5, 5, CvType.CV_8UC1);


        //Imgproc.erode(res, res, rubbishKernel);
       //Imgproc.dilate(res, res, rubbishKernel);
        Core.bitwise_not(res, res);
        new Window((BufferedImage) HighGui.toBufferedImage(res), "ИТОГ");

        System.out.println("Статистика:");
        mat = Imgcodecs.imread("src/main/resources/page.png");
        grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //Пороговая матрица
        Imgproc.threshold(grayMat, mat, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        new Window((BufferedImage) HighGui.toBufferedImage(mat), "Эталон");
        Mat notRes = new Mat();
        Mat notMat = new Mat();
        Mat stat = new Mat();

        Core.bitwise_not(mat, notMat);
        Core.bitwise_not(res, notRes);

        Core.bitwise_and(mat, notRes, stat);
        System.out.printf("Lost: %d\n", Core.countNonZero(stat));
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "LOST");

        Core.bitwise_and(res, notMat, stat);
        System.out.printf("False: %d\n", Core.countNonZero(stat));
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "FALSE");

        Core.bitwise_xor(res, mat, stat);
        System.out.printf("Error: %d\n", Core.countNonZero(stat));
        new Window((BufferedImage) HighGui.toBufferedImage(stat), "ERROR");
    }
}

















