package core;

/** Solo-server balancing for the normal map bosses. */
public final class BossRules {
    public static final int MAX_LEVEL_DIFFERENCE = 5;

    private BossRules() {
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
