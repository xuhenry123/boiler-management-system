package com.boiler.common.enums;

public enum EnergyType {
    NATURAL_GAS("天然气", "Nm³"),
    ELECTRICITY("电力", "kWh"),
    COAL("煤炭", "t"),
    BIOMASS("生物质", "t");

    private final String name;
    private final String unit;

    EnergyType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }
}
