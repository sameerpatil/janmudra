package com.janmudra.kuberbot.util;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.janmudra.kuberbot.utils.OrderCalculatorUtil;

public class TestOrderCalculatorUtil {

	@Ignore
	public void testGetNewPriceWithBrokerage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQuantityByPercentage() {
		String initialQuantity = "1";
		Double newQuantity = OrderCalculatorUtil.getQuantityByPercentage(initialQuantity, initialQuantity);
		System.out.println("New quantity = " + newQuantity);
		Assert.assertEquals(new Double(1.01), newQuantity);
	}

}
