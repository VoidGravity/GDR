package com.hotel.repository;

import com.hotel.model.*;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(int id);
    List<Reservation> findAll();
    void update(Reservation reservation);
    void delete(int id);
}