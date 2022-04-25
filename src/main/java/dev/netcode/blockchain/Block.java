package dev.netcode.blockchain;

import lombok.Getter;
import dev.netcode.util.StringUtils;

public class Block<T> {

	@Getter private long id;
	@Getter private String hash;
	@Getter private String previousHash;
	@Getter private T data;
	
	@Getter private static int blockCounter = 0;
	
	public Block(String previousHash, T data) {
		this.previousHash = previousHash;
		this.id = blockCounter++;
		this.data = data;
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		return StringUtils.applySha256(previousHash+data.toString());
	}
	
}
