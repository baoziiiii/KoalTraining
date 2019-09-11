package bwq.BlobStore.Exception;

public class AliasNotExistException extends Exception{
    @Override
    public void printStackTrace() {
        System.out.println("alias不存在");
    }
}
