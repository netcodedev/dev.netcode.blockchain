package dev.netcode.blockchain.exceptions;

/**
 * This exception gets thrown if the transaction values does not match
 * the blockchains transaction value conditions
 */
public class InvalidTransactionValueException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidTransactionValueException(String message) {
		super(message);
	}
	
}
