package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@Entity
@Table(name = "TBL_CLIENTS")
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String riskProfile;

    public Client() {
    }

    public Client(String riskProfile) {
        this.riskProfile = riskProfile;
    }
}
