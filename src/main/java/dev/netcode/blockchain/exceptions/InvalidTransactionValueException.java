package dev.netcode.blockchain.exceptions;

public class InvalidTransactionValueException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidTransactionValueException(String message) {
		super(message);
	}
	
}
