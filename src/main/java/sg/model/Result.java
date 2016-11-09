package sg.model;

import java.io.Serializable;

/**
 * Created by shiguang3 on 2016/11/9.
 */
public class Result implements Serializable {

  private static final long serialVersionUID = 5594158175260335479L;
  private int code;
  private String msg;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
