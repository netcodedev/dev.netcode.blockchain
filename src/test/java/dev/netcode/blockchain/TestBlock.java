package dev.netcode.blockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestBlock {
	
	@Test
	public void testBlock() {
		var block = new Block<String>("", "Hello World!");
		assertEquals(block.getId(), 0);
		assertEquals(block.getData(), "Hello World!");
		assertEquals(block.getHash(), "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069");
	}
	
}
