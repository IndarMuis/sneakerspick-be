package com.sneakerspick.domain;

import com.sneakerspick.enums.PaymentType;
import com.sneakerspick.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @Column(name = "total_price", precision = 10, scale = 2, columnDefinition = "default 0.00")
    private Double totalPrice;

    @Column(name = "shipping_price", precision = 10, scale = 2, columnDefinition = "default 0.00")
    private Double shippingPrice;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "transaction_status", columnDefinition = "integer default 2")
    private TransactionStatus transactionStatus;

    @Column(name = "payment_type", columnDefinition = "varchar(10) default 'MANUAL'")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "transaction")
    private List<TransactionItem> transactionItems;

}
