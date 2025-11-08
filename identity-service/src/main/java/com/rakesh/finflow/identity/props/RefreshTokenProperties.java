package com.rakesh.finflow.identity.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RefreshTokenProperties {

    @Value("${app.security.refresh-token.expiration-days:7}")
    private long expiryDays;
}
