package com.siniaieva.noths.service;

import com.siniaieva.noths.domain.order.Order;
import com.siniaieva.noths.domain.product.Product;
import com.siniaieva.noths.service.promotional.BuyXGetLowerPrice;
import com.siniaieva.noths.service.promotional.TotalSpendDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CheckoutTest {

    @Mock
    private BuyXGetLowerPrice promotionalRuleMock;

    @Mock
    private TotalSpendDiscount nextPromotionalRuleMock;

    @Mock
    private Order.OrderBuilder orderBuilderMock;

    @Mock
    private Order orderMock;

    private Product product = new Product("001", "Test", BigDecimal.TEN);

    private Checkout checkout;

    @BeforeEach
    public void setup() {
        when(orderBuilderMock.build()).thenReturn(orderMock);
        when(orderMock.getTotalPrice()).thenReturn(BigDecimal.TEN);
        checkout = new Checkout(promotionalRuleMock, orderBuilderMock);

    }

    @Test
    public void testPromotionalRulesChainWorksCorrect() {
        when(promotionalRuleMock.getNext()).thenReturn(nextPromotionalRuleMock);
        when(promotionalRuleMock.apply(orderMock)).thenReturn(orderMock);
        when(nextPromotionalRuleMock.apply(orderMock)).thenReturn(orderMock);
        checkout.total();
        verify(nextPromotionalRuleMock).apply(orderMock);
    }

    @Test
    public void testCheckoutScanCallsOrderAddItemMethod() {
        checkout.scan(product);
        verify(orderBuilderMock).addItem(product);
    }
}
