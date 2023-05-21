package com.siniaieva.noths;

import com.siniaieva.noths.service.Checkout;
import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.BuyXGetLowerPrice;
import com.siniaieva.noths.service.promotional.PromotionalRule;
import com.siniaieva.noths.service.promotional.TotalSpendDiscount;

import java.math.BigDecimal;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Product travelCardHolder = new Product("001", "Travel Card Holder", BigDecimal.valueOf(9.25));
        Product personalisedCufflinks = new Product("002", "Personalised cufflinks", BigDecimal.valueOf(45.00));
        Product kidsTShirt = new Product("003", "Kids T-shirt", BigDecimal.valueOf(19.95));


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

        Checkout checkout = new Checkout(buy2TravelCardHolderPriceDropTo£8_50);
        checkout.scan(travelCardHolder); // 001
        checkout.scan(personalisedCufflinks); // 002
        checkout.scan(travelCardHolder); // 001
        checkout.scan(kidsTShirt); // 003
        Double total = checkout.total();

        System.out.println(total);
    }
}