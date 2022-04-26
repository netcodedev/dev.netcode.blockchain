package dev.netcode.blockchain;

import lombok.Getter;
import lombok.Setter;

public class TransactionInput {

	@Getter private String transactionOutputID;
	@Getter @Setter private TransactionOutput UTXO;
	
	private TransactionInput(String transactionOutputID) {
		this.transactionOutputID = transactionOutputID;
	}
	
}
