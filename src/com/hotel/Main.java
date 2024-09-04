//package com.hotel;
package com.hotel;

import com.hotel.service.HotelService;
import com.hotel.ui.UserInterface;
//import com.hotel.*;

public class Main {
    public static void main(String[] args) {
        HotelService hotelService = new HotelService(10);
//        com.hotel.service.HotelService hotelService1 = new com.hotel.service.HotelService(10);
        UserInterface ui = new UserInterface(hotelService);
        ui.start();
    }
}