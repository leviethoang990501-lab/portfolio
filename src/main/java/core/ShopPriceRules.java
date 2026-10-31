package core;

public final class ShopPriceRules {
    public static final long HIGH_GEM_PRICE_THRESHOLD = 10_000L;
    public static final long HIGH_GEM_PRICE_DIVISOR = 100L;

    private ShopPriceRules() {
    }

    public static long normalizeGemPrice(long price) {
        if (price < HIGH_GEM_PRICE_THRESHOLD) {
            return price;
        }
        return Math.max(1L, price / HIGH_GEM_PRICE_DIVISOR);
    }
}
