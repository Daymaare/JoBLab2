package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartTest {

    @Mock
    private Item item;

    @InjectMocks
    private ShoppingCart shoppingCart;

    @Test
    void shoppingCartAddItemIncreasesShoppingCart() {
        when(item.getName()).thenReturn("Apple");
        when(item.getPrice()).thenReturn(1.0);
        when(item.getQuantity()).thenReturn(1);

        cart.addItem(item);

        assertThat(cart.getItems()).hasSize(1);
    }

}