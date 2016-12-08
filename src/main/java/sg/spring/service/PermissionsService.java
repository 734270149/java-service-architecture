package sg.spring.service;

import java.util.List;

/**
 * Created by shiguang3 on 2016/12/8.
 */
public interface PermissionsService {

  List<Integer> queryPermissionsByPin(String pin);
}
