package bwq.HMAC;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/*
 *  HMAC-SHA256 / HMAC-MD5 实验
 */

public class HMAC {

    private static String src="http://www.koal.com";
    private static String customKey="ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"; //自定义密钥256位

    public static void jdkHmacSHA256(){
        try {

            KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey=keyGenerator.generateKey();  //默认密钥长度与SHA摘要长度一致：256位。
            byte[] keyBytes = secretKey.getEncoded();
            Mac mac=Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] macBytes=mac.doFinal(src.getBytes());
            System.out.println("jdk HmacSHA256\ndefault keysize:"+keyBytes.length*8+"b\nkey:"+Hex.encodeHexString(keyBytes)+"\nHMAC:"+Hex.encodeHexString(macBytes));

            keyBytes=Hex.decodeHex(customKey.toCharArray());//使用自定义密钥customKey
            SecretKeySpec secretKeySpec=new SecretKeySpec(keyBytes,"HmacSHA256");
            mac.init(secretKeySpec);
            macBytes=mac.doFinal(src.getBytes());
            System.out.println("key:"+Hex.encodeHexString(keyBytes)+"\nHMAC:"+Hex.encodeHexString(macBytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void jdkHmacMD5(){
        try {

            KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacMD5");
            SecretKey secretKey=keyGenerator.generateKey();  //HmacMD5默认密钥长度512位
            byte[] keyBytes=secretKey.getEncoded();
            System.out.println("\njdk HmacMD5\ndefault keysize:"+keyBytes.length*8+"b\n"+"key:"+Hex.encodeHexString(keyBytes));
            Mac mac=Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] macBytes=mac.doFinal(src.getBytes());
            System.out.println("HMAC:"+Hex.encodeHexString(macBytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        jdkHmacSHA256();
        jdkHmacMD5();
    }
}
