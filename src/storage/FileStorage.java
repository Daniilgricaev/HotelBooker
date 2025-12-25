package storage;

import model.*;

import java.io.*;
import java.nio.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FileStorage implements Storage {
    private static final String USERS_FILE ="src/data/users.txt";
    private static final String HOTELS_FILE ="src/data/hotels.txt";
    private static final String BOOKINGS_FILE ="src/data/bookings.txt";

    private final List<User> users = new ArrayList<>();
    private final List<Hotel> hotels = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public FileStorage() {
        loadUsers();
        loadHotels();
        loadBookings();
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

    private void loadHotels() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HOTELS_FILE))) {
            String line;
            Hotel currHotel = null;
            List<Room> currRooms = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals("H")) {
                    if (currHotel != null) {
                        hotels.add(new Hotel(currHotel.get_id_hotel(), currHotel.get_hotel_name(), currHotel.get_city_hotel(), currHotel.get_address_hotel(), new ArrayList<>(currRooms)));
                        currRooms.clear();
                    }
                    currHotel = new Hotel(Integer.parseInt(parts[1]), parts[2], parts[3], parts[4], null);
                }
                else if (parts[0].equals("R") & currHotel != null) {
                    currRooms.add(new Room(
                            Integer.parseInt(parts[1]),
                            parts[2],
                            Integer.parseInt(parts[3]),
                            Double.parseDouble(parts[4])
                    ));
                }
            }
            if (currHotel != null) {
                hotels.add(new Hotel(currHotel.get_id_hotel(), currHotel.get_hotel_name(), currHotel.get_city_hotel(), currHotel.get_address_hotel(), currRooms));
            }
        } catch (IOException e) {
            System.err.println("ошибка загрузки отелей " + e.getMessage());
        }
    }

    private void loadBookings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    Booking booking = new Booking(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            LocalDate.parse(parts[4]),
                            LocalDate.parse(parts[5]));
                    booking.setStatus(BookingStatus.valueOf(parts[6]));
                    bookings.add(booking);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("ошибка загрузки бронирований " + e.getMessage());
        }
    }


    @Override
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, false))) {
            for (User user : users) {
                writer.write(String.format("%d;%s;%s;%s\n", user.getId(), user.getLogin(), user.getPassword(), user.getRole()));
            }
        } catch (IOException e) {
            System.err.println("ошибка сохранения пользователей " + e.getMessage());
        }
    }

    @Override
    public void saveBookings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE, false))) {
            for (Booking booking : bookings) {
                writer.write(String.format("%d;%d;%d;%d;%s;%s;%s\n", booking.getID(), booking.getUserID(), booking.getHotelID(), booking.getRoomID(), booking.getStartDate(), booking.getEndDate(), booking.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("ошибка сохранения бронирований " + e.getMessage());
        }
    }
}
