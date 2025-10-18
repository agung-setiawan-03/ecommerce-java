package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.DateUtil;
import com.yugungsetia.ecommerce_simple.config.JwtSecretConfig;
import com.yugungsetia.ecommerce_simple.model.UserInfo;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtSecretConfig jwtSecretConfig;
    private final SecretKey signKey;

    @Override
    public String generateToken(UserInfo userInfo) {
        LocalDateTime expirationTime = LocalDateTime.now().plus(jwtSecretConfig.getJwtExpirationTime());
        Date expirationDate = DateUtil.convertLocalDateTimeToDate(expirationTime);

        return Jwts.builder()
                .setSubject(userInfo.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(signKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build();
            parser.parseClaimsJws(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }


    }

    @Override
    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build();

        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();

    }
}
