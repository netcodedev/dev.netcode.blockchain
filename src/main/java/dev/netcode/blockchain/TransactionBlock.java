package dev.netcode.blockchain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import dev.netcode.blockchain.util.Utils;
import dev.netcode.util.StringUtils;

public class TransactionBlock extends Block<ArrayList<Transaction>> {
	
	public TransactionBlock(String previousHash, ArrayList<Transaction> transactions) {
		super(previousHash, transactions, Utils::getMerkleRoot, Map.of("timestamp", new Date().getTime(), "nonce", new SecureRandom().nextInt()));
	}
	
	@Override
	public String calculateHash() {
		return StringUtils.applySha256( 
				super.getPreviousHash() +
				Long.toString((Long)super.getMetadata().get("timestamp")) +
				Integer.toString((Integer)super.getMetadata().get("nonce")) + 
				Utils.getMerkleRoot(super.getData())
				);
	}

}
