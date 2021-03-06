package sg.springmvc.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.spring.service.PermissionsService;
import sg.springmvc.annotation.PermissionsAnnotation;

/**
 * Created by shiguang3 on 2016/12/8.
 */
public class PermissionsInterceptor implements HandlerInterceptor {

  /**
   * 处理拦截ajax请求的问题
   */
  private static final String AJAX_HEADER = "X-Requested-With";
  private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
  private static final String CODE = "code";
  @Resource
  private PermissionsService permissionsService;

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (HandlerMethod.class != handler.getClass()) {
      return true;
    }
    String parameter = request.getParameter(CODE);
    Method method = ((HandlerMethod) handler).getMethod();
    PermissionsAnnotation annotation = method.getAnnotation(PermissionsAnnotation.class);
    if (annotation == null) {
      return true;
    }
    if (StringUtils.isEmpty(parameter)) {
      return toDeny(request, response);
    }
    try {
      int code = annotation.code();
      int parseInt = Integer.parseInt(parameter);
      if (code != parseInt) {
        return toDeny(request, response);
      }
      // TODO: 2016/12/8 动态从session中获取
      List<Integer> sg = permissionsService.queryPermissionsByPin("sg");
      for (Integer integer : sg) {
        if (integer == code) {
          return true;
        }
      }
    } catch (Exception e) {
    }
    return toDeny(request, response);
  }

  private boolean toDeny(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (AJAX_HEADER_VALUE.equalsIgnoreCase(request.getHeader(AJAX_HEADER))) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader("Location", request.getContextPath() + "deny.html");
    } else {
      response.sendRedirect(request.getContextPath() + "deny.html");
    }
    return false;
  }

  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) throws Exception {
    System.out.println("PermissionsInterceptor postHandle");
  }

  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) throws Exception {
    System.out.println("PermissionsInterceptor afterCompletion");
  }
}
