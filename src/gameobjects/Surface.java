package gameobjects;

import java.io.Serializable;
import java.util.List;

/**
 * 该类代表地图中的所有多边形
 * 一个地图由很多个 Surface 对象组合而成
 */
public class Surface implements Serializable {

    // 一个 Surface 只有一个 border, 为不同点依次连接形成的多边形
    private List<Point> border;

    // Surface 中的点的坐标
    private double[] xCoords;
    private double[] yCoords;

    Surface() {
        border = null;
        xCoords = null;
        yCoords = null;
    }

    // 指定边界的构造器
    Surface(List<Point> border) {
        this.border = border;
        rearrangePoints();
    }

    // 设置 Surface 的边界为指定边界
    void setBorder(List<Point> border) {
        this.border = border;
        rearrangePoints();
    }


    List<Point> getBorder() {
        return border;
    }

    /**
     * border更改后，根据新的border, 更新 Surface 中的所有点的坐标
     */
    private void rearrangePoints() {
        xCoords = new double[border.size()];
        yCoords = new double[border.size()];

        for (int i = 0; i < border.size(); i++) {
            xCoords[i] = border.get(i).getxCoord();
            yCoords[i] = border.get(i).getyCoord();
        }
    }

    public double[] getyCoords() {
        return yCoords;
    }

    public double[] getxCoords() {
        return xCoords;
    }

    /**
     * @return 某个点是否在 Surface 中
     */
    boolean contains(Point point) {
        int i;
        int j;
        boolean result = false;

        for (Point surfacePoint : border) {
            if (surfacePoint.getxCoord() == point.getxCoord() && surfacePoint.getyCoord() == point.getyCoord()) {
                return true;
            }
        }

        for (i = 0, j = border.size() - 1; i < border.size(); j = i++) {
            if ((border.get(i).getyCoord() > point.getyCoord()) != (border.get(j).getyCoord() > point.getyCoord()) &&
                    (point.getxCoord() < (border.get(j).getxCoord() - border.get(i).getxCoord()) * (point.getyCoord() - border.get(i).getyCoord()) / (border.get(j).getyCoord() - border.get(i).getyCoord()) + border.get(i).getxCoord())) {
                result = !result;
            }
        }
        return result;
    }

    /**
     * @return 该 Surface 的边界中离指定点最近的点
     */
    int getIndexofNearestPoint(Point point) {

        double smallestDistance = Double.MAX_VALUE;
        int index = 0;

        for (Point borderPoint : border) {
            // 找出 border 中离指定点最近的距离
            if (smallestDistance > getDistance(borderPoint, point)) {
                smallestDistance = getDistance(borderPoint, point);
                index = border.indexOf(borderPoint);
            }
        }
        return index;
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }

}
