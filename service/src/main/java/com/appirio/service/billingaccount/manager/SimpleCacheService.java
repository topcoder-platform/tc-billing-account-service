package com.appirio.service.billingaccount.manager;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleCacheService is used to perform the time-expired cache.
 * It can be used by BillingAccountManager to cache the lookup data or the result of any complex operations.
 * 
 * It's added in Topcoder - Create Challenge Fee Management APIs For Billing Accounts 1.0v
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class SimpleCacheService {

    /**
     * The cache
     */
    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();


    /**
     * The timer
     */
    private Timer timer = new Timer();

    /**
     * Put the key and value
     *
     * @param key the key to use
     * @param value the value
     * @param expirySeconds the expirySeconds to use
     */
    public void put(String key, Object value, int expirySeconds) {
        cache.put(key, value);
        updateExpireTask(key, expirySeconds);
    }

    /**
     * Update expire task
     *
     * @param key the key to use
     * @param expirySeconds the expirySeconds to use
     */
    protected void updateExpireTask(String key, int expirySeconds) {
        String taskKey = ExpireTask.createKey(key);
        ExpireTask oldTask = (ExpireTask) cache.remove(taskKey);
        if (oldTask != null) {
            oldTask.cancel();
        }
        if (expirySeconds > 0) {
            ExpireTask task = new ExpireTask(key, cache);
            timer.schedule(task, expirySeconds * 1000L);
            cache.put(taskKey, task);
        }
    }

    /**
     * Get the value
     *
     * @param key the key to use
     * @return the cached result, null if not present
     */
    public Object get(String key) {
        return cache.get(key);
    }

    /**
     * Delete by key
     *
     * @param key the key to use
     * @return the cached result, null if not present
     */
    public Object delete(String key) {
        return cache.remove(key);
    }

    /**
     * ExpireTask is used to check the cache.
     * 
     * @author TCCoder
     * @version 1.0
     *
     */
    protected static class ExpireTask extends TimerTask {
        /**
         * The key
         */
        String key;


        /**
         * The cache
         */
        Map<String, Object> cache;

        /**
         * Create the instance
         * 
         * @param key the key
         * @param cache the cache
         */
        ExpireTask(String key, Map<String, Object> cache) {
            this.key = key;
            this.cache = cache;
        }

        /**
         * The run method
         */
        @Override
        public void run() {
            this.cache.remove(key);
            this.cache.remove(createKey(key));
        }

        /**
         * the create key method
         * 
         * @param key the key
         * @return the key
         */
        protected static String createKey(String key) {
            return "ExpireTask-" + key;
        }
    }
}
