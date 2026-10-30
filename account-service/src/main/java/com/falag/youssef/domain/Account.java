package com.falag.youssef.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String customerReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private double balance;

    @Version
    private Long version;

    public void debit(double amount) {
        assertValidAmount(amount);
        if (balance < amount) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        balance -= amount;
    }

    public void credit(double amount) {
        assertValidAmount(amount);
        if (!Double.isFinite(balance + amount)) {
            throw new IllegalArgumentException("Le solde résultant dépasse la plage autorisée");
        }
        balance += amount;
    }

    private void assertValidAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif et fini");
        }
    }
}
