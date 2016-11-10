package sg.spring.service.impl;

import com.alibaba.fastjson.JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

import redis.clients.jedis.Jedis;
import sg.domain.User;
import sg.mybatis.dao.UserDao;
import sg.spring.service.UserService;

/**
 * Created by SG on 2016/5/15.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

  private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

  @Resource
  private UserDao userDao;

  @Value("${serviceNumber}")
  private String serviceNumber;

  @Resource
  private RedisTemplate<String, String> redisTemplate;

  @Resource
  private Jedis jedis;

  public List<User> selectAllUsers(int limit) {
    log.debug("serviceNumber:{}", serviceNumber);
    log.debug("limit:{}", limit);
    List<User> users = userDao.selectAllUsers(limit);
    String jsonString = JSON.toJSONString(users);
    jedis.hset("a", "a", jsonString);
    return users;
  }

  public List<User> selectUsersFromRedis() {
    String s = jedis.hget("a", "a");
    List<User> users = JSON.parseArray(s, User.class);
    return users;
  }
}
