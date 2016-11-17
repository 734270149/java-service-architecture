package sg.spring.cache;

import com.alibaba.fastjson.JSON;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * 基于fastjson的缓存实现
 * Created by shiguang3 on 2016/11/11.
 */
public class MyCache implements Cache {

  private String name;

  private Jedis jedis;

  public void setJedis(Jedis jedis) {
    this.jedis = jedis;
  }

  /**
   * Return the cache name.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Return the the underlying native cache provider.
   */
  public Jedis getNativeCache() {
    return jedis;
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
    String json = jedis.hget(name, String.valueOf(key));
    return json != null ? new MyValueWrapper<String>(json) : null;
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
  public <T> T get(Object key, Class<T> type) {
    String json = jedis.hget(name, String.valueOf(key));
    return JSON.parseObject(json, type);
  }

  /**
   * Associate the specified value with the specified key in this cache.
   * <p>If the cache previously contained a mapping for this key, the old
   * value is replaced by the specified value.
   *
   * @param key   the key with which the specified value is to be associated
   * @param value the value to be associated with the specified key
   */
  public void put(Object key, Object value) {
    jedis.hset(name, String.valueOf(key), JSON.toJSONString(value));
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
  public ValueWrapper putIfAbsent(Object key, Object value) {
    String json = jedis.hget(name, String.valueOf(key));
    return json != null ? new MyValueWrapper<String>(json) : null;
  }

  /**
   * Evict the mapping for this key from this cache if it is present.
   *
   * @param key the key whose mapping is to be removed from the cache
   */
  public void evict(Object key) {
    jedis.hdel(name, String.valueOf(key));
  }

  /**
   * Remove all mappings from the cache.
   */
  public void clear() {
    jedis.del(name);
  }

  public <T> ValueWrapper get(Object key, Class<T> className, boolean array) {
    try {
      String json = jedis.hget(name, String.valueOf(key));
      if (array) {
        List<T> list = JSON.parseArray(json, className);
        return new MyValueWrapper<List<T>>(list);
      } else {
        T object = JSON.parseObject(json, className);
        return new MyValueWrapper<T>(object);
      }
    } catch (Exception ignored) {
    }
    return null;
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
