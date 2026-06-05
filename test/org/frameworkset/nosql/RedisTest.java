package org.frameworkset.nosql;

import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.frameworkset.nosql.redis.RedisTool;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Slowlog;

import java.util.List;
import java.util.Map;

public class RedisTest {

	public RedisTest() {
		// TODO Auto-generated constructor stub
	}
    
	@Test
	public void getByTool(){
		RedisTool.getInstance().set("aaa","ddd");
		RedisTool.getInstance().hset("ddd","aaa","xxxx");
		RedisTool.getInstance().get("aaa");
		RedisTool.getInstance().set("vops_biz_count_history_max","{sss}");
		System.out.println(RedisTool.getInstance().get("vops_biz_count_history_max"));
	}
	@Test
	public void get()
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			redisHelper.set("test", "value1");
			String value = redisHelper.get("test");
			System.out.println("test="+value);
			redisHelper.setex("foo", 1,"fasdfasf");//指定缓存有效期1秒
			
			System.out.println("foo ttl="+redisHelper.ttl("foo"));//获取有效期
			value = redisHelper.get("foo");//获取数据
			System.out.println("foo="+value);
			//删除数据
			redisHelper.del("foo");
			value = redisHelper.getSet("fowwero","test");
			
			System.out.println("fowwero="+value);
			value = redisHelper.getSet("fowwero","eeee");//获取后修改数据
			System.out.println("fowwero="+value);
			
			value = redisHelper.get("fowwero");
			
			System.out.println("fowwero="+value);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
	}
	@Test
	public void testconfigGet(){
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			Jedis jedis = redisHelper.getJedis();
			System.out.println(jedis.info());

			Map<String,String> redis_maxclients = jedis.configGet("maxclients");
            Map<String,String>  redis_maxmemory = jedis.configGet("maxmemory");
			System.out.println(redis_maxmemory);
			List<Slowlog> slowlogs = jedis.slowlogGet();
			System.out.println(slowlogs.size());
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
	}

    /**
     * 测试RedisTool分布式锁基本获取与释放
     */
    @Test
    public void testDistributedLockByTool() {
        String lockKey = "test:lock:tool";
        String lockValue = "owner_001";
        int expireTime = 5000; // 5秒过期时间（毫秒）

        boolean acquired = RedisTool.getInstance().tryLock(lockKey, lockValue, expireTime);
        System.out.println("获取锁结果: " + acquired);

        if (acquired) {
            try {
                System.out.println("执行业务逻辑...");
                // 模拟业务处理
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                boolean released = RedisTool.getInstance().releaseLock(lockKey, lockValue);
                System.out.println("释放锁结果: " + released);
            }
        }
    }

    /**
     * 测试RedisHelper分布式锁基本获取与释放
     */
    @Test
    public void testDistributedLockByHelper() {
        String lockKey = "test:lock:helper";
        String lockValue = "owner_002";
        int expireTime = 5000; // 5秒过期时间（毫秒）

        RedisHelper redisHelper = null;
        boolean acquired = false;
        try {
            redisHelper = RedisFactory.getRedisHelper();
            acquired = redisHelper.tryLock(lockKey, lockValue, expireTime);
            System.out.println("RedisHelper获取锁结果: " + acquired);

            if (acquired) {
                System.out.println("RedisHelper执行业务逻辑...");
                // 模拟业务处理
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquired) {
                boolean released = redisHelper.releaseLock(lockKey, lockValue);
                System.out.println("RedisHelper释放锁结果: " + released);
            }
            if (redisHelper != null) {
                redisHelper.release();
            }
        }
    }

    /**
     * 测试分布式锁超时自动释放场景
     */
    @Test
    public void testDistributedLockTimeout() throws InterruptedException {
        String lockKey = "test:lock:timeout";
        String lockValue1 = "owner_timeout_1";
        String lockValue2 = "owner_timeout_2";
        int expireTime = 2000; // 2秒过期时间（毫秒）

        // 第一个客户端获取锁
        boolean acquired1 = RedisTool.getInstance().tryLock(lockKey, lockValue1, expireTime);
        System.out.println("客户端1获取锁: " + acquired1);

        // 等待锁过期
        Thread.sleep(3000);

        // 第二个客户端在锁过期后获取锁
        boolean acquired2 = RedisTool.getInstance().tryLock(lockKey, lockValue2, expireTime);
        System.out.println("客户端2获取锁（锁已过期）: " + acquired2);

        // 清理
        if (acquired2) {
            RedisTool.getInstance().releaseLock(lockKey, lockValue2);
        }
    }

    /**
     * 测试分布式锁竞争场景：同一lockKey，不同owner竞争
     */
    @Test
    public void testDistributedLockCompetition() throws InterruptedException {
        String lockKey = "test:lock:competition";
        String lockValue1 = "owner_comp_1";
        String lockValue2 = "owner_comp_2";
        int expireTime = 10000; // 10秒过期时间

        // 客户端1获取锁
        boolean acquired1 = RedisTool.getInstance().tryLock(lockKey, lockValue1, expireTime);
        System.out.println("竞争测试-客户端1获取锁: " + acquired1);

        // 客户端2尝试获取同一锁（应该失败）
        boolean acquired2 = RedisTool.getInstance().tryLock(lockKey, lockValue2, expireTime);
        System.out.println("竞争测试-客户端2获取锁: " + acquired2);

        // 客户端1释放锁
        if (acquired1) {
            boolean released = RedisTool.getInstance().releaseLock(lockKey, lockValue1);
            System.out.println("竞争测试-客户端1释放锁: " + released);
        }

        // 客户端2再次尝试（应该成功）
        boolean acquired3 = RedisTool.getInstance().tryLock(lockKey, lockValue2, expireTime);
        System.out.println("竞争测试-客户端2再次获取锁: " + acquired3);

        if (acquired3) {
            RedisTool.getInstance().releaseLock(lockKey, lockValue2);
        }
    }

    /**
     * 测试释放他人锁（应该失败，保证锁安全性）
     */
    @Test
    public void testReleaseOthersLock() {
        String lockKey = "test:lock:security";
        String lockValue1 = "owner_sec_1";
        String lockValue2 = "owner_sec_2";
        int expireTime = 5000;

        boolean acquired = RedisTool.getInstance().tryLock(lockKey, lockValue1, expireTime);
        System.out.println("安全测试-获取锁: " + acquired);

        if (acquired) {
            // 使用不同的lockValue尝试释放（模拟他人释放）
            boolean released = RedisTool.getInstance().releaseLock(lockKey, lockValue2);
            System.out.println("安全测试-他人释放锁结果（应为false）: " + released);

            // 正确的owner释放
            boolean releasedByOwner = RedisTool.getInstance().releaseLock(lockKey, lockValue1);
            System.out.println("安全测试-拥有者释放锁结果（应为true）: " + releasedByOwner);
        }
    }



}
