package core;

public final class TestTopUpRules {

    public static final int VND_PER_GEM = 1_000;
    public static final int MIN_AMOUNT_VND = 1_000;
    public static final int MAX_AMOUNT_VND = 1_000_000_000;
    public static final int MAX_GEM_BALANCE = 2_000_000_000;

    private TestTopUpRules() {
    }

    public static boolean isValidAmount(long amountVnd) {
        return amountVnd >= MIN_AMOUNT_VND
                && amountVnd <= MAX_AMOUNT_VND
                && amountVnd % VND_PER_GEM == 0;
    }

    public static int gemsFor(long amountVnd) {
        if (!isValidAmount(amountVnd)) {
            throw new IllegalArgumentException("Số tiền nạp thử không hợp lệ");
        }
        return (int) (amountVnd / VND_PER_GEM);
    }

    public static boolean canAddGems(int currentGems, int gemsToAdd) {
        return currentGems >= 0 && gemsToAdd > 0
                && (long) currentGems + gemsToAdd <= MAX_GEM_BALANCE;
    }
}
