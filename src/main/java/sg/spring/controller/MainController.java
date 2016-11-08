package sg.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sg.domain.User;
import sg.spring.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by SG on 2016/5/14.
 */
@Controller
@RequestMapping("/")
public class MainController {

    @Resource
    private UserService userService;

    @Value("${controllerNumber}")
    private String controllerNumber;

    @RequestMapping("")
    public String index(Model model) {
        List<User> users = userService.selectAllUsers(5);
        model.addAttribute("users", users);
        System.out.println("controllerNumber:" + controllerNumber);
        return "index";
    }

    @RequestMapping("getString")
    public
    @ResponseBody
    String getString(String msg) {
        return "{\"code\": 0,\"msg\": \"" + msg + "\"}";
    }
}