package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    TimeProvider timeProvider;

    @Mock
    RoomRepository roomRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @ParameterizedTest
    @NullSource
    @DisplayName("BookRoom - startTime is Null")
    void BookRoomStartTimeIsNull(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("BookRoom - endTime is Null")
    void BookRoomEndTimeIsNull(LocalDateTime endTime) {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("BookRoom - roomID is Null")
    void BookRoomRoomIdIsNull(String roomId) {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("BookRoom - endTime is before startTime")
    void BookRoomEndTimeIsBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }

    @Test
    @DisplayName("BookRoom - startTime is after endTime")
    void BookRoomStartTimeIsAfterEndTime() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().minusHours(1);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
    @DisplayName("BookRoom - Room does not exist")
    void BookRoomRoomDoesNotExist() {
        String roomId = "1";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rummet existerar inte");
    }

    @Test
    @DisplayName("BookRoom - Room is not available")
    void BookRoomRoomIsNotAvailable() {
        String roomId = "1";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = mock(Room.class);
        when(room.isAvailable(startTime, endTime)).thenReturn(false);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("BookRoom - Room is available")
    void BookRoomRoomIsAvailable() {
        String roomId = "1";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = mock(Room.class);
        when(room.isAvailable(startTime, endTime)).thenReturn(true);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("GetAvailableRooms - startTime is Null")
    void GetAvailableRoomsStartTimeIsNull(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .hasMessageContaining("Måste ange både start- och sluttid");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("GetAvailableRooms - endTime is Null")
    void GetAvailableRoomsEndTimeIsNull(LocalDateTime endTime) {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .hasMessageContaining("Måste ange både start- och sluttid");
    }

    @Test
    @DisplayName("GetAvailableRooms - endTime is before startTime")
    void GetAvailableRoomsEndTimeIsBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.minusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
    @DisplayName("GetAvailableRooms - Find all rooms")
    void GetAvailableRoomsFindAllRooms() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);
        Room room3 = mock(Room.class);
        when(room1.isAvailable(startTime, endTime)).thenReturn(true);
        when(room2.isAvailable(startTime, endTime)).thenReturn(false);
        when(room3.isAvailable(startTime, endTime)).thenReturn(true);
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));

        List<Room> result = bookingSystem.getAvailableRooms(startTime, endTime);
        assertThat(result).containsExactly(room1, room3);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("CancelBooking - bookingId is Null")
    void CancelBookingBookingIdIsNull(String bookingId) {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Boknings-id kan inte vara null");
    }

    @Test
    @DisplayName("CancelBooking - bookingId is empty")
    void CancelBookingBookingIdIsEmpty() {
        String bookingId = "1";
        when(roomRepository.findAll()).thenReturn(List.of());
        boolean result = bookingSystem.cancelBooking(bookingId);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("CancelBooking - Started or ended booking throws exception")
    void cancelBookingStartedOrEndedBookingThrowsException() {
        String bookingId = "1";
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        Booking booking = new Booking(bookingId, "1", startTime, endTime);

        Room room = mock(Room.class);
        when(room.hasBooking(bookingId)).thenReturn(true);
        when(room.getBooking(bookingId)).thenReturn(booking);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Kan inte avboka påbörjad eller avslutad bokning");
    }

    @Test
    @DisplayName("CancelBooking - Booking is canceled")
    void cancelBookingBookingIsCanceled(){
        String bookingId = "1";
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(bookingId, "1", startTime, endTime);
        Room room = mock(Room.class);

        when(room.hasBooking(bookingId)).thenReturn(true);
        when(room.getBooking(bookingId)).thenReturn(booking);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isTrue();
    }
}