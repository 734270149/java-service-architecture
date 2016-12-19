package sg.spring.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

/**
 * Created by shiguang3 on 2016/12/19.
 */
@Intercepts({
    @Signature(type = StatementHandler.class,
        method = "prepare", args = {Connection.class, Integer.class})})
public class SelectInterceptor implements Interceptor {

  private static final int MAX = 1;
  private static final String limit = "limit";
  private static final DefaultObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();
  private static final DefaultObjectWrapperFactory
      WRAPPER_FACTORY =
      new DefaultObjectWrapperFactory();
  private static final DefaultReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
    MetaObject
        metaObject =
        MetaObject.forObject(statementHandler, OBJECT_FACTORY, WRAPPER_FACTORY, REFLECTOR_FACTORY);
    Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
    String type = configuration.getVariables().getProperty("type");
    if (!"mysql".equals(type)) {
      return invocation.proceed();
    }
    System.out.println(type);
    MappedStatement
        mappedStatement =
        (MappedStatement) metaObject.getValue("delegate.mappedStatement");
    String id = mappedStatement.getId();
    System.out.println(id);
    BoundSql boundSql = statementHandler.getBoundSql();
    String sql = boundSql.getSql();
    Object parameterObject = boundSql.getParameterObject();
    String countSql = "select count(1) count from (" + sql + " ) t";
    Connection connection = (Connection) invocation.getArgs()[0];
    PreparedStatement preparedStatement = connection.prepareStatement(countSql);
    DefaultParameterHandler
        defaultParameterHandler =
        new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    defaultParameterHandler.setParameters(preparedStatement);
    ResultSet resultSet = preparedStatement.executeQuery();
    int totalCount = 0;
    if (resultSet.next()) {
      totalCount = resultSet.getInt(1);
    }
    System.out.println(totalCount);
    if (parameterObject instanceof Map) {
      Map map = (Map) parameterObject;
      Object o = map.get(limit);
      if (o != null && o.getClass() == Integer.class) {
        int i = (Integer) o;
        if (i > MAX) {
          boundSql.setAdditionalParameter(limit, MAX);
        }
      }
    }
    //mybatis默认情况下全部查询出来后再截取当前页的记录。可是如果我们自己写了个分页插件的情况下采用了物理分页，查询出来的记录就是当前页的内容。所以现在要告诉mybatis不再对结果进行加工，所以要重置为不分页的设置：
    metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
    metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
    metaObject.setValue("delegate.boundSql.sql", limit(sql));
    return invocation.proceed();
  }

  private String limit(String sql) {
    return sql;
  }

  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  public void setProperties(Properties properties) {
  }
}
