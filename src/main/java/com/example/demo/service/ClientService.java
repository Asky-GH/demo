package com.example.demo.service;

import com.example.demo.model.Client;
import com.example.demo.model.ClientDTO;

import java.util.List;

public interface ClientService {

    List<Client> findAllClients();

    List<Client> mergeClients();

    Client getClient(Long id);

    Client createClient(ClientDTO clientDTO);

    void updateClient(Long id, ClientDTO clientDTO);

    void deleteClientById(Long id);
}
