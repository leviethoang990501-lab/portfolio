package regression;

import client.Pet;
import core.CharacterDefaults;
import core.EquipmentRules;
import core.SkillRules;
import core.UpgradeRules;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import map.LeaveItemMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import template.Item3;
import template.LvSkill;
import template.Skill;

public final class CoreRegressionTest {
    private static int checks;

    public static void main(String[] args) {
        testNewCharacterDefaults();
        testEquipmentSlots();
        testEquipmentEligibility();
        testUpgradeRequirements();
        testDropPoolFallback();
        testSkillDefaults();
        testPetCatalog();
        System.out.println("PASS - " + checks + " regression checks");
    }

    private static void testNewCharacterDefaults() {
        for (byte clazz = 0; clazz < 4; clazz++) {
            check(CharacterDefaults.isValidClazz(clazz), "valid class " + clazz);
            JSONArray wear = (JSONArray) JSONValue.parse(CharacterDefaults.starterWear(clazz));
            check(wear != null && wear.size() >= 3, "starter equipment class " + clazz);
            for (Object rowObject : wear) {
                JSONArray row = (JSONArray) rowObject;
                check(((Number) row.get(1)).byteValue() == clazz, "starter class ownership " + clazz);
                int slot = ((Number) row.get(9)).intValue();
                check(slot >= 0 && slot < 24, "starter slot range");
            }
        }
        check(!CharacterDefaults.isValidClazz((byte) 4), "reject invalid class");
    }

    private static void testEquipmentSlots() {
        check(EquipmentRules.wearSlot((byte) 16, (byte) 0) == 12, "medal slot");
        check(EquipmentRules.wearSlot((byte) 15, (byte) 0) == 11, "fashion/mount slot");
        check(EquipmentRules.wearSlot((byte) 22, (byte) 0) == 14, "fashion wing slot");
        check(EquipmentRules.wearSlot((byte) 21, (byte) 0) == 13, "equipment page 2 slot 13");
        check(EquipmentRules.wearSlot((byte) 23, (byte) 0) == 15, "equipment page 2 slot 15");
        check(EquipmentRules.wearSlot((byte) 25, (byte) 0) == 16, "equipment page 2 slot 16");
        check(EquipmentRules.wearSlot((byte) 24, (byte) 0) == 17, "equipment page 2 slot 17");
        check(EquipmentRules.wearSlot((byte) 26, (byte) 0) == 18, "equipment page 2 slot 18");
        check(EquipmentRules.wearSlot((byte) 27, (byte) 0) == 19, "equipment page 2 slot 19");
        check(EquipmentRules.wearSlot((byte) 4, (byte) 9) == 9, "second ring slot");
        check(EquipmentRules.wearSlot((byte) 99, (byte) 0) == -1, "unknown equipment rejected");
    }

    private static void testEquipmentEligibility() {
        Item3 item = new Item3();
        item.type = 16;
        item.clazz = 4;
        item.level = 10;
        check(EquipmentRules.canWear(item, (byte) 0, (short) 10), "universal medal wearable");
        item.clazz = 2;
        check(!EquipmentRules.canWear(item, (byte) 1, (short) 10), "wrong class rejected");
        item.clazz = 1;
        check(!EquipmentRules.canWear(item, (byte) 1, (short) 9), "low level rejected");
    }

    private static void testUpgradeRequirements() {
        check(UpgradeRules.materialsForTier(0).length == 2, "tier 0 materials");
        check(UpgradeRules.materialsForTier(6).length == 3, "tier 6 materials");
        check(UpgradeRules.materialsForTier(11).length == 4, "tier 11 materials");
        check(UpgradeRules.gemCost(0) == 4, "tier 0 gem cost");
        check(UpgradeRules.gemCost(14) == 10, "tier 14 gem cost");
        check(UpgradeRules.goldCost(5) == 10_000L, "gold cost");
    }

    private static void testDropPoolFallback() {
        LeaveItemMap.item0x.clear();
        LeaveItemMap.item1x.clear();
        LeaveItemMap.item2x.clear();
        LeaveItemMap.item1x.add((short) 123);
        List<Short> pool = LeaveItemMap.poolForLevel(1);
        check(pool.size() == 1 && pool.get(0) == 123, "nearest non-empty drop pool");
        LeaveItemMap.item1x.clear();
    }

    private static void testSkillDefaults() {
        JSONArray normal = (JSONArray) JSONValue.parse(CharacterDefaults.starterSkills(false));
        check(normal.size() == SkillRules.SKILL_COUNT, "normal skill count");
        check(((Number) normal.get(0)).intValue() == 1 && ((Number) normal.get(1)).intValue() == 0
                && ((Number) normal.get(20)).intValue() == 0, "normal starter skills");

        JSONArray full = (JSONArray) JSONValue.parse(CharacterDefaults.starterSkills(true));
        check(full.size() == SkillRules.SKILL_COUNT, "test profile skill count");
        for (Object level : full) check(((Number) level).intValue() == 10, "test profile full skill");

        byte[] normalized = SkillRules.normalizePoints(new byte[]{0, 4, -1});
        check(normalized.length == SkillRules.SKILL_COUNT, "skill normalization size");
        check(normalized[0] == 1 && normalized[1] == 4 && normalized[2] == 0,
                "skill normalization values");

        Skill[] skills = new Skill[SkillRules.SKILL_COUNT];
        skills[0] = skillWithLevels(1);
        skills[1] = skillWithLevels(5);
        byte[] clamped = SkillRules.normalizePoints(new byte[]{10, 10, 10}, skills);
        check(clamped[0] == 1 && clamped[1] == 5 && clamped[2] == 0,
                "persisted skill levels clamped to loaded metadata");
    }

    private static void testPetCatalog() {
        short[] eggs = new short[]{2943, 2944, 2939, 3269, 3616, 4617, 4626, 4631, 4699, 4761, 4762, 4768};
        Set<Byte> types = new HashSet<>();
        for (short egg : eggs) {
            Pet pet = Pet.get_pet(egg);
            check(pet != null, "pet egg " + egg);
            check(types.add(pet.type), "unique pet type " + pet.type);
            check(pet.op != null && !pet.op.isEmpty(), "pet options " + egg);
            check(pet.getAttackOption() != null, "pet attack option " + egg);
            check(Pet.get_id(pet.type) == egg, "pet type maps back to egg " + egg);
        }
        check(types.size() == 12, "full test pet catalog");
        check(Pet.get_pet((short) 2943).isOwl(), "owl feature identity");
        check(Pet.get_pet(Pet.ID_DAI_BANG).isEagle(), "eagle feature identity");
    }

    private static Skill skillWithLevels(int count) {
        Skill skill = new Skill();
        skill.mLvSkill = new LvSkill[count];
        for (int i = 0; i < count; i++) skill.mLvSkill[i] = new LvSkill();
        return skill;
    }

    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) throw new AssertionError(label);
    }
}
