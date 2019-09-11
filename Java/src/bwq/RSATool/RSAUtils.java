package bwq.RSATool;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;

//-s -f /Users/baowenqiang/证书/bwq-signature.pfx -i /Users/baowenqiang/测试/input.txt -o /Users/baowenqiang/测试/signdata
//-e -f /Users/baowenqiang/证书/lwb1.cer -i /Users/baowenqiang/测试/signdata -o /Users/baowenqiang/测试/crypt
//-d -f /Users/baowenqiang/证书/lwb1.pfx -i /Users/baowenqiang/测试/crypt -o /Users/baowenqiang/测试/decrypt
//-v -f /Users/baowenqiang/证书/bwq-signature.cer -i /Users/baowenqiang/测试/decrypt -o /Users/baowenqiang/测试/content

public class RSAUtils {

    private static RSAUtils rsaUtils = new RSAUtils();


    private RSAUtils() {}

    public static RSAUtils getRsaUtils() {
        return rsaUtils;
    }


    //签名
    public byte[] sign(byte[] content,PrivateKey privateKey) throws Exception {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ByteArrayOutputStream bosencrypt=new ByteArrayOutputStream();
        for (int i = 0; i < content.length; i+=117) {
            if(i+117>content.length){
                bos.write(content,i,content.length-i);
            }else {
                bos.write(content, i, 117);
            }
            bosencrypt.write(RSASign(bos.toByteArray(),privateKey));
            bos.reset();
        }
        byte[] output=bosencrypt.toByteArray();
        bos.close();
        bosencrypt.close();
        return bosencrypt.toByteArray();
    }

    //验证
    public byte[] verify(byte[] signedContent,PublicKey publicKey)throws Exception{
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ByteArrayOutputStream bosencrypt=new ByteArrayOutputStream();
        for (int i = 0; i < signedContent.length; i+=128) {
            if(i+128>signedContent.length){
                bos.write(signedContent,i,signedContent.length-i);
            }else {
                bos.write(signedContent, i, 128);
            }
            bosencrypt.write(RSAVerify(bos.toByteArray(),publicKey));
            bos.reset();
        }
        byte[] output=bosencrypt.toByteArray();
        bos.close();
        bosencrypt.close();
        return bosencrypt.toByteArray();
    }


    //加密
    public byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ByteArrayOutputStream bosencrypt=new ByteArrayOutputStream();
        for (int i = 0; i < content.length; i+=117) {
            if(i+117>content.length){
                bos.write(content,i,content.length-i);
            }else {
                bos.write(content, i, 117);
            }
            bosencrypt.write(RSAEncrypt(bos.toByteArray(),publicKey));
            bos.reset();
        }
        byte[] output=bosencrypt.toByteArray();
        bos.close();
        bosencrypt.close();
        return bosencrypt.toByteArray();
    }


    public byte[] decrypt(byte[] crypt,PrivateKey privateKey) throws Exception {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ByteArrayOutputStream bosencrypt=new ByteArrayOutputStream();
        for (int i = 0; i < crypt.length; i+=128) {
            if(i+128>crypt.length)
                bos.write(crypt,i, crypt.length-i);
            else {
                bos.write(crypt,i,128);
            }
            bosencrypt.write(RSADecrypt(bos.toByteArray(),privateKey));
            bos.reset();
        }
        byte[] output=bosencrypt.toByteArray();
        bos.close();
        bosencrypt.close();
        return bosencrypt.toByteArray();
    }


    //私钥签名
    private byte[] RSASign(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }


    private byte[] RSAVerify(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    //公钥加密
    private byte[] RSAEncrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    //私钥解密
    private byte[] RSADecrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }
}
