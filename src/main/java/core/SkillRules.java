package core;

import template.Skill;

/** Shared validation and level rules for every class skill tree. */
public final class SkillRules {
    public static final int SKILL_COUNT = 21;

    private SkillRules() {
    }

    public static boolean validIndex(Skill[] skills, int index) {
        return skills != null && index >= 0 && index < skills.length && skills[index] != null
                && skills[index].mLvSkill != null && skills[index].mLvSkill.length > 0;
    }

    public static int maxLearnedLevel(Skill skill) {
        if (skill == null || skill.mLvSkill == null) return 0;
        return Math.max(1, skill.mLvSkill.length > 5 ? skill.mLvSkill.length - 5 : skill.mLvSkill.length);
    }

    public static byte[] normalizePoints(byte[] source) {
        byte[] result = new byte[SKILL_COUNT];
        if (source != null) System.arraycopy(source, 0, result, 0, Math.min(source.length, result.length));
        if (result[0] < 1) result[0] = 1;
        for (int i = 0; i < result.length; i++) if (result[i] < 0) result[i] = 0;
        return result;
    }

    /** Normalizes persisted data and clamps every learned level to the loaded skill table. */
    public static byte[] normalizePoints(byte[] source, Skill[] skills) {
        byte[] result = normalizePoints(source);
        for (int i = 0; i < result.length; i++) {
            if (!validIndex(skills, i)) {
                result[i] = 0;
                continue;
            }
            result[i] = (byte) Math.min(result[i], maxLearnedLevel(skills[i]));
        }
        if (validIndex(skills, 0) && result[0] < 1) result[0] = 1;
        return result;
    }

    public static void unlockAll(byte[] points, Skill[] skills) {
        if (points == null) return;
        for (int i = 0; i < points.length; i++) {
            int max = validIndex(skills, i) ? maxLearnedLevel(skills[i]) : 10;
            points[i] = (byte) Math.min(10, max);
        }
        if (points.length > 0 && points[0] < 1) points[0] = 1;
    }
}
