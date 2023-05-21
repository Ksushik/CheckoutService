package com.siniaieva.noths.service.promotional;

import com.siniaieva.noths.domain.order.Order;
import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.BuyXGetLowerPrice;
import com.siniaieva.noths.service.promotional.PromotionalRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BuyXGetLowerPriceTest {

    @Mock
    private Order orderMock;

    @Mock
    private PromotionalRule.PromotionCondition<BuyXGetLowerPrice> promotionConditionMock;

    private Product promotionalProduct = new Product("001", "Test",
            new BigDecimal(20).setScale(2, RoundingMode.HALF_UP));

    @Mock
    private Map<Product, Integer> items;

    private BuyXGetLowerPrice buyXGetLowerPricePromotionalRule;

    @BeforeEach
    public void setup() {
        buyXGetLowerPricePromotionalRule = BuyXGetLowerPrice.builder()
                .condition(promotionConditionMock)
                .promotionalProduct(promotionalProduct)
                .newProductPrice(BigDecimal.TEN)
                .build();
    }

    @Test
    public void testImpossibleCreatePromotionalRuleWithoutAnyRequiredField() {
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder().build());
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder()
                .condition(promotionConditionMock)
                .build());
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder()
                .promotionalProduct(promotionalProduct)
                .build());
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder()
                .condition(promotionConditionMock)
                .promotionalProduct(promotionalProduct)
                .build());
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder()
                .newProductPrice(BigDecimal.TEN)
                .promotionalProduct(promotionalProduct)
                .build());
        assertThrows(NullPointerException.class, () -> BuyXGetLowerPrice.builder()
                .condition(promotionConditionMock)
                .newProductPrice(BigDecimal.TEN)
                .build());
    }

    @Test
    public void testPromotionalRuleConditionCanNotBeCreatedWithNullThreshold() {
        assertThrows(NullPointerException.class, () -> new BuyXGetLowerPrice.BuyXGetLowerPriceCondition(null));
    }


    @Test
    public void testPromotionIsNotAppliedIfOrderNotEligible() {
        when(promotionConditionMock.checkEligible(orderMock, buyXGetLowerPricePromotionalRule))
                .thenReturn(false);
        Order order = buyXGetLowerPricePromotionalRule.apply(orderMock);
        // if condition not met, promotionalRule should return the same instance of order without any change
        assertTrue(orderMock == order);
        assertEquals(orderMock, order);
    }

    @Test
    public void testPromotionIsAppliedIfOrderIsEligible() {
        when(promotionConditionMock.checkEligible(orderMock, buyXGetLowerPricePromotionalRule))
                .thenReturn(true);
        when(orderMock.getTotalPrice()).thenReturn(new BigDecimal(100));
        when(orderMock.getItemsInBasket()).thenReturn(items);
        when(items.get(promotionalProduct)).thenReturn(3);
        Order order = buyXGetLowerPricePromotionalRule.apply(orderMock);
        assertFalse(orderMock == order);
        assertNotEquals(orderMock, order);

        BigDecimal expectedTotalPrice = BigDecimal.valueOf(70).setScale(2);
        // old price of product was 20 and there are 3 products in the order. New price is 10, so total order price
        // should be decreased by £30, which is 100-30=70
        assertEquals(expectedTotalPrice, order.getTotalPrice());
    }
}
