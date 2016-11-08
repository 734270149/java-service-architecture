package sg.mybatis.dao;

import sg.domain.User;

import java.util.List;

/**
 * Created by SG on 2016/5/14.
 */
public interface UserDao {

    List<User> selectAllUsers(int limit);

    void insertUsers(List<User> list);
}
