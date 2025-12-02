package com.marketlink.models;

public class DashboardMetric {
    private String title;
    private String value;
    private int iconResId;
    private int backgroundDrawable; // Changed from backgroundColor to backgroundDrawable for gradients
    private String filterType; // For navigation to filtered lists

    public DashboardMetric(String title, String value, int iconResId, int backgroundDrawable, String filterType) {
        this.title = title;
        this.value = value;
        this.iconResId = iconResId;
        this.backgroundDrawable = backgroundDrawable;
        this.filterType = filterType;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public String getFilterType() {
        return filterType;
    }
}
