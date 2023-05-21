package com.siniaieva.noths.domain.order;

import com.siniaieva.noths.domain.product.Product;
import lombok.Value;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Value
public class Order {

    Map<Product, Integer> itemsInBasket;
    BigDecimal totalPrice;

    public static class OrderBuilder {
        Map<Product, Integer> itemsInBasket;
        BigDecimal totalPrice;
        public OrderBuilder() {
            itemsInBasket = new HashMap<>();
            totalPrice =  new BigDecimal(0);
        }

        public void addItem(Product product) {
            // if product is already in hash map, we need to increase quantity
            itemsInBasket.computeIfPresent(product, (k, v) -> v + 1);
            // if this is new item, add with quantity 1.
            itemsInBasket.putIfAbsent(product, 1);
            // calculate total price
            totalPrice = totalPrice.add(product.price());
        }

        public void deleteItem(Product product) {
            // if product is already in hash map, we need to decrease quantity
            Integer quantity = itemsInBasket.computeIfPresent(product, (k, v) -> v - 1);
            // if quantity is 0, we need to remove item from the map
            if (quantity!= null && quantity == 0) {
                itemsInBasket.remove(product);
            }
            if (quantity!= null) {
                // calculate total price
                totalPrice = totalPrice.subtract(product.price());
            }
        }

        public Order build() {
            return new Order(itemsInBasket, totalPrice);
        }
    }


}
