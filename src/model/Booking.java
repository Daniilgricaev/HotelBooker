package model;
import java.time.LocalDate;


public class Booking {
    int id;
    int userID;
    int hotelID;
    int roomID;
    LocalDate startDate;
    LocalDate endDate;
    BookingStatus status;

    public Booking(int id,int userID,int hotelID,int roomID,LocalDate startDate,LocalDate endDate){
        this.id = id;
        this.userID = userID;
        this.hotelID = hotelID;
        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public int getID(){
        return id;
    }
    public int getUserID(){
        return userID;
    }
    public int getHotelID(){
        return hotelID;
    }
    public int getRoomID(){
        return roomID;
    }
    public LocalDate getStartDate(){
        return startDate;
    }
    public LocalDate getEndDate(){
        return endDate;
    }
    public BookingStatus getStatus(){
        return status;
    }
    public void setStatus(BookingStatus status){
        this.status = status;
    }
}
