package dev.netcode.blockchain.exceptions;

/**
 * This exception gets thrown if the sum of the TransactionInputs is smaller than the
 * value of the transaction.
 */
public class InsufficientTransactionInputsValueException extends Exception {
	private static final long serialVersionUID = 1L;
	public InsufficientTransactionInputsValueException(String message) {
		super(message);
	}
}
