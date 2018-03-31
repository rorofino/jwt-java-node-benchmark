package com.example.demo

import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.KeyPair
import java.security.KeyPairGenerator
import com.sun.tools.javac.tree.TreeInfo.args
import org.springframework.boot.SpringApplication
import java.util.*


fun main(args: Array<String>) {

    val sampleSize = 200000

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
            if (i % 2000 == 0) {
                current++
                println("completed $current%")
            }
        } catch (exception: JWTCreationException) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }

    }
    var decodeTime = 0
    for (token in map) {
        val startTime = System.nanoTime()
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
            decodeTime += totalTime.toInt()
        }
    }

    println("Chaves geradas com sucesso $decodeTime")
}