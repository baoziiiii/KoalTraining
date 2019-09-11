package bwq.RSATool;


import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

/**
  RSATool帮助：
    -s:  签名
    -v:  验证
    -e:  数字信封加密
    -d:  数字信封解密
    -n:  解析并展示证书扩展项
    -f:  用于操作的PFX证书或CER证书(-s和-d模式导入PFX证书，-v和-e模式导入CER证书)
    -i:  指定待签名(-s模式下)/待验签(-v模式下)/待加密(-e模式下)/待解密(-d模式下)文件
    -o:  指定生成文件

  范例流程（数字签名+数字信封）:
  签名 RSATool -s -f /.../cert1.pfx -i /.../input -o /.../signdata
  加密 RSATool -e -f /.../cert2.cer -i /.../signdata -o /.../crypt
  解密 RSATool -d -f /.../cert2.pfx -i /.../crypt -o /.../decrypt
  验签 RSATool -v -f /.../cert1.cer -i /.../decrypt -o /.../content
  查看 RSATool -v -f /.../xxx.cer -n
 */
public class RSATool {


    private enum Mode {SIGN, VERIFY,ENCRIPTION,DECRIPTION}

    private static File CERT_FILE=null;
    private static File INPUT_FILE=null;
    private static File OUTPUT_FILE=null;
//    static RSAUtils rsaUtils = RSAUtils.getRsaUtils();
    private static final String SIGN_ALGORITHM="SHA1withRSA";
    private static final String PROVIDER="BC";
    private static final String SIGN_FORMAT="PKCS#7";

    private static final String COMMAND_SIGN="-s";
    private static final String COMMAND_VERIFY="-v";
    private static final String COMMAND_ENCRIPTION="-e";
    private static final String COMMAND_DECRIPTION="-d";
    private static final String COMMAND_INFO="-n";
    private static final String COMMAND_INPUT="-i";
    private static final String COMMAND_OUTPUT="-o";
    private static final String COMMAND_CERT="-f";

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                exit(null);
            }
            Mode mode = null;
            Boolean showExtra=false;
            if (COMMAND_SIGN.equals(args[0])) {
                mode = Mode.SIGN;
            } else if (COMMAND_VERIFY.equals(args[0])) {
                mode = Mode.VERIFY;
            } else if(COMMAND_ENCRIPTION.equals(args[0])){
                mode = Mode.ENCRIPTION;
            } else if(COMMAND_DECRIPTION.equals(args[0])){
                mode = Mode.DECRIPTION;
            } else
                exit(null);

            for (int i = 1; i < args.length; i++) {
                if (COMMAND_CERT.equals(args[i])) {
                    if (i + 1 >= args.length)
                        exit("[Error] "+COMMAND_CERT+" PFX/CER证书路径");
                    if ((CERT_FILE = openFile(args[i + 1])) == null)
                        exit("[Error] PFX/CER证书路径不存在");
                } else if (COMMAND_INPUT.equals(args[i])) {
                    if (i + 1 >= args.length)
                        exit("[Error] "+COMMAND_INPUT+" 待签名/验签文件路径");
                    if ((INPUT_FILE = openFile(args[i + 1])) == null)
                        exit("[Error] 输入文件路径不存在");
                } else if (COMMAND_OUTPUT.equals(args[i])) {
                    if (i + 1 >= args.length)
                        exit("[Error] "+COMMAND_OUTPUT+" 生成签名文件路径");
                    OUTPUT_FILE = new File(args[i+1]);
                    if(!OUTPUT_FILE.getParentFile().exists())
                        OUTPUT_FILE.getParentFile().mkdirs();
                } else if(COMMAND_INFO.equals(args[i])){
                    showExtra=true;
                }
            }

            if(CERT_FILE==null){
                exit("[Error] 请指定证书文件:"+COMMAND_CERT+" PFX/CER证书路径");
            }
            if ((!showExtra)) {
                if (INPUT_FILE == null || OUTPUT_FILE == null) {
                    exit("[Error] 请指定导入文件和导出文件路径:"+COMMAND_INPUT+" 导入文件路径 "+COMMAND_OUTPUT+" 导出文件路径");
                }
            }

            Provider provider = new BouncyCastleProvider();
            // 添加BouncyCastle作为安全提供
            Security.addProvider(provider);
            MyCertficate myCertficate = null;

            switch (mode) {
                case SIGN:
                    try {
                        myCertficate = openPFXCertificate(CERT_FILE);
                        if(myCertficate.getCertificate()==null){
                            exit("提取证书信息失败，无法进行签名，可能证书导出时开启了证书隐私。");
                        }
                    } catch (IOException e) {
//                        e.printStackTrace();
                        exit("Keystore密码错误");
                    } catch (UnrecoverableKeyException e2) {
                        exit("证书密码错误");
                    } catch (Exception e2) {
                        exit("证书导入错误");
                    }
                    if(INPUT_FILE!=null&&OUTPUT_FILE!=null)
                        signWithPKCS7(myCertficate);
                    break;
                case VERIFY:
                    try {
                        myCertficate=openCERCertificate(CERT_FILE);
                    }catch (Exception e){
                        exit("证书导入错误");
                    }
                    if(INPUT_FILE!=null&&OUTPUT_FILE!=null)
                        verifyWithPKCS7(myCertficate);
                    break;
                case ENCRIPTION:
                    try {
                        myCertficate=openCERCertificate(CERT_FILE);
                    }catch (Exception e){
                        exit("证书导入错误");
                    }
                    if(INPUT_FILE!=null&&OUTPUT_FILE!=null)
                        encryptWithPKCS7(myCertficate);
                    break;
                case DECRIPTION:
                    try {
                        myCertficate = openPFXCertificate(CERT_FILE);
                        if(myCertficate.getCertificate()==null){
                            exit("提取证书信息失败，无法进行解密，可能证书导出时开启了证书隐私。");
                        }
                    } catch (IOException e) {
//                        e.printStackTrace();
                        exit("Keystore密码错误");
                    } catch (UnrecoverableKeyException e2) {
                        exit("证书密码错误");
                    } catch (Exception e2) {
                        exit("证书导入错误");
                    }
                    if(INPUT_FILE!=null&&OUTPUT_FILE!=null)
                        decryptWithPKCS7(myCertficate);
                    break;
            }
            if(showExtra)
                myCertficate.showCertInfo();
        } catch (ArrayIndexOutOfBoundsException e) {
            exit(null);
        } catch (Exception e) {
            e.printStackTrace();
            exit("[Error]未知错误");
        }
    }

    private static void signWithPKCS7(MyCertficate myCertficate){

        FileInputStream fin = null;
        FileOutputStream fout =null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ContentSigner sha1Signer=null;
        try {
            fin = new FileInputStream(INPUT_FILE);
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = fin.read(buf)) != -1) {
                bout.write(buf, 0, length);
            }
            CMSTypedData msg = new CMSProcessableByteArray(
                    bout.toByteArray());
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            sha1Signer = new JcaContentSignerBuilder(
                    SIGN_ALGORITHM).setProvider(PROVIDER).build(myCertficate.getPrivateKey());
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().setProvider(PROVIDER)
                            .build()).build(sha1Signer,myCertficate.getCertificate()));
            CMSSignedData sigData = gen.generate(msg, true);
            fout = new FileOutputStream(OUTPUT_FILE);
            fout.write(sigData.getEncoded());
        } catch (Exception e1) {
//            e1.printStackTrace();
            exit("[Error]文件签名异常");
        }
        try {
            fin.close();
            fout.close();
            bout.close();
            System.out.println("成功生成签名文件:"+OUTPUT_FILE+"\n"+"签名算法:"+sha1Signer.getAlgorithmIdentifier().getAlgorithm()+"\n"+"签名规范:"+SIGN_FORMAT);
        } catch (Exception e) {
            exit("[Error]文件操作异常");
        }
    }

    private static void verifyWithPKCS7(MyCertficate myCertficate){
        FileInputStream fin = null;
        FileOutputStream fout = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            fin = new FileInputStream(INPUT_FILE);
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = fin.read(buf)) != -1) {
                bout.write(buf, 0, length);
            }

            CMSSignedData sign = new CMSSignedData(bout.toByteArray());
            SignerInformationStore signers = sign.getSignerInfos();
            Collection c = signers.getSigners();
            Iterator it = c.iterator();
            while (it.hasNext()) {
                SignerInformation signer = (SignerInformation) it.next();
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
                        .setProvider(PROVIDER).build(myCertficate.getCertificate()))) {
                    System.out.println(INPUT_FILE+":验签成功");
                    byte[] data=(byte[]) sign.getSignedContent().getContent();
                    fout=new FileOutputStream(OUTPUT_FILE);
                    fout.write(data);
                } else {
                    exit(INPUT_FILE+":验签失败");
                }

            }
        } catch (Exception e1) {
            exit("[Error]文件签名异常");
        }
        try {
            fin.close();
            bout.close();
            fout.close();
            System.out.println("成功导出解签后文件:"+OUTPUT_FILE);
        } catch (Exception e) {
            exit("[Error]文件操作异常");
        }
    }

    private static void encryptWithPKCS7(MyCertficate myCertficate){
        FileInputStream fin = null;
        FileOutputStream fout =null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        CMSEnvelopedData ed = null;
        try {
            fin = new FileInputStream(INPUT_FILE);
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = fin.read(buf)) != -1) {
                bout.write(buf, 0, length);
            }
            //添加数字信封
            CMSTypedData msg = new CMSProcessableByteArray(bout.toByteArray());
            CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
            edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(
                    myCertficate.getCertificate()).setProvider(PROVIDER));
            ed = edGen.generate(msg,
                    new JceCMSContentEncryptorBuilder(PKCSObjectIdentifiers.rc4)
                            .setProvider(PROVIDER).build());
            fout = new FileOutputStream(OUTPUT_FILE);
            fout.write(ed.getEncoded());
        }catch (Exception e){
            exit("[Error]文件加密异常");
        }
        try {
            fin.close();
            fout.close();
            bout.close();
            System.out.println("成功生成数字信封:"+OUTPUT_FILE+"\n"+"加密算法:"+ed.getContentEncryptionAlgorithm().getAlgorithm());
        } catch (Exception e) {
            exit("[Error]文件操作异常");
        }
    }

    private static void decryptWithPKCS7(MyCertficate myCertficate){
        FileInputStream fin = null;
        FileOutputStream fout =null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            fin = new FileInputStream(INPUT_FILE);
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = fin.read(buf)) != -1) {
                bout.write(buf, 0, length);
            }
            //获取密文
            CMSEnvelopedData ed = new CMSEnvelopedData(bout.toByteArray());
            RecipientInformationStore recipients = ed.getRecipientInfos();
            Collection c = recipients.getRecipients();
            Iterator it = c.iterator();
            byte[] recData = null;
            //解密
            if (it.hasNext()) {
                RecipientInformation recipient = (RecipientInformation) it.next();
                recData = recipient.getContent(new JceKeyTransEnvelopedRecipient(
                        myCertficate.getPrivateKey()).setProvider(PROVIDER));
            }
            fout = new FileOutputStream(OUTPUT_FILE);
            fout.write(recData);
        }catch (Exception e) {
            exit("[Error]文件解密异常");
        }
        try {
            fin.close();
            fout.close();
            bout.close();
            System.out.println("解密导出文件:"+OUTPUT_FILE);
        } catch (Exception e) {
            exit("[Error]文件操作异常");
        }

    }

    private static MyCertficate openPFXCertificate(File certfile) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        System.out.print("请输入Keystore密码:");
        Scanner scanner = new Scanner(System.in);
        String ksPassword = scanner.nextLine();
        System.out.print("请输入证书密码:");
        String certPassword = scanner.nextLine();
        MyCertficate myCertficate = null;
        myCertficate=new MyCertficate(certfile, ksPassword, certPassword);
        return myCertficate;
    }

    private static MyCertficate openCERCertificate(File certfile) throws IOException, CertificateException {
        return new MyCertficate(certfile);
    }

    private static File openFile(String path) {
        File f = new File(path);
        if (f.exists())
            return f;
        else
            return null;
    }

    private static void exit(String msg) {
        if (msg != null) {
            System.out.println(msg);
        } else {
            System.out.println("RSATool帮助：\n" +
                    "  "+COMMAND_SIGN+":  签名\n" +
                    "  "+COMMAND_VERIFY+":  验证\n" +
                    "  "+COMMAND_ENCRIPTION+":  数字信封加密\n" +
                    "  "+COMMAND_DECRIPTION+":  数字信封解密\n" +
                    "  "+COMMAND_INFO+":  展示证书信息\n" +
                    "  "+COMMAND_CERT+":  用于操作的PFX证书或CER证书(-s和-d模式导入PFX证书，-v和-e模式导入CER证书)\n" +
                    "  "+COMMAND_INPUT+":  指定待签名(-s模式下)/待验签(-v模式下)/待加密(-e模式下)/待解密(-d模式下)文件\n" +
                    "  "+COMMAND_OUTPUT+":  指定生成文件\n\n" +
                    "范例流程（数字签名+数字信封）:\n" +
                    "签名 RSATool -s -f /.../cert1.pfx -i /.../input -o /.../signdata\n"+
                    "加密 RSATool -e -f /.../cert2.cer -i /.../signdata -o /.../crypt\n"+
                    "解密 RSATool -d -f /.../cert2.pfx -i /.../crypt -o /.../decrypt\n"+
                    "验签 RSATool -v -f /.../cert1.cer -i /.../decrypt -o /.../content\n"+
                    "查看 RSATool -v -f /.../xxx.cer -n"
            );
        }
        System.exit(0);
    }
}


