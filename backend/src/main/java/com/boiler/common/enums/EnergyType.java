package com.boiler.common.enums;

/**
 * 能源类型枚举
 * 定义系统支持的能源类型，包括天然气、电力、煤炭、生物质等
 */
public enum EnergyType {
    /** 天然气，单位为立方米 */
    NATURAL_GAS("天然气", "Nm³"),
    /** 电力，单位为千瓦时 */
    ELECTRICITY("电力", "kWh"),
    /** 煤炭，单位为吨 */
    COAL("煤炭", "t"),
    /** 生物质，单位为吨 */
    BIOMASS("生物质", "t");

    /** 能源名称 */
    private final String name;
    /** 计量单位 */
    private final String unit;

    /**
     * 构造函数
     * @param name 能源名称
     * @param unit 计量单位
     */
    EnergyType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    /**
     * 获取能源名称
     * @return 能源名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取计量单位
     * @return 计量单位
     */
    public String getUnit() {
        return unit;
    }
}
