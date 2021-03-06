package models.services;

import models.entities.Line;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Comparator;

public class MarkersGradientComparator implements Comparator<Line> {

    private Mat originalMat;

    public MarkersGradientComparator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    @Override
    public int compare(Line m1, Line m2) {
        ArrayList<Point> pointListOfMarkerM1 = generateSetPointsBetweenTwoPoints(m1.getStartPoint().x, m1.getStartPoint().y, m1.getEndPoint().x, m1.getEndPoint().y);
        ArrayList<Point> pointListOfMarkerM2 = generateSetPointsBetweenTwoPoints(m2.getStartPoint().x, m2.getStartPoint().y, m2.getEndPoint().x, m2.getEndPoint().y);

        System.out.println("pointListOfMarkerM1: " + pointListOfMarkerM1.toString());
        System.out.println("pointListOfMarkerM2: " + pointListOfMarkerM2.toString());

        double gradientValueM1 = findMarkerGradient(pointListOfMarkerM1);
        double gradientValueM2 = findMarkerGradient(pointListOfMarkerM2);

        System.out.println("gradientValueM1: " + gradientValueM1 + "\ngradientValueM2: " + gradientValueM2 + "\n");

        return Double.compare(gradientValueM1, gradientValueM2);
    }

    private double findMarkerGradient(ArrayList<Point> pointListOfMarker) {
        double value = 0;
        for (Point point : pointListOfMarker) {
            double[] pixelChannels = originalMat.get((int) point.x, (int) point.y);
            double brightnessPixel = (pixelChannels[0] + pixelChannels[1] + pixelChannels[2]) / 3;
            value += brightnessPixel;
        }
        return value / pointListOfMarker.size();
    }

    /**
     * реализация алгоритма Брезенхема
     */
    private ArrayList<Point> generateSetPointsBetweenTwoPoints(int xstart, int ystart, int xend, int yend) {
        ArrayList<Point> pointArray = new ArrayList<>();
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;//проекция на ось X
        dy = yend - ystart;//проекция на ось Y

        incx = sign(dx);
        /*
         * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
         * справа налево по иксу, то incx будет равен -1.
         * Это будет использоваться в цикле постороения.
         */
        incy = sign(dy);
        /*
         * Аналогично. Если рисуем отрезок снизу вверх -
         * это будет отрицательный сдвиг для y (иначе - положительный).
         */

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        //эти две строчки можно записать и так: dx = Math.abs(dx); dy = Math.abs(dy);

        if (dx > dy)
        //определяем наклон отрезка:
        {
            /*
             * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
             * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
             * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incx;), при этом
             * по y сдвиг такой отсутствует.
             */
            pdx = incx;	pdy = 0;
            es = dy;	el = dx;
        }
        else//случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
        {
            pdx = 0;	pdy = incy;
            es = dx;	el = dy;//тогда в цикле будем двигаться по y
        }

        x = xstart;
        y = ystart;
        err = el/2;
        pointArray.add(new Point(x, y));
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        for (int t = 0; t < el; t++)//идём по всем точкам, начиная со второй и до последней
        {
            err -= es;
            if (err < 0)
            {
                err += el;
                x += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += incy;//или сместить влево-вправо, если цикл проходит по y
            }
            else
            {
                x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            pointArray.add(new Point(x, y));
        }
        return pointArray;
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    private ArrayList<Point> generateSetPointsBetweenTwoPoints(double xstart, double ystart, double xend, double yend) {
        return generateSetPointsBetweenTwoPoints((int)xstart, (int)ystart, (int)xend, (int)yend);
    }
}