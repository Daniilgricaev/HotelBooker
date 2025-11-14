package storage;

import model.Booking;
import model.Hotel;
import model.User;

import java.util.List;

public interface Storage {
    List<User>getUsers();
    List<Hotel>getHotels();
    List<Booking>getBookings();

    void saveUsers();
    void saveBookings();
}
