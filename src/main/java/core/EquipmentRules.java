package core;

import template.Item3;

/** Authoritative server-side rules for wearable item slots. */
public final class EquipmentRules {
    private EquipmentRules() {
    }

    public static int wearSlot(byte type, byte requestedSlot) {
        switch (type) {
            case 0: return 1;
            case 1: return 7;
            case 2: return 6;
            case 3: return 2;
            case 4: return requestedSlot == 9 ? 9 : 3;
            case 5: return 4;
            case 6: return 8;
            case 7: return 10;
            case 8:
            case 9:
            case 10:
            case 11: return 0;
            case 15: return 11;
            case 16: return 12; // medal
            case 21: return 13;
            case 22: return 14; // fashion wing
            case 23: return 15;
            case 24: return 17;
            case 25: return 16;
            case 26: return 18;
            case 27: return 19;
            default: return -1;
        }
    }

    public static boolean canWear(Item3 item, byte playerClazz, short playerLevel) {
        return item != null
                && wearSlot(item.type, (byte) -1) >= 0
                && (item.clazz == 4 || item.clazz == playerClazz)
                && (item.type == 27 || item.level <= playerLevel);
    }
}
