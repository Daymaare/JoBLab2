package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    List<Item> items;

    public ShoppingCart() {
        this.items = new ArrayList<Item>();
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new NullPointerException("item är null");
        }
        this.items.add(item);
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public double applyDiscount(double discount) {
        double totalDiscount = 0.0;
        for (Item item : items) {
            totalDiscount += (item.getPrice() * discount) * item.getQuantity();
        }
        return getTotalPrice() - totalDiscount;
    }

    public void updateItemQuantity(Item item, int NewQuantity) {
        items.stream()
                .filter(i -> i.equals(item))
                .forEach(i -> i.setQuantity(NewQuantity));
    }
}
