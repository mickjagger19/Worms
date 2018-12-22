package gameobjects;

import java.io.Serializable;

/**
 * 该类是枚举类，是 client 间传递的信息
 */
public enum UpdateInformation implements Serializable{
    World_a_Player,
    World,
    Player,
}
