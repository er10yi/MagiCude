package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 贰拾壹
 * @create 2019-07-01 17:12
 */
public class NameSuffix {
    public static String gen() {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateSuffix = dateFormat.format(today);
        return dateSuffix + "_" + new IdWorker(1, 1).nextId();
    }
}
