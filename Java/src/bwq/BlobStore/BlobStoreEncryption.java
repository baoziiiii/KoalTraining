package bwq.BlobStore;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

public class BlobStoreEncryption {

    private static BlobStoreEncryption blobStoreEncryption =null;



    private BlobStoreEncryption()  {}

    public static  BlobStoreEncryption getInstance() {
        if(blobStoreEncryption!=null){
            return blobStoreEncryption;
        }else{
            blobStoreEncryption=new BlobStoreEncryption();
        }
        return blobStoreEncryption;
    }

    public byte[] encrypt(byte[] data,String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] key=Hex.encodeHexString(messageDigest.digest(password.getBytes())).getBytes();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes=new byte[16];   //AES iv为16位
//        SecureRandom secureRandom=new SecureRandom();
//        secureRandom.nextBytes(ivBytes);
        Arrays.fill(ivBytes,(byte)0);
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,ivParameterSpec);
        byte[] crypt=cipher.doFinal(data);
        return crypt;
    }

    public byte[] decrypt(byte[] crypt,String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] key=Hex.encodeHexString(messageDigest.digest(password.getBytes())).getBytes();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        Cipher cipher=Cipher.getInstance("AES/CBC/NoPadding");
        byte[] ivBytes=new byte[16];   //AES iv为16位
//        SecureRandom secureRandom=new SecureRandom();
//        secureRandom.nextBytes(ivBytes);
        Arrays.fill(ivBytes,(byte)0);
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,ivParameterSpec);
        byte[] raw=cipher.doFinal(crypt);
        return raw;
    }
}
