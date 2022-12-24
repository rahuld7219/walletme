package com.wallet.transactionservice.entity;

import com.wallet.transactionservice.enums.TransactionStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder // for builder we need noarg and allarg constructors
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // we use UUID for this and provide it to check transaction status, as publicly exposed API should not use number
    // that could be easily guessed, we have not returned the id as then
    // anyone can take that number and can see other transactions also simply by just incrementing/decrementing the number.
    @Column(unique = true, nullable = false)
    private String txnId;

    @Column(nullable = false)
    private Long fromUserId;

    @Column(nullable = false)
    private Long toUserId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING) // with this annotation, it does not save the status in ordinal form but in the string form in the database
    private TransactionStatus status;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}
