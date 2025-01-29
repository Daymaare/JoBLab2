package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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

    @Test
    void BookRoomStartTimeIsNull() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", null, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void BookRoomEndTimeIsNull() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void BookRoomRoomIdIsNull() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom(null, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void BookRoomTimeIsBefore() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Kan inte boka tid i dåtid");
    }
}