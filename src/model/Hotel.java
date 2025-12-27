package model;
import java.util.List;


public class Hotel {
    int Hotel_id;
    String Hotel;
    String city;
    String address;
    List<Room>rooms;


    public Hotel(int idHotel,String HotelName,String cityHotel,String addressHotel,List<Room>rooms){
        this.Hotel_id = idHotel;
        this.Hotel = HotelName;
        this.city = cityHotel;
        this.address = addressHotel;
        this.rooms = rooms;
    }
    public int get_id_hotel(){
        return Hotel_id;
    }
    public String get_hotel_name(){
        return Hotel;
    }
    public String get_city_hotel(){
        return city;
    }
    public String get_address_hotel(){
        return address;
    }
    public List<Room>getRooms(){
        return rooms;
    }

    public String get_about_hotel(){
        return String.format("Hotel id : %d\nHotel name : %s\nAddress : %s\nCity : %s\n",Hotel_id,Hotel,city,address);
    }

    @Override
    public String toString() {
        return String.format("Hotel id : %d\nHotel name : %s\nCity : %s\nAddress : %s\n",Hotel_id,Hotel,city,address);
    }
}
