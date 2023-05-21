package com.siniaieva.noths.service.promotional;

import com.siniaieva.noths.domain.order.Order;
import com.siniaieva.noths.domain.product.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This is category of offers, provided for concrete item. For example in tech task it's:
 * "If you buy 2 or more travel card holders then the price drops to £8.50."
 * We can easily add here more promotional rules for the same product or different.
 * For the same product we could potentially add something like "buy 10+ travel card holders
 * and price drops to £8". The fact that we have strict order of promotion rules application
 * gives us opportunity to apply offer for 10+ items after 2+ items rule.
 */
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public non-sealed class BuyXGetLowerPrice implements PromotionalRule {


    /**
     * Next promotional rule to apply in a chain after current one. If this is null, this means
     * that current rule is the last one.
     */
    PromotionalRule nextPromotionalRule;

    /**
     * Product with discount offer.
     */
    @NonNull
    @Getter
    Product promotionalProduct;
    /**
     * Reduced price of promotional product with discount provided.
     */
    @NonNull
    BigDecimal newProductPrice;
    /**
     * For this promotional rule category condition will check if quantity of promotional product
     * is >= threshold.
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
            BigDecimal totalPrice = order.getTotalPrice();
            Integer quantityOfProductInOrder = order.getItemsInBasket().get(promotionalProduct);
            /*
                calculated as (old price - new price)*quantity
             */
            BigDecimal discountAbsoluteValue = promotionalProduct.getPrice().subtract(newProductPrice)
                    .multiply(BigDecimal.valueOf(quantityOfProductInOrder)).setScale(2, RoundingMode.HALF_DOWN);
            return new Order(order.getItemsInBasket(), totalPrice.subtract(discountAbsoluteValue));
        }
        return order;
    }

    @Value
    public static class BuyXGetLowerPriceCondition implements PromotionCondition<BuyXGetLowerPrice> {
        /**
         * For this promotional rule category here would be minimal number of items customer should by to
         * get discount. For uncountable products (if any) here would be amount (in kilo, for example).
         */
        @NonNull
        BigDecimal threshold;

        @Override
        public boolean checkEligible(Order order, BuyXGetLowerPrice rule) {

            return new BigDecimal(order.getItemsInBasket().get(rule.getPromotionalProduct()))
                    .compareTo(threshold) >= 0;
        }
    }
}
