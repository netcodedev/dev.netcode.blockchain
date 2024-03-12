package dev.netcode.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import dev.netcode.blockchain.exceptions.InsufficientTransactionInputsValueException;
import dev.netcode.blockchain.exceptions.InvalidSignatureException;
import dev.netcode.blockchain.exceptions.InvalidTransactionValueException;
import dev.netcode.blockchain.exceptions.TransactionOutputNotFoundException;
import dev.netcode.util.StringUtils;

public class TransactionBlockChain extends BlockChain<ArrayList<Transaction>> {

	private double minimumTransactionValue = 0;
	private Map<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

	public boolean isValidTransactionValue(double value) {
		return value > minimumTransactionValue;
	}

	public boolean processTransaction(Transaction transaction) throws InvalidSignatureException, TransactionOutputNotFoundException, InvalidTransactionValueException, InsufficientTransactionInputsValueException {
		var UTXOs = getUTXOs(transaction.getSender());
		if(transaction.verifySignature() == false) {
			throw new InvalidSignatureException();
		}

		for(TransactionInput i : transaction.getInputs()) {
			TransactionOutput output = UTXOs.get(i.getTransactionOutputID());
			if(output == null) {
				throw new TransactionOutputNotFoundException();
			}
			i.setUTXO(output);
		}

		if(!isValidTransactionValue(transaction.getValue())) {
			throw new InvalidTransactionValueException("Transaction value is not valid: "+transaction.getValue());
		}
		if(transaction.getInputsValue() < transaction.getValue()) {
			throw new InsufficientTransactionInputsValueException("The value of the Transaction Inputs is smaller than the transaction value");
		}

		if(transaction.isProcessed()) {
			return true;
		}
		double leftOver = transaction.getInputsValue() - transaction.getValue();
		transaction.getOutputs().add(new TransactionOutput(transaction.getRecipient(), transaction.getValue(), transaction.getHash()));
		if(leftOver > 0) {
			transaction.getOutputs().add(new TransactionOutput(transaction.getSender(), leftOver, transaction.getHash()));
		}
		transaction.setProcessed(true);
		return true;
	}

	public boolean verifyTransaction(Transaction transaction) throws InvalidSignatureException, InvalidTransactionValueException {
		if(!transaction.verifySignature()) {
			throw new InvalidSignatureException();
		}
		if(!isValidTransactionValue(transaction.getValue())) {
			throw new InvalidTransactionValueException("Transaction value is not valid");
		}
		for(var TXO : transaction.getOutputs()) {
			String TXOHash = StringUtils.applySha256(TXO.getNonce()+TXO.getRecipient()+Double.toString(TXO.getValue())+TXO.getParentTransactionID());
			if(!TXOHash.equals(TXO.getID())) {
				return false;
			}
		}
		return true;
	}

	public Map<String, TransactionOutput> refreshUTXOs(){
		var UTXOs = new HashMap<String,TransactionOutput>();
		for(var block : super.getBlocks()) {
			for(var transaction : block.getData()) {
				for(var transactionOutput : transaction.getOutputs()) {
					UTXOs.put(transactionOutput.getID(), transactionOutput);
				}
			}
		}
		for(var block : super.getBlocks()) {
			for(var transaction : block.getData()) {
				if(transaction.getInputs()==null) {
					continue;
				}
				for(var transactionInput : transaction.getInputs()) {
					UTXOs.remove(transactionInput.getUTXO().getID());
				}
			}
		}
		return this.UTXOs = UTXOs;
	}

	public Map<String, TransactionOutput> getUTXOs(){
		return UTXOs.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	public Map<String, TransactionOutput> getUTXOs(String address){
		return UTXOs.entrySet().stream().filter(i -> i.getValue().getRecipient().equals(address)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	public void addUTXO(TransactionOutput UTXO) {
		this.UTXOs.put(UTXO.getID(), UTXO);
	}

}
