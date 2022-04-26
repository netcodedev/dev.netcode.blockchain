package dev.netcode.blockchain;

import java.util.ArrayList;
import java.util.Date;

import dev.netcode.util.StringUtils;

public class TransactionBlock extends Block<ArrayList<Transaction>>{

	private long timestamp;
	private int nonce;
	
	public TransactionBlock(String previousHash, ArrayList<Transaction> transactions) {
		super(previousHash, transactions);
		this.timestamp = new Date().getTime();
	}
	
	@Override
	public String calculateHash() {
		return StringUtils.applySha256( 
				super.getPreviousHash() +
				Long.toString(timestamp) +
				Integer.toString(nonce) + 
				getMerkleRoot(super.getData())
				);
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getHash());
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(StringUtils.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

}
