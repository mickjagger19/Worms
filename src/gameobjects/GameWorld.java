package gameobjects;

import java.io.Serializable;
import java.util.*;

/**
 * GameWorld 有一个 Surface 列表
 * 并且负责生成和毁灭游戏世界
 */
public class GameWorld implements Serializable {

    private final static int EXPECTED_VARIATIONS = 10;    // 预测的地形变化数量
    private final static int MAX_VARIATION = 1000;        // 最大地形变化数量
    private final static int MIN_VARIATION_YWIDTH = 100;   // 地形变化的最小宽度
    final static int EXPLOSION_RADIUS = 45;       // 爆炸范围
    final static int EXPLOSION_POINTS = 60;       // 爆炸中包含的点的数量，该值可以控制爆炸的精细度
    private static int height = 576;
    public static int relativeHeight;
    private static LinkedList<Point> pointsToAdd = new LinkedList<>();

    private Surface upperSurface = new Surface();
    private Surface wholeSurface = new Surface();
    private final int width;            // 地图的宽度

    private boolean worldChanged = false;


    public GameWorld(int width, int height) {
        this.width = width;
//        this.height = height;
//        upperSurface = new Surface();
        generateSurface();
    }

    /**
     * 随机生成地图 Surface
     */
    private void generateSurface() {

        List<Point> generatedWorld = new LinkedList<>();

        Random random = new Random();

        //初始值介于100 到 350之间
        relativeHeight = random.nextInt(251) + 100;
        //X 方向上剩余的空间
        int remainingHorizontal = width;
        //山谷到山峰的长度
        int averageWaveLength = remainingHorizontal / EXPECTED_VARIATIONS;
        //更改的方向
        int direction;
        //变化的长度
        int waveLength;
        //变化的高度
        int waveHeight;


        while (remainingHorizontal > 0) {
            //如果 direction 是负的，就形成一座山；如果 direction 是正的，形成一块山谷
            direction = random.nextInt((relativeHeight - 20) * 2) - 190;
            //更改的长度
            waveLength = Math.max(random.nextInt(averageWaveLength) + averageWaveLength / 2, MIN_VARIATION_YWIDTH);
            //更新x方向上剩余的空间
            remainingHorizontal -= waveLength;

            if (remainingHorizontal <= averageWaveLength / 2) {
                //如果剩余的空间太小，就会将剩余空间添加到当前的长度
                waveLength += remainingHorizontal;
                remainingHorizontal = 0;
            }

            if (direction < 0) {
                //负数， 放大
                waveHeight = Math.min(random.nextInt(Math.abs(360 - relativeHeight - 20)), MAX_VARIATION);

                for (int i = 0; i <= waveLength; i++) {
                    generatedWorld.add(new Point(width - remainingHorizontal - waveLength + i, (int) (576 - (relativeHeight + 20 + waveHeight * mountainFormula((double) i / (double) waveLength)))));
                }
                relativeHeight += waveHeight;
            } else {
                //正数， 减小
                waveHeight = Math.min(random.nextInt(relativeHeight - 20), MAX_VARIATION);

                for (int i = 0; i <= waveLength; i++) {
                    generatedWorld.add(new Point(width - remainingHorizontal - waveLength + i, (int) (576 - (relativeHeight + 20 + waveHeight * valleyFormula((double) i / (double) waveLength) - waveHeight))));
                }
                relativeHeight -= waveHeight;
            }
        }


        List<Point> wholeBorder = new LinkedList<>();
//
        //添加地图左侧的所有点
        for (int i = height; i >= height - relativeHeight; i -= 2) {
            wholeBorder.add(new Point(0, i));
        }
//
        wholeBorder.addAll(generatedWorld);

        //添加地图右侧的点
        for (int i = 576 - relativeHeight; i < height; i += 2) {
            wholeBorder.add(new Point(width, i));
        }

        //添加地图底部的点
        for (int i = width; i >= 0; i -= 2) {
            wholeBorder.add(new Point(i, height));
        }

        wholeSurface = new Surface(wholeBorder);
//        getSurface().add(new Surface(generatedWorld));
        upperSurface = new Surface(generatedWorld);

        worldChanged = true;
    }

//    /**
//     * 所有 Surface 都通过爆炸来破坏
//     * 该函数会清除处于 explosion 范围中的所有点，并将爆炸的圆周上的点存为新的点，新建 surface
//     */
//    public void destroySurface(Explosion explosion) {
//
//        List<Surface> newSurfaces = new LinkedList<>();
//
//        // 遍历地图中的所有 Surface
//        for (Surface surface : getSurface()) {
//            // 被爆炸摧毁的点
//            List<Point> pointsToRemove = new LinkedList<>();
//            // 爆炸后新增的点
//            List<Point> pointsToAdd = new LinkedList<>();
//            // 爆炸接触地面的点的 index
//            int initialIndex = -1;
//            // 爆炸接触地面的点的 位置
//            Point initialPoint = null;
//
//            // 遍历当前 border中的所有点, 清除爆炸范围内的点
//            for (Point point : surface.getBorder()) {
//
//                if (explosion.cover(point)) {
//                    // 设置处于爆炸范围内的第一个点的信息
//                    if (initialIndex == -1) {
//                        initialIndex = surface.getBorder().indexOf(point);
//                        initialPoint = point;
//                    }
//                    // 加入将要移除的点的列表
//                    pointsToRemove.add(point);
//                }
//            }
//
//            // 当前的 Surface 有某个点处于爆炸范围内，开始清除
//            if (initialIndex != -1) {
//
//                // 爆炸的圆周中离initialPoint最近的点的index
//                int indexOfNearestPoint = explosion.getIndexofNearestPoint(initialPoint);
//
////                int maxLength = indexOfNearestPoint + explosion.getBorder().length;
//
//                int consideredPointNumber = 0;
//
//                // 遍历爆炸的圆周的点，直到该点在 surface 中为止
////                while (!surface.contains(explosion.getBorder()[indexOfNearestPoint % explosion.getBorder().length]) && consideredPointNumber < maxLength) {
////                    indexOfNearestPoint++;
////                }
//
//                // 遍历爆炸的圆周的点，直到该点在 surface 中为止
//                while (true)
//                {
//                    if (surface.contains(explosion.getBorder()[indexOfNearestPoint % explosion.getBorder().length]) || consideredPointNumber++ >= explosion.getBorder().length)
//                        break;
//                }
//
//                indexOfNearestPoint %= explosion.getBorder().length;
//                int index = indexOfNearestPoint;
//
//                // 将 surface 和 explosion 重合的所有点加入将要清除的点的列表
//                while (surface.contains(explosion.getBorder()[index % explosion.getBorder().length]) && index < explosion.getBorder().length + indexOfNearestPoint) {
//                    pointsToAdd.add(explosion.getBorder()[index % explosion.getBorder().length]);
//                    index++;
//                }
//
//                int newSurfaceInitial = -1;
//                int newSurfaceFinal;
//                List<Point> newSurfacePoints = new LinkedList<>();
//
//                // 设置新的 surface
//                while (index < explosion.getBorder().length + indexOfNearestPoint) {
//
//                    /*
//                    * 搜索一个作为新 surface 起点的点，只有存在新的浮动表面时才会出现该点
//                    */
//                    if (newSurfaceInitial == -1 && surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {
//
//                        // 设置新 surface 的起始点
//                        newSurfaceInitial = surface.getIndexofNearestPoint(explosion.getBorder()[index % explosion.getBorder().length]);
//
//                        int cnt = 0;
//                        while (!explosion.cover(surface.getBorder().get(newSurfaceInitial)) && surface.getBorder().size() > newSurfaceInitial + 1 && cnt < 5) {
//                            newSurfaceInitial++;
//                            cnt++;
//                        }
//                    }
//
//                    // 爆炸圆周上的所有点都被加入到  newSurfacePoints
//                    if (newSurfaceInitial != -1 && surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {
//                        newSurfacePoints.add(explosion.getBorder()[index % explosion.getBorder().length]);
//                    }
//
//                    // 搜索新 surface 结束的点
//                    if (newSurfaceInitial != -1 && !surface.contains(explosion.getBorder()[index % explosion.getBorder().length])) {
//
//                        newSurfaceFinal = surface.getIndexofNearestPoint(explosion.getBorder()[(index - 1) % explosion.getBorder().length]);
//
//                        // 以刚刚得到的 newSurfacePoints为基础，创建 newSurface, 并加入 newSurfaces中
//                        if (newSurfaceInitial < newSurfaceFinal) {
//                            newSurfaces.add(new Surface( new ArrayList<>(surface.getBorder().subList(newSurfaceInitial, newSurfaceFinal))));
//                        } else {
//                            newSurfacePoints.addAll(new ArrayList<>(surface.getBorder().subList(newSurfaceFinal, newSurfaceInitial)));
//                            newSurfaces.add(new Surface(newSurfacePoints));
//                        }
//
//                        // 重新设置变量，以便再次创建新的 surface
//                        newSurfaceInitial = -1;
//                        newSurfacePoints = new LinkedList<>();
//                    }
//
//                    index++;
//                }// 设置 newSurfaces 结束
//
//                // 设置旧 surface 的 newBorder, 是 旧border - 爆炸点 + 爆炸生成的新点
//                List<Point> newBorder = new LinkedList<>(surface.getBorder());
//                newBorder.removeAll(pointsToRemove);
//                newBorder.addAll(initialIndex, pointsToAdd);
//
//                // 为了保险，去除所有被删除的点
//                for (Surface newSurface : newSurfaces) {
//                    newBorder.removeAll(newSurface.getBorder());
//                }
//
//                // 设置旧 surface 的 border
//                surface.setBorder(newBorder);
//
//            }// 结束清除过程
//
//        }//下一个 surface
//
//        upperSurface.addAll(newSurfaces);
//        worldChanged = true;
//
//        System.out.println("服务器 destroy surface 完毕");
//    }


    public void destroySurface(Explosion explosion) {

        Point startPoint = null, endPoint;

        pointsToAdd = new LinkedList<>();

        // 遍历地图中的所有 Surface
        Surface surface = upperSurface;


        // 遍历当前 border中的所有点, 清除爆炸范围内的点
        for (Point point : surface.getBorder()) {
            boolean inExplosion = explosion.cover(point);
            if (startPoint == null && inExplosion) {
                startPoint = point;
            } else if (startPoint != null && !inExplosion) {
                endPoint = point;
                addExplosionArc(startPoint, endPoint, explosion);
                startPoint = null;
            } else if (!inExplosion) {
                pointsToAdd.add(point);
            }
        }

        upperSurface.setBorder(pointsToAdd);

        List<Point> wholeBorder = new LinkedList<>();

        for (int i = height; i >= height - relativeHeight; i -= 2) {
            wholeBorder.add(new Point(0, i));
        }
        wholeBorder.addAll(pointsToAdd);

        for (int i = 576 - relativeHeight; i < height; i += 2) {
            wholeBorder.add(new Point(width, i));
        }

        for (int i = width; i >= 0; i -= 2) {
            wholeBorder.add(new Point(i, height));
        }

        wholeSurface.setBorder(wholeBorder);

        worldChanged = true;
//        System.out.println("服务器 destroy surface 完毕");
    }

    private void addExplosionArc(Point start, Point end, Explosion explosion) {
        double angleStart = Math.asin((explosion.getCenter().getyCoord() - start.getyCoord()) / EXPLOSION_RADIUS);
        double angleEnd = Math.asin((explosion.getCenter().getyCoord() - end.getyCoord()) / EXPLOSION_RADIUS);


        if (start.getxCoord() < explosion.getCenter().getxCoord()) angleStart = Math.PI - angleStart;
        if (angleStart < 0) angleStart += 2 * Math.PI;
        if (end.getxCoord() < explosion.getCenter().getxCoord()) angleEnd = -Math.PI - angleEnd;
        if (angleEnd <= 0 || angleEnd < angleStart) angleEnd += 2 * Math.PI;



        for (double i = angleStart; i <= angleEnd; i += 1.0 / 20) {
            Point p = new Point((int)explosion.getCenter().getxCoord() + EXPLOSION_RADIUS * Math.cos(i), (int)explosion.getCenter().getyCoord() - EXPLOSION_RADIUS * Math.sin(i));

            pointsToAdd.add(p);
        }

    }


    /**
     * 山谷的曲面公式
     * y = 2x^3 - 3x^2 + 1
     */
    private double valleyFormula(double x) {
        return (2 * Math.pow(x, 3) - 3 * Math.pow(x, 2) + 1);
    }

    /**
     * 山峰的曲面公式
     * y = -2x^3 + 3*x^2
     */
    private double mountainFormula(double x) {
        return (-2 * Math.pow(x, 3) + 3 * Math.pow(x, 2));
    }


    // 返回当面 surface 是否包含某个点
    public boolean containsPoint(Point p) {
        return upperSurface.contains(p);
    }

    public synchronized Surface getSurface() {
        return upperSurface;
    }

    public synchronized Surface getWholeSurface() {
        return wholeSurface;
    }


    //计算地图中距离p最近的点
    public Point getNearestPoint(Point p) {
        Point nearestPoint = null;

        if (!upperSurface.getBorder().isEmpty()) {
            nearestPoint = upperSurface.getBorder().get(upperSurface.getIndexofNearestPoint(p));
        }

        if (nearestPoint == null)
            return p;
        else {
//            System.out.println("getNearestPoint x: " + nearestPoint.getxCoord() + "\ty: " + nearestPoint.getyCoord());
            return new Point(nearestPoint.getxCoord(), nearestPoint.getyCoord());
        }
    }

    public boolean isWorldChanged() {
        return worldChanged;
    }

    public void setWorldChanged() {
        worldChanged = false;
    }
}
