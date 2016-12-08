package sg.spring.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import sg.spring.service.PermissionsService;

/**
 * Created by shiguang3 on 2016/12/8.
 */
@Service("permissionsService")
public class PermissionsServiceImpl implements PermissionsService {

  public List<Integer> queryPermissionsByPin(String pin) {
    if (StringUtils.isEmpty(pin)) {
      return new ArrayList<Integer>(0);
    }
    // TODO: 2016/12/8 待加查询权限库或redis
    return new ArrayList<Integer>(1) {
      private static final long serialVersionUID = -2217308783295995555L;

      {
        add(400);
      }
    };
  }
}
