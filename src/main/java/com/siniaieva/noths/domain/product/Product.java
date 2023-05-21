package com.siniaieva.noths.domain.product;


import lombok.NonNull;

import java.math.BigDecimal;

/**
 * This is class to represent items which customer can potentially buy in market place.
 * I have only implemented fields required for this task, however in real world this class would have
 * much more fields, like Description,
 * @param code product code
 * @param productName product name
 * @param price product price
 */

public record Product (@NonNull String code, @NonNull String productName, @NonNull BigDecimal price) {

    public BigDecimal getPrice() {
        return price;
    }
}
