package dev.netcode.blockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import dev.netcode.blockchain.exceptions.InsufficientTransactionInputsValueException;
import dev.netcode.blockchain.exceptions.InvalidSignatureException;
import dev.netcode.blockchain.exceptions.InvalidTransactionValueException;
import dev.netcode.blockchain.exceptions.TransactionOutputNotFoundException;
import dev.netcode.security.encryption.RSAEncrypter;

public class TestTransactionBlockChain {

	private static HashMap<String, KeyPair> keys = new HashMap<>();
	private static KeyPair keypairSender = RSAEncrypter.generateKeyPair(2048);
	private static String thumbprintSender = RSAEncrypter.getFingerprint(keypairSender.getPublic());
	private static KeyPair keypairReceiver = RSAEncrypter.generateKeyPair(2048);
	private static String thumbprintReceiver = RSAEncrypter.getFingerprint(keypairReceiver.getPublic());
	{
		keys.put(thumbprintSender, keypairSender);
		keys.put(thumbprintReceiver, keypairReceiver);
	}
	
	@Test
	public void testTransactionOutput() {
		assertThrows(NullPointerException.class, () -> new TransactionOutput(null, 100, null));
	}
	
	@Test
	public void testTransaction() throws InvalidKeyException, SignatureException, InvalidSignatureException, InvalidTransactionValueException, TransactionOutputNotFoundException, InsufficientTransactionInputsValueException {
		var blockchain = new TransactionBlockChain(TestTransactionBlockChain::resolveThumbprint);
		double value = 100.0;
		assertThrows(NullPointerException.class, ()->new Transaction(null, thumbprintSender, thumbprintReceiver, value, null));
		assertThrows(NullPointerException.class, ()->new Transaction(blockchain, null, thumbprintReceiver, value, null));
		assertThrows(NullPointerException.class, ()->new Transaction(blockchain, thumbprintSender, null, value, null));
		assertThrows(IllegalArgumentException.class, ()->new Transaction(blockchain, thumbprintSender, thumbprintReceiver, -1.0, null));
		var transaction = new Transaction(blockchain, thumbprintSender, thumbprintReceiver, value, null);
		assertEquals(thumbprintSender, transaction.getSender());
		assertEquals(thumbprintReceiver, transaction.getRecipient());
		assertEquals(100.0, transaction.getValue());
		assertEquals(0, transaction.getInputs().size());
		assertEquals(transaction.calculateHash(), transaction.getHash());
		assertEquals(thumbprintSender+thumbprintReceiver+Double.toString(value)+transaction.getHash(), transaction.getData());
		assertEquals(0, transaction.getInputsValue());
		assertEquals(0, transaction.getOutputsValue()); // outputs are not yet set because the transaction has not yet been processed
		assertEquals(false, transaction.verifySignature()); // because transaction is not yet signed
		assertThrows(InvalidSignatureException.class, ()->transaction.verify());
		assertThrows(InvalidSignatureException.class, ()->transaction.processTransaction());
		String signature = RSAEncrypter.sign(keypairSender.getPrivate(), transaction.getData());
		transaction.setSignature(signature);
		assertEquals(true, transaction.verifySignature());
		assertEquals(true, transaction.verify());
		assertThrows(InsufficientTransactionInputsValueException.class, ()->transaction.processTransaction());
		var utxos = new HashMap<String, TransactionOutput>();
		var to = new TransactionOutput(thumbprintReceiver, value, "");
		utxos.put(to.getID(), to);
		assertEquals(thumbprintReceiver, to.getRecipient());
		assertEquals(value, to.getValue());
		assertEquals("", to.getParentTransactionID());
		assertEquals(0, to.getNonce());
		var ti = new TransactionInput(to.getID());
		ti.setUTXO(to);
		var transactionInputs = new ArrayList<TransactionInput>();
		transactionInputs.add(ti);
		var newTransaction = new Transaction(blockchain, thumbprintSender, thumbprintReceiver, value, transactionInputs);
		assertEquals(value, newTransaction.getInputsValue());
		String newSignature = RSAEncrypter.sign(keypairSender.getPrivate(), newTransaction.getData());
		newTransaction.setSignature(newSignature);
		assertEquals(true, newTransaction.verify());
		assertThrows(TransactionOutputNotFoundException.class, ()->newTransaction.processTransaction());
		blockchain.addUTXO(to);
		assertEquals(true, newTransaction.processTransaction());
	}
	
	@Test
	public void testTransactionBlockChain() {
		var blockchain = new TransactionBlockChain(TestTransactionBlockChain::resolveThumbprint);
		assertThrows(NullPointerException.class, ()->new TransactionBlock("0", null));
		var block1 = new TransactionBlock("0",new ArrayList<Transaction>());
		blockchain.addBlock(block1);
		assertEquals(1, blockchain.getBlockCount());
	}
	
	private static PublicKey resolveThumbprint(String thumbprint) {
		return keys.get(thumbprint).getPublic();
	}
	
}
