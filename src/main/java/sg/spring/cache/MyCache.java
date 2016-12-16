package sg.spring.cache;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import redis.clients.util.SafeEncoder;

/**
 * 基于fastjson的缓存实现
 * Created by shiguang3 on 2016/11/11.
 */
public class MyCache implements Cache {

  private byte[] name;
  private StringRedisTemplate redisTemplate;

  public void setRedisTemplate(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * Return the cache name.
   */
  public String getName() {
    return SafeEncoder.encode(name);
  }

  public void setName(String name) {
    this.name = SafeEncoder.encode(name);
  }

  /**
   * Return the the underlying native cache provider.
   */
  public StringRedisTemplate getNativeCache() {
    return redisTemplate;
  }

  /**
   * Return the value to which this cache maps the specified key.
   * <p>Returns {@code null} if the cache contains no mapping for this key;
   * otherwise, the cached value (which may be {@code null} itself) will
   * be returned in a {@link ValueWrapper}.
   *
   * @param key the key whose associated value is to be returned
   * @return the value to which this cache maps the specified key,
   * contained within a {@link ValueWrapper} which may also hold
   * a cached {@code null} value. A straight {@code null} being
   * returned means that the cache contains no mapping for this key.
   * @see #get(Object, Class)
   */
  public ValueWrapper get(Object key) {
    if (key == null) {
      return null;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    return redisTemplate.execute(new RedisCallback<ValueWrapper>() {
      public ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
        byte[] bytes = connection.hGet(name, field);
        if (ArrayUtils.isEmpty(bytes)) {
          return null;
        }
        return new MyValueWrapper<Object>(JSON.parse(SafeEncoder.encode(bytes)));
      }
    });
  }

  /**
   * Return the value to which this cache maps the specified key,
   * generically specifying a type that return value will be cast to.
   * <p>Note: This variant of {@code get} does not allow for differentiating
   * between a cached {@code null} value and no cache entry found at all.
   * Use the standard {@link #get(Object)} variant for that purpose instead.
   *
   * @param key  the key whose associated value is to be returned
   * @param type the required type of the returned value (may be
   *             {@code null} to bypass a type check; in case of a {@code null}
   *             value found in the cache, the specified type is irrelevant)
   * @return the value to which this cache maps the specified key
   * (which may be {@code null} itself), or also {@code null} if
   * the cache contains no mapping for this key
   * @throws IllegalStateException if a cache entry has been found
   *                               but failed to match the specified type
   * @see #get(Object)
   * @since 4.0
   */
  public <T> T get(Object key, final Class<T> type) {
    if (key == null) {
      return null;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    return redisTemplate.execute(new RedisCallback<T>() {
      public T doInRedis(RedisConnection connection) throws DataAccessException {
        byte[] bytes = connection.hGet(name, field);
        if (ArrayUtils.isEmpty(bytes)) {
          return null;
        }
        return JSON.parseObject(SafeEncoder.encode(bytes), type);
      }
    });
  }

  /**
   * Associate the specified value with the specified key in this cache.
   * <p>If the cache previously contained a mapping for this key, the old
   * value is replaced by the specified value.
   *
   * @param key   the key with which the specified value is to be associated
   * @param value the value to be associated with the specified key
   */
  public void put(final Object key, final Object value) {
    if (key == null || value == null) {
      return;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    redisTemplate.execute(new RedisCallback<Object>() {
      public Object doInRedis(RedisConnection connection) throws DataAccessException {
        connection.hSet(name, field, SafeEncoder.encode(JSON.toJSONString(value)));
        return null;
      }
    });
  }

  /**
   * Atomically associate the specified value with the specified key in this cache
   * if it is not set already.
   * <p>This is equivalent to:
   * <pre><code>
   * Object existingValue = cache.get(key);
   * if (existingValue == null) {
   *     cache.put(key, value);
   *     return null;
   * } else {
   *     return existingValue;
   * }
   * </code></pre>
   * except that the action is performed atomically. While all out-of-the-box
   * {@link CacheManager} implementations are able to perform the put atomically,
   * the operation may also be implemented in two steps, e.g. with a check for
   * presence and a subsequent put, in a non-atomic way. Check the documentation
   * of the native cache implementation that you are using for more details.
   *
   * @param key   the key with which the specified value is to be associated
   * @param value the value to be associated with the specified key
   * @return the value to which this cache maps the specified key (which may be
   * {@code null} itself), or also {@code null} if the cache did not contain any
   * mapping for that key prior to this call. Returning {@code null} is therefore
   * an indicator that the given {@code value} has been associated with the key.
   * @since 4.1
   */
  public ValueWrapper putIfAbsent(Object key, final Object value) {
    if (key == null || value == null) {
      return null;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    return redisTemplate.execute(new RedisCallback<ValueWrapper>() {
      public ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
        byte[] bytes = connection.hGet(name, field);
        if (ArrayUtils.isEmpty(bytes)) {
          return null;
        }
        Object parse = JSON.parse(SafeEncoder.encode(bytes));
        return value.equals(parse) ? new MyValueWrapper<Object>(parse) : null;
      }
    });
  }

  /**
   * Evict the mapping for this key from this cache if it is present.
   *
   * @param key the key whose mapping is to be removed from the cache
   */
  public void evict(Object key) {
    if (key == null) {
      return;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    redisTemplate.execute(new RedisCallback<Object>() {
      public Object doInRedis(RedisConnection connection) throws DataAccessException {
        connection.hDel(name, field);
        return null;
      }
    });
  }

  /**
   * Remove all mappings from the cache.
   */
  public void clear() {
    redisTemplate.execute(new RedisCallback<Object>() {
      public Object doInRedis(RedisConnection connection) throws DataAccessException {
        connection.del(name);
        return null;
      }
    });
  }

  public <T> ValueWrapper get(Object key, final Class<T> className, final boolean array) {
    if (key == null) {
      return null;
    }
    final byte[] field =
        key.getClass() == String.class ?
        SafeEncoder.encode((String) key) :
        SafeEncoder.encode(JSON.toJSONString(key));
    return redisTemplate.execute(new RedisCallback<ValueWrapper>() {
      public ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
        byte[] bytes = connection.hGet(name, field);
        if (ArrayUtils.isEmpty(bytes)) {
          return null;
        }
        if (array) {
          return new MyValueWrapper<List<T>>(JSON.parseArray(SafeEncoder.encode(bytes), className));
        } else {
          return new MyValueWrapper<T>(JSON.parseObject(SafeEncoder.encode(bytes), className));
        }
      }
    });
  }

  private static final class MyValueWrapper<T> implements ValueWrapper {

    private final T value;

    private MyValueWrapper(T value) {
      this.value = value;
    }

    /**
     * Return the actual value in the cache.
     */
    public T get() {
      return value;
    }
  }
}
