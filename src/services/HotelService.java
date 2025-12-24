package services;

import model.Hotel;
import model.Room;
import storage.Storage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HotelService {
    private final Storage storage;

    public HotelService(Storage storage) {
        this.storage = storage;
    }

    public List<Hotel> findHotelsByCity(String city, Double minP, Double maxP,
                                        Integer cap, String type, LocalDate start,
                                        LocalDate end, BookingService bookingService) {
        return storage.getHotels().stream()
                .filter(h -> h.get_city_hotel().equalsIgnoreCase(city)).filter(h -> h.getRooms().stream().anyMatch(room ->
                        (minP == null || room.get_price_per_night() >= minP) &&
                                (maxP == null || room.get_price_per_night() <= maxP) &&
                                (cap == null || room.room_capacity() >= cap) &&
                                (type == null || room.get_type().equalsIgnoreCase(type)) &&
                                bookingService.isRoomAvailable(room.get_room_id(), start, end))).collect(Collectors.toList());
    }

    public void sortByPrice(List<Hotel> hotels, boolean a) {
        Comparator<Hotel> priceComparator = Comparator.comparingDouble(h -> h.getRooms().stream()
                .mapToDouble(Room::get_price_per_night).min()
                .orElse(Double.MAX_VALUE));
        if (a) {
            hotels.sort(priceComparator);
        } else {
            hotels.sort(priceComparator.reversed());
        }
    }

    public Hotel findHotelById(int id) {
        return storage.getHotels().stream().filter(h -> h.get_id_hotel() == id).findFirst().orElse(null);
    }

    public Room findRoomById(Hotel hotel, int roomId) {
        return hotel.getRooms().stream().filter(r -> r.get_room_id() == roomId).findFirst().orElse(null);
    }
}
