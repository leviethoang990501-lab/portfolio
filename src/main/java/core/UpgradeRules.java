package core;

/** Pure upgrade requirements, kept separate so they can be regression-tested. */
public final class UpgradeRules {
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
}
