package core;

/** Pure combat EXP modifiers, kept separate so balance changes can be tested. */
public final class ExpRules {
    private ExpRules() {
    }

    public static long applyLevelDifference(long baseExp, int playerLevel, int monsterLevel) {
        int difference = monsterLevel - playerLevel;
        if (difference == 0) return baseExp;

        int steps = Math.min(5, Math.abs(difference));
        int tenths = difference > 0 ? 10 + steps : 10 - steps;
        return (baseExp * tenths) / 10;
    }
}
