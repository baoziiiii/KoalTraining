package bwq.SymmetricEncription;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/*
 * AES 加解密工具类
 */

public class AESUtil {

    //分组密码有常用四种工作模式-->>ECB：电子密码本模式（简单，不安全）、CBC：加密分组链接模式（比ECB安全，openssl标配）、CFB：加密反馈模式（适合处理流数据）、OFB：输出反馈模式（适合处理流数据）
    public static final String CIPHER_MODE_ECB ="AES/ECB/";
    public static final String CIPHER_MODE_CBC ="AES/CBC/";
    public static final String CIPHER_MODE_CFB ="AES/CFB/";
    public static final String CIPHER_MODE_OFB ="AES/OFB/";

    public static final String PADDING_PKCS5="PKCS5Padding";
    public static final String PADDING_NO="NoPadding";
    public static final String PADDING_ISO10126="ISO10126";


    private static SecretKey generateKey(String keyHexStr) throws Exception{
        byte input[]= Hex.decodeHex(keyHexStr);
        SecretKeySpec secretKeySpec=new SecretKeySpec(input,"AES");
        return secretKeySpec;
    }

    /*
     * Using modes such as CFB and OFB, block ciphers can encrypt data in units smaller than the cipher's actual block size.
     * When requesting such a mode, you may optionally specify the number of bits to be processed at a time by appending this number
     * to the mode name as shown in the "AES/CFB8/NoPadding" and "AES/OFB32/PKCS5Padding" transformations. If no such number is specified,
     * a provider-specific default is used. Thus, block ciphers can be turned into byte-oriented stream ciphers by using an 8 bit mode
     * such as CFB8 or OFB8.
     */

    private static String encryptECB(String data,String keystr,String padding)throws Exception{
        Cipher cipher=Cipher.getInstance(CIPHER_MODE_ECB+padding);
        cipher.init(Cipher.ENCRYPT_MODE,generateKey(keystr));
        byte[] result=cipher.doFinal(data.getBytes());
        System.out.println("binary crypt("+cipher.getAlgorithm()+"):"+Hex.encodeHexString(result));
        return Base64.encodeBase64String(result);
    }

    public static String decryptECB(String base64crypt,String key,String padding)throws Exception{
        Cipher cipher=Cipher.getInstance(CIPHER_MODE_ECB+padding);
        cipher.init(Cipher.DECRYPT_MODE,generateKey(key));
        byte[] result=cipher.doFinal(Base64.decodeBase64(base64crypt.getBytes()));
        return new String(result);
    }

    /*
        在CBC/CFB/OFB（不光是DES算法）模式下，iv通过随机数（或伪随机）机制产生是一种比较常见的方法。iv的作用主要是用于产生密文的第一个block，以使最终生成的密文产生差异（明文相同的情况下），
        使密码攻击变得更为困难，除此之外iv并无其它用途。因此iv通过随机方式产生是一种十分简便、有效的途径。此外，在IPsec中采用了DES-CBC作为缺省的加密方式，其使用的iv是通讯包的时间戳。
        从原理上来说，这与随机数机制并无二致。
    */
    private static String encryptCBC(String data,String keystr,byte[] ivBytes,String padding)throws Exception{
        return encryptWithIv(data,keystr,ivBytes,CIPHER_MODE_CBC+padding);
    }


    public static String decryptCBC(String base64crypt,String keystr,byte[] ivBytes,String padding)throws Exception{
        return decryptWithIv(base64crypt,keystr,ivBytes,CIPHER_MODE_CBC+padding);
    }

    public static String encryptCFB(String data,String keystr,byte[] ivBytes,String padding)throws Exception{
        return encryptWithIv(data,keystr,ivBytes,CIPHER_MODE_CFB+padding);
    }

    public static String decryptCFB(String data,String keystr,byte[] ivBytes,String padding)throws Exception{
        return decryptWithIv(data,keystr,ivBytes,CIPHER_MODE_CFB+padding);
    }

    public static String encryptOFB(String data,String keystr,byte[] ivBytes,String padding)throws Exception{
        return encryptWithIv(data,keystr,ivBytes,CIPHER_MODE_OFB+padding);
    }

    public static String decryptOFB(String data,String keystr,byte[] ivBytes,String padding)throws Exception{
        return decryptWithIv(data,keystr,ivBytes,CIPHER_MODE_OFB+padding);
    }

    private static String encryptWithIv(String data,String keystr,byte[] ivBytes,String mode)throws Exception{
        Cipher cipher=Cipher.getInstance(mode);
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE,generateKey(keystr),ivParameterSpec);
        byte[] crypt=cipher.doFinal(data.getBytes());
        System.out.println("binary crypt("+cipher.getAlgorithm()+"):"+Hex.encodeHexString(crypt));
        return Base64.encodeBase64String(crypt);
    }

    private static String decryptWithIv(String base64crypt,String keystr,byte[] ivBytes,String mode)throws Exception{
        Cipher cipher=Cipher.getInstance(mode);
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE,generateKey(keystr),ivParameterSpec);
        byte[] result=cipher.doFinal(Base64.decodeBase64(base64crypt));
        return new String(result);
    }

    public static void main(String[] args)throws Exception{


        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(128);  //AES支持128或192或256位
        byte[] keyCoded=keyGenerator.generateKey().getEncoded();
        String keystr=Hex.encodeHexString(keyCoded);

        System.out.println("key:"+Arrays.toString(Hex.decodeHex(keystr)));
        System.out.println("keysize:"+keystr.length()*4+"b");
        String data="0123456789abcdef";
        System.out.println("messege:"+data+"\n");


        System.out.println(CIPHER_MODE_ECB+PADDING_PKCS5);
        String base64crypt=encryptECB(data,keystr,PADDING_PKCS5);
        System.out.println("base64 crypt("+CIPHER_MODE_ECB+PADDING_PKCS5+"):"+base64crypt);
        System.out.println("decrypted:"+decryptECB(base64crypt,keystr,PADDING_PKCS5)+"\n");

        //CBC/CFB/OFB需要iv参数
        System.out.println(CIPHER_MODE_CBC+PADDING_PKCS5);
        byte[] ivBytes=new byte[16];   //AES iv为16位
        SecureRandom secureRandom=new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        System.out.println("iv:"+Hex.encodeHexString(ivBytes));
        base64crypt=encryptCBC(data,keystr,ivBytes,PADDING_PKCS5);
        System.out.println("base64 crypt("+CIPHER_MODE_CBC+PADDING_PKCS5+"):"+base64crypt);
        System.out.println("decrypted:"+decryptCBC(base64crypt,keystr,ivBytes,PADDING_PKCS5)+"\n");

        System.out.println(CIPHER_MODE_CFB+PADDING_PKCS5);
        secureRandom.nextBytes(ivBytes);
        System.out.println("iv:"+Hex.encodeHexString(ivBytes));
        base64crypt=encryptCFB(data,keystr,ivBytes,PADDING_PKCS5);
        System.out.println("base64 crypt("+CIPHER_MODE_CFB+PADDING_PKCS5+"):"+base64crypt);
        System.out.println("decrypted:"+decryptCFB(base64crypt,keystr,ivBytes,PADDING_PKCS5)+"\n");

        System.out.println(CIPHER_MODE_OFB+PADDING_PKCS5);
        secureRandom.nextBytes(ivBytes);
        System.out.println("iv:"+Hex.encodeHexString(ivBytes));
        base64crypt=encryptOFB(data,keystr,ivBytes,PADDING_PKCS5);
        System.out.println("base64 crypt("+CIPHER_MODE_OFB+PADDING_PKCS5+"):"+base64crypt);
        System.out.println("decrypted:"+decryptOFB(base64crypt,keystr,ivBytes,PADDING_PKCS5)+"\n");

    }
}
