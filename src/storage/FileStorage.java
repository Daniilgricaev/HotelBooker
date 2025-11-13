package storage;

import model.*;

import java.io.*;
import java.nio.*;
import java.time.LocalTime;
import java.util.*;

public class FileStorage implements Storage {
    private static final String USERS_FILE ="data/users.txt";
    private static final String HOTELS_FILE ="data/hotels.txt";
    private static final String BOOKINGS_FILE ="data/bookings.txt";

    private final List<User> users = new ArrayList<>();
    private final List<Hotel> hotels = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public FileStorage() {
        loadUsers();
    }

    @Override
    public List<User>getUsers() {
        return users;
    }
    @Override
    public List<Hotel>getHotels() {
        return hotels;
    }
    @Override
    public List<Booking>getBookings() {
        return bookings;
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line=reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    users.add(new User(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            Role.valueOf(parts[3])
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("ошибка загрузки пользователей" + e.getMessage());
        }
    }


    @Override
    public void saveUsers() {

    }

    @Override
    public void saveBookings() {

    }
}
