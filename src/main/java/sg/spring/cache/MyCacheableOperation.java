package sg.spring.cache;

import org.springframework.cache.interceptor.CacheableOperation;

/**
 * Created by shiguang3 on 2016/11/17.
 */
public class MyCacheableOperation extends CacheableOperation {

  private boolean isArray = false;

  private Class<?> className = Object.class;

  public boolean isArray() {
    return isArray;
  }

  public void setArray(boolean array) {
    isArray = array;
  }

  public Class<?> getClassName() {
    return className;
  }

  public void setClassName(Class<?> className) {
    this.className = className;
  }

  @Override
  protected StringBuilder getOperationDescription() {
    StringBuilder sb = super.getOperationDescription();
    sb.append(" | isArray='");
    sb.append(this.isArray);
    sb.append("' | className='");
    sb.append(this.className);
    sb.append("'");
    return sb;
  }
}
