package com.hotel.model;

public class Room {
    private int number;
    private RoomType type;
    private boolean available;

    public Room(int number, RoomType type) {
        this.number = number;
        this.type = type;
        this.available = true;
    }

    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Room " + number + " (" + type + ")";
    }
}