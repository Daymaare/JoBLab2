package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void BookRoomStartTimeIsNull(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @ParameterizedTest
    @NullSource
    void BookRoomEndTimeIsNull(LocalDateTime endTime) {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @ParameterizedTest
    @NullSource
    void BookRoomRoomIdIsNull(String roomId) {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void BookRoomTimeIsBefore() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }

    @Test
    void BookRoomTimeIsAfter() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().minusHours(1);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
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
    void GetAvailableRoomsStartTimeIsNull(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime)).hasMessageContaining("Måste ange både start- och sluttid");
    }
}