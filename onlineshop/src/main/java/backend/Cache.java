package backend;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

import javax.cache.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Cache {
    private static final Logger LOGGER = Logger.getLogger(Cache.class.getName());
    private static final CacheFactory CACHE_FACTORY = CreateFactory();

    private static javax.cache.CacheFactory CreateFactory() {
        try {
            return CacheManager.getInstance().getCacheFactory();
        }
        catch (CacheException e) {
            LOGGER.severe(e.toString());
            return null;
        }
    }

    public static javax.cache.Cache Create() {
        try {
            Map<Object, Object> properties = new HashMap<>();
            properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.HOURS.toSeconds(1));
            return CACHE_FACTORY.createCache(properties);
        }
        catch (CacheException e) {
            LOGGER.severe(e.toString());
            return null;
        }
    }
}