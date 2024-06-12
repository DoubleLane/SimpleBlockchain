package blockchain;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class StringUtil{
	
public static String applySha256(String input) {
		
		try {
			MessageDigest digest=MessageDigest.getInstance("SHA-256");
			byte[] hash=digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString=new StringBuffer();
			
			for(int i=0;i<hash.length;i++) {
				String hex=Integer.toHexString(0xff & hash[i]);
				if(hex.length()==1)hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		
				
	}
	public static byte[] applySig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[]output=new byte[0];
		try {
			dsa = Signature.getInstance("SHA256withRSA");
			dsa.initSign(privateKey);
			byte[]strByte=input.getBytes();
			dsa.update(strByte);
			byte[]realSig=dsa.sign();
			output=realSig;
		} catch (Exception e) {
			System.out.println("ECDSABC");
		}
		return output;
	}
  

	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {			
			Signature ecdsaVerify = Signature.getInstance("SHA256withRSA");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			return false;
		}
	}
	
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	public static String get(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count=transactions.size();
		ArrayList<String> previousLayer=new ArrayList<String>();
		for(Transaction transaction: transactions) {
			previousLayer.add(transaction.transactionId);
		}
		
		ArrayList<String> treeLayer= previousLayer;
		while(count>1) {
			treeLayer=new ArrayList<String>();
			for(int i=1;i<previousLayer.size();i++) {
				treeLayer.add(applySha256(previousLayer.get(i-1)+ previousLayer.get(i)));
				
			}
			count=treeLayer.size();
			previousLayer=treeLayer;
		}
		
		
		
		return (treeLayer.size()==1)?treeLayer.get(0):"";
	}

	
	
}
