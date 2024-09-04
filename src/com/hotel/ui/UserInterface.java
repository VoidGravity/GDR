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
    //colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //
    public UserInterface(HotelService hotelService) {
        this.hotelService = hotelService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println(ANSI_RED+"\n============================"+ANSI_RESET);
            System.out.println(ANSI_GREEN+"\n1. Create reservation");
            System.out.println("2. Cancel reservation");
            System.out.println("3. Edit reservation");
            System.out.println("4. Display reservations");
            System.out.println("5. Exit"+ANSI_RESET);
            System.out.println(ANSI_RED+"\n============================\n"+ANSI_RESET);
            System.out.print(ANSI_PURPLE+"Choose an option: "+ANSI_RESET);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED+"Invalid input. Please enter a number."+ANSI_RESET);
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
                    displayReservations();
                    break;
                case 5:
                    System.out.println("Goodbye :( !");
                    return;
                default:
                    System.out.println(ANSI_RED+"Invalid option."+ANSI_RESET);
            }
        }
    }

    private void createReservation() {
        System.out.print("Client name: ");
        String clientName = scanner.nextLine();

        if (clientName.trim().isEmpty()) {
            System.out.println(ANSI_RED+"Client name cannot be empty."+ANSI_RESET);
            return;
        }

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
        if (hotelService.getReservations().isEmpty()) {
            System.out.println(ANSI_RESET+"No reservations exist."+ANSI_RESET);
            return;
        }
        System.out.print("Client name: ");
        String clientName = scanner.nextLine();
        //if client name is empty, it will not be able to edit the reservation the give him chance to enter the name again
        if (clientName.trim().isEmpty()) {
            System.out.println(ANSI_RED+"Client name cannot be empty."+ANSI_RESET);
            return;
        }

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
                    System.out.println(ANSI_RESET+"Failed to edit reservation. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(ANSI_RESET+"No reservation found for client: " +ANSI_RESET+ clientName);
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