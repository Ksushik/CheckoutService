package com.siniaieva.noths.service;

import com.siniaieva.noths.domain.order.Order;
import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.PromotionalRule;


public class Checkout {
    private PromotionalRule promotionalRule;
    private Order.OrderBuilder orderBuilder;


    public Checkout(PromotionalRule promotionalRules) {
        this.promotionalRule = promotionalRules;
        this.orderBuilder = new Order.OrderBuilder();
    }

    /**
     * This constructor is needed for test purposes only.
     */
    protected Checkout(PromotionalRule promotionalRules, Order.OrderBuilder builder) {
        this.promotionalRule = promotionalRules;
        this.orderBuilder = builder;
    }

    public void scan(Product item) {
        orderBuilder.addItem(item);
    }

    public Double total() {
        Order order = orderBuilder.build();
        while (promotionalRule != null) {
            order = promotionalRule.apply(order);
            promotionalRule = promotionalRule.getNext();
        }
        return order.getTotalPrice().doubleValue();
    }

}
