package com.siniaieva.noths.service;

import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.BuyXGetLowerPrice;
import com.siniaieva.noths.service.promotional.PromotionalRule;
import com.siniaieva.noths.service.promotional.TotalSpendDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckoutEndToEndTest {
    Product travelCardHolder = new Product("001", "Travel Card Holder", BigDecimal.valueOf(9.25));
    Product personalisedCufflinks = new Product("002", "Personalised cufflinks", BigDecimal.valueOf(45.00));
    Product kidsTShirt = new Product("003", "Kids T-shirt", BigDecimal.valueOf(19.95));

    private Checkout checkout;


    @BeforeEach
    public void setup() {
        PromotionalRule get10PercentOfPurchaseIfSpentMoreThan£60 = TotalSpendDiscount.builder()
                .condition(new TotalSpendDiscount.TotalSpendDiscountCondition(new BigDecimal(60)))
                .discountPercentToApply(new TotalSpendDiscount.Discount(BigDecimal.TEN))
                .build();

        PromotionalRule buy2TravelCardHolderPriceDropTo£8_50 = BuyXGetLowerPrice.builder()
                .nextPromotionalRule(get10PercentOfPurchaseIfSpentMoreThan£60)
                .promotionalProduct(travelCardHolder)
                .newProductPrice(new BigDecimal(8.5))
                .condition(new BuyXGetLowerPrice.BuyXGetLowerPriceCondition(new BigDecimal(2)))
                .build();

        checkout = new Checkout(buy2TravelCardHolderPriceDropTo£8_50);
    }
    @Test
    public void testCase1() {
        double expected = 66.78;
        checkout.scan(travelCardHolder); // 001
        checkout.scan(personalisedCufflinks); // 002
        checkout.scan(kidsTShirt); // 003
        Double total = checkout.total();
        assertEquals(expected, total);
    }


    @Test
    public void testCase2() {
        double expected = 36.95;
        checkout.scan(travelCardHolder); // 001
        checkout.scan(kidsTShirt); // 003
        checkout.scan(travelCardHolder); // 001
        Double total = checkout.total();
        assertEquals(expected, total);
    }

    @Test
    public void testCase3() {
        double expected = 73.76;
        checkout.scan(travelCardHolder); // 001
        checkout.scan(personalisedCufflinks); // 002
        checkout.scan(travelCardHolder); // 001
        checkout.scan(kidsTShirt); // 003
        Double total = checkout.total();
        assertEquals(expected, total);
    }

}

