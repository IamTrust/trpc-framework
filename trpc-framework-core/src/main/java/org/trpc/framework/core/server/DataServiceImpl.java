package org.trpc.framework.core.server;

import org.trpc.framework.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程调用中服务端本地的方法
 */
public class DataServiceImpl implements DataService {
    @Override
    public String sendData(String body) {
        System.out.println("body: " + body);
        return "success";
    }

    @Override
    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("data1");
        list.add("data2");
        list.add("data3");
        return list;
    }

    @Override
    public String getDataWithException(boolean hasException) throws Exception {
        if (hasException)
            throw new Exception("getDataWithException 抛出了异常");
        return "called getDataWithException";
    }
}
