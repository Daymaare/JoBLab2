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
    private ShoppingCart cart;

    @Test
    void shoppingCartAddItemIncreasesShoppingCart() {
        cart.addItem(item);

        assertThat(cart.getItems()).hasSize(1);
    }

    @Test
    void shoppingCartRemoveItemDecreasesShoppingCart() {
        cart.addItem(item);
        cart.removeItem(item);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void shoppingCartCalculatingPriceInShoppingCartWorks() {
        Item apple = mock(Item.class);
        Item banana = mock(Item.class);

        when(apple.getPrice()).thenReturn(1.0);
        when(apple.getQuantity()).thenReturn(2);
        when(banana.getPrice()).thenReturn(2.5);
        when(banana.getQuantity()).thenReturn(3);

        cart.addItem(apple);
        cart.addItem(banana);

        double totalPrice = cart.getTotalPrice();

        assertThat(totalPrice).isEqualTo(9.5);
    }

}