package blockchain;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey receipient; // Recipients address/public key.
	public float value;
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	public static int sequence=1;
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	public Transaction(PublicKey from, PublicKey to, float val, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.receipient = to;
		this.value = val;
		this.inputs = inputs;
	}
	
	public String calculateHash() {
		return StringUtil.applySha256(
				StringUtil.get(sender)+
				StringUtil.get(receipient)+
				Float.toString(value)+
				sequence++);
	}


	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.get(sender) + StringUtil.get(receipient) + Float.toString(value)	;
		signature = StringUtil.applySig(privateKey,data);		
	}

	public boolean verifiySignature() {
		String data = StringUtil.get(sender) + StringUtil.get(receipient) + Float.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
		
		if(verifiySignature()==false) {
			System.out.println("#Transaction signature failed to verify!");
			return false;
		}
		
		for(TransactionInput i:inputs) {
			i.UTXO=Chain.UTXOs.get(i.transactionOutId);
		}
		if(getInputsValue()<Chain.minimumTransaction) {
			System.out.println("#Transaction failed, input too small: "+getInputsValue());
			return false;
		}
		float left=getInputsValue()-value;
		transactionId=calculateHash();
		outputs.add(new TransactionOutput(this.receipient, value,transactionId));
		outputs.add(new TransactionOutput(this.sender, left,transactionId));
		
		for(TransactionOutput o: outputs) {
			Chain.UTXOs.put(o.id, o);
		}
		
		for(TransactionInput i:inputs) {
			if(i.UTXO==null) continue;
			Chain.UTXOs.remove(i.UTXO.id);
		}
		
		
		return true;
	}
	
	public float getInputsValue() {
		float total=0;
		for(TransactionInput i:inputs) {
			if(i.UTXO==null)continue;
			total+=i.UTXO.value;
		}
		
		return total;
	}
	public float getOutputsValue() {
		float total=0;
		for(TransactionOutput o: outputs) {
			total+=o.value;
		}
		
		return total;
	}
}
