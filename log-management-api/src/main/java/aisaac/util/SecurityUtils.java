package aisaac.util;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SecurityUtils {
    
    public static final String ENC_PREFIX = "ENC(";
    public static final String ENC_SUFFIX = ")";
    public static final String ENC_ENVELOPE = "ENC(%s)";
	
	/* This class is used to encryption text password.*/
    public static String encrypt256(String plainText) throws Exception {
        if (StringUtils.isBlank(plainText)) {
            throw new Exception("Blank text cannot be encrypted");
        }
        String encryptedText = encrypt(plainText);
        String enclosedEncryptedText = String.format(
                SecurityUtils.ENC_ENVELOPE, encryptedText);
        log.info("Encryption applied with formatting");
        log.info("Encrypted text: "+enclosedEncryptedText);
        return enclosedEncryptedText;    	
    }

    /* used for decryption the password */
    public static String decrypt256(String encryptedText) throws Exception {
        if (StringUtils.isBlank(encryptedText)) {
            log.info("Decryption not applied as text is blank");
            return encryptedText;
        }
        if (!(StringUtils.startsWith(encryptedText, SecurityUtils.ENC_PREFIX) &&
                StringUtils.endsWith(encryptedText, SecurityUtils.ENC_SUFFIX)) ) {
            log.info("Decryption not applied as text is not in format");
            return encryptedText; 
        }
        String cleanEncryptedText = 
                StringUtils.removeStart(encryptedText, SecurityUtils.ENC_PREFIX);
        cleanEncryptedText = 
                StringUtils.removeEnd(cleanEncryptedText, SecurityUtils.ENC_SUFFIX);
        String decryptedText = decrypt(cleanEncryptedText);
        log.info("Decrypted text: "+decryptedText);
        return decryptedText;
    }
    
    public static String encrypt(String plainText) throws Exception {
		String secretKey = "Paladion1234!@#$";
		String salt = "Paladion1234!@#$";
		String encryptStr = null;
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
			encryptStr = Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return encryptStr;
	}

	public static String decrypt(String encryptedText) throws Exception {
		String secretKey = "Paladion1234!@#$";
		String salt = "Paladion1234!@#$";
		String decryptStr = null;
		try {

			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
			decryptStr = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));

		} catch (Exception ex) {
			throw ex;
		}
		return decryptStr;
	}
    /* used for decryption the password */

    public static String decrypt256Enc(String encryptedText) {
    	log.info("Encrypted text - {}", encryptedText);
        if (StringUtils.isBlank(encryptedText)) {
            log.info("Decryption not applied as text is blank");
            return encryptedText;
        }
        if (!(StringUtils.startsWithIgnoreCase(encryptedText, SecurityUtils.ENC_PREFIX) &&
                StringUtils.endsWithIgnoreCase(encryptedText, SecurityUtils.ENC_SUFFIX)) ) {
            log.info("Decryption not applied as text is not in format");
            return encryptedText; 
        }
        String cleanEncryptedText = 
                StringUtils.replaceIgnoreCase(encryptedText, SecurityUtils.ENC_PREFIX, "");
        cleanEncryptedText = 
                StringUtils.replace(cleanEncryptedText, SecurityUtils.ENC_SUFFIX, "");
        String decryptedText = encryptedText;
        try {
            decryptedText = SecurityUtils.decrypt(cleanEncryptedText);
        } catch (Exception e) {
            log.error(
                    String.format(
                            "Unable to decrypt %s due to %s", 
                            encryptedText, 
                            e.getMessage()), 
                    e);
        }
        return decryptedText;
    }

}

