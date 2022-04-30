package dev.netcode.blockchain;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dev.netcode.util.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Block<T> {

	@Getter private long id;
	@Getter private String hash;
	@Getter private String previousHash;
	@Getter private T data;
	@Getter private Map<String, Object> metadata;
	@Setter Function<T, String> dataHashFunction;
	
	@Getter @Setter private static int blockCounter = 0;

	public Block(@NonNull String previousHash, T data) {
		this.previousHash = previousHash;
		this.id = blockCounter++;
		this.data = data;
		this.dataHashFunction = Object::toString;
		this.metadata = new HashMap<>();
		this.hash = calculateHash();
	}
	
	public Block(@NonNull String previousHash, T data, Map<String, Object> metadata) {
		this.previousHash = previousHash;
		this.id = blockCounter++;
		this.data = data;
		this.dataHashFunction = Object::toString;
		this.metadata = metadata;
		this.hash = calculateHash();
	}
	
	public Block(@NonNull String previousHash, T data, @NonNull Function<T, String> dataHasher) {
		this.previousHash = previousHash;
		this.id = blockCounter++;
		this.data = data;
		this.dataHashFunction = dataHasher;
		this.metadata = new HashMap<>();
		this.hash = calculateHash();
	}
	
	public Block(@NonNull String previousHash, T data, @NonNull Function<T, String> dataHasher, Map<String, Object> metadata) {
		this.previousHash = previousHash;
		this.id = blockCounter++;
		this.data = data;
		this.dataHashFunction = dataHasher;
		this.metadata = metadata;
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		return StringUtils.applySha256(previousHash+dataHashFunction.apply(data));
	}
	
	public boolean validate() {
		return hash.equals(calculateHash());
	}
	
	public void addMetadata(String key, Object metadata) {
		this.metadata.put(key, metadata);
	}
	
}
