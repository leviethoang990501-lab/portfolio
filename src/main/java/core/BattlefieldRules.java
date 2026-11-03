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
    public static final int MIN_LEVEL = 40;
    public static final int FEE_VANG = 500_000;
    public static final int FEE_NGOC = 2;
    public static final int GUARDS_PER_HOUSE = 10;
    public static final int MAX_FACTION_PER_HOUSE = 10;
    public static final int FIRST_BOSS_TIME_LEFT = MATCH_SECONDS - 5 * 60;
    public static final int SECOND_BOSS_TIME_LEFT = MATCH_SECONDS - 10 * 60;
    public static final long HENSHIN_MILLIS = 60_000L;
    public static final int ITEM_COOLDOWN_MIN_MILLIS = 250_000;
    public static final int ITEM_COOLDOWN_MAX_MILLIS = 270_000;
    public static final int ITEM_PERSONAL_EXTRA_MILLIS = 100;
    public static final long ZONE_CHANGE_WAIT_MILLIS = 5_000L;
    public static final int ZONE_CHANGE_FREE_TIME_LEFT = 15 * 60; // tu phut 45 khong con gioi han
    private static final int[] REVIVE_COST = {500, 1_000, 2_000, 3_000, 4_000, 5_000, 6_000, 7_000,
        8_000, 9_000, 10_000, 12_000, 14_000, 16_000};
    private static final int REVIVE_LIMITED_TIMES = 24;
    private static final int REVIVE_COST_AFTER_LIMIT = 55_000;
    public static final int BOSS_POINT = 30;
    public static final int MONSTER_POINT = 2;
    public static final int GUARD_POINT = 2;
    public static final int HOUSE_POINT = 20;
    public static final int TRADE_ROLE_POINT_COST = 2;
    // Nguyen lieu vang chi co o chien truong.
    public static final int MONSTER_YELLOW_DROP_PERCENT = 35;
    public static final int HIT_YELLOW_DROP_PERCENT = 5;
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

    public static boolean isHouseMap(int mapId) {
        return houseMapOwner(mapId) != -1;
    }

    /** Phe (typepk) so huu map nha chinh, -1 neu khong phai map nha chinh. */
    public static int houseMapOwner(int mapId) {
        switch (mapId) {
            case 54: return 5;
            case 56: return 2;
            case 58: return 4;
            case 60: return 1;
            default: return -1;
        }
    }

    public static boolean mustWaitZoneChange(int mapId, int typepk, long millisInZone, int timeLeft) {
        return isHouseMap(mapId) && houseMapOwner(mapId) != typepk
                && timeLeft > ZONE_CHANGE_FREE_TIME_LEFT && millisInZone < ZONE_CHANGE_WAIT_MILLIS;
    }

    /** Phi hoi sinh theo so lan da hoi sinh trong tran; bai goc khong ghi lan 15-24 nen tang deu 2.000. */
    public static int reviveCost(int revivedTimes) {
        if (revivedTimes < REVIVE_COST.length) {
            return REVIVE_COST[Math.max(0, revivedTimes)];
        }
        if (revivedTimes < REVIVE_LIMITED_TIMES) {
            return REVIVE_COST[REVIVE_COST.length - 1] + (revivedTimes - REVIVE_COST.length + 1) * 2_000;
        }
        return REVIVE_COST_AFTER_LIMIT;
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
