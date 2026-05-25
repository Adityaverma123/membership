package com.firstclub.membership.domain;

public enum TierType {
    SILVER(1),
    GOLD(2),
    PLATINUM(3);

    private final int rank;

    TierType(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
