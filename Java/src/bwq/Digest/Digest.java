package bwq.Digest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 *  摘要算法SHA/MD5实验
 */

public class Digest {

    private static String s="http://www.koal.com";

    @org.junit.Test
    public static void jdkSHA(){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(s.getBytes());
            byte[] shaBytes=messageDigest.digest();
            System.out.println("jdk SHA-256:"+Hex.encodeHexString(shaBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public static void ccSHA(){
        System.out.println("CC SHA-256:"+DigestUtils.sha256Hex(s));
    }

    //对MD5算法简要的叙述可以为：MD5以512位分组来处理输入的信息，且每一分组又被划分为16个32位子分组，经过了一系列的处理后，算法的输出由四个32位分组组成，
    //将这四个32位分组级联后将生成一个128位散列值。

    public static void jdkMD5(){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            byte[] md5Bytes=messageDigest.digest(s.getBytes());
            System.out.println("jdk MD5:"+Hex.encodeHexString(md5Bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void ccMD5(){
        System.out.println("cc MD5:"+DigestUtils.md5Hex(s.getBytes()));
    }

    public static void main(String[] args) {
        System.out.println("src:"+s);
        jdkSHA();
        ccSHA();
        jdkMD5();
        ccMD5();
    }
}
