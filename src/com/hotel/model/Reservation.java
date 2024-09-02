package com.hotel.model;

import java.time.LocalDate;

public class Reservation {
    private String clientName;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Reservation(String clientName, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.clientName = clientName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        room.setAvailable(false);
    }

    @Override
    public String toString() {
        return clientName + " in room " + room.getNumber() + " from " + checkInDate + " to " + checkOutDate;
    }

    public String getClientName() {
        return clientName;
    }

    public Room getRoom() {
        return room;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}