package blockchain;

import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey receiver;
	public float value;
	public String parentTransactionId;
	
	public TransactionOutput(PublicKey _receiver, float val, String parentId) {
		this.receiver = _receiver;
		this.value = val;
		this.parentTransactionId = parentId;
		this.id=StringUtil.applySha256(StringUtil.get(_receiver)+Float.toString(val)+parentId);
	}
	
	public boolean isMine(PublicKey publicKey) {
		return publicKey==receiver;
	}
	
	
}
