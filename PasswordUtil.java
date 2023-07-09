/*
 * Authors: Michael Fultz
 * Description: Class for encrypting and validating sensitivity data
 */
import javax.crypto.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;



public class PasswordUtil {
	
	/* function for generating hashes
   * public so other classes can access
	 */
	public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		/* iterations is the number of times the password is hashed during derivation
		 * more iterations = more processing power required
		 * minimum = 1000, but can be increased as much as possible 
		 * so long as it does not decrease performance
		 */
		int iterations = 1000;
		char[] chars = password.toCharArray();		// password needs to be charArray as it will be overwritten
		byte[] salt = getSalt();
		
		// spec is the encoded password with additonal salt
		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);		// 64 * 8 is the key length
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");	// algorithm is PBKFDF2 and SHA-256

		byte[] hash = skf.generateSecret(spec).getEncoded();		// generates hash, encodes it, and stores it in byte array
		return iterations + ":" + toHex(salt) + ":" + toHex(hash);	// converts byte array into hex string and returns
	}
	
	// salt to be added with data
	private static byte[] getSalt() throws NoSuchAlgorithmException {
		// using the SHA1PRNG algorithm to generate random bytes
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}
	
	
	// validates encrypted data
	public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String[] parts = storedPassword.split(":");
		int iterations = Integer.parseInt(parts[0]);

		byte[] salt = fromHex(parts[1]);
		byte[] hash = fromHex(parts[2]);

		PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] testHash = skf.generateSecret(spec).getEncoded();

		int diff = hash.length ^ testHash.length;
		for(int i = 0; i < hash.length && i < testHash.length; i++) {
			diff |= hash[i] ^ testHash[i];
		}
		return diff == 0;
	}
	
	// converts hex numbers in a string format to a byte array
	private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[hex.length() / 2];
		for(int i = 0; i < bytes.length ;i++) {
			bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
	
	  // converts a byte array into a hex number represented as a string
		private static String toHex(byte[] array) throws NoSuchAlgorithmException {
			BigInteger bi = new BigInteger(1, array);
			String hex = bi.toString(16);

			int paddingLength = (array.length * 2) - hex.length();
			if (paddingLength > 0) {
				return String.format("%0"  +paddingLength + "d", 0) + hex;
			} else {
				return hex;
			}
		}
}
