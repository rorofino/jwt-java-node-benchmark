package com.example.demo

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.apache.tomcat.jni.SSL.setPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration





@RunWith(SpringRunner::class)
@SpringBootTest
class DemoApplicationTests {

	@Test
	fun contextLoads() {
	    println()
	}

}
