package sg.spring.cache;

import redis.clients.jedis.Jedis;

/**
 * Created by SG on 2016/11/16.
 */
public class MyJedis extends Jedis {
    public MyJedis(String host, int port) {
        super(host, port);
    }

    public MyJedis(String host, int port, String password) {
        super(host, port);
        auth(password);
    }
}
