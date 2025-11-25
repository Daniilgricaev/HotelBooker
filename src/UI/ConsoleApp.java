package UI;

import model.*;
import services.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final HotelService hotelService;
    private final BookingService bookingService;
    private User currentUser = null;

    public ConsoleApp(UserService us, HotelService hs, BookingService bs) {
        this.userService = us;
        this.hotelService = hs;
        this.bookingService = bs;
    }

    public void run() {
        System.out.println("Добро пожаловать в HotelBooker!");
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                if (currentUser.getRole() == Role.ADMIN) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Меню авторизации ---");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выход из программы");
        System.out.print("Выберите опцию: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: handleLogin(); break;
                case 2: handleRegister(); break;
                case 3: System.out.println("До свидания!"); System.exit(0); break;
                default: System.out.println("Неверный ввод.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число.");
        }
    }

    private void handleLogin() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        currentUser = userService.authenticate(username, password);
        if (currentUser == null) {
            System.out.println("Ошибка: неверный логин или пароль.");
        } else {
            System.out.println("Добро пожаловать, " + currentUser.getLogin() + "!");
        }
    }

    private void handleRegister() {
        System.out.print("Введите новый логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите новый пароль: ");
        String password = scanner.nextLine();

        User newUser = userService.registerUser(username, password);
        if (newUser != null) {
            System.out.println("Регистрация прошла успешно! Теперь вы можете войти.");
        } else {
            System.out.println("Ошибка: пользователь с таким логином уже существует.");
        }
    }

    private void logout() {
        System.out.println("Выход из аккаунта...");
        currentUser = null;
    }

    // --- МЕНЮ Юзера ---
    private void showUserMenu() {
        System.out.println("\n--- Главное меню (" + currentUser.getLogin() + ") ---");
        System.out.println("1. Поиск и бронирование отелей");
        System.out.println("2. Мои бронирования");
        System.out.println("3. Изменить данные аккаунта");
        System.out.println("4. Выйти из аккаунта");
        System.out.print("Выберите опцию: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: handleSearchAndBook(); break;
                case 2: handleViewMyBookings(); break;
                case 3: handleEditAccount(); break;
                case 4: logout(); break;
                default: System.out.println("Неверный ввод.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число.");
        }
    }

    private void handleSearchAndBook() {
        System.out.print("\nВведите город для поиска: ");
        String city = scanner.nextLine();
        List<Hotel> hotels = hotelService.findHotelsByCity(city);

        if (hotels.isEmpty()) {
            System.out.println("Отели в городе '" + city + "' не найдены.");
            return;
        }
        System.out.println("Найденные отели:");
        hotels.forEach(System.out::println);
        System.out.print("Введите ID отеля для просмотра номеров: ");

        try {
            int hotelId = Integer.parseInt(scanner.nextLine());
            Hotel selectedHotel = hotelService.findHotelById(hotelId);
            if (selectedHotel == null) {
                System.out.println("Отель с ID " + hotelId + " не найден.");
                return;
            }
            if (!selectedHotel.get_city_hotel().equalsIgnoreCase(city)) {
                System.out.println("Отель с таким ID не найден в результатах поиска.");
                return;
            }
            System.out.println("Доступные номера в отеле '" + selectedHotel.get_hotel_name() + "':");
            selectedHotel.getRooms().forEach(System.out::println);

            System.out.print("Введите ID номера для бронирования: ");
            int roomId = Integer.parseInt(scanner.nextLine());
            Room selectedRoom = hotelService.findRoomById(selectedHotel, roomId);

            if (selectedRoom == null) {
                System.out.println("Номер с таким ID не найден.");
                return;
            }
            System.out.print("Введите дату заезда (ГГГГ-ММ-ДД): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());
            System.out.print("Введите дату выезда (ГГГГ-ММ-ДД): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            // проверка корректности дат
            if (endDate.isBefore(startDate) || startDate.isBefore(LocalDate.now())) {
                System.out.println("Ошибка: некорректные даты бронирования.");
                return;
            }

            bookingService.createBooking(currentUser, selectedHotel, selectedRoom, startDate, endDate);
            System.out.println("Бронирование успешно создано!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом.");
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД.");
        }
    }

    private void handleViewMyBookings() {
        List<Booking> bookings = bookingService.findBookingByUser(currentUser);
        if (bookings.isEmpty()) {
            System.out.println("\nУ вас нет активных или архивных бронирований.");
            return;
        }
        System.out.println("\n--- Мои бронирования ---");
        bookings.forEach(b -> {
            Hotel h = hotelService.findHotelById(b.getHotelID());
            String hotelName = (h != null) ? h.get_hotel_name() : "Отель не найден";
            System.out.printf("ID: %d, Отель: %s, Даты: %s - %s, Статус: %s\n", b.getHotelID(),
                    hotelName,
                    b.getStartDate(),
                    b.getEndDate(),
                    b.getStatus());
        });
        System.out.print("\nХотите отменить бронирование? (да/нет): ");
        if (scanner.nextLine().equalsIgnoreCase("да")) {
            System.out.print("Введите ID бронирования для отмены: ");
            try {
                int bookingId = Integer.parseInt(scanner.nextLine());
                if (bookingService.cancelBooking(currentUser, bookingId)) {
                    System.out.println("Бронирование #" + bookingId + " успешно отменено.");
                } else {
                    System.out.println("Не удалось найти бронирование с таким ID, принадлежащее вам.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть числом.");
            }
        }
    }

    private void handleEditAccount() {
        System.out.println("\n--- Редактирование аккаунта ---");
        System.out.println("Текущий логин: " + currentUser.getLogin());
        System.out.print("Введите новый логин (или оставьте пустым, чтобы не менять): ");
        String newUsername = scanner.nextLine();
        if (newUsername.isEmpty()) {
            newUsername = currentUser.getLogin();
        }

        System.out.print("Введите новый пароль (или оставьте пустым, чтобы не менять): ");
        String newPassword = scanner.nextLine();
        if (newPassword.isEmpty()) {
            newPassword = currentUser.getPassword();
        }

        if (userService.updateUser(currentUser.getId(), newUsername, newPassword)) {
            System.out.println("Данные успешно обновлены.");
            // Обновляем данные текущего пользователя в сессии
            currentUser.setLogin(newUsername);
            currentUser.setPassword(newPassword);
        } else {
            System.out.println("Не удалось обновить данные.");
        }
    }

    // --- МЕНЮ АДМИНA---
    private void showAdminMenu() {
        System.out.println("\n--- Панель администратора (" + currentUser.getLogin() + ") ---");
        System.out.println("1. Просмотреть все бронирования");
        System.out.println("2. Редактировать аккаунт");
        System.out.println("3. Выйти из аккаунта");
        System.out.print("Выберите опцию: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: handleViewAllBookings(); break;
                case 2: handleEditAccount(); break;
                case 3: logout(); break;
                default: System.out.println("Неверный ввод.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число.");
        }
    }

    private void handleViewAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("\nВ системе пока нет бронирований.");
            return;
        }

        System.out.println("\n--- Все бронирования в системе ---");
        bookings.forEach(b -> {
            Hotel h = hotelService.findHotelById(b.getHotelID());
            String hotelName = (h != null) ? h.get_hotel_name() : "Отель не найден";
            System.out.printf("ID: %d, ID Пользователя: %d, Отель: %s, Даты: %s - %s, Статус: %s\n",
                    b.getHotelID(), b.getUserID(), hotelName, b.getStartDate(), b.getEndDate(), b.getStatus());
        });
    }
}
