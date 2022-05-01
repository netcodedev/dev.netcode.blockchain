package dev.netcode.blockchain.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.netcode.blockchain.Transaction;
import dev.netcode.util.StringUtils;

public class Utils {

	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		var previousTreeLayer = transactions.stream().map(Transaction::getHash).collect(Collectors.toList());
		var treeLayer = previousTreeLayer;
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
