package bwq.BlobStore;

import bwq.BlobStore.Exception.XMLBlobStoreException;
import org.dom4j.DocumentException;

public class BlobStoreFactory {


    public static enum STORETYPE{DIR,REG,SQLITE,XML};

    /**
     *  构造器
     *  @param  type，支持以下4种
     *      "DIR"       文件系统（目录结构）
     *      "REG"       Windows注册表
     *      "SQLITE"    Sqlite数据库
     *      "XML"       XML文件
     *  @param  storePath，对应不同type的路径定义如下
     *      "DIR"       文件目录路径
     *      "REG"       Windows注册表路径
     *      "SQLITE"        Sqlite数据库文件路径
     *      "XML"       XML文件路径
     */
    public static IBlobStore createInstance(STORETYPE type, String storePath,Boolean newone) throws XMLBlobStoreException {
        switch (type){
            case SQLITE:
                return null;
            case XML:
                try {
                    return new XMLBlobStore(storePath,newone);
                } catch (DocumentException e) {
                    e.printStackTrace();
                    throw new XMLBlobStoreException();
                }
        }
        return null;
    }


}
