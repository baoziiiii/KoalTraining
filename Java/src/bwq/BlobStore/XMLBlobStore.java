package bwq.BlobStore;

import bwq.BlobStore.Exception.AliasNotExistException;
import bwq.BlobStore.Exception.EncryptionErrorException;
import bwq.BlobStore.Exception.EntryNotEncryptedException;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLBlobStore implements IBlobStore {

    private String storePath;
    private Element root;
    private Document document;

    private static final String ROOT_NAME="XMLBlobStore";
    private static final String ATTR_IS_CRYPT="isCrypt";
    private static final String BOOL_TRUE="true";
    private static final String BOOL_FALSE="false";

    private BlobStoreEncryption blobStoreEncryption=BlobStoreEncryption.getInstance();

    /**
     *  构造器
     * @param storePath 文件路径
     * @param newone
     *      true    创建新文件
     *      false   读取
     *
     *  @return  int
     *      0       读取失败
     *      1       读取成功
     */
    public XMLBlobStore(String storePath,Boolean newone) throws DocumentException {
        this.storePath=storePath;
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        if(newone){
            document = DocumentHelper.createDocument();
            root = document.addElement( ROOT_NAME);
        }else {
            document = reader.read(new File(storePath));
            //获取根节点元素对象
            checkXMLValidity(document);
        }
    }

    @Override
    public String getType() {
        return "XMLBlobStore";
    }

    /**
     *  检查XML文件是否符合BlobStore规则
     *  @throws DocumentException
     */
    private void checkXMLValidity(Document document)throws DocumentException{
        root = document.getRootElement();
        if(!ROOT_NAME.equals(root.getName()))
            throw new DocumentException();
        else{
            Iterator<Element> it=root.elementIterator();
            while(it.hasNext()){
                Element e=it.next();
                if(e.attributeValue(ATTR_IS_CRYPT)==null){
                    throw new DocumentException();
                }
            }
        }
    }

    /**
     *  读取
     *  @return  int
     *      0       读取失败
     *      1       读取成功
     */
    @Override
    public int load() {
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = null;
        try {
            document = reader.read(new File(storePath));
        } catch (DocumentException e) {
            return 0;
        }
        //获取根节点元素对象
        root = document.getRootElement();
        return 1;
    }

    /**
     *  保存
     *  @return  int
     *      0       保存失败
     *      1       保存成功
     */
    @Override
    public int save() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置编码
        format.setEncoding("UTF-8");
        // 创建XMLWriter对象,指定了写出文件及编码格式
        // XMLWriter writer = new XMLWriter(new FileWriter(new
        // File("src//a.xml")),format);
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(storePath)), "UTF-8"), format);
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                return 0;
            }
        }
        // 关闭操作
        return 1;
    }

    /**
     *  返回所有别名
     * @return String[] 所有别名数组
     */
    @Override
    public String[] listAliases() {
        List<Element> elementList =root.elements();
        Iterator<Element> it=root.elementIterator();
        int size=elementList.size();
        String[] aliases=new String[size];
        int i=0;
        for (Element e;i<size;i++) {
            e=it.next();
            aliases[i]=e.getName();
        }
        return aliases;
    }

    /**
     *  查询是否含有别名
     * @return boolean
     */
    @Override
    public boolean containsAlias(String alias) {
        return findElementByAlias(alias)!=null;
    }



    /**
     *  查询条目数量
     * @return int
     */
    @Override
    public int size() {
        return root.elements().size();
    }

    /**
     *  查询指定别名数据是否加密
     * @return boolean
     */
    @Override
    public boolean isEncrypted(String alias) {
        Element e= findElementByAlias(alias);
        return BOOL_TRUE.equals(e.attributeValue(ATTR_IS_CRYPT));
    }

    /**
     *  查询数据对应的所有别名
     * @return List<String>
     */
    @Override
    public List<String> findAlias(byte[] value) {
        Iterator<Element> it=root.elementIterator();

        String b64=Base64.encodeBase64String(value);

        List<String> aliases=new ArrayList<>();
        while(it.hasNext()){
            Element e = it.next();
            String etext=e.getText();
            if(b64.equals(etext)){
                aliases.add(e.getName());
            }
        }
        return aliases;
    }

    /**
     *  添加别名明文数据条目
     * @param alias 别名
     * @param value 数据(会通过base64编码后存储)
     *
     * @return int
     *      1   添加成功
     *      0   添加失败
     */
    @Override
    public int setBlob(String alias, String value) {
        if(containsAlias(alias))
            return 0;
        Element e = root.addElement( alias );
        e.addAttribute(ATTR_IS_CRYPT,BOOL_FALSE);
        e.addText(Base64.encodeBase64String(value.getBytes()));
        return 1;
    }

    /**
     *  添加别名明文数据条目
     * @param alias 别名
     * @param value 数据(会通过base64编码后存储)
     *
     * @return int
     *      1   添加成功
     *      0   添加失败
     */

    @Override
    public int setBlob(String alias, byte[] value) {
        if(containsAlias(alias))
            return 0;
        Element e = root.addElement( alias );
        e.addAttribute(ATTR_IS_CRYPT,BOOL_FALSE);
        e.addText(Base64.encodeBase64String(value));
        return 1;
    }

    /**
     *  别名查询明文数据条目
     * @param alias 查询别名
     *
     * @return byte[] （已经过base64解码）
     */
    @Override
    public byte[] getBlob(String alias) {
        Element e= findElementByAlias(alias);
        return Base64.decodeBase64(e.getText());
    }

    /**
     *  别名查询明文数据条目
     * @param alias 查询别名
     *
     * @return String 原数据（已经过base64解码）
     */
    @Override
    public String getBlobAsString(String alias) {
        Element e= findElementByAlias(alias);
        return new String(Base64.decodeBase64(e.getText()));
    }

    /**
     *  添加新加密数据条目
     * @param alias 别名
     * @param entryPassword 密码
     * @param value  待加密数据
     *
     * @throws EncryptionErrorException
     *          加密异常
     * @return int
     *      1   添加成功
     *      0   添加失败
     */
    @Override
    public int setEncryptedBlob(String alias, String entryPassword, String value) throws EncryptionErrorException {
       return setEncryptedBlob(alias,entryPassword,value.getBytes());
    }

    /**
     *  添加新加密数据条目
     * @param alias 别名
     * @param entryPassword 密码
     * @param value  待加密数据
     *
     * @throws EncryptionErrorException
     *          加密异常
     * @return int
     *      1   添加成功
     *      0   添加失败
     */
    @Override
    public int setEncryptedBlob(String alias, String entryPassword, byte[] value) throws EncryptionErrorException {
        try {
            byte[] crypt=blobStoreEncryption.encrypt(value,entryPassword);
            if(containsAlias(alias))
                return 0;
            Element e = root.addElement( alias );
            e.addAttribute(ATTR_IS_CRYPT,BOOL_TRUE);
            e.addText(Base64.encodeBase64String(crypt));
        } catch (Exception e){
            e.printStackTrace();
            throw new EncryptionErrorException();
        }
        return 1;
    }

    /**
     *  别名查询加密条目数据
     * @param alias 别名
     * @param entryPassword 密码
     *
     * @throws AliasNotExistException 别名不存在
     * @throws EntryNotEncryptedException 条目非加密数据
     * @throws EncryptionErrorException 加密异常
     *
     * @return byte[] 解密后数据
     */
    @Override
    public byte[] getEncryptedBlob(String alias, String entryPassword) throws AliasNotExistException,EntryNotEncryptedException,EncryptionErrorException {
        Element e = findElementByAlias(alias);
        byte[] raw;
        if(e==null)
            throw new AliasNotExistException();
        if(BOOL_FALSE.equals(e.attributeValue(ATTR_IS_CRYPT)))
            throw new EntryNotEncryptedException();
        byte[] crypt=Base64.decodeBase64(e.getText());
        try {
            raw=blobStoreEncryption.decrypt(crypt, entryPassword);
        }catch (Exception err){
            err.printStackTrace();
            throw new EncryptionErrorException();
        }
        return raw;
    }

    /**
     *  别名查询加密条目数据
     * @param alias 别名
     * @param entryPassword 密码
     *
     * @throws AliasNotExistException 别名不存在
     * @throws EntryNotEncryptedException 条目非加密数据
     * @throws EncryptionErrorException 加密异常
     *
     * @return String 解密后数据
     */
    @Override
    public String getEncryptedBlobAsString(String alias, String entryPassword) throws AliasNotExistException,EntryNotEncryptedException,EncryptionErrorException{
        return new String(getEncryptedBlob(alias,entryPassword));
    }

    /**
     *  删除条目
     * @param alias 别名
     *
     * @throws AliasNotExistException 别名不存在
     *
     * @return int
     *      1   删除成功
     *      0   删除失败
     */
    @Override
    public int deleteBlob(String alias) throws AliasNotExistException {
        Element e=findElementByAlias(alias);
        if(e==null)
            throw new AliasNotExistException();
        return root.remove(e)?1:0;
    }

    /**
     *  删除所有条目
     *
     * @return int
     *      1   删除成功
     *      0   删除失败
     */
    @Override
    public int clearAll() {
        Iterator<Element> it=root.elementIterator();
        while(it.hasNext()){
            root.remove(it.next());
        }
        return 1;
    }

    /**
     *  打印XML
     */
    public void printXML(){
        Iterator<Element> it=root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            System.out.println("<"+e.getName()+" "+ATTR_IS_CRYPT+"=\""+e.attributeValue(ATTR_IS_CRYPT)+"\">");
            System.out.println("\t"+e.getText());
            System.out.println("</"+e.getName()+">");
            System.out.println();
        }
    }

    /**
     *  拷贝
     */
    @Override
    public int copyTo(IBlobStore other) {
        XMLBlobStore xmlother=(XMLBlobStore)other;
        xmlother.document=(Document)(this.document.clone());
        xmlother.root=xmlother.document.getRootElement();
        return 1;
    }

    private Element findElementByAlias(String alias){
        Iterator<Element> it=root.elementIterator();
        while(it.hasNext()){
            Element e = it.next();
            if(e.getName().equals(alias)){
                return e;
            }
        }
        return null;
    }
}
