package UI;

import model.*;
import services.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    Scanner scanner = new Scanner(System.in);
    UserService userService;
    HotelService hotelService;
    BookingService bookingService;
    User currentUser = null;

    public ConsoleApp(UserService us, HotelService hs, BookingService bs){
        this.userService = us;
        this.hotelService = hs;
        this.bookingService = bs;
    }

    private void handleRegister(){
        System.out.print("Введите новый логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите новый пароль: ");
        String password = scanner.nextLine();

        User newUser = userService.registerUser(username,password);
        if(newUser != null){
            System.out.println("Регистрация прошла успешно! Теперь вы можете войти.");

        }else{
            System.out.println("Ошибка: пользователь с таким логином уже существует.");
        }
    }

    private void handleLogin(){
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        currentUser = userService.authenticate(username, password);
        if(currentUser == null){
            System.out.println("Ошибка: неверный логин или пароль.");
        }else{
            System.out.println("Добро пожаловать, " + currentUser.getLogin() + "!");
        }
    }

    private void showAuthMenu(){
        System.out.println("\n Меню авторизации ");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выход из программы");
        System.out.print("Выберите опцию: ");
        try{
            int ch = Integer.parseInt(scanner.nextLine());
            switch (ch){
                case 1: handleLogin();break;
                case 2: handleRegister();break;
                case 3: System.out.println("До свидания!"); System.exit(0); break;
                default: System.out.println("Неверный ввод.");
            }
        }catch(NumberFormatException e){
            System.out.println("Ошибка: введите число.");
        }
    }
    //User menu
    private void showUserMenu(){
        System.out.println("\n--- Главное меню (" + currentUser.getLogin() + ") ---");
        System.out.println("1. Поиск и бронирование отелей");
        System.out.println("2. Мои бронирования");
        System.out.println("3. Изменить данные аккаунта");
        System.out.println("4. Выйти из аккаунта");
        System.out.print("Выберите опцию: ");
        try{
            int ch = Integer.parseInt(scanner.nextLine());
            switch (ch){
                case 1: handleSearchAndBook(); break;
                case 2: handleViewMyBookings(); break;
                case 3: handleEditAccount(); break;
                case 4: logout(); break;
                default: System.out.println("Неверный ввод.");
            }
        }catch(NumberFormatException e){
            System.out.println("Ошибка: введите число.");
        }
        }
        private void handleSearchAndBook(){
            System.out.print("\nВведите город для поиска: ");
            String city = scanner.nextLine();
            List<Hotel>hotels = hotelService.findHotelsByCity(city);
            if(hotels.isEmpty()){
                System.out.println("Отели в городе '" + city + "' не найдены.");
                return;
            }
            System.out.println("Найденные отели:");
            hotels.forEach(System.out::println);

            System.out.println("Введите ID отеля для просмотра номеров: ");
            try{
                int hotelID = Integer.parseInt(scanner.nextLine());
                Hotel selectedHotel = hotelService.findHotelById(hotelID);

                if(selectedHotel == null || selectedHotel.get_city_hotel().equalsIgnoreCase(city)){
                    System.out.println("Отель с таким ID не найден в результатах поиска.");
                    return;
                }
                System.out.println("Доступные номера в отеле " + selectedHotel.get_hotel_name() + ":");
                selectedHotel.get_about_hotel().forEach(System.out::println);
                System.out.print("Введите ID номера для бронирования: ");
                int roomId = Integer.parseInt(scanner.nextLine());
                Room selectedRoom = hotelService.findRoomById(selectedHotel,roomId);
                if(selectedRoom == null){
                    System.out.println("Номер с таким ID не найден.");
                    return;
                }
                System.out.print("Введите дату заезда (ГГГГ-ММ-ДД): ");
                LocalDate startDate = LocalDate.parse(scanner.nextLine());
                System.out.print("Введите дату выезда (ГГГГ-ММ-ДД): ");
                LocalDate endDate = LocalDate.parse(scanner.nextLine());

                bookingService.cancelBooking(currentUser, selectedHotel , selectedRoom,startDate,endDate);
            }
        }


    public void Run(){
        System.out.println("Добро пожаловать в HotelBooker!");
        while(true){
            if(currentUser == null){
                showAuthMenu();
            }else{
                if(currentUser.getRole()==Role.ADMIN){

                }
            }
        }
    }

}
