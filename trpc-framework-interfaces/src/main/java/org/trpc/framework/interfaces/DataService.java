package org.trpc.framework.interfaces;

import java.util.List;

public interface DataService {

    /**
     * 发送数据
     *
     * @param body
     */
    String sendData(String body);

    /**
     * 获取数据
     *
     * @return
     */
    List<String> getList();

    /**
     * 测试抛出异常
     *
     * @return
     */
    String getDataWithException(boolean hasException) throws Exception;

}
