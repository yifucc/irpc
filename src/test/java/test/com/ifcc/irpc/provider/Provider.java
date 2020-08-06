package test.com.ifcc.irpc.provider;

import com.ifcc.irpc.spi.annotation.Cell;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
@Cell
public class Provider implements Iprovider {
    @Override
    public String hello(String name) {
        return name;
    }
}
