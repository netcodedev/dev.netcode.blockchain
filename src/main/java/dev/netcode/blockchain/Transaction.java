package dev.netcode.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.security.PublicKey;

import dev.netcode.security.encryption.RSAEncrypter;
import dev.netcode.util.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Transaction {

	@Getter private int ID;
	@Getter private String hash;
	@Getter private String sender;
	@Getter private transient PublicKey publicKey;
	@Getter private String recipient;
	@Getter @Setter private String signature;
	@Getter private double value;
	@Getter @Setter private boolean processed;

	@Getter List<TransactionInput> inputs = new ArrayList<>();
	@Getter List<TransactionOutput> outputs = new ArrayList<>();
	private static int transactionCounter = 0;

	public Transaction(@NonNull PublicKey sender, @NonNull String recipient, double value, List<TransactionInput> inputs) {
		this.sender = RSAEncrypter.getFingerprint(sender);
		this.publicKey = sender;
		this.recipient = recipient;
		this.value = value;
		this.inputs = inputs;
		if(this.inputs == null)
			this.inputs = new ArrayList<>();
		this.ID = transactionCounter++;
		this.hash = calculateHash();
	}

	public String calculateHash() {
		String inputsHash = "";
		if(inputs.size() > 0) {
			inputsHash = StringUtils.applySha256(inputs.stream().map(TransactionInput::getTransactionOutputID).map(Object::toString).collect(Collectors.joining()));
		}
		return StringUtils.applySha256(sender + recipient + Double.toString(value) + ID + inputsHash);
	}

	public boolean verifySignature() {
		return RSAEncrypter.verifySignature(publicKey, getData(), signature);
	}

	public String getData() {
		return sender + recipient + Double.toString(value) + hash;
	}

	public double getInputsValue() {
		return inputs.stream()
				.filter(i -> i.getUTXO()!=null)
				.map(TransactionInput::getUTXO)
				.collect(Collectors.summingDouble(TransactionOutput::getValue));
	}

	public double getOutputsValue() {
		return outputs.stream().collect(Collectors.summingDouble(TransactionOutput::getValue));
	}
}
