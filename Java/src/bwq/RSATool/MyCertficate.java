package bwq.RSATool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class MyCertficate {
    private KeyStore ks;

    private char[] ksPassword = null;
    private char[] certPassword = null;
    private PrivateKey prikey;
    private Certificate cert;
    File certfile;

    public MyCertficate(File certfile) throws IOException, CertificateException{
        this.certfile=certfile;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = cf.generateCertificate(new FileInputStream(certfile));
    }


    public MyCertficate(File certfile, String keyStorePassword, String certificatePassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.certfile=certfile;
        if ((keyStorePassword == null) || keyStorePassword.trim().equals("")) {
            ksPassword = null;
        } else {
            ksPassword = keyStorePassword.toCharArray();
        }
        if ((certificatePassword == null) || certificatePassword.trim().equals("")) {
            certPassword = null;
        } else {
            certPassword = certificatePassword.toCharArray();
        }

        ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(certfile);
        ks.load(fis, ksPassword);
        fis.close();
        Enumeration<String> enums = ks.aliases();
        String keyAlias = "";
        while (enums.hasMoreElements()) {
            keyAlias = enums.nextElement();
            if (ks.isKeyEntry(keyAlias)) {
                prikey = (PrivateKey) ks.getKey(keyAlias, certPassword);
                cert = (X509Certificate) ks.getCertificate(keyAlias);
                break;
            }
        }
    }

    public PrivateKey getPrivateKey() {
        return prikey;
    }

    public void showCertInfo(){
        if(cert!=null) {
            System.out.println("===================================证书信息=================================== \n" +this.certfile.getPath()+"\n"+ cert);
            System.out.println("===================================证书信息===================================");
        }else{
            System.out.println("证书信息无法读取，可能证书导出时开启了证书隐私");
        }
    }

    public X509Certificate getCertificate() {
        return (X509Certificate) cert;
    }
}