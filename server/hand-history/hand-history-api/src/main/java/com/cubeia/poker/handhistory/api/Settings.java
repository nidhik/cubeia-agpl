package com.cubeia.poker.handhistory.api;

import java.io.Serializable;

public class Settings implements Serializable {

    private static final long serialVersionUID = 294678158891008493L;

    private String variant;
    private String currencyCode;
    private String betStrategyType;


    public Settings() {
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getBetStrategyType() {
        return betStrategyType;
    }

    public void setBetStrategyType(String betStrategyType) {
        this.betStrategyType = betStrategyType;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "variant='" + variant + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", betStrategyType='" + betStrategyType + '\'' +
                '}';
    }

}
