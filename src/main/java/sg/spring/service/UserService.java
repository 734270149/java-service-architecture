package sg.spring.service;

import sg.domain.User;

import java.util.List;

/**
 * Created by SG on 2016/5/15.
 */
public interface UserService {

    List<User> selectAllUsers(int limit);

    void insertUsers(List<User> list);
}
