import services.*;
import storage.Storage;
import storage.FileStorage;
import UI.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        Storage storage = new FileStorage();
        UserService userService = new UserService(storage);
        HotelService hotelService = new HotelService(storage);
        BookingService bookingService = new BookingService(storage);

        ConsoleApp app = new ConsoleApp(userService, hotelService, bookingService);
        app.run();

    }
}
