package UI;

import model.*;
import services.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final HotelService hotelService;
    private final BookingService bookingService;
    private User currentUser = null;
    private static final String LOGIN_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

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

    private String readValidLogin(String pr, boolean isEdit, String currLogin) {
        while (true) {
            System.out.println(pr + " (или 0 для отмены)");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) return null;
            if (isEdit && input.isEmpty()) return currLogin;

            if (input.isEmpty()) {
                System.out.println("логин не может быть пустым");
                continue;
            }

            if (!input.matches(LOGIN_REGEX)) {
                System.out.println("Ошибка: Логин должен быть 3-20 симв. (латиница, цифры, '_').");
                continue;
            }

            if (!input.equalsIgnoreCase(currLogin) && userService.isLoginExist(input)) {
                System.out.println("Ошибка: Этот логин уже занят. Выберите другой.");
                continue;
            }
            return input;
        }
    }

    private String readValidPassword(String pr, boolean isEdit, String currPassword) {
        while (true) {
            System.out.println(pr + " (или 0 для отмены)");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) return null;
            if (isEdit && input.isEmpty()) return currPassword;

            if (input.isEmpty()) {
                System.out.println("Пароль не может быть пустым.");
                continue;
            }
            if (!input.matches(PASSWORD_REGEX)) {
                System.out.println("Ошибка: пароль слишком слабый. Пароль должен содержать 8-20 символов, без пробелов. Также нужно: цифру, заглавную, строчную буквы и спецсимвол (@#$%^&+=!)");
                continue;
            }
            return input;
        }
    }

    private Double readDoubleOrNull(String pr) {
        System.out.println(pr + " (или нажмите enter, чтобы пропустить)");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;
        try {
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа, параметр пропущен.");
            return null;
        }
    }

    private Integer readIntOrNull(String pr) {
        System.out.println(pr + " (или нажмите enter, чтобы пропустить)");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа, параметр пропущен.");
            return null;
        }
    }

    private String readStrOrNull(String pr) {
        System.out.println(pr + " (или нажмите enter, чтобы пропустить)");
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? null : line;
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
        String login = readValidLogin("введите новый логин: ", false, null);
        if (login == null) return;

        String password = readValidPassword("введите новый пароль: ", false, null);
        if (password == null) return;

        User newUser = userService.registerUser(login, password);
        if (newUser != null) {
            System.out.println("Регистрация прошла успешно! Теперь вы можете войти.");
        } else {
            System.out.println("Произошла непредвиденная ошибка при регистрации, sorry..");
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
        System.out.print("\nВведите город для поиска (или 0 для выхода): ");
        String city = scanner.nextLine().trim();
        if (city.equals("0") || city.isEmpty()) return;

        LocalDate startDate, endDate;
        while (true) {
            startDate = readDate("Введите дату заезда (ГГГГ-ММ-ДД): ");
            if (startDate == null) return;
            endDate = readDate("Введите дату выезда (ГГГГ-ММ-ДД): ");
            if (endDate == null) return;

            if (startDate.isBefore(LocalDate.now())) {
                System.out.println("Ошибка: нельзя бронировать в прошлом");
            } else if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                System.out.println("Ошибка: дата выезда должна быть позже заезда");
            } else {
                break;
            }
        }

        System.out.print("Хотите применить фильтры по цене/типу? (да/нет): ");
        boolean extraFilter =scanner.nextLine().trim().equalsIgnoreCase("да");
        Double minP = null, maxP = null;
        Integer cap = null;
        String type = null;

        if (extraFilter) {
            minP = readDoubleOrNull("Введите мин. цену: ");
            maxP = readDoubleOrNull("Введите макс. цену: ");
            cap = readIntOrNull("Вместимость: ");
            type =readStrOrNull("Тип номера: ");
        }

        List<Hotel> hotels = hotelService.findHotelsByCity(city, minP, maxP, cap, type, startDate, endDate, bookingService);

        if (hotels.isEmpty()) {
            System.out.println("На эти даты нет свободных номеров (или они не подходят под фильтры).");
            return;
        }
        System.out.println("ортировать по цене? (1 - от дешевых, 2 - от дорогих, enter - без сортировки): ");
        String sortCh = scanner.nextLine().trim();
        if (sortCh.equals("1")) {
            hotelService.sortByPrice(hotels, true);
        } else if (sortCh.equals("2")) {
            hotelService.sortByPrice(hotels, false);
        }

        System.out.println("\nРезультаты поиска (только свободные на ваши даты)");
        hotels.forEach(System.out::println);

        int hotelID = readInt("Введите ID отеля (0 - назад)");
        if (hotelID == 0) return;
        Hotel selectedHotel = hotelService.findHotelById(hotelID);

        System.out.println("Свободные номера в '" + selectedHotel.get_hotel_name() + "':");

        LocalDate fStart = startDate;
        LocalDate fEnd = endDate;
        Double fMin = minP;
        Double fMax = maxP;
        Integer fCap = cap;
        String fType = type;

        selectedHotel.getRooms().stream().filter(r -> (fMin == null || r.get_price_per_night() >= fMin))
                .filter(r -> (fMax == null || r.get_price_per_night() <= fMax))
                .filter(r -> (fCap == null || r.room_capacity() >= fCap))
                .filter(r -> (fType == null || r.get_type().equalsIgnoreCase(fType)))
                .filter(r -> bookingService.isRoomAvailable(r.get_room_id(), fStart, fEnd))
                .forEach(System.out::println);

        int roomID = readInt("Введите номер комнаты (0 - назад):");
        if (roomID == 0) return;
        Room selectedRoom = hotelService.findRoomById(selectedHotel, roomID);

        if (selectedRoom != null) {
            long nights = ChronoUnit.DAYS.between(startDate, endDate);
            double total = nights * selectedRoom.get_price_per_night();

            System.out.println("\n=== ПРЕДВАРИТЕЛЬНЫЙ ЧЕК ===");
            System.out.println("Отель: " + selectedHotel.get_hotel_name());
            System.out.println("Номер: " + selectedRoom.get_type());
            System.out.println("Период: " + startDate + " — " + endDate);
            System.out.println("Количество ночей: " + nights);
            System.out.println("Цена за ночь: " + selectedRoom.get_price_per_night());
            System.out.println("---------------------------");
            System.out.printf("ИТОГО К ОПЛАТЕ: %.2f\n", total);

            System.out.println("Подтвердить бронирование? (да/нет): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("да")) {
                Booking res = bookingService.createBooking(currentUser, selectedHotel, selectedRoom, startDate, endDate);
                if (res != null) {
                    System.out.println("Бронирование успешно создано! ID брони: " + res.getID());
                } else {
                    System.out.println("Ошибка бронирования");
                }
            } else {
                System.out.println("Бронирование отменено пользователем.");
            }
        }
//        long nights = ChronoUnit.DAYS.between(startDate, endDate);
//        double total = nights * selectedRoom.get_price_per_night();
//
//        System.out.println("\n=== ПРЕДВАРИТЕЛЬНЫЙ ЧЕК ===");
//        System.out.println("Отель: " + selectedHotel.get_hotel_name());
//        System.out.println("Номер: " + selectedRoom.get_type());
//        System.out.println("Период: " + startDate + " — " + endDate);
//        System.out.println("Количество ночей: " + nights);
//        System.out.println("Цена за ночь: " + selectedRoom.get_price_per_night());
//        System.out.println("---------------------------");
//        System.out.printf("ИТОГО К ОПЛАТЕ: %.2f\n", total);
//
//        System.out.println("Подтвердить бронирование? (да/нет): ");
//        String confirm = scanner.nextLine().trim().toLowerCase();
//
//        if (confirm.equals("да")) {
//            Booking res = bookingService.createBooking(currentUser, selectedHotel, selectedRoom, startDate, endDate);
//            if (res != null) {
//                System.out.println("Бронирование успешно создано! ID брони: " + res.getID());
//            } else {
//                System.out.println("Ошибка бронирования");
//            }
//        } else {
//            System.out.println("Бронирование отменено пользователем.");
//        }
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

        String newLogin = readValidLogin("Введите новый логин (или оставьте пустым, чтобы не менять): ", true, currentUser.getLogin());
        if (newLogin == null) return;

        String newPassword = readValidPassword("Введите новый пароль (или оставьте пустым, чтобы не менять): ", true, currentUser.getPassword());
        if (newPassword == null) return;

        if (newLogin.equals(currentUser.getLogin()) && newPassword.equals(currentUser.getPassword())) {
            System.out.println("Изменений нет.");
            return;
        }

        if (userService.updateUser(currentUser.getId(), newLogin, newPassword)) {
            System.out.println("Данные успешно обновлены.");
            currentUser.setLogin(newLogin);
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

        int choice = readInt("Выберите опцию: ");
        switch (choice) {
            case 1: handleViewAllBookings(); break;
            case 2: handleEditAccount(); break;
            case 3: logout(); break;
            default: System.out.println("Неверный ввод.");
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
