package sg.spring.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

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

  public List<User> selectAllUsers(int limit) {
    log.debug("serviceNumber:{}", serviceNumber);
    log.debug("limit:{}", limit);
    return userDao.selectAllUsers(limit);
  }

  public void insertUsers(List<User> list) {
    userDao.insertUsers(list);
  }
}
