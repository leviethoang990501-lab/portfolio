package core;

import template.Item3;
import template.Option;

/** Class-specific rules for the four craftable medals. */
public final class MedalRules {
    public static final int CHANGE_LINE_GEM_COST = 100;
    private static final short[] MEDAL_IDS = new short[]{4587, 4589, 4588, 4590};
    private static final short[] MEDAL_ICONS = new short[]{954, 956, 955, 957};
    private static final byte[] FLAT_DAMAGE_OPTIONS = new byte[]{2, 4, 1, 3};
    private static final byte[] PERCENT_DAMAGE_OPTIONS = new byte[]{9, 11, 8, 10};

    private MedalRules() {
    }

    public static short medalIdForClass(byte clazz) {
        return validClass(clazz) ? MEDAL_IDS[clazz] : -1;
    }

    public static byte flatDamageOptionForClass(byte clazz) {
        return validClass(clazz) ? FLAT_DAMAGE_OPTIONS[clazz] : -1;
    }

    public static byte percentDamageOptionForClass(byte clazz) {
        return validClass(clazz) ? PERCENT_DAMAGE_OPTIONS[clazz] : -1;
    }

    public static byte flatDamageOptionForMedal(short medalId) {
        int clazz = classForMedal(medalId);
        return clazz >= 0 ? FLAT_DAMAGE_OPTIONS[clazz] : -1;
    }

    public static byte percentDamageOptionForMedal(short medalId) {
        int clazz = classForMedal(medalId);
        return clazz >= 0 ? PERCENT_DAMAGE_OPTIONS[clazz] : -1;
    }

    /**
     * Returns the other valid flat-damage line for this medal. A medal can
     * switch between physical damage (0) and its own elemental damage line.
     */
    public static byte alternateFlatDamageOption(Item3 item, int currentOptionId) {
        if (item == null || item.type != 16) return -1;
        int current = Byte.toUnsignedInt((byte) currentOptionId);
        if (current < 0 || current > 4) return -1;
        byte elemental = flatDamageOptionForMedal(item.id);
        if (elemental < 0) return -1;
        return current == 0 ? elemental : (byte) 0;
    }

    /**
     * Returns the other valid percent-damage line for this medal. A medal can
     * switch between physical damage percent (7) and its own elemental line.
     */
    public static byte alternatePercentDamageOption(Item3 item, int currentOptionId) {
        if (item == null || item.type != 16) return -1;
        int current = Byte.toUnsignedInt((byte) currentOptionId);
        if (current < 7 || current > 11) return -1;
        byte elemental = percentDamageOptionForMedal(item.id);
        if (elemental < 0) return -1;
        return current == 7 ? elemental : (byte) 7;
    }

    public static boolean canWear(Item3 item, byte clazz) {
        if (item == null || item.type != 16 || !isClassMedal(item.id)) return true;
        return item.id == medalIdForClass(clazz);
    }

    /**
     * Repairs an already-equipped class medal after a class/profile mismatch.
     * Tier, colour and option values are preserved; only the elemental family
     * and visual template are changed to the character's class.
     */
    public static boolean normalizeEquipped(Item3 item, byte clazz) {
        if (item == null || item.type != 16 || !validClass(clazz) || !isClassMedal(item.id)) return false;
        short expectedId = medalIdForClass(clazz);
        if (item.id == expectedId) return false;

        int sourceClass = classForMedal(item.id);
        byte sourceFlat = FLAT_DAMAGE_OPTIONS[sourceClass];
        byte sourcePercent = PERCENT_DAMAGE_OPTIONS[sourceClass];
        byte targetFlat = FLAT_DAMAGE_OPTIONS[clazz];
        byte targetPercent = PERCENT_DAMAGE_OPTIONS[clazz];
        if (item.op != null) {
            for (Option option : item.op) {
                if (option == null) continue;
                if (option.id == sourceFlat) option.id = targetFlat;
                else if (option.id == sourcePercent) option.id = targetPercent;
            }
        }
        item.id = expectedId;
        item.icon = MEDAL_ICONS[clazz];
        item.clazz = 4;
        item.part = 0;
        return true;
    }

    private static boolean validClass(byte clazz) {
        return clazz >= 0 && clazz < MEDAL_IDS.length;
    }

    public static boolean isClassMedal(short id) {
        return classForMedal(id) >= 0;
    }

    private static int classForMedal(short id) {
        for (int clazz = 0; clazz < MEDAL_IDS.length; clazz++) {
            if (MEDAL_IDS[clazz] == id) return clazz;
        }
        return -1;
    }
}
