package core;

import client.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import template.Item3;
import template.Item47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Option;

/** One-time test profile used to expose the bundled server features consistently. */
public final class VietHoangProfile {
    public static final String PENDING = "viet-hoang-profile-pending";
    public static final String APPLIED_V1 = "viet-hoang-profile-v1";
    public static final String APPLIED_V2 = "viet-hoang-profile-v2";
    public static final String APPLIED_V3 = "viet-hoang-profile-v3";
    public static final String APPLIED = "viet-hoang-profile-v4";
    private VietHoangProfile() {
    }

    public static boolean applyPending(Player player) {
        return player != null && player.giftcode != null && player.giftcode.contains(PENDING) && apply(player);
    }

    public static boolean apply(Player player) {
        if (player == null || player.item == null || player.giftcode == null || player.giftcode.contains(APPLIED)) return false;
        player.giftcode.remove(PENDING);
        player.giftcode.remove(APPLIED_V1);
        player.giftcode.remove(APPLIED_V2);
        player.giftcode.remove(APPLIED_V3);
        player.giftcode.add(APPLIED);
        short testLevel = (short) Math.min(Manager.gI().vietHoangLevel, Manager.gI().lvmax);
        if (player.level < testLevel) {
            player.level = testLevel;
            player.exp = 0;
        }
        if (player.get_vang() < 1_000_000_000L) player.update_vang(1_000_000_000L - player.get_vang());
        player.tiemnang = Math.max(player.tiemnang, 10_000);
        player.kynang = (short) Math.max(player.kynang, 500);
        ensureBagCapacity(player, 126);
        player.skill_point = SkillRules.normalizePoints(player.skill_point);
        // Skill metadata is loaded later by Player.set_in4(). That method unlocks
        // and clamps the profile after the real per-skill level counts are known.
        if (player.skills != null) SkillRules.unlockAll(player.skill_point, player.skills);
        grantEquipmentKit(player);
        grantStackableKit(player);
        return true;
    }

    private static void ensureBagCapacity(Player player, int capacity) {
        if (player.item.bag3.length < capacity) player.item.bag3 = Arrays.copyOf(player.item.bag3, capacity);
        player.maxbag = (byte) Math.max(Byte.toUnsignedInt(player.maxbag), capacity);
    }

    private static void grantEquipmentKit(Player player) {
        Set<Byte> wantedTypes = new HashSet<>(Arrays.asList(
                (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7,
                (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 14, (byte) 15, (byte) 16,
                (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) 26, (byte) 27));
        for (byte type : wantedTypes) {
            ItemTemplate3 best = null;
            for (ItemTemplate3 template : ItemTemplate3.item) {
                if (template == null || template.getType() != type || template.getLevel() > player.level) continue;
                if (template.getClazz() != 4 && template.getClazz() != player.clazz) continue;
                if (best == null || template.getLevel() > best.getLevel()
                        || (template.getLevel() == best.getLevel() && template.getColor() > best.getColor())) best = template;
            }
            if (best != null) {
                Item3 item = findTemplate(player, best.getId());
                if (item == null) item = addTemplateIfMissing(player, best.getId());
                equipIfEmpty(player, item);
                if (type == 4 && player.item.wear[9] == null) {
                    Item3 secondRing = createTemplate(best.getId());
                    if (player.item.add_item_bag3(secondRing)) equipIfEmpty(player, secondRing, (byte) 9);
                }
            }
        }
        int[] featureItems = new int[]{3593, 3596, 3599, 4577 + player.clazz * 2, 4578 + player.clazz * 2};
        for (int id : featureItems) if (id >= 0 && id < ItemTemplate3.item.size()) addTemplateIfMissing(player, (short) id);
    }

    private static Item3 findTemplate(Player player, short id) {
        for (Item3 item : player.item.wear) if (item != null && item.id == id) return item;
        for (Item3 item : player.item.bag3) if (item != null && item.id == id) return item;
        return null;
    }

    private static Item3 addTemplateIfMissing(Player player, short id) {
        Item3 existing = findTemplate(player, id);
        if (existing != null || id < 0 || id >= ItemTemplate3.item.size()) return existing;
        Item3 item = createTemplate(id);
        return player.item.add_item_bag3(item) ? item : null;
    }

    private static Item3 createTemplate(short id) {
        ItemTemplate3 template = ItemTemplate3.item.get(id);
        Item3 item = new Item3();
        item.id = id;
        item.name = template.getName();
        item.clazz = template.getClazz();
        item.type = template.getType();
        item.level = template.getLevel();
        item.icon = template.getIcon();
        item.color = template.getColor();
        item.part = template.getPart();
        item.tier = 0;
        item.islock = false;
        item.time_use = 0;
        item.op = new ArrayList<>();
        if (template.getOp() != null) {
            for (Option option : template.getOp()) item.op.add(new Option(option.id, option.getParam(0)));
        }
        return item;
    }

    /**
     * Populate empty release-client equipment positions. Slots 12..19 are the
     * native "Trang bị 2" page; keeping this server-side preserves the original
     * MIDlet while making the complete feature set immediately visible.
     */
    private static void equipIfEmpty(Player player, Item3 item) {
        equipIfEmpty(player, item, (byte) -1);
    }

    private static void equipIfEmpty(Player player, Item3 item, byte requestedSlot) {
        if (item == null || item.type == 14) return;
        int slot = EquipmentRules.wearSlot(item.type, requestedSlot);
        if (slot < 0 || slot >= player.item.wear.length || player.item.wear[slot] != null) return;
        for (int i = 0; i < player.item.bag3.length; i++) {
            if (player.item.bag3[i] != item) continue;
            player.item.bag3[i] = null;
            item.islock = true;
            item.name = ItemTemplate3.item.get(item.id).getName() + " [Khóa]";
            player.item.wear[slot] = item;
            return;
        }
    }

    private static void grantStackableKit(Player player) {
        for (short id = 0; id <= 3; id++) addStack(player, 7, id, 999);
        if (player.medal_create_material != null) {
            for (short id : player.medal_create_material) addStack(player, 7, id, 999);
        }
        for (short id = 136; id <= 145; id++) addStack(player, 7, id, 999);
        for (short id = 62; id <= 66; id++) addStack(player, 4, id, 99);
    }

    private static void addStack(Player player, int category, short id, int quantity) {
        boolean valid = category == 4 ? id >= 0 && id < ItemTemplate4.item.size()
                : id >= 0 && id < ItemTemplate7.item.size();
        if (!valid || player.item.total_item_by_id(category, id) > 0) return;
        Item47 item = new Item47();
        item.id = id;
        item.quantity = (short) Math.min(32_000, quantity);
        player.item.add_item_bag47(category, item);
    }
}
