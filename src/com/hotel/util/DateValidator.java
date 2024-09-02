package com.hotel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate validateDate(String dateString, String dateType) throws IllegalArgumentException {
        try {
            LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
            LocalDate today = LocalDate.now();

            if (date.isBefore(today)) {
                throw new IllegalArgumentException("The " + dateType + " date cannot be in the past.");
            }

            return date;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    public static void validateDateRange(LocalDate checkInDate, LocalDate checkOutDate) throws IllegalArgumentException {
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("The check-out date must be after the check-in date.");
        }
    }
}