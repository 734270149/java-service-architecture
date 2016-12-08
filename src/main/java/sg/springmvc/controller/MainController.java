package sg.springmvc.controller;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import sg.domain.User;
import sg.model.Result;
import sg.spring.service.UserService;
import sg.springmvc.annotation.PermissionsAnnotation;

/**
 * Created by SG on 2016/5/14.
 */
@Controller
@RequestMapping("/")
public class MainController {

  private static final Logger log = LogManager.getLogger(MainController.class);

  @Resource
  private UserService userService;

  @Value("${controllerNumber}")
  private String controllerNumber;

  @Resource
  private ThreadPoolExecutor executor;

  @RequestMapping("")
  public String index(Model model) {
    List<User> users = userService.selectAllUsers(5);
    model.addAttribute("users", users);
    log.debug("controllerNumber:{}", controllerNumber);
    return "index";
  }

  @PermissionsAnnotation(code = 400)
  @RequestMapping("getFastJson")
  public String getFastJson(Model model, String msg) {
    List<User> users = userService.selectUsersFromRedis();
    model.addAttribute("users", users);
    log.debug("controllerNumber:{}", controllerNumber);
    return "index";
  }

  @RequestMapping("getGson")
  public
  @ResponseBody
  String getGson(String msg) {
    Result result = new Result();
    result.setCode(1);
    result.setMsg(msg);
    return new Gson().toJson(result);
  }
}
