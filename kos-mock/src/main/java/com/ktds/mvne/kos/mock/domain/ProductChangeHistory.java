// kosmock/src/main/java/com/ktds/mvne/kosmock/domain/ProductChangeHistory.java
package com.ktds.mvne.kos.mock.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_change_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChangeHistory {
    @Id
    private String transactionId;

    private String phoneNumber;
    private String custId;
    private String oldProductCode;
    private String newProductCode;
    private LocalDateTime changeDate;
    private Boolean success;
    private String errorMessage;
    private Integer additionalFee;
    private String changeReason;
}
