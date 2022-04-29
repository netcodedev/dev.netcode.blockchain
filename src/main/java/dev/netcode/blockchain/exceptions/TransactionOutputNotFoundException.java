package dev.netcode.blockchain.exceptions;

/**
 * This exception gets thrown in case the specified {@link TransactionOutput}
 * could not be found. This could be either because it never existed or it has
 * already been spend.
 */
public class TransactionOutputNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
}
