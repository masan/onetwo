package org.onetwo.boot.module.redis;

import org.onetwo.boot.module.redis.JFishRedisProperties.LockRegistryProperties;
import org.onetwo.common.spring.copier.CopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author wayshall
 * <br/>
 */
@EnableConfigurationProperties({JFishRedisProperties.class})
@ConditionalOnClass({JedisConnectionFactory.class, RedisLockRegistry.class})
public class RedisConfiguration {
	
	private static final String BEAN_REDISCONNECTIONFACTORY = "redisConnectionFactory";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JFishRedisProperties redisProperties;
    
	@Bean
    @ConditionalOnMissingBean(name=BEAN_REDISCONNECTIONFACTORY)
	@ConditionalOnProperty(name=JFishRedisProperties.ENABLED_KEY, havingValue="true")
    public JedisConnectionFactory redisConnectionFactory() throws Exception {
		JedisConnectionFactory jf = new JedisConnectionFactory();
		CopyUtils.copy(jf, redisProperties);
		if(redisProperties.getPool()!=null){
			jf.setPoolConfig(redisProperties.getPool());
		}
		return jf;
    }
	
	@Bean
	@ConditionalOnMissingBean(name="stringKeyRedisTemplate")
	@ConditionalOnProperty(name=JFishRedisProperties.ENABLED_KEY, havingValue="true")
	public RedisTemplate<String, Object> stringKeyRedisTemplate(@Autowired @Qualifier(BEAN_REDISCONNECTIONFACTORY) JedisConnectionFactory jedisConnectionFactory) throws Exception  {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(jedisConnectionFactory);
		
		return template;
	}
	
	@Bean
	@ConditionalOnProperty(name=JFishRedisProperties.ENABLED_LOCK_REGISTRY)
	public RedisLockRegistry redisLockRegistry(@Autowired JedisConnectionFactory jedisConnectionFactory){
		LockRegistryProperties lockRegistryProperties = redisProperties.getLockRegistry();
		RedisLockRegistry lockRegistry = new RedisLockRegistry(jedisConnectionFactory, 
																lockRegistryProperties.getKey(), 
																lockRegistryProperties.getExpireAfter());
		return lockRegistry;
	}
    
}
