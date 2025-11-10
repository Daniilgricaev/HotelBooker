package model;

public class Room {
    int id;
    String type;
    int capacity;
    double price_per_night;

    public Room(int id_room,String type_room, int capacity_room,double price_for_room){
        this.id = id_room;
        this.type = type_room;
        this.capacity = capacity_room;
        this.price_per_night = price_for_room;
    }
    public int get_room_id(){
        return id;
    }
    public String get_type(){
        return type;
    }
    public int room_capacity(){
        return capacity;
    }
    public double get_price_per_night(){
        return price_per_night;
    }
    @Override
    public String toString(){
        return String.format("Комната №%d: Тип: %s, Вместимость: %d, Цена: %.2f/ночь", id,type,capacity,price_per_night );
    }
}
