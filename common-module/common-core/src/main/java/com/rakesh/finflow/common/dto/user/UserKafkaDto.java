package com.rakesh.finflow.common.dto.user;

import com.rakesh.finflow.common.entity.kafka.UserEventType;
import com.rakesh.finflow.common.entity.userservice.Currency;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKafkaDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UserEventType userEventType;
    private String email;
    private String name;
    private String username;
    private Currency currency;
}
