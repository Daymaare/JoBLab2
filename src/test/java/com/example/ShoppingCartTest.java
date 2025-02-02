package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartTest {

    @Mock
    private Item item;

    @InjectMocks
    private ShoppingCart cart;

    @Test
    @DisplayName("ShoppingCart - Add item increases shopping cart")
    void shoppingCartAddItemIncreasesShoppingCart() {
        cart.addItem(item);
        assertThat(cart.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("ShoppingCart - Remove item decreases shopping cart")
    void shoppingCartRemoveItemDecreasesShoppingCart() {
        cart.addItem(item);
        cart.removeItem(item);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("ShoppingCart - Calculating total price works correctly")
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

    @Test
    @DisplayName("ShoppingCart - Applying discount reduces total price")
    void shoppingCartApplyingDiscountReducesPrice() {
        Item banana = mock(Item.class);
        Item apple = mock(Item.class);

        when(banana.getPrice()).thenReturn(10.0);
        when(banana.getQuantity()).thenReturn(1);
        when(apple.getPrice()).thenReturn(20.0);
        when(apple.getQuantity()).thenReturn(2);

        cart.addItem(banana);
        cart.addItem(apple);
        double discount = cart.applyDiscount(0.5);
        assertThat(discount).isEqualTo(25);
    }

    @Test
    @DisplayName("ShoppingCart - Updating item quantity changes total price")
    void shoppingCartUpdatingItemQuantityChangesTotalPrice() {
        Item apple = mock(Item.class);

        when(apple.getPrice()).thenReturn(2.0);
        when(apple.getQuantity()).thenReturn(2);

        cart.addItem(apple);
        cart.updateItemQuantity(apple, 5);

        when(apple.getQuantity()).thenReturn(6);

        double totalPrice = cart.getTotalPrice();
        assertThat(totalPrice).isEqualTo(12.0);
    }

    @Test
    @DisplayName("ShoppingCart - Adding null item throws NullPointerException")
    void shoppingCartAddingNullItemThrowsNullPointerException() {
        assertThatThrownBy(() -> cart.addItem(null))
                .hasMessageContaining("item är null");
    }

    @Test
    @DisplayName("ShoppingCart - Removing null item throws NullPointerException")
    void shoppingCartRemovingNullItemThrowsNullPointerException() {
        assertThatThrownBy(() -> cart.removeItem(null))
                .hasMessageContaining("item är null");
    }

    @Test
    @DisplayName("ShoppingCart - Updating null item throws NullPointerException")
    void shoppingCartUpdatingNullItemThrowsNullPointerException() {
        assertThatThrownBy(() -> cart.updateItemQuantity(null, 5))
                .hasMessageContaining("item är null");
    }

    @Test
    @DisplayName("ShoppingCart - Updating quantity to negative value throws InvalidQuantityException")
    void shoppingCartUpdatingQuantityToNegativeValueThrowsInvalidQuantityException() {
        assertThatThrownBy(() -> cart.updateItemQuantity(item, -1))
                .hasMessageContaining("Antal kan ej uppdateras till negativt tal");
    }
}
