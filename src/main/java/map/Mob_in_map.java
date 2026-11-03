package map;

import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.BossRules;
import core.Util;
import java.util.HashMap;
import template.Mob;

public class Mob_in_map {
	public static final List<Mob_in_map> list_boss = new ArrayList<>();
	public static final int time_refresh = 3;
	public static int num_boss = 0;
	public Mob template;
	public int index;
	public short x;
	public short y;
	public short level;
	public byte map_id;
	public boolean isdie;
	public byte color_name;
	public boolean is_boss;
	public int hpmax;
	public int hp;
	public long time_back;
	public byte zone_id;
	public List<Player> list_fight;
	public long time_fight;
	public boolean is_boss_active;
        public HashMap<String, Long> top_dame = new HashMap<>();
        public int violet_dropped;
        public long time_despawn;

	public synchronized static void create_boss() {
		if (Mob_in_map.list_boss.size() > 10) {
			List<Mob_in_map> list = new ArrayList<>();
			for (Mob_in_map mob : Mob_in_map.list_boss) {
				if (!mob.is_boss_active) {
					list.add(mob);
				}
			}
			if (list.size() > 0) {
				activate_boss(list.get(Util.random(list.size())));
			}
		}
	}

	private static void activate_boss(Mob_in_map boss) {
		boss.is_boss_active = true;
		boss.time_despawn = System.currentTimeMillis() + BossRules.LIFETIME_MILLIS;
		num_boss++;
	}

	/** Boss cung loai, cung map o khu gan nhat phia sau con vua chet; null neu het khu. */
	public static Mob_in_map find_next_zone_boss(List<Mob_in_map> bosses, Mob_in_map dead) {
		Mob_in_map next = null;
		for (Mob_in_map mob : bosses) {
			if (!mob.is_boss_active && mob.map_id == dead.map_id && mob.template == dead.template
					&& mob.zone_id > dead.zone_id && (next == null || mob.zone_id < next.zone_id)) {
				next = mob;
			}
		}
		return next;
	}

	public static synchronized void spawn_next_zone_boss(Mob_in_map dead) {
		Mob_in_map next = find_next_zone_boss(list_boss, dead);
		if (next != null) {
			activate_boss(next);
		}
	}

	/** Het 3 tieng ma chua bi tieu diet thi boss bien mat. */
	public static synchronized boolean despawn_if_expired(Mob_in_map boss, long now) {
		if (!boss.is_boss || !boss.is_boss_active || boss.time_despawn <= 0 || now < boss.time_despawn) {
			return false;
		}
		boss.is_boss_active = false;
		boss.isdie = true;
		boss.time_despawn = 0;
		boss.top_dame.clear();
		boss.violet_dropped = 0;
		num_boss--;
		return true;
	}
	
	public static synchronized void mo_boss_now() {
    for (int i = 0; i < 5; i++) {
        create_boss();
        }
    }
    
    
}
