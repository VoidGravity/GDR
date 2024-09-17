package com.hotel.repository;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReservationRepository implements ReservationRepository {

    @Override
    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservations (client_name, room_number, check_in_date, check_out_date, status, total_price, special_notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reservation.getClientName());
            pstmt.setInt(2, reservation.getRoom().getNumber());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(5, reservation.getStatus().name());
            pstmt.setDouble(6, reservation.getTotalPrice());
            pstmt.setString(7, reservation.getSpecialNotes());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving reservation", e);
        }
    }

    @Override
    public Optional<Reservation> findById(int id) {
        String sql = "SELECT r.*, rm.type AS room_type FROM reservations r JOIN rooms rm ON r.room_number = rm.number WHERE r.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, rm.type AS room_type FROM reservations r JOIN rooms rm ON r.room_number = rm.number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reservations", e);
        }
        return reservations;
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET client_name = ?, room_number = ?, check_in_date = ?, check_out_date = ?, status = ?, total_price = ?, special_notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getClientName());
            pstmt.setInt(2, reservation.getRoom().getNumber());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(5, reservation.getStatus().name());
            pstmt.setDouble(6, reservation.getTotalPrice());
            pstmt.setString(7, reservation.getSpecialNotes());
            pstmt.setInt(8, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation", e);
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String clientName = rs.getString("client_name");
        int roomNumber = rs.getInt("room_number");
        RoomType roomType = RoomType.valueOf(rs.getString("room_type"));
        LocalDate checkInDate = rs.getDate("check_in_date").toLocalDate();
        LocalDate checkOutDate = rs.getDate("check_out_date").toLocalDate();
        ReservationStatus status = ReservationStatus.valueOf(rs.getString("status"));
        double totalPrice = rs.getDouble("total_price");
        String specialNotes = rs.getString("special_notes");

        Room room = new Room(roomNumber, roomType);
        Reservation reservation = new Reservation(clientName, room, checkInDate, checkOutDate, totalPrice);
        reservation.setId(id);
        reservation.setStatus(status);
        reservation.setSpecialNotes(specialNotes);
        return reservation;
    }
}