package top.huzhurong.agent.hook;

import java.util.HashMap;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/10/13
 */
public class StrictMap<K, V> extends HashMap<K, V> {

    StrictMap() {
        this(16);
    }

    private StrictMap(int init) {
        super(init);
    }


    @Override
    public V get(Object key) {
        V v = super.get(key);
        if (v == null) {
            throw new RuntimeException(key + ":对应的value为null");
        }
        return v;
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new RuntimeException("key 和 value 不能为null");
        }
        V put = super.put(key, value);
        if (put != null) {
            throw new RuntimeException(key + ":重复put");
        }
        return null;
    }

}
