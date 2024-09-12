package com.hotel.ui;

import com.hotel.service.HotelService;
import com.hotel.model.Reservation;
import com.hotel.model.RoomType;
import com.hotel.util.DateValidator;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class UserInterface {


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    private HotelService hotelService;
    private Scanner scanner;

    // Colors (unchanged)

    public UserInterface(HotelService hotelService) {
        this.hotelService = hotelService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            displayMenu();
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    createReservation();
                    break;
                case 2:
                    cancelReservation();
                    break;
                case 3:
                    editReservation();
                    break;
                case 4:
                    displayReservations();
                    break;
                case 5:
                    displayStatistics();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println(ANSI_RED + "Invalid option." + ANSI_RESET);
            }
        }
    }

    private void displayMenu() {
        System.out.println(ANSI_RED + "\n============================" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "\n1. Create reservation");
        System.out.println("2. Cancel reservation");
        System.out.println("3. Edit reservation");
        System.out.println("4. Display reservations");
        System.out.println("5. Display statistics");
        System.out.println("6. Exit" + ANSI_RESET);
        System.out.println(ANSI_RED + "\n============================" + ANSI_RESET);
    }

    private void createReservation() {
        String clientName = getStringInput("Client name: ");
        RoomType roomType = getRoomTypeInput();
        LocalDate checkInDate = getValidatedDate("check-in");
        LocalDate checkOutDate = getValidatedDate("check-out");
        String specialNotes = getStringInput("Special notes (optional): ");

        try {
            DateValidator.validateDateRange(checkInDate, checkOutDate);
            hotelService.createReservation(clientName, roomType, checkInDate, checkOutDate, specialNotes);
        } catch (IllegalArgumentException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }
    }

    private void cancelReservation() {
        String clientName = getStringInput("Client name: ");
        hotelService.cancelReservation(clientName);
    }

    private void editReservation() {
        String clientName = getStringInput("Client name: ");
        Optional<Reservation> reservationOpt = hotelService.findReservation(clientName);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            System.out.println("Current reservation: " + reservation);

            RoomType newRoomType = getRoomTypeInput();
            LocalDate newCheckInDate = getValidatedDate("new check-in");
            LocalDate newCheckOutDate = getValidatedDate("new check-out");

            try {
                DateValidator.validateDateRange(newCheckInDate, newCheckOutDate);
                boolean success = hotelService.editReservation(reservation, newCheckInDate, newCheckOutDate, newRoomType);
                if (!success) {
                    System.out.println(ANSI_RED + "Failed to edit reservation. Please try again." + ANSI_RESET);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED + "No reservation found for client: " + clientName + ANSI_RESET);
        }
    }

    private void displayReservations() {
        if (hotelService.getReservations().isEmpty()) {
            System.out.println("No reservations exist.");
        } else {
            for (Reservation reservation : hotelService.getReservations()) {
                System.out.println(reservation);
            }
        }
    }

    private void displayStatistics() {
        Map<String, Double> stats = hotelService.getStatistics();
        System.out.println("\nHotel Statistics:");
        for (Map.Entry<String, Double> entry : stats.entrySet()) {
            System.out.printf("%s: %.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private LocalDate getValidatedDate(String dateType) {
        while (true) {
            String dateString = getStringInput(dateType + " date (yyyy-MM-dd): ");
            try {
                return DateValidator.validateDate(dateString, dateType);
            } catch (IllegalArgumentException e) {
                System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            }
        }
    }

    private RoomType getRoomTypeInput() {
        System.out.println("Room Types:");
        for (int i = 0; i < RoomType.values().length; i++) {
            System.out.printf("%d. %s%n", i + 1, RoomType.values()[i]);
        }
        int choice = getIntInput("Choose a room type (1-" + RoomType.values().length + "): ");
        return RoomType.values()[choice - 1];
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
            }
        }
    }
}