package core;

/** Defaults shared by character creation and regression tests. */
public final class CharacterDefaults {
    private CharacterDefaults() {
    }

    public static boolean isValidClazz(byte clazz) {
        return clazz >= 0 && clazz <= 3;
    }

    public static String starterWear(byte clazz) {
        switch (clazz) {
            case 0:
                return "[[0,0,8,1,0,0,0,0,[[0,54],[40,120]],0],[80,0,0,1,16,0,0,0,[[14,52],[16,100]],1],[120,0,1,1,24,0,0,0,[[14,18],[25,3]],7]]";
            case 1:
                return "[[5,1,9,1,1,0,0,0,[[0,54],[40,120]],0],[105,1,0,1,21,0,1,0,[[14,52],[20,100]],1],[145,1,1,1,29,0,1,0,[[14,18],[24,3]],7]]";
            case 2:
                return "[[10,2,11,1,2,0,0,0,[[0,50],[40,120]],0],[90,2,0,1,18,0,2,0,[[14,42],[16,200]],1],[50,2,2,1,10,0,2,0,[[7,200],[14,12]],6],[130,2,1,1,26,0,2,0,[[14,12],[26,4]],7]]";
            case 3:
                return "[[15,3,10,1,3,0,0,0,[[0,50],[40,120]],0],[95,3,0,1,19,0,3,0,[[14,44],[16,200]],1],[55,3,2,1,11,0,3,0,[[7,200],[14,14]],6],[135,3,1,1,27,0,3,0,[[14,14],[24,4]],7]]";
            default:
                throw new IllegalArgumentException("Invalid character class: " + clazz);
        }
    }

    public static String starterSkills(boolean fullTestProfile) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < SkillRules.SKILL_COUNT; i++) {
            if (i > 0) result.append(',');
            result.append(fullTestProfile ? 10 : (i == 0 ? 1 : 0));
        }
        return result.append(']').toString();
    }
}
