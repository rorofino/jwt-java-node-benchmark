package com.example.demo

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.TokenExpiredException
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DemoJWT() {

    @Autowired
    lateinit var template: RedisTemplate<String, Any>


    fun run() {

        val sampleSize = 500000

        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(1048)
        val kp = kpg.genKeyPair()
        val publicKey = kp.public as RSAPublicKey
        val privateKey = kp.private as RSAPrivateKey

        val rand = Random()

        val date = Calendar.getInstance()
        val t = date.timeInMillis

        val map = HashSet<String>()

        var current = 0
        val algorithm = Algorithm.RSA256(publicKey, privateKey)

        for (i in 0 until sampleSize) {
            try {
                val token = JWT.create()
                        .withClaim("pId", UUID.randomUUID().toString())
                        .withExpiresAt(Date(t + rand.nextInt(5) * 60000))
                        .withIssuer("auth0")
                        .sign(algorithm)

                map.add(token)
                template.opsForValue().set(token, token)
                if (i % (sampleSize/100) == 0) {
                    current++
                    println("completed $current%")
                }
            } catch (exception: JWTCreationException) {
                //Invalid Signing configuration / Couldn't convert Claims.
            }

        }
        var decodeTime: Long = 0
        for (token in map) {
            val startTime: Long = System.nanoTime()
            val endTime: Long
            val totalTime: Long
            try {
                val verifier = JWT.require(algorithm)
                        .acceptLeeway(1) // 1 sec for nbf, iat and exp
                        .build()
                val jwt = verifier.verify(token)
            } catch (exception: TokenExpiredException) {
                //Invalid token
            } finally {
                endTime = System.nanoTime()
                totalTime = endTime - startTime
                decodeTime += totalTime
            }
        }

        println("Chaves verificas em $decodeTime ${decodeTime/1000000000} ${decodeTime/sampleSize}")

        decodeTime = 0
        var retrivedTotal = 0
        for (token in map) {
            val startTime = System.nanoTime()
            val endTime: Long
            val totalTime: Long
            try {
                val retrived = template.opsForValue().get(token)
                if (retrived != null) {
                    retrivedTotal++
                }
            } catch (exception: Exception) {
                //Invalid token
            } finally {
                endTime = System.nanoTime()
                totalTime = endTime - startTime
                decodeTime += totalTime
            }
        }

        println("chaves geradas: ${map.size} chaves recuperadas: $retrivedTotal")
        println("Chaves verificas em $decodeTime ${decodeTime/1000000000} ${decodeTime/sampleSize}")
    }
}
