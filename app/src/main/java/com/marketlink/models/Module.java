package com.marketlink.models;

public class Module {
    private String name;
    private int iconResId;
    private int gradientResId;
    private String navigationId; // For navigation destination

    public Module(String name, int iconResId, int gradientResId, String navigationId) {
        this.name = name;
        this.iconResId = iconResId;
        this.gradientResId = gradientResId;
        this.navigationId = navigationId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getGradientResId() {
        return gradientResId;
    }

    public String getNavigationId() {
        return navigationId;
    }
}
