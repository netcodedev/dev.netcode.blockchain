package dev.netcode.blockchain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import dev.netcode.security.encryption.KeyThumbprintResolver;
import lombok.Getter;

public class TransactionBlockChain extends BlockChain<ArrayList<Transaction>> {

	private KeyThumbprintResolver keyThumbprintResolver;
	@Getter private double minimumTransactionValue = 0;
	@Getter private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public TransactionBlockChain(KeyThumbprintResolver keyThumbprintResolver) {
		this.keyThumbprintResolver = keyThumbprintResolver;
	}
	
	public PublicKey getPublicKeyFromThumbprint(String sender) {
		return keyThumbprintResolver.resolve(sender);
	}

}
