package com.neotys.action.sp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SPExecuteActionTest {
	@Test
	public void shouldReturnType() {
		final SPExecuteAction action = new SPExecuteAction();
		System.out.println(action.getType());
		assertEquals("SPExecute", action.getType());
	}

}
