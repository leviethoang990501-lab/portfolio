package regression;

import client.Pet;
import core.CharacterDefaults;
import core.EquipmentRules;
import core.SkillRules;
import core.UpgradeRules;
import core.Khu2Rules;
import core.ExpRules;
import core.BagUpgradeRules;
import core.GameSrc;
import core.MedalRules;
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
        testKhu2Rules();
        testExpRules();
        testBagUpgradeRules();
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

        check(CharacterDefaults.STARTER_GOLD == 3_000L, "new character starts with 3,000 gold");
        check(CharacterDefaults.STARTER_GEMS == 10_000, "new character starts with 10,000 gems");
        check(CharacterDefaults.starterGold(false) == 3_000L, "normal starter gold");
        check(CharacterDefaults.starterGems(false) == 10_000, "normal starter gems");
        check(CharacterDefaults.STARTER_BAG_CAPACITY == 126, "new character starts with full bag");

        JSONArray materials = (JSONArray) JSONValue.parse(CharacterDefaults.starterUpgradeMaterials());
        check(materials != null && materials.size() == 5, "starter upgrade material stacks");
        int[] expectedIds = new int[]{0, 1, 2, 3, 14};
        int[] expectedQuantities = new int[]{999, 999, 999, 999, 50};
        for (int i = 0; i < expectedIds.length; i++) {
            JSONArray stack = (JSONArray) materials.get(i);
            check(((Number) stack.get(0)).intValue() == expectedIds[i], "starter material id " + expectedIds[i]);
            check(((Number) stack.get(1)).intValue() == expectedQuantities[i],
                    "starter material quantity " + expectedIds[i]);
        }

        JSONArray pets = (JSONArray) JSONValue.parse(CharacterDefaults.starterEaglePet());
        check(pets != null && pets.size() == 1, "one starter pet");
        JSONArray eagle = (JSONArray) pets.get(0);
        check(((Number) eagle.get(1)).intValue() == Pet.TYPE_DAI_BANG, "starter pet is eagle");
        check(((Number) eagle.get(5)).intValue() == 300 && ((Number) eagle.get(6)).intValue() == 300,
                "starter eagle is fully grown");
        check(((Number) eagle.get(13)).intValue() == 1, "starter eagle follows character");
        check(((Number) eagle.get(14)).intValue() == 0, "starter eagle is already hatched");

        Pet loadedEagle = new Pet();
        loadedEagle.setup(eagle);
        check(loadedEagle.isEagle(), "starter eagle loads through Pet.setup");
        check(loadedEagle.is_follow && !loadedEagle.is_hatch, "loaded starter eagle is active");
        check(loadedEagle.grown == loadedEagle.maxgrown && loadedEagle.grown > 0,
                "loaded starter eagle can attack immediately");
        check(loadedEagle.getAttackOption() != null, "loaded starter eagle has attack option");
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

        Item3 wrongMedal = new Item3();
        wrongMedal.id = 4588;
        wrongMedal.type = 16;
        wrongMedal.clazz = 4;
        wrongMedal.level = 1;
        wrongMedal.icon = 955;
        wrongMedal.op = new java.util.ArrayList<>();
        wrongMedal.op.add(new template.Option(96, 6));
        wrongMedal.op.add(new template.Option(8, 39));
        wrongMedal.op.add(new template.Option(1, 487));
        check(!EquipmentRules.canWear(wrongMedal, (byte) 3, (short) 35), "wrong-class medal rejected");
        check(MedalRules.normalizeEquipped(wrongMedal, (byte) 3), "equipped medal class repaired");
        check(wrongMedal.id == 4590 && wrongMedal.icon == 957, "archer medal template restored");
        check(wrongMedal.getParamByOptionId(10) == 39, "archer medal percent damage restored");
        check(wrongMedal.getParamByOptionId(3) == 487, "archer medal flat damage restored");
        check(EquipmentRules.canWear(wrongMedal, (byte) 3, (short) 35), "repaired medal wearable");
        check(MedalRules.flatDamageOptionForMedal((short) 4587) == 2, "warrior medal uses fire damage");
        check(MedalRules.flatDamageOptionForMedal((short) 4588) == 1, "mage medal uses ice damage");
        check(MedalRules.flatDamageOptionForMedal((short) 4589) == 4, "assassin medal uses poison damage");
        check(MedalRules.flatDamageOptionForMedal((short) 4590) == 3, "archer medal uses electric damage");
        check(MedalRules.percentDamageOptionForClass((byte) 0) == 9, "warrior medal percent is fire");
        check(MedalRules.percentDamageOptionForClass((byte) 1) == 11, "assassin medal percent is poison");
        check(MedalRules.percentDamageOptionForClass((byte) 2) == 8, "mage medal percent is ice");
        check(MedalRules.percentDamageOptionForClass((byte) 3) == 10, "archer medal percent is electric");
        check(MedalRules.CHANGE_LINE_GEM_COST == 100, "medal line change costs 100 gems");
    }

    private static void testUpgradeRequirements() {
        check(GameSrc.percent[6] == 75, "tier 6 success rate");
        check(GameSrc.percent[7] == 60, "tier 7 success rate");
        check(GameSrc.percent[10] == 32, "tier 10 success rate");
        check(GameSrc.percent[14] == 15, "tier 14 success rate");
        check(UpgradeRules.materialsForTier(0).length == 2, "tier 0 materials");
        check(UpgradeRules.materialsForTier(6).length == 3, "tier 6 materials");
        check(UpgradeRules.materialsForTier(11).length == 4, "tier 11 materials");
        check(UpgradeRules.gemCost(0) == 4, "tier 0 gem cost");
        check(UpgradeRules.gemCost(14) == 10, "tier 14 gem cost");
        check(UpgradeRules.goldCost(5) == 10_000L, "gold cost");
        check(UpgradeRules.isLuckyMaterial(UpgradeRules.THREE_LEAF_CLOVER), "three-leaf clover accepted");
        check(UpgradeRules.isLuckyMaterial(UpgradeRules.FOUR_LEAF_CLOVER), "four-leaf clover accepted");
        check(UpgradeRules.luckyBonus(UpgradeRules.THREE_LEAF_CLOVER) == 30, "three-leaf clover bonus");
        check(UpgradeRules.failureTier(UpgradeRules.THREE_LEAF_CLOVER, 10, false) == 8,
                "three-leaf clover loses two tiers");
        check(UpgradeRules.failureTier(UpgradeRules.FOUR_LEAF_CLOVER, 10, false) == 9,
                "four-leaf clover loses one tier");
        check(UpgradeRules.failureTier(UpgradeRules.THREE_COLOR_STONE, 10, false) == 10,
                "three-color stone protects tier");
    }

    private static void testKhu2Rules() {
        check(Khu2Rules.COST_GEMS == 5, "Khu 2 gem cost");
        check(Khu2Rules.DURATION_HOURS == 2, "Khu 2 duration hours");
        check(Khu2Rules.DURATION_MILLIS == 7_200_000, "Khu 2 duration milliseconds");
    }

    private static void testExpRules() {
        check(ExpRules.applyLevelDifference(1_000, 50, 50) == 1_000, "same-level EXP");
        check(ExpRules.applyLevelDifference(1_000, 50, 51) == 1_100, "one-level-higher EXP");
        check(ExpRules.applyLevelDifference(1_000, 50, 55) == 1_500, "five-level-higher EXP");
        check(ExpRules.applyLevelDifference(1_000, 50, 60) == 1_500, "higher-level EXP cap");
        check(ExpRules.applyLevelDifference(1_000, 50, 49) == 900, "one-level-lower EXP");
        check(ExpRules.applyLevelDifference(1_000, 50, 45) == 500, "five-level-lower EXP");
        check(ExpRules.applyLevelDifference(1_000, 50, 40) == 500, "lower-level EXP floor");
    }

    private static void testBagUpgradeRules() {
        check(BagUpgradeRules.nextCapacity(42) == 48, "first bag upgrade capacity");
        check(BagUpgradeRules.gemCost(42) == 5, "first bag upgrade cost");
        check(BagUpgradeRules.nextCapacity(48) == 54, "second bag upgrade capacity");
        check(BagUpgradeRules.gemCost(48) == 10, "second bag upgrade cost");
        check(BagUpgradeRules.nextCapacity(124) == 126, "bag upgrade clamps to maximum");
        check(!BagUpgradeRules.canUpgrade(126), "bag maximum cannot be upgraded");
        check(BagUpgradeRules.menuLabel(126).contains("tối đa"), "bag maximum menu label");
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
        check(Pet.EAGLE_GOLD_MIN == 2_000, "eagle minimum gold drop");
        check(Pet.EAGLE_GOLD_MAX == 60_000, "eagle maximum gold drop");
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
