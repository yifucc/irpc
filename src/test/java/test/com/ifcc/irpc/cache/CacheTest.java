package test.com.ifcc.irpc.cache;

import com.ifcc.irpc.cache.Cache;
import com.ifcc.irpc.cache.decorators.ScheduledCache;
import com.ifcc.irpc.cache.decorators.SoftCache;
import com.ifcc.irpc.cache.impl.IrpcCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description
 */
public class CacheTest {

    @Test
    void test1() {
        SoftCache<String, String> softCache = new SoftCache<>(new IrpcCache());
        softCache.put("name", "ifcc");
        softCache.put("age", "26");
        softCache.put("region", "hz");
        System.out.println(softCache.size());
        System.out.println(softCache.get("age"));
        System.out.println(softCache.get("name"));
        System.out.println(softCache.remove("name"));
        System.out.println(softCache.get("name"));
    }

    @Test
    void test2() throws InterruptedException {
        ScheduledCache<String, String> cache = new ScheduledCache(new IrpcCache());
        cache.setClearInterval(TimeUnit.SECONDS.toMillis(5));
        cache.put("name", "ifcc");
        System.out.println(cache.get("name"));
        Thread.sleep(10 * 1000);
        System.out.println(cache.get("name"));
    }
}
