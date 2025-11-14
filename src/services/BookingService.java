package services;

import model.*;
import storage.FileStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookingService {

    private FileStorage storage;
    private AtomicInteger bookingCounter;

    public BookingService(FileStorage storage) {
        this.storage = storage;

        // Инициализируем счетчик на основе существующих бронирований
        this.bookingCounter = new AtomicInteger(
                storage.getBookings().stream()
                        .mapToInt(Booking::getID)
                        .max()
                        .orElse(0)
        );
    }

    public Booking createBooking(User user, Hotel hotel, Room room, LocalDate start, LocalDate end) {
        // Валидация входных параметров
        if (user == null || hotel == null || room == null) {
            throw new IllegalArgumentException("User, hotel and room cannot be null");
        }

        if (start == null || end == null || start.isAfter(end) || start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid dates");
        }

        // Проверка доступности номера
        if (!isRoomAvailable(room.get_room_id(), start, end)) {
            throw new IllegalStateException("Room is not available for selected dates");
        }

        // Создание новой брони
        Booking newBooking = new Booking(
                bookingCounter.incrementAndGet(),
                user.getId(),
                hotel.get_id_hotel(),
                room.get_room_id(),
                start,
                end
        );

        // Сохранение брони
        storage.getBookings().add(newBooking);
        storage.saveBookings(); // Сохраняем изменения

        return newBooking;
    }

    private boolean isRoomAvailable(int roomId, LocalDate start, LocalDate end) {
        return storage.getBookings().stream()
                .filter(booking -> booking.getRoomID() == roomId &&
                        booking.getStatus() != BookingStatus.CANCELLED)
                .noneMatch(existingBooking ->
                        start.isBefore(existingBooking.getEndDate()) &&
                                end.isAfter(existingBooking.getStartDate())
                );
    }

    public List<Booking> findBookingByUser(User user) {
        if (user == null) {
            return List.of();
        }

        return storage.getBookings().stream()
                .filter(booking -> booking.getUserID() == user.getId())
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return storage.getBookings();
    }

    public boolean cancelBooking(User user, int bookingId) {
        boolean cancelled = storage.getBookings().stream()
                .filter(booking -> booking.getID() == bookingId &&
                        booking.getUserID() == user.getId())
                .findFirst()
                .map(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    storage.saveBookings(); // Сохраняем изменения
                    return true;
                })
                .orElse(false);

        return cancelled;
    }
}