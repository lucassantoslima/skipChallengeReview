package com.skipthedishes.service.factory;

import java.math.BigDecimal;

public class CalculateOrderTypeDelivery extends OrderTypeFactory {

	@Override
	public BigDecimal calculateOrderType(BigDecimal total) {
		return total.multiply(new BigDecimal( 0.10 ));  
	}


}
