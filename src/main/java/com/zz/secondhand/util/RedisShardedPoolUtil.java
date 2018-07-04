package com.zz.secondhand.util;

import com.zz.secondhand.common.RedisPool;
import com.zz.secondhand.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {

    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{}",key,value,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

//设置expire时间
    public static String setEx(String key,int exTime,String value){
        ShardedJedis jedis = null;
        String result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("set key:{} value:{}",key,value,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    //重新expire时间，单位是秒，1成功 0失败
    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("set key:{}",key,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }


    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("set key:{}",key,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    //删除key >0成功，0失败
    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("set key:{}",key,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    //setnx
    public static Long setnx(String key,String value){
        ShardedJedis jedis = null;
        Long result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.setnx(key,value);
        } catch (Exception e) {
            log.error("setnx key:{} value:{}",key,value,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    //setnx
    public static String getSet(String key,String value){
        ShardedJedis jedis = null;
        String result = null;

        jedis = RedisShardedPool.getJedis();
        try {
            result = jedis.getSet(key,value);
        } catch (Exception e) {
            log.error("getSet key:{} value:{}",key,value,e);
            RedisShardedPool.returnResource(jedis);
            return result;
        }
        return result;
    }

    public static void main(String[] args) {
        ShardedJedis jedis = RedisShardedPool.getJedis();

        RedisShardedPoolUtil.set("foggy","studio");

        String value = RedisShardedPoolUtil.get("foggy");

        RedisShardedPoolUtil.setEx("keye",60*10,"valuee");

        RedisShardedPoolUtil.expire("foggy",60*20);

        RedisShardedPoolUtil.del("keye");
    }

}
