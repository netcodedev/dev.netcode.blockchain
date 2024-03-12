package dev.netcode.blockchain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import dev.netcode.security.encryption.KeyThumbprintResolver;

public class TransactionBlockChain extends BlockChain<ArrayList<Transaction>> {

	private KeyThumbprintResolver keyThumbprintResolver;
	private double minimumTransactionValue = 0;
	private Map<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

	public TransactionBlockChain(KeyThumbprintResolver keyThumbprintResolver) {
		this.keyThumbprintResolver = keyThumbprintResolver;
	}

	public PublicKey getPublicKeyFromThumbprint(String sender) {
		return keyThumbprintResolver.resolve(sender);
	}

	public boolean isValidTransactionValue(double value) {
		return value > minimumTransactionValue;
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
