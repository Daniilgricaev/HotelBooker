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

    private int readInt(String pr) {
        while (true) {
            System.out.println(pr);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("введите целое число");
            }
        }
    }

    private LocalDate readDate(String pr) {
        while (true) {
            System.out.println(pr);
            String line = scanner.nextLine().trim();
            if (line.equals("0")) return null;
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("неверный формат даты, используйте ГГГГ-ММ-ДД");
                System.out.println("введите число 0 для отмены");
            }
        }
    }

    private String readString(String pr) {
        while (true) {
            System.out.println(pr);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("поле не может быть пустым");
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Меню авторизации ---");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выход из программы");

        int choice = readInt("Выберите опцию: ");
        switch (choice) {
            case 1: handleLogin(); break;
            case 2: handleRegister(); break;
            case 3:
                System.out.println("До свидания!");
                System.exit(0);
                break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void handleLogin() {
        while (true) {
            System.out.println("введите 0 в поле логина для возврата");
            String username = readString("Введите логин: ");

            if (username.equals("0")) return;

            String password = readString("Введите пароль: ");

            currentUser = userService.authenticate(username, password);
            if (currentUser == null) {
                System.out.println("Ошибка: неверный логин или пароль.");
            } else {
                System.out.println("Добро пожаловать, " + currentUser.getLogin() + "!");
                return;
            }
        }
    }

    private void handleRegister() {
        while (true) {
            System.out.println("введите 0 в поле логина для возврата");
            String username = readString("Введите новый логин: ");

            if (username.equals("0")) return;

            String password = readString("Введите новый пароль: ");

            User newUser = userService.registerUser(username, password);
            if (newUser != null) {
                System.out.println("Регистрация прошла успешно! Теперь вы можете войти.");
                return;
            } else {
                System.out.println("Ошибка: пользователь с таким логином уже существует.");
            }
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

        int choice = readInt("Выберите опцию: ");
        switch (choice) {
            case 1: handleSearchAndBook(); break;
            case 2: handleViewMyBookings(); break;
            case 3: handleEditAccount(); break;
            case 4: logout(); break;
            default: System.out.println("Неверный ввод.");
        }
    }

    private void handleSearchAndBook() {
        String city;
        List<Hotel> hotels;
        while (true) {
            System.out.print("\nВведите город для поиска (или 0 для выхода): ");
            city = scanner.nextLine().trim();
            if (city.equals("0")) return;
            if (city.isEmpty()) continue;

            hotels = hotelService.findHotelsByCity(city);
            if (hotels.isEmpty()) {
                System.out.println("Отели в городе '" + city + "' не найдены.");
                return;
            } else {
                break;
            }
        }

        System.out.println("Найденные отели:");
        hotels.forEach(System.out::println);

        Hotel selectedHotel = null;
        while (selectedHotel == null) {
            int hotelID = readInt("Введите ID отеля для просмотра номеров (0 - назад)");
            if (hotelID == 0) return;

            selectedHotel = hotelService.findHotelById(hotelID);
            if (selectedHotel == null || !selectedHotel.get_city_hotel().toLowerCase().contains(city.toLowerCase())) {
                System.out.println("Отель с таким ID не найден в результатах поиска.");
                selectedHotel = null;
            }
        }

        System.out.println("Доступные номера в отеле '" + selectedHotel.get_hotel_name() + "':");
        selectedHotel.getRooms().forEach(System.out::println);

        Room selectedRoom = null;
        while (selectedRoom == null) {
            int roomID = readInt("Введите номер комнаты для бронирования (0 - назад)");
            if (roomID == 0) return;

            selectedRoom = hotelService.findRoomById(selectedHotel, roomID);
            if (selectedRoom == null) {
                System.out.println("Номер с таким ID не найден в этом отеле.");
            }
        }

        LocalDate startDate, endDate;
        while (true) {
            startDate = readDate("Введите дату заезда (ГГГГ-ММ-ДД): ");
            if (startDate == null) return;

            endDate = readDate("Введите дату выезда (ГГГГ-ММ-ДД): ");
            if (endDate == null) return;

            if (startDate.isBefore(LocalDate.now())) {
                System.out.println("Ошибка: нельзя бронировать в прошлом");
            } else if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                System.out.println("Ошибка: дата выезда должна быть позже даты заезда!");
            } else {
                break;
            }
        }

        Booking res = bookingService.createBooking(currentUser, selectedHotel, selectedRoom, startDate, endDate);
        if (res != null) {
            System.out.println("Бронирование успешно создано! ID брони: " + res.getID());
        } else {
            System.out.println("Ошибка бронирования: Номер занят на выбранные даты или произошла другая ошибка.");
        }
        // После бронирования (успешного или нет) нажимаем Enter, чтобы вернуться
        System.out.println("Нажмите Enter, чтобы вернуться в меню...");
        scanner.nextLine();

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
//            System.out.printf("ID: %d, Отель: %s, Даты: %s - %s, Статус: %s\n", b.getHotelID(),
//                    hotelName,
//                    b.getStartDate(),
//                    b.getEndDate(),
//                    b.getStatus());
            System.out.printf("Бронь #%d | Отель: %s (ID отеля: %d), Даты: %s - %s, Статус: %s\n",
                    b.getID(),
                    hotelName,
                    b.getHotelID(),
                    b.getStartDate(),
                    b.getEndDate(),
                    b.getStatus());
        });
        System.out.print("\nХотите отменить бронирование? (да/нет): ");
        if (scanner.nextLine().equalsIgnoreCase("да")) {
            int bookingId = readInt("Введите ID бронирования для отмены (0 - назад): ");
            if (bookingId == 0) return;

            if (bookingService.cancelBooking(currentUser, bookingId)) {
                System.out.println("Бронирование #" + bookingId + " успешно отменено.");
            } else {
                System.out.println("Не удалось найти бронирование с таким ID, принадлежащее вам.");
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
