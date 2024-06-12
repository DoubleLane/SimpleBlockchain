package blockchain;

public class TransactionInput {
	public String transactionOutId;
	public TransactionOutput UTXO;
	
	public TransactionInput(String transactionOut) {
		transactionOutId=transactionOut;
	}
}
