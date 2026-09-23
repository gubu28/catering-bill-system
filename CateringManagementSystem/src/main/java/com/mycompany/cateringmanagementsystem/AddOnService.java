package com.mycompany.cateringmanagementsystem;

import java.io.Serializable;

public class AddOnService implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double flatCost;
    private double perGuestCost;

    public AddOnService(String name, double flatCost, double perGuestCost) {
        this.name = name;
        this.flatCost = flatCost;
        this.perGuestCost = perGuestCost;
    }

    public String getName() { return name; }
    public double getFlatCost() { return flatCost; }
    public double getPerGuestCost() { return perGuestCost; }

    public double calculateCost(int guests) {
        return flatCost + (perGuestCost * guests);
    }
}
