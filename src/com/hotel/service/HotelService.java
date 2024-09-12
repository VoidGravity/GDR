package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class HotelService {
    private Map<Integer, Room> rooms;
    private ReservationRepository reservationRepository;

    public HotelService(int numberOfRooms, ReservationRepository reservationRepository) {
        this.rooms = new HashMap<>();
        for (int i = 0; i < numberOfRooms; i++) {
            RoomType type = RoomType.values()[i % RoomType.values().length];
            rooms.put(i + 1, new Room(i + 1, type));
        }
        this.reservationRepository = reservationRepository;
    }

    public void createReservation(String clientName, RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate, String specialNotes) {
        Room room = findAvailableRoom(roomType, checkInDate, checkOutDate)
                .orElseThrow(() -> new RuntimeException("No available rooms of type " + roomType));

        double totalPrice = calculateTotalPrice(room, checkInDate, checkOutDate);

        Reservation reservation = new Reservation(clientName, room, checkInDate, checkOutDate, totalPrice);
        reservation.setSpecialNotes(specialNotes);
        reservationRepository.save(reservation);
        System.out.println("Reservation created: " + reservation);
    }

    public double cancelReservation(String clientName) {
        List<Reservation> allReservations = reservationRepository.findAll();
        for (Reservation reservation : allReservations) {
            if (reservation.getClientName().equals(clientName)) {
                double refundAmount = calculateRefund(reservation);
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.update(reservation);
                System.out.println("Reservation cancelled for " + clientName);
                return refundAmount;
            }
        }
        System.out.println("Reservation not found.");
        return 0;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findReservation(String clientName) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getClientName().equals(clientName))
                .findFirst();
    }

    public boolean editReservation(Reservation reservation, LocalDate newCheckInDate, LocalDate newCheckOutDate, RoomType newRoomType) {
        Room newRoom = findAvailableRoom(newRoomType, newCheckInDate, newCheckOutDate)
                .orElseThrow(() -> new RuntimeException("No available rooms of type " + newRoomType));

        double newTotalPrice = calculateTotalPrice(newRoom, newCheckInDate, newCheckOutDate);

        reservation.setRoom(newRoom);
        reservation.setCheckInDate(newCheckInDate);
        reservation.setCheckOutDate(newCheckOutDate);
        reservation.setTotalPrice(newTotalPrice);

        reservationRepository.update(reservation);
        System.out.println("Reservation updated: " + reservation);
        return true;
    }

    private Optional<Room> findAvailableRoom(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        return rooms.values().stream()
                .filter(room -> room.getType() == roomType && isRoomAvailable(room, checkInDate, checkOutDate))
                .findFirst();
    }

    private boolean isRoomAvailable(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> allReservations = reservationRepository.findAll();
        return allReservations.stream()
                .filter(r -> r.getRoom().getNumber() == room.getNumber() && r.getStatus() != ReservationStatus.CANCELLED)
                .noneMatch(r -> (checkInDate.isBefore(r.getCheckOutDate()) || checkInDate.isEqual(r.getCheckOutDate())) &&
                        (checkOutDate.isAfter(r.getCheckInDate()) || checkOutDate.isEqual(r.getCheckInDate())));
    }

    private double calculateTotalPrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double basePrice = room.getType().getBasePrice();
        return basePrice * nights * getSeasonMultiplier(checkInDate);
    }

    private double getSeasonMultiplier(LocalDate date) {
        int month = date.getMonthValue();
        if (month >= 6 && month <= 8) return 1.5; // Summer season
        if (month == 12 || month <= 2) return 1.2; // Winter season
        return 1.0; // Regular season
    }

    private double calculateRefund(Reservation reservation) {
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getCheckInDate());
        if (daysUntilCheckIn > 7) {
            return reservation.getTotalPrice(); // Full refund
        } else if (daysUntilCheckIn > 3) {
            return reservation.getTotalPrice() * 0.5; // 50% refund
        } else {
            return 0; // No refund
        }
    }

    public Map<String, Double> getStatistics() {
        List<Reservation> allReservations = reservationRepository.findAll();

        long totalReservations = allReservations.size();
        long cancelledReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        double totalRevenue = allReservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        double occupancyRate = (double) allReservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .count() / (rooms.size() * 365) * 100;

        Map<String, Double> stats = new HashMap<>();
        stats.put("Total Reservations", (double) totalReservations);
        stats.put("Cancelled Reservations", (double) cancelledReservations);
        stats.put("Total Revenue", totalRevenue);
        stats.put("Occupancy Rate", occupancyRate);

        return stats;
    }
}