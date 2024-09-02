package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotelService {
    private List<Room> rooms;
    private List<Reservation> reservations;

    public HotelService(int numberOfRooms) {
        rooms = new ArrayList<>();
        for (int i = 0; i < numberOfRooms; i++) {
            rooms.add(new Room(i + 1));
        }
        reservations = new ArrayList<>();
    }

    public void createReservation(String clientName, LocalDate checkInDate, LocalDate checkOutDate) {
        for (Room room : rooms) {
            if (room.isAvailable()) {
                Reservation reservation = new Reservation(clientName, room, checkInDate, checkOutDate);
                reservations.add(reservation);
                System.out.println("Reservation created: " + reservation);
                return;
            }
        }
        System.out.println("No available rooms.");
    }

    public void cancelReservation(String clientName) {
        for (Reservation reservation : reservations) {
            if (reservation.getClientName().equals(clientName)) {
                reservation.getRoom().setAvailable(true);
                reservations.remove(reservation);
                System.out.println("Reservation cancelled for " + clientName);
                return;
            }
        }
        System.out.println("Reservation not found.");
    }

    public void displayReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations.");
        } else {
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
        }
    }

    public Optional<Reservation> findReservation(String clientName) {
        return reservations.stream()
                .filter(r -> r.getClientName().equals(clientName))
                .findFirst();
    }

    public boolean editReservation(Reservation reservation, LocalDate newCheckInDate, LocalDate newCheckOutDate) {
        reservation.setCheckInDate(newCheckInDate);
        reservation.setCheckOutDate(newCheckOutDate);
        System.out.println("Reservation updated: " + reservation);
        return true;
    }
}