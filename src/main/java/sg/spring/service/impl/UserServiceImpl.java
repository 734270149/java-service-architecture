package sg.spring.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sg.domain.User;
import sg.mybatis.dao.UserDao;
import sg.spring.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by SG on 2016/5/15.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Value("${serviceNumber}")
    private String serviceNumber;

    public List<User> selectAllUsers(int limit) {
        System.out.println("serviceNumber:" + serviceNumber);
        return userDao.selectAllUsers(limit);
    }

    public void insertUsers(List<User> list) {
        userDao.insertUsers(list);
    }
}
