package sg.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shiguang3 on 2016/12/8.
 */
public final class HttpUtils {

  /**
   * 获取访问者真实IP地址
   *
   * @param request
   * @return
   */
  public static String getRemoteIP(HttpServletRequest request) {
    String ip = request.getHeader("J-Forwarded-For");
    if (isIpEnabled(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (isIpEnabled(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (isIpEnabled(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (isIpEnabled(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip != null) {
      //对于通过多个代理的情况，最后IP为客户端真实IP,多个IP按照','分割
      int position = ip.lastIndexOf(",");
      if (position > 0) {
        ip = ip.substring(position + 1, ip.length());
      }
    }
    return ip;
  }

  private static boolean isIpEnabled(String ip) {
    return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
  }
}
