package dev.netcode.blockchain.exceptions;

public class InsufficientTransactionInputsValueException extends Exception {
	public InsufficientTransactionInputsValueException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
