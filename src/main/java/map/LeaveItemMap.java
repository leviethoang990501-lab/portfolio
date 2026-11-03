package map;

import event.EventManager;
import event.Event_1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import core.BossRules;
import client.Player;
import core.LanternRules;
import core.Manager;
import core.Util;
import io.Message;
import template.EffTemplate;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Medal_Material;
import template.Option;

public class LeaveItemMap {
	// Item IDs for Light & Dark Gems (Category 7)
	public static final short ITEM_LIGHT_GEM_LV1 = 23;
	public static final short ITEM_LIGHT_GEM_LV2 = 24;
	public static final short ITEM_DARK_GEM_LV1 = 28;
	public static final short ITEM_DARK_GEM_LV2 = 29;

	// Drop rate configurations (scale: 10000 = 100%)
	// Normal zones: Lv1 ~ 5% (500 / 10000), Lv2 = 0%
	public static final int NORMAL_LIGHT_DARK_GEM_LV1_RATE = 500; // 5.0%
	public static final int NORMAL_LIGHT_DARK_GEM_LV2_RATE = 0;   // 0.0%

	// Zone 2: Lv1 higher than normal (e.g., 1000 / 10000 = 10%), Lv2 ~ 1-2% (150 / 10000 = 1.5%)
	public static final int ZONE2_LIGHT_DARK_GEM_LV1_RATE = 1000; // 10.0%
	public static final int ZONE2_LIGHT_DARK_GEM_LV2_RATE = 150;  // 1.5%

	public static List<Short> item0x = new ArrayList<>();
	public static List<Short> item1x = new ArrayList<>();
	public static List<Short> item2x = new ArrayList<>();
	public static List<Short> item3x = new ArrayList<>();
	public static List<Short> item4x = new ArrayList<>();
	public static List<Short> item5x = new ArrayList<>();
	public static List<Short> item6x = new ArrayList<>();
	public static List<Short> item7x = new ArrayList<>();
	public static List<Short> item8x = new ArrayList<>();
	public static List<Short> item9x = new ArrayList<>();
	public static List<Short> item10x = new ArrayList<>();
	public static List<Short> item11x = new ArrayList<>();
	public static List<Short> item12x = new ArrayList<>();
	public static List<Short> item13x = new ArrayList<>();
	public static List<Short> item14x = new ArrayList<>();
	public static List<Short> item15x = new ArrayList<>();

	public static void leave_vang(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			int index_item_map = map.get_item_map_index_able();
			if (index_item_map > -1) {
				//
				map.item_map[index_item_map] = new ItemMap();
				map.item_map[index_item_map].id_item = -1;
				map.item_map[index_item_map].color = 0;
				int vang_drop = Util.random(mob.level * 15, mob.level * 45);
				if (Map.is_khu_2(map)) {
					vang_drop *= 2;
				}
				EffTemplate ef = p.get_eff(52);
				if (ef != null) {
					vang_drop += (vang_drop * (ef.param / 100)) / 100;
				}
				vang_drop += (vang_drop * (p.lantern_param(LanternRules.EFF_GOLD) / 100)) / 100;
				map.item_map[index_item_map].quantity = (short) Math.min(Short.MAX_VALUE, Math.max(1, vang_drop));
				map.item_map[index_item_map].category = 4;
				map.item_map[index_item_map].idmaster = (short) p.id;
				map.item_map[index_item_map].time_exist = System.currentTimeMillis() + 60_000L;
				map.item_map[index_item_map].time_pick = System.currentTimeMillis() + 1_500L;
				String name = "vàng *" + map.item_map[index_item_map].quantity;
				// add in4 game scr
				Message mi = new Message(19);
				mi.writer().writeByte(4);
				mi.writer().writeShort(mob.index); // index mob die
				mi.writer().writeShort(0); // id icon (0 : vang)
				mi.writer().writeShort(index_item_map); //
				mi.writer().writeUTF(name);
				mi.writer().writeByte(0); // color
				mi.writer().writeShort(-1); // id player
				MapService.send_msg_player_inside(map, p, mi, true);
				mi.cleanup();
			}
		}
	}

	public static void leave_vang_eagle(Map map, int mob_index, Player p) throws IOException {
		int remainingGold = Util.random(client.Pet.EAGLE_GOLD_MIN, client.Pet.EAGLE_GOLD_MAX + 1);
		while (remainingGold > 0) {
			int index_item_map = map.get_item_map_index_able();
			if (index_item_map < 0) {
				break;
			}
			int pileGold = Math.min(Short.MAX_VALUE, remainingGold);
			remainingGold -= pileGold;
			map.item_map[index_item_map] = new ItemMap();
			map.item_map[index_item_map].id_item = -1;
			map.item_map[index_item_map].color = 0;
			map.item_map[index_item_map].quantity = (short) pileGold;
			map.item_map[index_item_map].category = 4;
			map.item_map[index_item_map].idmaster = (short) p.id;
			map.item_map[index_item_map].time_exist = System.currentTimeMillis() + 60_000L;
			map.item_map[index_item_map].time_pick = System.currentTimeMillis() + 1_500L;
			String name = "vàng *" + map.item_map[index_item_map].quantity;
			Message mi = new Message(19);
			mi.writer().writeByte(4);
			mi.writer().writeShort(mob_index); // target mob index
			mi.writer().writeShort(0); // id icon (0 : vang)
			mi.writer().writeShort(index_item_map);
			mi.writer().writeUTF(name);
			mi.writer().writeByte(0); // color
			mi.writer().writeShort(-1); // id player
			MapService.send_msg_player_inside(map, p, mi, true);
			mi.cleanup();
		}
	}

	public static void leave_item_3(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			short id_item_can_drop;
			byte color_ = 0;
			if (Map.is_khu_2(map)) {
				// Khu 2: Chỉ rơi trang bị Color 3 và Color 4, tăng tỷ lệ so với map thường
				if (35 > Util.random(0, 100)) {
					color_ = 4;
				} else {
					color_ = 3;
				}
			} else {
				if (60 > Util.random(0, 200)) {
					color_ = 1;
				} else if (25 > Util.random(0, 275)) {
					color_ = 2;
				} else if (15 > Util.random(0, 350)) {
					color_ = 3;
				} else if (5 > Util.random(0, 500)) {
					color_ = 4;
				}
			}
			if (mob.color_name != 0) {
				color_ = 3;
			}
			List<Short> pool = poolForLevel(mob.level);
			if (pool.isEmpty()) return;
			id_item_can_drop = pool.get(Util.random(0, pool.size()));
			if (id_item_can_drop < 0 || id_item_can_drop >= ItemTemplate3.item.size()) return;
			String name = ItemTemplate3.item.get(id_item_can_drop).getName();
			short index_real = id_item_can_drop;
			if (id_item_can_drop < 20) {
				for (int i = 0; i < 20; i++) {
					if (ItemTemplate3.item.get(i).getName().equals(name) && ItemTemplate3.item.get(i).getColor() == color_) {
						index_real = (short) i;
						break;
					}
				}
			} else {
				int from = Math.max(0, id_item_can_drop - 5);
				int to = Math.min(ItemTemplate3.item.size(), id_item_can_drop + 6);
				for (int i = from; i < to; i++) {
					if (ItemTemplate3.item.get(i).getName().equals(name) && ItemTemplate3.item.get(i).getColor() == color_) {
						index_real = (short) i;
						break;
					}
				}
			}
			//
			leave_item_by_type3(map, index_real, color_, p, name, mob.index);
		}
	}

	public static List<Short> poolForLevel(int level) {
		if (level < 1) return java.util.Collections.emptyList();
		List<Short>[] pools = new List[]{item0x, item1x, item2x, item3x, item4x, item5x, item6x,
			item7x, item8x, item9x, item10x, item11x, item12x, item13x};
		int index = Math.min(13, level / 10);
		for (int distance = 0; distance < pools.length; distance++) {
			int down = index - distance;
			if (down >= 0 && !pools[down].isEmpty()) return pools[down];
			int up = index + distance;
			if (distance > 0 && up < pools.length && !pools[up].isEmpty()) return pools[up];
		}
		return java.util.Collections.emptyList();
	}

	private static void leave_item_by_type3(Map map, int index_real, int color_, Player p_master, String name, int index)
	      throws IOException {
		int index_item_map = map.get_item_map_index_able();
		if (index_item_map > -1) {
			//
			map.item_map[index_item_map] = new ItemMap();
			map.item_map[index_item_map].id_item = (short) index_real;
			map.item_map[index_item_map].color = (byte) color_;
			map.item_map[index_item_map].quantity = 1;
			map.item_map[index_item_map].category = 3;
			map.item_map[index_item_map].idmaster = (short) p_master.id;
			List<Option> opnew = new ArrayList<Option>();
			for (Option op_old : ItemTemplate3.item.get(index_real).getOp()) {
				Option temp = new Option(1, 1);
				temp.id = op_old.id;
				if (temp.id != 37 && temp.id != 38) {
					if (op_old.getParam(0) < 10) {
						temp.setParam(Util.random(0, 10));
					} else {
						temp.setParam(Util.random((9 * op_old.getParam(0)) / 10, op_old.getParam(0)));
					}
				} else {
					temp.setParam(1);
				}
				opnew.add(temp);
			}
			map.item_map[index_item_map].op = new ArrayList<>();
			map.item_map[index_item_map].op.addAll(opnew);
			map.item_map[index_item_map].time_exist = System.currentTimeMillis() + 60_000L;
			map.item_map[index_item_map].time_pick = System.currentTimeMillis() + 1_500L;
			// add in4 game scr
			Message mi = new Message(19);
			mi.writer().writeByte(3);
			mi.writer().writeShort(index); // index mob die
			mi.writer().writeShort(ItemTemplate3.item.get(map.item_map[index_item_map].id_item).getIcon());
			mi.writer().writeShort(index_item_map); //
			mi.writer().writeUTF(name);
			mi.writer().writeByte(color_); // color
			mi.writer().writeShort(-1); // id player
			MapService.send_msg_player_inside(map, p_master, mi, true);
			mi.cleanup();
		}
	}

	public static void leave_item_4(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			short index_real = (short) Util.random(0, 6);
                        if (mob.level > 100 && 25 > Util.random(250)) {
                            index_real = 320;
                        }
                        if (mob.level > 100 && 25 > Util.random(250)) {
                            index_real = 318;
                        }
                        if (mob.level > 100 && 25 > Util.random(250)) {
                            index_real = 319;
                        }
			//
			leave_item_by_type4(map, index_real, p, mob.index);
		}
	}

	private static void leave_item_by_type4(Map map, short index_real, Player p_master, int index) throws IOException {
		int index_item_map = map.get_item_map_index_able();
		if (index_item_map > -1) {
			//
			map.item_map[index_item_map] = new ItemMap();
			map.item_map[index_item_map].id_item = index_real;
			map.item_map[index_item_map].color = 0;
			map.item_map[index_item_map].quantity = 1;
			map.item_map[index_item_map].category = 4;
			map.item_map[index_item_map].idmaster = (short) p_master.id;
			map.item_map[index_item_map].time_exist = System.currentTimeMillis() + 60_000L;
			map.item_map[index_item_map].time_pick = System.currentTimeMillis() + 1_500L;
			// add in4 game scr
			Message mi = new Message(19);
			mi.writer().writeByte(4);
			mi.writer().writeShort(index); // id mob die
			mi.writer().writeShort(ItemTemplate4.item.get(map.item_map[index_item_map].id_item).getIcon());
			mi.writer().writeShort(index_item_map); //
			mi.writer().writeUTF(ItemTemplate4.item.get(map.item_map[index_item_map].id_item).getName());
			mi.writer().writeByte(0); // color
			mi.writer().writeShort(-1); // id player
			MapService.send_msg_player_inside(map, p_master, mi, true);
			mi.cleanup();
		}
	}

	public static void leave_item_7(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			short index_real = (short) Util.random(0, 4);
			if (Map.is_khu_2(map)) {
				// Khu 2: drop rate dac biet:
				// Cỏ 3 lá (12), Cỏ 4 lá (13) tỷ lệ trung bình
				// Các item đặc biệt: Đá cường hóa (0), Sắt vụn (1), Thỏi titan (2), Kim loại vũ trụ (3), Lông đỏ (9), Lông vàng (10), Khúc xương (11)
				if (25 > Util.random(0, 100)) {
					// 25% roi co 3 la hoac co 4 la
					index_real = (short) (Util.random(0, 2) == 0 ? 12 : 13);
				} else {
					short[] special_items = new short[]{0, 1, 2, 3, 9, 10, 11};
					index_real = special_items[Util.random(special_items.length)];
				}
			}
			//
			leave_item_by_type7(map, index_real, p, mob.index);
		}
	}

	private static void leave_item_by_type7(Map map, short index_real, Player p_master, int index) throws IOException {
		int index_item_map = map.get_item_map_index_able();
		if (index_item_map > -1) {
			//
			map.item_map[index_item_map] = new ItemMap();
			map.item_map[index_item_map].id_item = index_real;
			if (ItemTemplate7.item.get(map.item_map[index_item_map].id_item).getColor() == 21) {
				map.item_map[index_item_map].color = 1;
			} else {
				map.item_map[index_item_map].color = 0;
			}
			map.item_map[index_item_map].quantity = 1;
			map.item_map[index_item_map].category = 7;
			map.item_map[index_item_map].idmaster = (short) p_master.id;
			map.item_map[index_item_map].time_exist = System.currentTimeMillis() + 60_000L;
			map.item_map[index_item_map].time_pick = System.currentTimeMillis() + 1_500L;
			// add in4 game scr
			Message mi = new Message(19);
			mi.writer().writeByte(7);
			mi.writer().writeShort(index); // id mob die
			mi.writer().writeShort(ItemTemplate7.item.get(map.item_map[index_item_map].id_item).getIcon());
			mi.writer().writeShort(index_item_map); //
			mi.writer().writeUTF(ItemTemplate7.item.get(map.item_map[index_item_map].id_item).getName());
			mi.writer().writeByte(map.item_map[index_item_map].color); // color
			mi.writer().writeShort(-1); // id player
			MapService.send_msg_player_inside(map, p_master, mi, true);
			mi.cleanup();
		}
	}

	public static void leave_yellow_medal_material(Map map, int targetIndex, Player p) throws IOException {
		short id = Medal_Material.m_yellow[Util.random(Medal_Material.m_yellow.length)];
		leave_item_by_type7(map, id, p, targetIndex);
	}

	// Danh dau cac vat pham moi rot de nhung nguoi tham gia cung nhat duoc.
	public static void share_new_drops(Map map, ItemMap[] before, Set<String> names) {
		Set<String> sharedWith = new HashSet<>(names);
		for (int i = 0; i < map.item_map.length; i++) {
			if (map.item_map[i] != null && map.item_map[i] != before[i]) {
				map.item_map[i].sharedWith = sharedWith;
			}
		}
	}

	// Moi nhat danh tu duoi 50% hp rot 1 nguyen lieu tim cho den khi het phan boss mang theo.
	public static void leave_boss_violet_material(Map map, Mob_in_map mob, Player p) throws IOException {
		short[] types = BossRules.violetTypes(mob.template.mob_id);
		if (types.length == 0 || mob.violet_dropped >= BossRules.VIOLET_STOCK) {
			return;
		}
		mob.violet_dropped++;
		ItemMap[] before = map.item_map.clone();
		leave_item_by_type7(map, types[Util.random(types.length)], p, mob.index);
		share_new_drops(map, before, mob.top_dame.keySet());
	}

	public static void leave_house_yellow_material(Map map, Mob_in_map mob, Player p) throws IOException {
		ItemMap[] before = map.item_map.clone();
		leave_yellow_medal_material(map, mob.index, p);
		share_new_drops(map, before, mob.top_dame.keySet());
	}

	public static void leave_dungeon_boss_material(Map map, int index, Player p, Set<String> names) throws IOException {
		ItemMap[] before = map.item_map.clone();
		for (int i = 0; i < BossRules.DUNGEON_BLUE_DROP; i++) {
			leave_item_by_type7(map, Medal_Material.m_blue[Util.random(Medal_Material.m_blue.length)], p, index);
		}
		share_new_drops(map, before, names);
	}

	public static void leave_random_skill_book(Map map, Mob_in_map mob, Player p) throws IOException {
		int id = Util.random(4577, 4585);
		if (id < 0 || id >= ItemTemplate3.item.size()) return;
		ItemTemplate3 book = ItemTemplate3.item.get(id);
		leave_item_by_type3(map, id, book.getColor(), p, book.getName(), mob.index);
	}

	public static void leave_item_boss(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null && mob.is_boss) {
			// roi do boss co dinh
			short[] id_item_leave4 = new short[] {};
			short[] id_item_leave7 = new short[] {};
			boolean leave_orange_equipment = false;
			switch (mob.template.mob_id) {
				case 101: { // xa nu
					id_item_leave4 = new short[] {-1, 53};
					id_item_leave7 = new short[] {14, 0, 4, 12, 2, 1, 10};
					leave_orange_equipment = true;
					break;
				}
				case 84: { // de vang
					id_item_leave4 = new short[] {-1, 27, 54};
					id_item_leave7 = new short[] {0, 4, 8, 9, 10, 13, 14};
					leave_orange_equipment = true;
					break;
				}
				case 83: { // de bac
					id_item_leave4 = new short[] {-1, 53};
					id_item_leave7 = new short[] {0, 4, 12, 11, 2, 3, 14};
					leave_orange_equipment = true;
					break;
				}
				case 103: { // bo cap chua
					id_item_leave4 = new short[] {-1, 48, 50, 9, 18, 2, 5};
					id_item_leave7 = new short[] {0};
					break;
				}
				case 104: { // quy 1 mat
					id_item_leave4 = new short[] {-1, 10};
					id_item_leave7 = new short[] {2, 3, 8, 9, 10, 12};
					break;
				}
				case 105: { // quy dau bo
					id_item_leave4 = new short[] {-1, 49, 54};
					id_item_leave7 = new short[] {0, 4, 13, 11, 2, 3};
					leave_orange_equipment = true;
					break;
				}
				case 106: { // ky sy dia nguc
					id_item_leave4 = new short[] {-1, 18, 54, 53};
					id_item_leave7 = new short[] {13, 11, 2, 3, 14};
					leave_orange_equipment = true;
					break;
				}
				case 149: { // nhen chua
					id_item_leave4 = new short[] {-1, 18, 54, 53};
					id_item_leave7 = new short[] {13, 11, 2, 3, 14};
					leave_orange_equipment = true;
					break;
				}
				case 155: { // giant skeleton
					id_item_leave4 = new short[] {-1, 18, 54, 53};
					id_item_leave7 = new short[] {13, 11, 2, 3, 14};
					leave_orange_equipment = true;
					break;
				}
                                case 174: { // cho 3 dau
					id_item_leave4 = new short[] {-1, 18, 54, 53};
					id_item_leave7 = new short[] {13, 11, 2, 3, 14};
					leave_orange_equipment = true;
					break;
				}
			}
			for (short id : id_item_leave4) {
				if (id == -1) {
					leave_vang(map, mob, p);
				} else {
					leave_item_by_type4(map, id, p, mob.index);
				}
			}
			for (short id : id_item_leave7) {
				leave_item_by_type7(map, id, p, mob.index);
			}
			if (leave_orange_equipment) {
				leave_orange_equipment(map, mob, p);
			}
                        if (50>Util.random(120)) {
                            leave_item_by_type4(map,(short) 319, p, mob.index);
                        }
			//
			// roi do boss ngau nhien
			// id_item_leave3 = new short[] {1};
			// ItemTemplate3 temp = ItemTemplate3.item.get(id_item_leave3[Util.random(0,
			// id_item_leave3.length)]);
			// leave_item_by_type3(map, temp.getId(), temp.getColor(), p, temp.getName(), index);
			// //
			// id_item_leave4 = new short[] {1};
			// leave_item_by_type4(map, id_item_leave4[Util.random(0, id_item_leave4.length)], p, index);
			// id_item_leave7 = new short[] {1};
			// leave_item_by_type7(map, id_item_leave7[Util.random(0, id_item_leave7.length)], p, index);
		}
	}

	private static void leave_orange_equipment(Map map, Mob_in_map mob, Player p) throws IOException {
		List<Short> pool = poolForLevel(mob.level);
		if (pool.isEmpty()) return;
		short baseId = pool.get(Util.random(pool.size()));
		if (baseId < 0 || baseId >= ItemTemplate3.item.size()) return;
		String name = ItemTemplate3.item.get(baseId).getName();
		for (int i = 0; i < ItemTemplate3.item.size(); i++) {
			ItemTemplate3 candidate = ItemTemplate3.item.get(i);
			if (candidate.getColor() == 4 && candidate.getName().equals(name)) {
				leave_item_by_type3(map, i, 4, p, candidate.getName(), mob.index);
				return;
			}
		}
	}

	public static void leave_material(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			short index_real = -1;
			switch (mob.level) {
				case 1:
				case 2:
				case 3:
				case 4: {
					index_real = Medal_Material.m_white[0][Util.random(0, 3)];
					break;
				}
				case 5: {
					index_real = Medal_Material.m_white[0][Util.random(3, 6)];
					break;
				}
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17: {
					index_real = Medal_Material.m_white[0][Util.random(6, 10)];
					break;
				}
				case 19: {
					index_real = Medal_Material.m_white[1][Util.random(0, 3)];
					break;
				}
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26: {
					index_real = Medal_Material.m_white[1][Util.random(3, 6)];
					break;
				}
				case 28:
				case 29: {
					index_real = Medal_Material.m_white[1][Util.random(6, 9)];
					break;
				}
				case 31:
				case 32:
				case 33:
				case 34:
				case 35: {
					index_real = Medal_Material.m_white[1][9];
					break;
				}
				case 37: {
					index_real = Medal_Material.m_white[2][Util.random(0, 3)];
					break;
				}
				case 39:
				case 40:
				case 41:
				case 42:
				case 43: {
					index_real = Medal_Material.m_white[2][Util.random(3, 6)];
					break;
				}
				case 44:
				case 45:
				case 46:
				case 47:
				case 48:
				case 49:
				case 50:
				case 51:
				case 52:
				case 53:
				case 54: {
					index_real = Medal_Material.m_white[2][Util.random(6, 10)];
					break;
				}
				case 55: {
					index_real = Medal_Material.m_white[3][Util.random(0, 3)];
					break;
				}
				case 57:
				case 58:
				case 59: {
					index_real = Medal_Material.m_white[3][Util.random(3, 6)];
					break;
				}
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
				case 69: {
					index_real = Medal_Material.m_white[3][Util.random(6, 10)];
					break;
				}
				case 71:
				case 72:
				case 73:
				case 74: {
					index_real = Medal_Material.m_white[4][Util.random(0, 3)];
					break;
				}
				case 76:
				case 77: {
					index_real = Medal_Material.m_white[4][Util.random(3, 6)];
					break;
				}
				case 78:
				case 79:
				case 80:
				case 81:
				case 82:
				case 83:
				case 84:
				case 85:
				case 86:
				case 87:
				case 88:
				case 89: {
					index_real = Medal_Material.m_white[4][Util.random(6, 10)];
					break;
				}
				case 90:
				case 91:
				case 92: {
					index_real = Medal_Material.m_white[5][Util.random(0, 3)];
					break;
				}
				case 93:
				case 94:
				case 95: {
					index_real = Medal_Material.m_white[5][Util.random(3, 6)];
					break;
				}
				case 96:
				case 97:
				case 98:
				case 99:
				case 100:
				case 101:
				case 102:
				case 103:
				case 104:
				case 105:
				case 106:
				case 107: {
					index_real = Medal_Material.m_white[5][Util.random(6, 10)];
					break;
				}
				case 109:
				case 110: {
					index_real = Medal_Material.m_white[6][Util.random(0, 3)];
					break;
				}
				case 112:
				case 113: {
					index_real = Medal_Material.m_white[6][Util.random(3, 6)];
					break;
				}
				case 114:
				case 115:
				case 116:
				case 117:
				case 118:
				case 119:
				case 120:
				case 121:
				case 122:
				case 123: {
					index_real = Medal_Material.m_white[6][Util.random(6, 10)];
					break;
				}
				case 125:
				case 126:
				case 127: {
					index_real = Medal_Material.m_white[0][Util.random(6, 10)];
					break;
				}
			}
			//
			if (index_real > -1) {
				//
				leave_item_by_type7(map, index_real, p, mob.index);
			}
			if (25 > Util.random(0, 100)) {
				index_real = Medal_Material.m_yellow[Util.random(0, 10)];
				//
				leave_item_by_type7(map, index_real, p, mob.index);
			}
			if (25 > Util.random(0, 100)) {
				index_real = (short) ((15 > Util.random(0, 120)) ? 11
				      : ((35 > Util.random(0, 120)) ? 10 : ((50 > Util.random(0, 120)) ? 9 : 8)));
				//
				leave_item_by_type7(map, index_real, p, mob.index);
			}
		}
	}

  public static void leave_item_event(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			switch (Manager.gI().event) {
				case 1: {
					short index_real = EventManager.item_leave[Manager.gI().event - 1][Util
					      .random(EventManager.item_leave[Manager.gI().event - 1].length)];
					if (!Event_1.check(p.name) && index_real >= 153 && index_real <= 156) {
						return;
					}
					//
					leave_item_by_type4(map, index_real, p, mob.index);
					break;
				}
				case 2: {
					if (10 > Util.random(120)) {
						short index_real = (short) Util.random(137, 140);
						leave_item_by_type4(map, index_real, p, mob.index);
					}
					break;
				}
			}
		}
	}
    public static void leave_material_ngockham(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob != null) {
			short index_real = -1;
			//
			if (25 > Util.random(120)) {
				switch (mob.template.mob_id) {
					case 167: {
						index_real = 362;
						break;
					}
					case 168: {
						index_real = 372;
						break;
					}
					case 169: {
						index_real = 367;
						break;
					}
					case 170: {
						index_real = 357;
						break;
					}
					case 171: {
						index_real = 377;
						break;
					}
					case 172: {
						index_real = 352;
						break;
					}
				}
			}
			if (index_real != -1) {
				leave_item_by_type7(map, index_real, p, mob.index);
			}
		}
	}

	public static void leave_light_dark_gem(Map map, Mob_in_map mob, Player p) throws IOException {
		if (mob == null || p == null || map == null) {
			return;
		}
		boolean isKhu2 = Map.is_khu_2(map);
		int rateLv1 = isKhu2 ? ZONE2_LIGHT_DARK_GEM_LV1_RATE : NORMAL_LIGHT_DARK_GEM_LV1_RATE;
		int rateLv2 = isKhu2 ? ZONE2_LIGHT_DARK_GEM_LV2_RATE : NORMAL_LIGHT_DARK_GEM_LV2_RATE;

		short gemToDrop = -1;
		int roll = Util.random(0, 10000);

		if (rateLv2 > 0 && roll < rateLv2) {
			// Roll thành công Lv2 (chỉ ở Khu 2)
			gemToDrop = (Util.random(0, 2) == 0) ? ITEM_LIGHT_GEM_LV2 : ITEM_DARK_GEM_LV2;
		} else if (roll < rateLv2 + rateLv1) {
			// Roll thành công Lv1
			gemToDrop = (Util.random(0, 2) == 0) ? ITEM_LIGHT_GEM_LV1 : ITEM_DARK_GEM_LV1;
		}

		if (gemToDrop != -1) {
			leave_item_by_type7(map, gemToDrop, p, mob.index);
		}
	}
}
