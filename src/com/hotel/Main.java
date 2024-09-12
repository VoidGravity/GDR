package com.hotel;

import com.hotel.service.HotelService;
import com.hotel.ui.UserInterface;
import com.hotel.repository.JdbcReservationRepository;
import com.hotel.repository.ReservationRepository;

public class Main {
    public static void main(String[] args) {
        ReservationRepository repository = new JdbcReservationRepository();
        HotelService hotelService = new HotelService(10, repository);
        UserInterface ui = new UserInterface(hotelService);
        ui.start();
    }
}