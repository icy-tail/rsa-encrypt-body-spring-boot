/**
 * 
 */
package cn.icytail.springboot.rsa.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加密工具
 * 
 * @author icytail
 */
public class AESUtil {

	private static final String ALGORITHM = "AES";
	private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

	/**
	 * 生成KEY
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateDesKey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
		kgen.init(128);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}

	/**
	 * 加密
	 * 
	 * @param content
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String content, byte[] encryptKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey, ALGORITHM));
		return cipher.doFinal(content.getBytes("utf-8"));
	}

	/**
	 * 解密
	 * 
	 * @param encryptBytes
	 * @param decryptKey
	 * @return
	 * @throws @throws
	 */
	public static byte[] decrypt(byte[] encryptBytes, byte[] decryptKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey, ALGORITHM));
		return cipher.doFinal(encryptBytes);
	}
}
