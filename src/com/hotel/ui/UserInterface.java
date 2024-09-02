package com.hotel.ui;

import com.hotel.service.HotelService;
import com.hotel.util.DateValidator;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class UserInterface {
    private HotelService hotelService;
    private Scanner scanner;

    public UserInterface(HotelService hotelService) {
        this.hotelService = hotelService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n1. Create reservation");
            System.out.println("2. Cancel reservation");
            System.out.println("3. Edit reservation");
            System.out.println("4. Display reservations");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

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
                    hotelService.displayReservations();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void createReservation() {
        System.out.print("Client name: ");
        String clientName = scanner.nextLine();

        LocalDate checkInDate = getValidatedDate("check-in");
        LocalDate checkOutDate = getValidatedDate("check-out");

        try {
            DateValidator.validateDateRange(checkInDate, checkOutDate);
            hotelService.createReservation(clientName, checkInDate, checkOutDate);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cancelReservation() {
        System.out.print("Client name: ");
        String clientName = scanner.nextLine();
        hotelService.cancelReservation(clientName);
    }

    private void editReservation() {
        System.out.print("Client name: ");
        String clientName = scanner.nextLine();

        Optional<Reservation> reservationOpt = hotelService.findReservation(clientName);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            System.out.println("Current reservation: " + reservation);

            LocalDate newCheckInDate = getValidatedDate("new check-in");
            LocalDate newCheckOutDate = getValidatedDate("new check-out");

            try {
                DateValidator.validateDateRange(newCheckInDate, newCheckOutDate);
                boolean success = hotelService.editReservation(reservation, newCheckInDate, newCheckOutDate);
                if (!success) {
                    System.out.println("Failed to edit reservation. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No reservation found for client: " + clientName);
        }
    }

    private LocalDate getValidatedDate(String dateType) {
        while (true) {
            System.out.print(dateType + " date (yyyy-MM-dd): ");
            String dateString = scanner.nextLine();
            try {
                return DateValidator.validateDate(dateString, dateType);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}