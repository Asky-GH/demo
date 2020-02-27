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
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public List<Client> merge() {
        List<Client> clients = clientRepository.findAll();
        List<String> riskProfiles = StreamSupport.stream(clients.spliterator(), false)
                .map(Client::getRiskProfile)
                .distinct().collect(Collectors.toList());
        return mergeClients(clients, riskProfiles);
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Client create(ClientDTO clientDTO) {
        checkResource(clientDTO);
        return clientRepository.save(new Client(clientDTO.getRiskProfile()));
    }

    @Transactional
    public void update(Client client, ClientDTO clientDTO) {
        checkResource(clientDTO);
        client.setRiskProfile(clientDTO.getRiskProfile());
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    private List<Client> mergeClients(List<Client> clients, List<String> riskProfiles) {
        List<Client> mergedClients;
        if (riskProfiles.contains(RiskProfile.HIGH.name())) {
            mergedClients = StreamSupport.stream(clients.spliterator(), false)
                    .peek(client -> client.setRiskProfile(RiskProfile.HIGH.name())).collect(Collectors.toList());
        } else if (riskProfiles.contains(RiskProfile.NORMAL.name())) {
            mergedClients = StreamSupport.stream(clients.spliterator(), false)
                    .peek(client -> client.setRiskProfile(RiskProfile.NORMAL.name())).collect(Collectors.toList());
        } else {
            mergedClients = StreamSupport.stream(clients.spliterator(), false)
                    .peek(client -> client.setRiskProfile(RiskProfile.LOW.name())).collect(Collectors.toList());
        }
        return mergedClients;
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
