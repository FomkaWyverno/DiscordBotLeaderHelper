package com.wyverno.request;

public enum Fraction {
    LCN("LCN"),
    YAKUZA("Yakuza");

    private final String label;

    Fraction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
