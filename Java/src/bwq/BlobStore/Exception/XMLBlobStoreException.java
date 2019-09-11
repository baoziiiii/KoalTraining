package bwq.BlobStore.Exception;

public class XMLBlobStoreException extends Exception{
    @Override
    public void printStackTrace() {
        System.out.println("XML文件不符合BlobStore格式，无法打开！");
    }
}
