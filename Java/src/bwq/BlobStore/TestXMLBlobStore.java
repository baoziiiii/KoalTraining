package bwq.BlobStore;

import bwq.BlobStore.Exception.AliasNotExistException;
import bwq.BlobStore.Exception.EncryptionErrorException;
import bwq.BlobStore.Exception.EntryNotEncryptedException;
import bwq.BlobStore.Exception.XMLBlobStoreException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestXMLBlobStore {

    static final String teststr = "Hello world!";
    static final byte[] testbytes = {0x0, 0x1, 0x2, 0x3};
    static final String testpassword = "123456";
    static XMLBlobStore xmlbs;

    static {
        try {
            xmlbs = (XMLBlobStore) BlobStoreFactory.createInstance(BlobStoreFactory.STORETYPE.XML, "/Users/baowenqiang/测试/new.xml", false);
        } catch (XMLBlobStoreException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void SetBlob() {
        xmlbs.setBlob("alias1", "Hello world!");
        byte[] b = {0x0, 0x1, 0x2, 0x3};
        xmlbs.setBlob("alias2", b);
        xmlbs.save();
    }

    @Test
    public void ListAlias() {
        for (String s : xmlbs.listAliases()) {
            System.out.println(s);
        }
    }

    @Test
    public void ContainAlias() {
        System.out.println(xmlbs.containsAlias("alias1"));
    }

    @Test
    public void IsEncrypt() {
        System.out.println(xmlbs.isEncrypted("alias3"));
    }

    @Test
    public void FindAlias() {
        List<String> aliases = xmlbs.findAlias(teststr.getBytes());
        List<String> aliases2 = xmlbs.findAlias(testbytes);
        System.out.println(aliases.get(0));
        System.out.println(aliases2.get(0));
    }

    @Test
    public void GetBlob() {
        byte[] b = xmlbs.getBlob("alias2");
        System.out.println(Arrays.toString(b));
    }

    @Test
    public void GetBlobAsString() {
        System.out.println(xmlbs.getBlobAsString("alias1"));
    }

    @Test
    public void EncryptBlob() {
        try {
            xmlbs.setEncryptedBlob("alias3", testpassword, teststr);
            xmlbs.setEncryptedBlob("alias4", testpassword, testbytes);
            xmlbs.save();
            System.out.println(xmlbs.getEncryptedBlobAsString("alias3", testpassword));
            System.out.println(Arrays.toString(xmlbs.getEncryptedBlob("alias4", testpassword)));
        } catch (EncryptionErrorException e) {
            e.printStackTrace();
        } catch (EntryNotEncryptedException e) {
            e.printStackTrace();
        } catch (AliasNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Delete() {
        try {
            xmlbs.deleteBlob("alias1");
            xmlbs.save();
            for (String s : xmlbs.listAliases()) {
                System.out.println(s);
            }
        } catch (AliasNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Clear() {
        xmlbs.clearAll();
        xmlbs.save();
        for (String s : xmlbs.listAliases()) {
            System.out.println(s);
        }
    }

    @Test
    public void PrintXML() {
        xmlbs.printXML();
    }

    @Test
    public void Copy() {
        try {
            XMLBlobStore xmlbs2=(XMLBlobStore) BlobStoreFactory.createInstance(BlobStoreFactory.STORETYPE.XML,"/Users/baowenqiang/测试/new2.xml",true);
            xmlbs.copyTo(xmlbs2);
            xmlbs2.save();
            xmlbs2.printXML();
        } catch (XMLBlobStoreException e) {
            e.printStackTrace();
        }
    }
}