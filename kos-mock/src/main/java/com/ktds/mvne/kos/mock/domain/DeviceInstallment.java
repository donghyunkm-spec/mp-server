// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/DeviceInstallment.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInstallment {
    private String deviceId;
    private String model;
    private Integer amount;
    private Integer remainingMonths;
}