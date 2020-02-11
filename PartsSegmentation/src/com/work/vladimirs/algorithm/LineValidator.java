package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import org.opencv.core.Point;

import java.util.ArrayList;

public class LineValidator {

    public static boolean validateLineLength(Line currentLine) {
        Point p1 = currentLine.getStartPoint();
        Point p2 = currentLine.getEndPoint();
        ArrayList<Point> pointArrayList = generateSetPointsBetweenTwoPoints(p1.x, p1.y, p2.x, p2.y);
//        if (pointArrayList.size() < 40) {
//            System.out.println("FALSE!!!"); return false;}

        return true;
    }

    /**
     * реализация алгоритма Брезенхема
     */
    private static ArrayList<Point> generateSetPointsBetweenTwoPoints(int xstart, int ystart, int xend, int yend) {
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

    private static int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    private static ArrayList<Point> generateSetPointsBetweenTwoPoints(double xstart, double ystart, double xend, double yend) {
        return generateSetPointsBetweenTwoPoints((int)xstart, (int)ystart, (int)xend, (int)yend);
    }

    public static ArrayList<Line> findCollinear(ArrayList<Line> lines, Line line) {
        ArrayList<Line> collinear = new ArrayList<Line>();

        for (int i = 0; i < lines.size(); i++) {
            Line temp = lines.get(i);
            Point xy1 = line.getStartPoint();
            Point xy2 = line.getEndPoint();
            Point xy3 = temp.getStartPoint();
            Point xy4 = temp.getEndPoint();

            double deteminant = ((xy2.x - xy1.x) / (xy2.y - xy1.y)) - ((xy4.x - xy3.x) / (xy4.y - xy3.y));
            System.out.println(deteminant);

            if (Double.compare(deteminant, 0.0) == 0) {
                collinear.add(temp);
            }
        }

        return collinear;
    }

    public static ArrayList<Line> findCollinearNearby(ArrayList<Line> lines, Line line, int distance) {
        ArrayList<Line> collinearNearby = new ArrayList<Line>();

        for (int i = 0; i < lines.size(); i++) {
            Line temp = lines.get(i);

            Point xy1 = line.getStartPoint();
            Point xy2 = line.getEndPoint();
            Point xy3 = temp.getStartPoint();
            Point xy4 = temp.getEndPoint();

            double deteminant = ((xy2.x - xy1.x) / (xy2.y - xy1.y)) - ((xy4.x - xy3.x) / (xy4.y - xy3.y));
            System.out.println(deteminant);

            if (Double.compare(deteminant, 0.0) == 0) {
                collinearNearby.add(temp);
            }
        }

        return collinearNearby;
    }
}
