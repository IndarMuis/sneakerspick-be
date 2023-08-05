package com.sneakerspick.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sneakerspick.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckoutResponse {

    private Long id;

    private Long userId;

    private String address;

    private Double totalPrice;

    private Double shippingPrice;

    private TransactionStatus status;

    private List<ItemCheckoutResponse> items;

}
