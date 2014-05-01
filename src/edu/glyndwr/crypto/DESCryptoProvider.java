package edu.glyndwr.crypto;

/*
 * DES encrypting/decrypting for text data
 */

import java.security.*;
import javax.crypto.*;

public class DESCryptoProvider {
	public static String seed = "AM6ROFFBABFAKILLEMALL";

	public static String decrypt(String src) {

		try {
			javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(getRawKey(), "DES");
			Cipher ecipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.DECRYPT_MODE, key);

			byte[] utf8 = toByte(src);

			// Descrypt
			byte[] dec = ecipher.doFinal(utf8);

			return new String(dec);
		} catch (Exception exc) {
			try {
				exc.printStackTrace();
			} catch (Exception exc2) {
			}

		}
		return src;

	}

	public static String encrypt(String src) {
		try {
			javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(getRawKey(), "DES");
			Cipher ecipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] utf8 = src.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			return toHex(enc);
		} catch (Exception exc) {
			try {
				exc.printStackTrace();
			} catch (Exception exc2) {
			}

		}
		return src;
	}

	private static byte[] getRawKey() throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("DES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed.getBytes());
		kgen.init(56, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	// these methods are using for convertation bytes to ASCII symbols

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

}
