// kosmock/src/main/java/com/ktds/mvne/kosmock/dto/NotificationResponseDTO.java
package com.ktds.mvne.kos.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private boolean success;
    private String notificationId;
    private String message;
}
