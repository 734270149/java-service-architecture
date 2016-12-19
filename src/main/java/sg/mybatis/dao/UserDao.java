package sg.mybatis.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import sg.domain.User;

/**
 * Created by SG on 2016/5/14.
 */
public interface UserDao {

  List<User> selectAllUsers(@Param("limit") int limit);

  void insertUsers(List<User> list);
}
