package core;

import java.util.List;

/** Central configuration for socket gems: 352-381 thuong, 382-411 tinh luyen, moi loai 5 cap. */
public final class GemRules {
    public static final int FAMILY_NONE = -1;
    public static final int FAMILY_HON_NGUYEN = 0;
    public static final int FAMILY_KHAI_HOAN = 1;
    public static final int FAMILY_LUC_BAO = 2;
    public static final int FAMILY_PHONG_MA = 3;
    public static final int FAMILY_SINH_MENH = 4;
    public static final int FAMILY_TAM_LINH = 5;

    public static final int EFF_HON_NGUYEN = -210;
    public static final int EFF_KHAI_HOAN = -211;
    public static final int EFF_PHONG_MA = -212;
    public static final int EFF_LUC_BAO = -213;
    public static final int EFF_SINH_MENH = -214;

    // Param hieu ung: ngoc thuong co them mat loi theo mo ta, ngoc tinh luyen thi khong.
    public static final int KHAI_HOAN_PENALTY = 20;
    public static final int NO_PENALTY = 1;
    public static final int WITH_PENALTY = 2;

    public static final int SOCKET_SUCCESS_PERCENT = 55;
    public static final int EFFECT_MILLIS = 3000;
    public static final int PHONG_MA_REFINED_MILLIS = 5000;
    public static final int LUC_BAO_PERCENT = 10;
    public static final int SINH_MENH_BOSS_PERCENT = 150;
    public static final int SINH_MENH_MOB_PERCENT = 80;

    // Ty le tinh tren 1000 (5 = 0.5%).
    private static final int[] NORMAL_CHANCE = {5, 10, 15, 25, 35};
    private static final int[] REFINED_PHONG_MA_CHANCE = {15, 20, 25, 35, 45};
    private static final int[] SINH_MENH_CHANCE = {20, 25, 30, 35, 45};
    private static final int[] PHONG_MA_HP_PERCENT = {20, 30, 40, 50, 70};
    private static final int[] PHONG_MA_HEAL_PERCENT = {1, 2, 5, 7, 10};
    private static final int[] TAM_LINH_CRIT_PERCENT = {3, 5, 7, 9, 14};

    private GemRules() {
    }

    public static boolean isRefined(int gemId) {
        return gemId >= 382 && gemId <= 411;
    }

    public static int family(int gemId) {
        if (gemId >= 352 && gemId <= 381) {
            return (gemId - 352) / 5;
        }
        if (isRefined(gemId)) {
            return (gemId - 382) / 5;
        }
        return FAMILY_NONE;
    }

    public static int level(int gemId) {
        return family(gemId) == FAMILY_NONE ? 0 : (gemId - 352) % 5 + 1;
    }

    /** Vien tot nhat cua mot loai: cap cao hon truoc, cung cap thi uu tien tinh luyen. */
    public static int bestGem(int family, List<Integer> gems) {
        int best = -1;
        for (int gemId : gems) {
            if (family(gemId) != family) {
                continue;
            }
            if (best == -1 || level(gemId) > level(best)
                    || (level(gemId) == level(best) && isRefined(gemId))) {
                best = gemId;
            }
        }
        return best;
    }

    public static int requiredHits(int gemId) {
        return (6 - level(gemId)) * 5;
    }

    public static int lucBaoChance(int gemId) {
        return NORMAL_CHANCE[level(gemId) - 1];
    }

    public static int phongMaChance(int gemId) {
        return (isRefined(gemId) ? REFINED_PHONG_MA_CHANCE : NORMAL_CHANCE)[level(gemId) - 1];
    }

    public static int phongMaHpPercent(int gemId) {
        return PHONG_MA_HP_PERCENT[level(gemId) - 1];
    }

    public static int phongMaHealPercent(int gemId) {
        return PHONG_MA_HEAL_PERCENT[level(gemId) - 1];
    }

    public static int sinhMenhChance(int gemId) {
        return SINH_MENH_CHANCE[level(gemId) - 1];
    }

    public static int critDamagePercent(int gemId) {
        return family(gemId) == FAMILY_TAM_LINH ? TAM_LINH_CRIT_PERCENT[level(gemId) - 1] : 0;
    }

    /** Vu khi: type 8-11; ao, quan, non, gang, giay: 0-3, 6; nhan, day chuyen: 4, 5. */
    public static boolean canSocket(int gemId, int itemType) {
        if (itemType >= 8 && itemType <= 11) {
            return (gemId >= 23 && gemId <= 32) || family(gemId) == FAMILY_HON_NGUYEN
                    || family(gemId) == FAMILY_KHAI_HOAN;
        }
        if (itemType == 0 || itemType == 1 || itemType == 2 || itemType == 3 || itemType == 6) {
            return family(gemId) == FAMILY_LUC_BAO || family(gemId) == FAMILY_PHONG_MA;
        }
        if (itemType == 4 || itemType == 5) {
            return family(gemId) == FAMILY_SINH_MENH || family(gemId) == FAMILY_TAM_LINH;
        }
        return false;
    }

    public static boolean isValidDrillTool(int itemId) {
        return itemId == 33 || itemId == 44 || itemId == 45;
    }

    /** Cac hieu ung xau ma ngoc khai hoan khang duoc. */
    public static boolean isResistedByKhaiHoan(int effId) {
        return (effId >= 0 && effId <= 4) || (effId >= -124 && effId <= -121) || effId == -201 || effId == -202
                || effId == -205 || effId == -206 || effId == -233 || effId == -236;
    }

    /** Ap dung phan tram sat thuong chi mang: mac dinh x2 (200%), cong them tu ngoc tam linh. */
    public static long critDamage(long dame, int bonusPercent) {
        return (dame * (200 + bonusPercent)) / 100;
    }
}
