package com.example.demo.service;

import com.example.demo.exception.InvalidResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Client;
import com.example.demo.model.ClientDTO;
import com.example.demo.model.RiskProfile;
import com.example.demo.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> mergeClients() {
        List<Client> mergedClientList;
        List<Client> clients = clientRepository.findAll();
        Client client = findClientWithTheHighestRiskProfile();
        mergedClientList = merge(clients, client.getRiskProfile());
        return mergedClientList;
    }

    @Override
    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Client createClient(ClientDTO clientDTO) {
        checkResource(clientDTO);
        return clientRepository.save(new Client(clientDTO.getRiskProfile()));
    }

    @Transactional
    @Override
    public void updateClient(Long id, ClientDTO clientDTO) {
        Client client = getClient(id);
        checkResource(clientDTO);
        client.setRiskProfile(clientDTO.getRiskProfile());
    }

    @Override
    public void deleteClientById(Long id) {
        clientRepository.deleteById(id);
    }

    private Client findClientWithTheHighestRiskProfile() {
        Client client;
        if (clientRepository.existsByRiskProfile(RiskProfile.HIGH.name())) {
            client = new Client(RiskProfile.HIGH.name());
        } else if (clientRepository.existsByRiskProfile(RiskProfile.NORMAL.name())) {
            client = new Client(RiskProfile.NORMAL.name());
        } else {
            client = new Client(RiskProfile.LOW.name());
        }
        return client;
    }

    private List<Client> merge(List<Client> clients, String riskProfile) {
        return clients.stream().peek(client -> client.setRiskProfile(riskProfile)).collect(Collectors.toList());
    }

    private void checkResource(ClientDTO clientDTO) {
        if (clientDTO == null || hasInvalidRiskProfile(clientDTO)) {
            throw new InvalidResourceException();
        }
    }

    private boolean hasInvalidRiskProfile(ClientDTO clientDTO) {
        return ! Arrays.stream(RiskProfile.values())
                .map(Enum::name)
                .collect(Collectors.toList())
                .contains(clientDTO.getRiskProfile());
    }
}
