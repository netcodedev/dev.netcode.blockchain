package dev.netcode.blockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.netcode.util.StringUtils;

public class TestBlockAndBlockChain {
	
	@Test
	public void testBlock() {
		var block = new Block<String>("", "Hello World!");
		assertEquals(0, block.getId());
		assertEquals("Hello World!", block.getData());
		assertEquals(StringUtils.applySha256(block.getData()), block.getHash());
		Block.setBlockCounter(0); //reset block counter for other tests
	}
	
	@Test
	public void testBlockchain() {
		var block = new Block<String>("", "Hello World!");
		var blockchain = new BlockChain<String>();
		assertEquals(0, blockchain.getBlockCount());
		assertEquals(null, blockchain.getBlock(0));
		assertEquals(null, blockchain.getLastBlock());
		blockchain.addBlock(block);
		assertEquals(1, blockchain.getBlockCount());
		assertEquals(block, blockchain.getBlock(0));
		assertEquals(block, blockchain.getLastBlock());
		assertEquals(true, blockchain.validateChain());
		var secondBlock = new Block<String>(block.getHash(), "Ooops i did it again...");
		assertEquals(0, block.getId());
		assertEquals(1, secondBlock.getId());
		assertEquals("Ooops i did it again...", secondBlock.getData());
		assertEquals(1, blockchain.getBlockCount());
		blockchain.addBlock(secondBlock);
		assertEquals(2, blockchain.getBlockCount());
		assertEquals(block, blockchain.getBlock(0));
		assertEquals(secondBlock, blockchain.getBlock(1));
		assertEquals(secondBlock, blockchain.getLastBlock());
		assertEquals(true, blockchain.validateChain());
		var fraudaulentBlock = new Block<String>("invalid previous hash", "I am based on an old block");
		assertEquals(2, fraudaulentBlock.getId());
		blockchain.addBlock(fraudaulentBlock);
		assertEquals(false, blockchain.validateChain());
		blockchain.restoreLastValidState();
		assertEquals(true, blockchain.validateChain());
		assertEquals(2, blockchain.getBlockCount());
	}
	
}
