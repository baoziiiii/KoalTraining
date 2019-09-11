package bwq.BlobStore;

import bwq.BlobStore.Exception.AliasNotExistException;
import bwq.BlobStore.Exception.EncryptionErrorException;
import bwq.BlobStore.Exception.EntryNotEncryptedException;

import java.util.List;

interface IBlobStore {
    public String getType();

    /* 持久化 */
    int load();
    int save();

    /* 将所有Key/Value复制到另一个Store（可以是不同类型的Store） */
    int copyTo(IBlobStore other);

    /* 遍历与查找 */
    int size();                              //获取容量
    String[] listAliases();                  //获取名称列表
    boolean containsAlias(String alias);     //判断存在性
    boolean isEncrypted(String alias);       //判断条目是否加密
    List<String> findAlias(byte[] value);              //根据二进制内容查找名称

    /* 明文数据操作 */
    int setBlob(String alias, String value);
    int setBlob(String alias, byte[] value);
    byte[] getBlob(String alias);
    String getBlobAsString(String alias);

    /* 密文数据操作（固定使用SM4或AES256-CBC加密，密钥使用password的MD5摘要值） */
    int setEncryptedBlob(String alias, String entryPassword, String value) throws EncryptionErrorException;
    int setEncryptedBlob(String alias, String entryPassword, byte[] value) throws EncryptionErrorException;
    byte[] getEncryptedBlob(String alias, String entryPassword) throws AliasNotExistException, EntryNotEncryptedException, EncryptionErrorException;
    String getEncryptedBlobAsString(String alias, String entryPassword) throws AliasNotExistException, EntryNotEncryptedException, EncryptionErrorException;

    /* 删除 */
    int deleteBlob(String alias) throws AliasNotExistException;
    int clearAll();
}




