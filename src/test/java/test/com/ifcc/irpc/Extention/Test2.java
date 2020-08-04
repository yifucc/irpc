package test.com.ifcc.irpc.Extention;

import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.annotation.Inject;
import lombok.Data;

import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-27
 * @description
 */
@Cell
@Data
public class Test2 {
    @Inject
    List<Itest> list;

    @Inject("test")
    Itest test3;
}
