package bwq.BlobStore;

import java.util.List;

public class DbBlobStore implements IBlobStore {
    @Override
    public String getType() {
        return null;
    }

    @Override
    public int load() {
        return 0;
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int copyTo(IBlobStore other) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String[] listAliases() {
        return new String[0];
    }

    @Override
    public boolean containsAlias(String alias) {
        return false;
    }

    @Override
    public boolean isEncrypted(String alias) {
        return false;
    }

    @Override
    public List<String> findAlias(byte[] value) {
        return null;
    }

    @Override
    public int setBlob(String alias, String value) {
        return 0;
    }

    @Override
    public int setBlob(String alias, byte[] value) {
        return 0;
    }

    @Override
    public byte[] getBlob(String alias) {
        return new byte[0];
    }

    @Override
    public String getBlobAsString(String alias) {
        return null;
    }

    @Override
    public int setEncryptedBlob(String alias, String entryPassword, String value) {
        return 0;
    }

    @Override
    public int setEncryptedBlob(String alias, String entryPassword, byte[] value) {
        return 0;
    }

    @Override
    public byte[] getEncryptedBlob(String alias, String entryPassword) {
        return new byte[0];
    }

    @Override
    public String getEncryptedBlobAsString(String alias, String entryPassword) {
        return null;
    }

    @Override
    public int deleteBlob(String alias) {
        return 0;
    }

    @Override
    public int clearAll() {
        return 0;
    }
}
