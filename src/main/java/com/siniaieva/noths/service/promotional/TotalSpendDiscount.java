package com.siniaieva.noths.service.promotional;

import com.siniaieva.noths.domain.order.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This promotional rule is being used for offers category which should be applied
 * when customer spent more than some amount of money in total and gives discount for purchase.
 * In test task we have only one such a rule, however, we possibly will want to add in future
 * some more, like "spend more than £100 and get 15% of discount".
 */
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public non-sealed class TotalSpendDiscount implements PromotionalRule {

    /*
    This rule is last by default.
    */
    @Builder.Default
    PromotionalRule nextPromotionalRule = null;

    /**
     * This is class to store amount of percent off from purchase, which customer will receive if eligible
     * for this promotion.
     */
    @NonNull
    Discount discountPercentToApply;

    /**
     * This is condition to met to become eligible for this promotion. For this category of promotion
     * condition will check if total spent amount of money in order is >= threshold.
     */

    @NonNull
    PromotionCondition condition;

    @Override
    public PromotionalRule getNext() {
        return nextPromotionalRule;
    }

    @Override
    public Order apply(Order order) {
        if(condition.checkEligible(order, this)) {
            return new Order(order.getItemsInBasket(), order.getTotalPrice()
                    .multiply(discountPercentToApply.getDiscountMultiplierToApply()).setScale(2,RoundingMode.HALF_UP));
        }
        return order;
    }

    @Value
    public static class Discount {
        private static final BigDecimal HUNDRED = new BigDecimal(100);

        @NonNull
        BigDecimal discountPercent;

        public Discount(BigDecimal discountPercent) {
            validate(discountPercent);
            this.discountPercent = discountPercent;

        }

        /**
         * During calculation of total price after promotion applied it's easier to use decimal
         * interpretation of percentage of order price which still needs to be paid.
         * For example, if discount is 10%, to calculate updated total price after promotion
         * applied we would use next formula: totalPrice*0.9.
         * @return for example, for discountPercent = 10 (10% off purchase) this value would be 0.9
         */
        public BigDecimal getDiscountMultiplierToApply() {
            return HUNDRED.subtract(discountPercent).divide(HUNDRED).setScale(2);
        }

        /**
         * We need to make sure discount value is correct. As far as it's percentage it has to be strictly
         * less than 100% (as we don't want to have 100% discount) and more than 0% (can't be negative).
         * @param discount discount value.
         */
        private void validate(BigDecimal discount) {
            if(discount.compareTo(HUNDRED) >= 0) {
                throw new IllegalArgumentException("Discount percent should be strictly less than 100");
            } else if (discount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Discount percent should be more than 0");
            }
        }
    }

    @Value
    public static class TotalSpendDiscountCondition implements PromotionCondition <TotalSpendDiscount> {

        /**
         * For this category of promotion in threshold field will be minimal spent money amount
         * to become eligible for the discount.
         */
        @NonNull
        BigDecimal threshold;

        @Override
        public boolean checkEligible(Order order, TotalSpendDiscount rule) {
            return order.getTotalPrice().compareTo(threshold) >= 0;
        }

    }
}
