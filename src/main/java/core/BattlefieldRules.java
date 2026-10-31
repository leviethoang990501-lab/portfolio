package core;

/** Pure balancing values for the solo battlefield and its persistent points. */
public final class BattlefieldRules {
    public static final int ON_DEMAND_REGISTER_SECONDS = 30;
    public static final int MATCH_SECONDS = 60 * 60;
    public static final int GUARD_HP = 300_000;
    public static final int GUARD_DAMAGE = 3_000;
    public static final int HOUSE_HP = 5_000_000;
    public static final int HOUSE_GUARDED_DAMAGE_DIVISOR = 100;
    public static final int HOUSE_CLEARED_DAMAGE_DIVISOR = 5;
    public static final int MONSTER_POINT = 1;
    public static final int GUARD_POINT = 2;
    public static final int HOUSE_POINT = 20;
    public static final int TRADE_ROLE_POINT_COST = 2;
    public static final int MONSTER_VIOLET_DROP_PERCENT = 35;
    public static final int HOUSE_SKILL_BOOK_DROP_PERCENT = 25;
    public static final int MATERIAL_MONSTER_COUNT = 8;
    static final int[][] MATERIAL_MONSTER_SPAWNS = {
        {33, 300, 240}, {34, 540, 240}, {35, 780, 240},
        {36, 300, 480}, {33, 780, 480},
        {34, 300, 720}, {35, 540, 720}, {36, 780, 720}
    };

    private BattlefieldRules() {
    }

    public static boolean isHouse(int mobId) {
        return mobId >= 89 && mobId <= 92;
    }

    public static int houseDamageDivisor(boolean guardsAlive) {
        return guardsAlive ? HOUSE_GUARDED_DAMAGE_DIVISOR : HOUSE_CLEARED_DAMAGE_DIVISOR;
    }

    public static int countLivingEnemyHouses(int[] houseInfo, int ownVillage) {
        if (houseInfo == null || ownVillage < 2 || ownVillage > 5) return 0;
        int living = 0;
        for (int i = 0; i < houseInfo.length; i++) {
            if (i != ownVillage - 2 && houseInfo[i] > 0) living++;
        }
        return living;
    }
}
