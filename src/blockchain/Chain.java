package blockchain;

import java.util.ArrayList;
import java.util.HashMap;


public class Chain {
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static HashMap<String, TransactionOutput> UTXOs=new HashMap<String, TransactionOutput>();
	
	public static int difficulty = 3;
	public static Wallet walletA;
	public static Wallet walletB;
	public static float minimumTransaction = 0.1f;
	public static Transaction genesisTransaction;
	
	public static Boolean isChainValid() {
		Block currentBlock, previousBlock;
		String hasTarget=new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tmpUTXOs=new HashMap<String, TransactionOutput>();
		tmpUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			
			if(!currentBlock.hash.substring(0,difficulty).equals(hasTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
				
			}
			TransactionOutput tmpOut;
			
			for(int t=0;t<currentBlock.transactions.size();t++) {
				Transaction currTransaction=currentBlock.transactions.get(t);
				
				if(!currTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction("+t+")is invalid");
					return false;
					
				}
				
				if(currTransaction.getInputsValue()!= currTransaction.getOutputsValue()) {
					System.out.println("#Inputs are not equal to outputs on Transaction("+t+")");
					return false;
				}
				
				for(TransactionInput input: currTransaction.inputs) {
					tmpOut = tmpUTXOs.get(input.transactionOutId);
					
					if(tmpOut==null) {
						System.out.println("#Reference input on transaction("+t+") is missing");
						return false;
					}
					if(input.UTXO.value!=tmpOut.value) {
						System.out.println("Referenced input transaction ("+t+") value is invalid");
						return false;
					}
					tmpUTXOs.remove(input.transactionOutId);
				}
				for(TransactionOutput out:currTransaction.outputs) {
					tmpUTXOs.put(out.id, out);
				}
				if(currTransaction.outputs.get(0).receiver!= currTransaction.receipient) {
					System.out.println("#Transaction("+t+") output receiver is not who it should be");
					return false;
				}
				if(currTransaction.outputs.get(1).receiver!=currTransaction.sender) {
					System.out.println("#Transaction("+t+") output 'change' is not sender.");
					return false;
				}
			}
			
		}
		System.out.println("Blockchain is rock-solid!");
		return true;
	}
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	public static void main(String[] args) {

		walletA=new Wallet();
		walletB=new Wallet();
		Wallet exchange=new Wallet();
		
		genesisTransaction=new Transaction(exchange.publicKey, walletA.publicKey, 100, null);
		genesisTransaction.generateSignature(exchange.privateKey);
		genesisTransaction.transactionId="0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receipient, genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		System.out.println("Inititating genesis block... ");
		Block genesis=new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		Block b1=new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: "+walletA.getBalance());
		System.out.println("WalletA is attempting to send funds to WalletB");
		b1.addTransaction(walletA.sendFunds(walletB.publicKey, 40));
		addBlock(b1);
		System.out.println("WalletA's balance: "+walletA.getBalance());
		System.out.println("WalletB's balance: "+walletB.getBalance());
		
		Block b2=new Block(b1.hash);
		System.out.println("\nWallet is attempting to send more funds than it has...");
		b2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000));
		addBlock(b2);
		System.out.println("WalletA's balance: "+walletA.getBalance());
		System.out.println("WalletB's balance:"+walletB.getBalance());
		
		Block b3=new Block(b2.hash);
		System.out.println("\nWalletB is attempting to send fund to WalletA...");
		b3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
		System.out.println("WalletA's balance: "+walletA.getBalance());
		System.out.println("WalletB's balance: "+walletB.getBalance());
		
		isChainValid();
	
	}

	
}
