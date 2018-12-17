package gameobjects;


import java.io.Serializable;
import java.util.Random;

/**
 * 该类是玩家类，用于描述玩家信息
 * 玩家信息包括：worms的 Position, Team, Health 等
 * 还包含 移动worms, 并利用重力加速度计算它们应该有的动作、位置的方法
 *
 */
public class Player implements Serializable {

    public static final int WORM_SKINS = 23;
    private String name;
    private Point position;
    /**
     * @param fallingspeed 下落速度
     */
    private int fallingspeed;
    private int health;

    private Shoot ownShoot;
    private boolean isCurrent;
    private String team;

    private int wormSkin = 0;

    private boolean changed = false;

    public enum direction{
        left,
        right;
    }

    public direction dir = direction.right;
    public direction previousDir = direction.right;

    //指定昵称，皮肤随机
    public Player(String playername) {
        name = playername;
        fallingspeed = 0;
        health = 100;
        setWormSkin(new Random().nextInt(WORM_SKINS));
    }

    //指定昵称和皮肤
    public Player(String playername, int skin) {
        name = playername;
        fallingspeed = 0;
        health = 100;
        setWormSkin(skin);
    }

    public String getName() {
        return name;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void applyPhysics(GameWorld gameWorld) {
        if (position != null) {

            if (getDistance(gameWorld.getNearestPoint(position), position) > 3)
                //向上移动5个单位
                position.setyCoord(position.getyCoord() + 5);

            if (position.getyCoord() > 576)
                //超出地图，判定为死亡，扣除100点生命值
                removeHealth(100);

            if (!isDead() && gameWorld.containsPoint(position)) {
                Point point = gameWorld.getNearestPoint(position);
                point.setyCoord(point.getyCoord() - 2);
                position = point;
            }
            changed = true;
        }
    }

    public void movePlayer(int value) {
        position.setxCoord(position.getxCoord() + value);
    }

    /*
     * 计算两个点之间的距离
     */
    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }

    public int getHealth() {
        return health;
    }

    /**
     * 计算受伤害后的 health 值
     * @param removedHealth 将要扣除的 health 值
     */
    public void removeHealth(int removedHealth) {
        if (health > 0) {
            if (health-removedHealth < 0) {
                health = 0;
            }
            else {
                this.health = health - removedHealth;
            }
            changed = true;
        }
        else {
            changed = false;
        }

    }

    public boolean isDead() {
        return health <= 0 || position.getyCoord() > 576;
    }

    public Shoot getShoot() {
        if (ownShoot == null) {
            ownShoot = new Shoot(0.5, 50, false);
        }
        return ownShoot;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", fallingspeed=" + fallingspeed +
                ", health=" + health +
                ", ownShoot=" + ownShoot +
                '}';
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public int getWormSkin() {
        return wormSkin;
    }

    /**
     * @param wormSkin 设置worm的皮肤
     */
    public void setWormSkin(int wormSkin) {
        if (wormSkin == 100) {
            this.wormSkin = 100;
        }
        else if (wormSkin >= WORM_SKINS)
            throw new IllegalArgumentException(String.format("指定的皮肤序号 [%d] 超出上限", WORM_SKINS, wormSkin));
        else if (wormSkin < 0)
            throw new IllegalArgumentException(String.format("指定的皮肤序号 [%d] 为负数", wormSkin));

        this.wormSkin = wormSkin;
    }

//    public void setChanged(boolean changed) {
//        this.changed = changed;
//        if (changed == false) {
//            if (position != null)
//                position.setChanged(false);
//            if (ownShoot != null)
//                ownShoot.setChanged(false);
//        }
//    }

    /**
     * 治疗，返回正确的health值
     */
    public void heal(int hp) {
//        if (hp < 0)
//            throw new IllegalArgumentException("Der Spieler wird nicht geheilt!");

        if (hp >= 100) {
            health = 100;
        } else {
            health += hp;
        }
    }

}
