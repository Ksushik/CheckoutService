package com.siniaieva.noths.service.promotional;

import com.siniaieva.noths.domain.order.Order;
import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.PromotionalRule;
import com.siniaieva.noths.service.promotional.TotalSpendDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TotalSpendDiscountTest {

    @Mock
    private Order orderMock;

    @Mock
    private PromotionalRule.PromotionCondition<TotalSpendDiscount> promotionConditionMock;

    @Mock
    private TotalSpendDiscount.Discount discountMock;

    @Mock
    private Map<Product, Integer> items;

    private TotalSpendDiscount totalSpendDiscountPromotionalRule;

    @BeforeEach
    public void setup() {
        totalSpendDiscountPromotionalRule = TotalSpendDiscount.builder()
                .condition(promotionConditionMock)
                .discountPercentToApply(discountMock)
                .build();
    }

    @Test
    public void testImpossibleCreatePromotionalRuleWithoutAnyRequiredField() {
        assertThrows(NullPointerException.class, () -> TotalSpendDiscount.builder().build());
        assertThrows(NullPointerException.class, () -> TotalSpendDiscount.builder()
                .condition(promotionConditionMock)
                .build());
        assertThrows(NullPointerException.class, () -> TotalSpendDiscount.builder()
                .discountPercentToApply(discountMock)
                .build());
    }

    @Test
    public void testPromotionalRuleConditionCanNotBeCreatedWithNullThreshold() {
        assertThrows(NullPointerException.class, () -> new TotalSpendDiscount.TotalSpendDiscountCondition(null));
    }


    @Test
    public void testPromotionIsNotAppliedIfOrderNotEligible() {
        when(promotionConditionMock.checkEligible(orderMock, totalSpendDiscountPromotionalRule))
                .thenReturn(false);
        Order order = totalSpendDiscountPromotionalRule.apply(orderMock);
        // if condition not met, promotionalRule should return the same instance of order without any change
        assertTrue(orderMock == order);
        assertEquals(orderMock, order);
    }

    @Test
    public void testPromotionIsAppliedIfOrderIsEligible() {
        when(promotionConditionMock.checkEligible(orderMock, totalSpendDiscountPromotionalRule))
                .thenReturn(true);
        when(orderMock.getTotalPrice()).thenReturn(new BigDecimal(100));
        when(orderMock.getItemsInBasket()).thenReturn(items);
        when(discountMock.getDiscountMultiplierToApply()).thenReturn(new BigDecimal(0.9));
        Order order = totalSpendDiscountPromotionalRule.apply(orderMock);
        BigDecimal expectedTotalPrice = BigDecimal.valueOf(90).setScale(2);
        assertFalse(orderMock == order);
        assertNotEquals(orderMock, order);
        assertEquals(expectedTotalPrice,
                order.getTotalPrice());
    }


    private static Stream<Arguments> discountsSource() {
        return Stream.of(
                Arguments.of(10, 0.90),
                Arguments.of(15, 0.85),
                Arguments.of(50, 0.50),
                Arguments.of(95, 0.05)
        );
    }

    @Test
    public void testDiscountCanNotBeCreatedWithNullValue() {
        assertThrows(NullPointerException.class, () -> new TotalSpendDiscount.Discount(null));
    }
    @ParameterizedTest
    @MethodSource("discountsSource") // six numbers
    public void testDiscountMultiplierCalculatedCorrectly(double input, double expected) {
        TotalSpendDiscount.Discount discount = new TotalSpendDiscount.Discount(new BigDecimal(input));
        assertEquals(BigDecimal.valueOf(expected).setScale(2),
                discount.getDiscountMultiplierToApply());
    }

    @ParameterizedTest
    @ValueSource(doubles = {101, 100, -3})
    public void testInvalidDiscountThrowsException(double discount) {
        assertThrows(IllegalArgumentException.class, () -> new TotalSpendDiscount.Discount(new BigDecimal(discount)));
    }

}
