package core;

/** Pure upgrade requirements, kept separate so they can be regression-tested. */
public final class UpgradeRules {
    public static final short LUCKY_STONE = 4;
    public static final short THREE_LEAF_CLOVER = 12;
    public static final short FOUR_LEAF_CLOVER = 13;
    public static final short THREE_COLOR_STONE = 14;

    private UpgradeRules() {
    }

    public static short[] materialsForTier(int tier) {
        if (tier < 0 || tier > 14) return new short[0];
        if (tier <= 5) return new short[]{0, 1};
        if (tier <= 10) return new short[]{0, 1, 2};
        return new short[]{0, 1, 2, 3};
    }

    public static int gemCost(int tier) {
        if (tier <= 2) return 4;
        if (tier <= 5) return 5;
        if (tier <= 8) return 6;
        if (tier <= 10) return 7;
        return tier <= 14 ? 10 : Integer.MAX_VALUE;
    }

    public static long goldCost(int tier) {
        return tier >= 0 && tier <= 14 ? 10_000L : Long.MAX_VALUE;
    }

    public static boolean isLuckyMaterial(short itemId) {
        return itemId == LUCKY_STONE || itemId == THREE_LEAF_CLOVER
                || itemId == FOUR_LEAF_CLOVER || itemId == THREE_COLOR_STONE;
    }

    public static int luckyBonus(short itemId) {
        if (itemId == THREE_COLOR_STONE) return 35;
        return isLuckyMaterial(itemId) ? 30 : 0;
    }

    public static int failureInsurancePercent(short itemId) {
        if (itemId == THREE_COLOR_STONE) return 100;
        return itemId == LUCKY_STONE || itemId == THREE_LEAF_CLOVER || itemId == FOUR_LEAF_CLOVER
                ? 30 : 0;
    }

    public static int failureTier(short itemId, int currentTier, boolean insuranceTriggered) {
        if (itemId == THREE_COLOR_STONE || insuranceTriggered) return currentTier;
        int protectedFloor = currentTier >= 11 ? 11 : currentTier >= 6 ? 6 : currentTier >= 1 ? 1 : 0;
        if (itemId == FOUR_LEAF_CLOVER) return Math.max(protectedFloor, currentTier - 1);
        if (itemId == THREE_LEAF_CLOVER) return Math.max(protectedFloor, currentTier - 2);
        return protectedFloor;
    }
}
