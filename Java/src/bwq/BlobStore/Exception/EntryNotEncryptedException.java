package bwq.BlobStore.Exception;

public class EntryNotEncryptedException extends Exception{
    @Override
    public void printStackTrace() {
        System.out.println("该条目非加密数据");
    }
}
