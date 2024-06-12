package blockchain;

import java.security.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public HashMap<String, TransactionOutput> UTXOs=new HashMap<String, TransactionOutput>();
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public Wallet() {
		generateKeyPair();
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			
			KeyPair keyPair=keyGen.generateKeyPair();
			privateKey=keyPair.getPrivate();
			publicKey=keyPair.getPublic();
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} 
	}
	
	
	public float getBalance() {
		float total=0;
		for(Map.Entry<String, TransactionOutput> item: Chain.UTXOs.entrySet()) {
			TransactionOutput UTXO=item.getValue();
			
			if(UTXO.isMine(publicKey)) {
				UTXOs.put(UTXO.id, UTXO);
				total+=UTXO.value;
			}
		}
		
		return total;
	}
	
	public Transaction sendFunds(PublicKey _receiver, float val) {
		if(getBalance()<val) {
			System.out.println("#Insufficient funds!");
			return null;
		}
		
		ArrayList<TransactionInput> inputs=new ArrayList<TransactionInput>();
		float total=0;
		
		for(Map.Entry<String, TransactionOutput> item:UTXOs.entrySet()) {
			TransactionOutput UTXO=item.getValue();
			total+=UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total>val)break;
		}
		Transaction newT=new Transaction(publicKey, _receiver, val, inputs);
		newT.generateSignature(privateKey);
		
		for(TransactionInput input: inputs) {
			UTXOs.remove(input.transactionOutId);
		}
		return newT;
	}
	
}






