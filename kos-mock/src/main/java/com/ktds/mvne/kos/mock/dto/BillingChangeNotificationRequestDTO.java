// kosmock/src/main/java/com/ktds/mvne/kosmock/dto/BillingChangeNotificationRequestDTO.java
package com.ktds.mvne.kos.mock.dto;

import com.ktds.mvne.kos.mock.dto.BillingChangeDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingChangeNotificationRequestDTO {
    private String phoneNumber;
    private String billingMonth;
    private String changeType;
    private List<BillingChangeDetailDTO> details;
}