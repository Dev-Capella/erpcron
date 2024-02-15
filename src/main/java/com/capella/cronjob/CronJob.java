package com.capella.cronjob;

import com.capella.domain.data.user.JwtUserData;
import com.capella.security.constant.AuthorizationConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@Getter
public class CronJob implements Runnable{

    public final static String CRONJOB_USER = "cronjob_user";

    @Override
    public void run() {
        authenticate();
    }

    protected void authenticate(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.isNull(authentication) || !authentication.isAuthenticated()){
            var token = new UsernamePasswordAuthenticationToken(CRONJOB_USER,null,
                    AuthorityUtils.createAuthorityList(AuthorizationConstants.ADMIN));
            var jwtUserData = new JwtUserData();
            jwtUserData.setJwtId(UUID.randomUUID().toString());
            token.setDetails(jwtUserData);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }
}
