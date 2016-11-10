package sg.spring.service;

import java.util.List;

import sg.domain.User;

/**
 * Created by SG on 2016/5/15.
 */
public interface UserService {

  List<User> selectAllUsers(int limit);

  List<User> selectUsersFromRedis();
}
