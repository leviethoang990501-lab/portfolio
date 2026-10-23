package core;

/** Quy tắc mở rộng hành trang dùng chung cho menu và xử lý mua. */
public final class BagUpgradeRules {
    public static final int START_CAPACITY = 42;
    public static final int MAX_CAPACITY = 126;
    public static final int SLOTS_PER_UPGRADE = 6;
    public static final int BASE_GEM_COST = 5;

    private BagUpgradeRules() {
    }

    public static boolean canUpgrade(int currentCapacity) {
        return currentCapacity < MAX_CAPACITY;
    }

    public static int nextCapacity(int currentCapacity) {
        int safeCurrent = Math.max(START_CAPACITY, currentCapacity);
        return Math.min(MAX_CAPACITY, safeCurrent + SLOTS_PER_UPGRADE);
    }

    public static int gemCost(int currentCapacity) {
        int safeCurrent = Math.max(START_CAPACITY, currentCapacity);
        int completedUpgrades = (safeCurrent - START_CAPACITY + SLOTS_PER_UPGRADE - 1)
                / SLOTS_PER_UPGRADE;
        return BASE_GEM_COST * (completedUpgrades + 1);
    }

    public static String menuLabel(int currentCapacity) {
        if (!canUpgrade(currentCapacity)) {
            return "Ô hành trang đã tối đa";
        }
        return "Mở thêm " + (nextCapacity(currentCapacity) - currentCapacity)
                + " ô (" + gemCost(currentCapacity) + " ngọc)";
    }
}
