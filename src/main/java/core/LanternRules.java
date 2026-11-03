package core;

/** Central configuration for lantern items (item4) and the effects they grant. */
public final class LanternRules {
    public static final int EFF_EXP = -240;
    public static final int EFF_GOLD = -241;
    public static final int EFF_DEF = -242;
    public static final int EFF_DAME = -243;
    public static final int EFF_HP = -244;
    public static final int EFF_BUTTERFLY = -246;

    // Param tinh theo don vi 1/100 %, giong cac chi so % trong Body (300 = 3%).
    public static final int NORMAL_PARAM = 300;
    public static final int BUTTERFLY_EXP_PARAM = 2500;
    public static final int BUTTERFLY_HP_DEF_PARAM = 1000;

    public static final long HOUR_MILLIS = 60L * 60L * 1000L;
    public static final long DAY_MILLIS = 24L * HOUR_MILLIS;
    // Hieu ung luu theo thoi gian con lai nen "vinh vien" la mot moc rat xa.
    public static final long PERMANENT_MILLIS = 100L * 365L * DAY_MILLIS;
    public static final long ALREADY_PERMANENT = -1L;

    // Hieu ung hinh long den co san trong client (data/part_char/imgver: nhom 111).
    public static final long SPRITE_INTERVAL_MILLIS = 2000L;
    private static final int SPRITE_LOTUS = 81;
    private static final int SPRITE_CARP = 82;
    private static final int SPRITE_BUTTERFLY = 83;
    private static final int SPRITE_MOON = 84;
    private static final int SPRITE_GOLD = 85;

    private LanternRules() {
    }

    /** Hinh hien tren nhan vat cua hieu ung long den, hoac 0 neu khong phai long den. */
    public static int spriteId(int effId) {
        switch (effId) {
            case EFF_EXP:
                return SPRITE_GOLD;
            case EFF_GOLD:
                return SPRITE_LOTUS;
            case EFF_DEF:
                return SPRITE_CARP;
            case EFF_DAME:
            case EFF_BUTTERFLY:
                return SPRITE_BUTTERFLY;
            case EFF_HP:
                return SPRITE_MOON;
            default:
                return 0;
        }
    }

    /** Effect id cua long den, hoac 0 neu item khong phai long den. */
    public static int effectId(short itemId) {
        switch (itemId) {
            case 144:
            case 208:
            case 263:
                return EFF_EXP;
            case 173:
            case 210:
            case 264:
                return EFF_GOLD;
            case 174:
            case 211:
            case 265:
                return EFF_DEF;
            case 175:
            case 212:
            case 266:
                return EFF_DAME;
            case 176:
            case 213:
            case 267:
                return EFF_HP;
            case 214:
            case 260:
                return EFF_BUTTERFLY;
            default:
                return 0;
        }
    }

    public static boolean isLantern(short itemId) {
        return effectId(itemId) != 0;
    }

    public static boolean isLanternEffect(int effId) {
        return effId <= EFF_EXP && effId >= EFF_BUTTERFLY;
    }

    public static int effectParam(int effId) {
        switch (effId) {
            case EFF_EXP:
            case EFF_GOLD:
            case EFF_DEF:
            case EFF_DAME:
            case EFF_HP:
                return NORMAL_PARAM;
            default:
                return 1; // add_eff bo qua param 0
        }
    }

    public static long duration(short itemId) {
        switch (itemId) {
            case 144:
            case 173:
            case 174:
            case 175:
            case 176:
                return DAY_MILLIS;
            case 208:
            case 210:
            case 211:
            case 212:
            case 213:
                return 30L * DAY_MILLIS;
            case 260:
                return 7L * DAY_MILLIS;
            case 214:
            case 263:
            case 264:
            case 265:
            case 266:
            case 267:
                return PERMANENT_MILLIS;
            default:
                return 0L;
        }
    }

    /** Dung them long den cung loai se cong don thoi gian; da vinh vien thi khong dung them. */
    public static long nextDuration(long remaining, long duration) {
        if (remaining >= PERMANENT_MILLIS / 2) {
            return ALREADY_PERMANENT;
        }
        return Math.max(0L, remaining) + duration;
    }
}
