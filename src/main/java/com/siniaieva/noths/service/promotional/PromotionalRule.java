package com.siniaieva.noths.service.promotional;

import com.siniaieva.noths.domain.order.Order;

/**
 * As far as Promotional rules have different nature and structure, I decided to use
 * promotional rule categories, which can have some common structure (so can be used in DB).
 * At this moment we only have two categories, but it will be easy to add more.
 */
public sealed interface PromotionalRule permits TotalSpendDiscount, BuyXGetLowerPrice {

    /**
     * This is needed to make sure promotional rules will be implemented in strict order.
     * @return next promotional rule to apply. If it's null, this means, current rule is the last one.
     */
    PromotionalRule getNext();

    /**
     * This is main method of promotion rule class. In this method will be discount logic implemented.
     * PromotionalRule still returns Order object, assuming there could be some bogo offers, like
     * "buy 2 get 1 free" or just some free samples for order with total price >= X, which means
     * Promotional rules can potentially change not only total price and price of some items, but
     * also items list itself as well.
     * @param order takes existing Order instance as an input.
     * @return new Order instance with updated total price after promotion rule application.
     */
    Order apply(Order order);

    /**
     * There are certain conditions which should met to get some promotions, this class is needed
     * to store this condition and check if order met condition during checkout.
     */
    @FunctionalInterface
    interface PromotionCondition <P extends PromotionalRule> {
         boolean checkEligible(Order order, P rule);
    }
}
