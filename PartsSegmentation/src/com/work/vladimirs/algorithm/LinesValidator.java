package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.entities.Line;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.*;

public class LinesValidator {

    private static final double DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES = 25.0;
    private static final int MINIMAL_LINE_LENGTH = 40;
    private Mat originalMat;

    public LinesValidator(Mat originalMat) {
        this.originalMat = originalMat;
    }

    public ArrayList<Line> validate(ArrayList<Line> lines) {
        ArrayList<Line> approvedLines = new ArrayList<Line>();



        return approvedLines;
    }







    //==================================================================================================================
    //Находим все близлежащие линии меньше заданной дистанции
    //fixme правильно расчитывать раастояние между именно лежащими рядом парралельными линиями
 /*   private Map<Line, Line> findCollinearNearby(HashSet<Line> lines, double distance) {
        Map<Line, Line> lineLineMap = new HashMap<>();

        *//*for (int i = 0; i < list.size(); i++) {
            for (int j = i+1; j < list.size(); j++) {
                // compare list.get(i) and list.get(j)
            }
        }*//*

        ArrayList<Line> rl = new ArrayList<Line>(lines);
        ArrayList<Line> lr = new ArrayList<Line>(rl);
        Collections.reverse(lr);
        for (Line lineA : rl) {
            lr.remove(lr.size()-1);
            for (Line lineZ : lr) {
                double d = linePointDistance(lineA, lineZ.getStartPoint());
//                System.out.println("d = " + d);
                //if (Double.compare(d, 0.0) == 1 && Double.compare(d, DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES) == -1)
                if (Double.compare(d, 0.0) == 1 && Double.compare(d, DISTANCE_BETWEEN_TWO_PARALLEL_NEAREST_LINES) == -1) {
                    Point centerLineA = findLineCenter(lineA);
                    Point centerLineZ = findLineCenter(lineZ);
                    double lengthLineA = lengthBetweenPoint(lineA.getStartPoint(),lineA.getEndPoint());
                    double lengthLineZ = lengthBetweenPoint(lineZ.getStartPoint(),lineZ.getEndPoint());
                    double lengthBetweenCenter = lengthBetweenPoint(centerLineA, centerLineZ);
                    if (Double.compare(lengthBetweenCenter, (lengthLineA + lengthLineZ)/2) == -1) {
                        lineLineMap.put(lineA, lineZ);
                    }
                }

            }
        }
        return lineLineMap;
    }

    private HashSet<Line> findCollinear(ArrayList<Line> lines, Line line) {
        HashSet<Line> collinear = new HashSet<>();
        collinear.add(line);
        for (int i = 1; i < lines.size(); i++) {
            Line temp = lines.get(i);
            Point xy1 = line.getStartPoint();
            Point xy2 = line.getEndPoint();
            Point xy3 = temp.getStartPoint();
            Point xy4 = temp.getEndPoint();

            double deteminant = ((xy2.x - xy1.x) / (xy2.y - xy1.y)) - ((xy4.x - xy3.x) / (xy4.y - xy3.y));
//            System.out.println(deteminant);

            if (Double.compare(deteminant, 0.0) == 0 || Double.isNaN(deteminant)) {
                collinear.add(temp);
            }
        }

        return collinear;
    }

    private double linePointDistance(Line line, Point point) {
        double vector_x = line.getEndPoint().x - line.getStartPoint().x;
        double vector_y = line.getEndPoint().y - line.getStartPoint().y;
        double lineLen = lengthBetweenPoint(vector_x, vector_y);
        if (Double.compare(lineLen, 0.0) == 0) // Replace with appropriate epsilon
        {
            return lengthBetweenPoint(point.x - line.getStartPoint().x, point.y - line.getStartPoint().y);
        }

        double norm_perpendicular_x = -(vector_y / lineLen);
        double norm_perpendicular_y = vector_x/lineLen;

        double dot = ((point.x - line.getStartPoint().x) * norm_perpendicular_x + (point.y - line.getStartPoint().y) * norm_perpendicular_y); // Compute dot product (P3 - P1) dot( norm ( P2 - P1 ))
        return Math.abs(dot);
    }

    private boolean isLineLengthRight(Line currentLine) {
        Point p1 = currentLine.getStartPoint();
        Point p2 = currentLine.getEndPoint();
        return (lengthBetweenPoint(p1, p2) >= MINIMAL_LINE_LENGTH);
    }

    *//**
     * реализация алгоритма Брезенхема
     *//*
    private  ArrayList<Point> generateSetPointsBetweenTwoPoints(int xstart, int ystart, int xend, int yend) {
        ArrayList<Point> pointArray = new ArrayList<>();
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;//проекция на ось X
        dy = yend - ystart;//проекция на ось Y

        incx = sign(dx);
        *//*
         * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
         * справа налево по иксу, то incx будет равен -1.
         * Это будет использоваться в цикле постороения.
         *//*
        incy = sign(dy);
        *//*
         * Аналогично. Если рисуем отрезок снизу вверх -
         * это будет отрицательный сдвиг для y (иначе - положительный).
         *//*

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        //эти две строчки можно записать и так: dx = Math.abs(dx); dy = Math.abs(dy);

        if (dx > dy)
        //определяем наклон отрезка:
        {
            *//*
             * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
             * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
             * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incx;), при этом
             * по y сдвиг такой отсутствует.
             *//*
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

    private  int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    private  ArrayList<Point> generateSetPointsBetweenTwoPoints(double xstart, double ystart, double xend, double yend) {
        return generateSetPointsBetweenTwoPoints((int)xstart, (int)ystart, (int)xend, (int)yend);
    }*/
}