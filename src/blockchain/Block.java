package blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	public String previousHash,hash,data,merkleRoot;
	
	public long timeStamp;
	public int nonce;
	
	public ArrayList<Transaction> transactions=new ArrayList<Transaction>();

	public Block( String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash=calculateHash();
	}
	
	public String calculateHash() {
		
		return StringUtil.applySha256(previousHash+Integer.toString(nonce) + Long.toString(timeStamp)+merkleRoot);
	}
	
	public void mineBlock(int difficulty) {
		merkleRoot=StringUtil.getMerkleRoot(transactions);
		String target= StringUtil.getDificultyString(difficulty);
		
		while(!hash.substring(0,difficulty).equals(target)) {
			nonce++;
			hash=calculateHash();
		}
		System.out.println("Block Mined! : "+hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if(transaction==null)return false;
		
		if(previousHash!="0") {
			if(transaction.processTransaction()!=true) {
				System.out.println("Transaction failed to process!");
				return false;
			}
		}
		transactions.add(transaction);
		 System.out.println("Transaction successfully added to block");
		 return true;
		
	}
	
	
	
	
	
}
