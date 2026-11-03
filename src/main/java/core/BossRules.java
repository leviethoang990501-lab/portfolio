package core;

/** Solo-server balancing for the normal map bosses. */
public final class BossRules {
    public static final int MAX_LEVEL_DIFFERENCE = 5;
    public static final long LIFETIME_MILLIS = 3L * 60L * 60L * 1000L;
    // So nguyen lieu tim moi boss mang theo, rot dan tu khi con duoi 50% hp.
    public static final int VIOLET_STOCK = 10;
    public static final int DUNGEON_BLUE_DROP = 3;
    private static final int DUNGEON_BOSS_WAVE = 21; // wave da tang sau khi bao "Hiep 20"
    private static final int[] DUNGEON_BOSS_BY_MODE = {103, 104, 101, 84, 105};

    private BossRules() {
    }

    /** 2-3 loai nguyen lieu tim (136-145) ma boss mang theo; rong neu khong phai boss ban do. */
    public static short[] violetTypes(int mobId) {
        switch (mobId) {
            case 103: return new short[]{136, 137};
            case 104: return new short[]{137, 138};
            case 101: return new short[]{138, 139};
            case 84:  return new short[]{139, 140};
            case 105: return new short[]{140, 141};
            case 83:  return new short[]{141, 142};
            case 106: return new short[]{142, 143, 144};
            case 149: return new short[]{143, 144, 145};
            case 155: return new short[]{136, 140, 145};
            case 174: return new short[]{137, 141, 144};
            default:  return new short[0];
        }
    }

    public static boolean isDungeonBossWave(int wave) {
        return wave == DUNGEON_BOSS_WAVE;
    }

    /** Moi do kho pho ban co mot boss rieng, chi xuat hien 1 con o hiep cuoi. */
    public static int dungeonBossId(int mode) {
        return DUNGEON_BOSS_BY_MODE[Math.max(0, Math.min(mode, DUNGEON_BOSS_BY_MODE.length - 1))];
    }

    public static int soloHp(int mobId, int originalHp) {
        switch (mobId) {
            case 103: return 250_000;   // Bọ cạp chúa, level 14
            case 104: return 600_000;   // Quỷ một mắt, level 34
            case 101: return 1_000_000; // Xà nữ, level 54
            case 84:  return 1_500_000; // Dê vàng, level 74
            case 105: return 2_000_000; // Quỷ đầu bò, level 84
            case 83:  return 2_500_000; // Dê bạc, level 94
            case 106: return 3_000_000; // Kỵ sĩ địa ngục, level 104
            case 149: return 3_500_000; // Nhện chúa, level 114
            case 155: return 4_000_000; // Giant Skeleton, level 124
            case 174: return 2_000_000; // Chó ba đầu
            default: return originalHp;
        }
    }

    public static boolean canDamage(int mobId, boolean boss, boolean battlefield,
            int attackerLevel, int bossLevel) {
        return !boss || battlefield || mobId == 174
                || Math.abs(attackerLevel - bossLevel) <= MAX_LEVEL_DIFFERENCE;
    }
}
