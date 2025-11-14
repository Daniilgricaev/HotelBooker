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
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice){
                case 1: handleLogin();break;
                case 2: handleRegister();break;
                case 3: System.out.println("До свидания!"); System.exit(0); break;
                default: System.out.println("Неверный ввод.");
            }
        }catch(NumberFormatException e){
            System.out.println("Ошибка: введите число.");
        }
    }
    private void handleSearchAndBook(){
        //in process
    }
    //User menu
    private void showUserMenu(){
        System.out.println("\n--- Главное меню (" + currentUser.getLogin() + ") ---");
        System.out.println("1. Поиск и бронирование отелей");
        System.out.println("2. Мои бронирования");
        System.out.println("3. Изменить данные аккаунта");
        System.out.println("4. Выйти из аккаунта");
        System.out.print("Выберите опцию: ");

        // in process
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
