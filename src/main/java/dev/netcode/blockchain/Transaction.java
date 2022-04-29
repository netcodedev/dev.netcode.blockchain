package dev.netcode.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import dev.netcode.blockchain.exceptions.InsufficientTransactionInputsValueException;
import dev.netcode.blockchain.exceptions.InvalidSignatureException;
import dev.netcode.blockchain.exceptions.InvalidTransactionValueException;
import dev.netcode.blockchain.exceptions.TransactionOutputNotFoundException;
import dev.netcode.security.encryption.RSAEncrypter;
import dev.netcode.util.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Transaction {

	@Getter private int ID;
	@Getter private String hash;
	@Getter private String sender;
	@Getter private String recipient;
	@Getter @Setter private String signature;
	@Getter private double value;
	@Getter private boolean processed;
	
	@Getter ArrayList<TransactionInput> inputs = new ArrayList<>();
	@Getter ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	private transient TransactionBlockChain blockchain;
	private static int transactionCounter = 0;
	
	public Transaction(@NonNull TransactionBlockChain blockchain, @NonNull String sender, @NonNull String recipient, double value, ArrayList<TransactionInput> inputs) {
		if(blockchain.isValidTransactionValue(value)) {
			throw new IllegalArgumentException("Transaction value must be greater or equal to blockchains minimum transaction value amount");
		}
		this.blockchain = blockchain;
		this.sender = sender;
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
	
	public boolean processTransaction() throws InvalidSignatureException, TransactionOutputNotFoundException, InvalidTransactionValueException, InsufficientTransactionInputsValueException {
		return processTransaction(blockchain.getUTXOs());
	}
	
	public boolean processTransaction(HashMap<String, TransactionOutput> UTXOs) throws InvalidSignatureException, TransactionOutputNotFoundException, InvalidTransactionValueException, InsufficientTransactionInputsValueException {
		if(verifySignature() == false) {
			throw new InvalidSignatureException();
		}
		
		for(TransactionInput i : inputs) {
			TransactionOutput output = UTXOs.get(i.getTransactionOutputID());
			if(output == null) {
				throw new TransactionOutputNotFoundException();
			}
			i.setUTXO(output);
		}

		if(blockchain.isValidTransactionValue(getInputsValue())) {
			throw new InvalidTransactionValueException("Transaction value is smaller than minimum transaction value");
		}
		if(getInputsValue() < value) {
			throw new InsufficientTransactionInputsValueException("The value of the Transaction Inputs is smaller than the transaction value");
		}
		
		if(processed) {
			return true;
		}
		double leftOver = getInputsValue() - value;
		outputs.add(new TransactionOutput(this.recipient, value, hash));
		if(leftOver > 0) {
			outputs.add(new TransactionOutput(this.sender, leftOver, hash));
		}
		return processed = true;
	}
	
	public boolean verify() throws InvalidSignatureException, InvalidTransactionValueException {
		if(!verifySignature()) {
			throw new InvalidSignatureException();
		}
		if(!blockchain.isValidTransactionValue(getInputsValue())) {
			throw new InvalidTransactionValueException("Transaction value is smaller than minimum transaction value");
		}
		for(var TXO : outputs) {
			String TXOHash = StringUtils.applySha256(TXO.getNonce()+TXO.getRecipient()+Double.toString(TXO.getValue())+TXO.getParentTransactionID());
			if(!TXOHash.equals(TXO.getID())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean verifySignature() {
		return RSAEncrypter.verifySignature(blockchain.getPublicKeyFromThumbprint(sender), getData(), signature);
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
