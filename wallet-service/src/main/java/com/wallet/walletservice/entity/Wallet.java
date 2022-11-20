package com.wallet.walletservice.entity;

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
public class Wallet {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user id of the user to which the wallet is registered to
     */
    @Column(unique = true, nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email; // storing the email here as we have to send the email
                        // and by storing the email here we save call to user service to get the email for the user id

    /**
     * Balance of the wallet
     */
    private Double balance;

    /**
     * Automatically saves create time when the tuple get created.
     */
    @CreationTimestamp
    private Date createdOn;

    /**
     * Automatically saves update time whenever the tuple data get updated.
     */
    @UpdateTimestamp
    private Date updatedOn;

    // TODO: we can have ENUM for the status of wallet like active/locked/disabled
}
