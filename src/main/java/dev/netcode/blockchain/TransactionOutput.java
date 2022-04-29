package dev.netcode.blockchain;

import dev.netcode.util.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class TransactionOutput {

	private static int UID;
	@Getter private int nonce;
	@Getter private String ID;
	@Getter private String recipient;
	@Getter @Setter private double value;
	@Getter private String parentTransactionID;
	
	public TransactionOutput(@NonNull String recipient, double value, String parentTransactionID) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionID = parentTransactionID;
		this.nonce = UID++;
		this.ID = StringUtils.applySha256(nonce+recipient+Double.toString(value)+parentTransactionID);
	}
	
}
