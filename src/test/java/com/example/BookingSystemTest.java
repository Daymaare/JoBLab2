package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        BookingSystem bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", null, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    };

    @Test
    void BookRoomEndTimeIsNull() {
        BookingSystem bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    };

}