package com.siniaieva.noths.domain.order;

import com.siniaieva.noths.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    private Order.OrderBuilder orderBuilder;

    private final Product product1 = new Product("001", "Test1", BigDecimal.ONE);
    private final Product product2 = new Product("002", "Test2", BigDecimal.TEN);
    private final Map<Product, Integer> items = new HashMap<>();

    @BeforeEach
    public void setup() {
        orderBuilder = new Order.OrderBuilder();
        items.put(product1, 1);
        items.put(product2, 1);
    }

    @Test
    public void testEmptyOrderWithZeroTotalPriceIsCreated() {
        Order order = orderBuilder.build();
        assertEquals(BigDecimal.ZERO, order.getTotalPrice());
        assertEquals(Collections.EMPTY_MAP, order.getItemsInBasket());
    }

    @Test
    public void testTotalPriceIsCalculatedCorrectly() {
        orderBuilder.addItem(product1);
        orderBuilder.addItem(product2);

        Order order = orderBuilder.build();
        assertEquals(new BigDecimal(11), order.getTotalPrice());
        assertEquals(items, order.getItemsInBasket());
    }

    @Test
    public void testItemQuantityIsCorrect() {
        orderBuilder.addItem(product1);
        orderBuilder.addItem(product1);
        orderBuilder.addItem(product2);
        orderBuilder.addItem(product2);
        orderBuilder.addItem(product2);
        items.put(product1, 2);
        items.put(product2, 3);

        Order order = orderBuilder.build();
        assertEquals(new BigDecimal(32), order.getTotalPrice());
        assertEquals(items, order.getItemsInBasket());
    }

    @Test
    public void testDeleteItemWorksCorrectly() {
        orderBuilder.addItem(product1);
        orderBuilder.addItem(product2);

        orderBuilder.deleteItem(product1);

        Order order = orderBuilder.build();
        assertEquals(new BigDecimal(10), order.getTotalPrice());
        Map<Product, Integer> itemsInBasket = order.getItemsInBasket();
        assertFalse(itemsInBasket.containsKey(product1));
        assertTrue(itemsInBasket.containsKey(product2));
    }

}
