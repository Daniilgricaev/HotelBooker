package services;

import model.Hotel;
import model.Room;
import storage.Storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class HotelService {
    private final Storage storage;

    @Autowired
    public HotelService(Storage storage) {
        this.storage = storage;
    }

    public List<Hotel> findHotelsByCity(String city) {
        //return storage.getHotels().stream().filter(h -> h.get_city_hotel().equalsIgnoreCase(city)).collect(Collectors.toList());
        return storage.getHotels().stream()
                .filter(h -> h.get_city_hotel().toLowerCase().contains(city.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Hotel findHotelById(int id) {
        return storage.getHotels().stream().filter(h -> h.get_id_hotel() == id).findFirst().orElse(null);
    }

    public Room findRoomById(Hotel hotel, int roomId) {
        return hotel.getRooms().stream().filter(r -> r.get_room_id() == roomId).findFirst().orElse(null);
    }
}
