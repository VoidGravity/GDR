package com.hotel.model;

public enum RoomType {
    STANDARD(100),
    DELUXE(150),
    SUITE(200);

    private final double basePrice;

    RoomType(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }
}