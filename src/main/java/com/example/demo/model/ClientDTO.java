package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ClientDTO implements Serializable {

    private String riskProfile;

    public ClientDTO() {
    }
}
