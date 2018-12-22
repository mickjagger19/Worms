package gameobjects;

import java.util.List;

/**
 *
 * 该类包含计算爆炸的方法, 从而计算爆炸的伤害
 */
public class Explosion {

    // 爆炸的 border， 简化为圆周
    private Point[] border;
    private Point center;

    // 指定爆炸中心点的构造器
    public Explosion(Point point) {
        center = point;
        border = new Point[GameWorld.EXPLOSION_POINTS];
        determineBorder(point);
    }

    public Point[] getBorder() {
        return border;
    }

    /**
     * 计算爆炸的边缘
     */
    private void determineBorder(Point point) {
        double angle = 0;
        // 计算 border 圆周上的每个点的坐标
        for (int i = GameWorld.EXPLOSION_POINTS - 1; i >= 0; i--) {
            border[i] = new Point((int) (point.getxCoord() + Math.cos(Math.toRadians(angle)) * GameWorld.EXPLOSION_RADIUS), (int) (point.getyCoord() + Math.sin(Math.toRadians(angle)) * GameWorld.EXPLOSION_RADIUS));
            angle += (360.0 / GameWorld.EXPLOSION_POINTS);
        }
    }

    /**
     * @return 指定点是否处于爆炸范围
     */
    boolean cover(Point point) {

        return getDistance(center, point) < GameWorld.EXPLOSION_RADIUS;

    }

    /**
     * @return 爆炸的 border 上，最接近指定点的 index
     */
    int getIndexofNearestPoint(Point point) {
        double smallestDistance = Double.MAX_VALUE;
        int index = 0;
        // 遍历 border 上的所有点，记录最小距离和相应 index
        for (int i = 0; i < border.length; i++) {
            if (smallestDistance > getDistance(border[i], point)) {
                smallestDistance = getDistance(border[i], point);
                index = i;
            }
        }

        return index;
    }

    /**
     * 计算爆炸周围的 worm 受到的伤害
     */
    public void calculateDamage(List<Player> players) {
        for (Player p : players) {
            if (cover(p.getPosition())) {
                // 如果处于爆炸范围内， 伤害用 worm 的位置到圆周的距离来计算
                p.removeHealth((int) (GameWorld.EXPLOSION_RADIUS - getDistance(p.getPosition(), center)) * 2);
            }
        }
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }

    Point getCenter(){
        return center;
    }
}
