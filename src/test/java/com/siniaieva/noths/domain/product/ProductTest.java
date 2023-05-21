package com.siniaieva.noths.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    public void testProductCantBeCreatedWithoutProductCode() {
        assertThrows(NullPointerException.class, () -> new Product(null, "name", BigDecimal.TEN));
    }

    @Test
    public void testProductCantBeCreatedWithoutProductName() {
        assertThrows(NullPointerException.class, () -> new Product("code", null, BigDecimal.TEN));
    }

    @Test
    public void testProductCantBeCreatedWithoutPrice() {
        assertThrows(NullPointerException.class, () -> new Product("code", "name", null));
    }
}
