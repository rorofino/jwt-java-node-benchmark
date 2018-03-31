
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        int sampleSize = 200000;

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1048);
        KeyPair kp = kpg.genKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
        
        Random rand = new Random();

        Calendar date = Calendar.getInstance();
        long t= date.getTimeInMillis();

        HashSet<String> map = new HashSet<>();
        
        int current = 0;
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

        for (int i=0; i<sampleSize; i++) {
            try {
                String token = JWT.create()
                    .withClaim("pId", UUID.randomUUID().toString())
                    .withExpiresAt(new Date(t + (rand.nextInt(5) * 60000)))
                    .withIssuer("auth0")
                    .sign(algorithm);

                map.add(token);
                if (i % 2000 == 0) {
                    current++;
                    System.out.println("completed "+ current + "%");
                }
            } catch (JWTCreationException exception){
                //Invalid Signing configuration / Couldn't convert Claims.
            }
        }
        int decodeTime = 0;
        for (String token : map) {
            long startTime = System.nanoTime();
            long endTime;
            long totalTime;
            try {
                JWTVerifier verifier = JWT.require(algorithm)
                .acceptLeeway(1) // 1 sec for nbf, iat and exp
                .build();
                DecodedJWT jwt = verifier.verify(token);
            } catch (TokenExpiredException exception){
                //Invalid token
            } finally {
                endTime = System.nanoTime();
                totalTime = endTime - startTime;
                decodeTime += totalTime;
            }
        }
        
        System.out.println("Chaves geradas com sucesso " + decodeTime);
    }
}