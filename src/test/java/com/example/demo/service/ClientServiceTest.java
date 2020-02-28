package com.example.demo.service;

import com.example.demo.exception.InvalidResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Client;
import com.example.demo.model.ClientDTO;
import com.example.demo.model.RiskProfile;
import com.example.demo.repository.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private ClientService clientService;
    private List<Client> existingClients;

    @Mock
    private ClientRepository clientRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clientService = new ClientServiceImpl(clientRepository);
        existingClients = Arrays.asList(
                new Client(1L, RiskProfile.HIGH.name()),
                new Client(2L, RiskProfile.NORMAL.name()),
                new Client(3L, RiskProfile.LOW.name())
        );
    }

    @Test
    public void shouldReturnAllClientsWhenThereAreAny() {
        when(clientRepository.findAll()).thenReturn(existingClients);

        List<Client> clientsFound = clientService.findAllClients();
        assertEquals(clientsFound.size(), existingClients.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnEmptyListWhenThereAreNoClients() {
        existingClients = new ArrayList<>();

        when(clientRepository.findAll()).thenReturn(existingClients);

        List<Client> clientsFound = clientService.findAllClients();
        assertEquals(clientsFound.size(), existingClients.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnClientsWithHighRiskProfileWhenTheHighestRiskProfileIsHigh() {
        when(clientRepository.findAll()).thenReturn(existingClients);
        when(clientRepository.existsByRiskProfile(RiskProfile.HIGH.name())).thenReturn(true);

        List<Client> mergedClientList = Arrays.asList(
                new Client(1L, RiskProfile.HIGH.name()),
                new Client(2L, RiskProfile.HIGH.name()),
                new Client(3L, RiskProfile.HIGH.name())
        );

        List<Client> result = clientService.mergeClients();

        verify(clientRepository, times(1)).findAll();
        verify(clientRepository, times(1)).existsByRiskProfile(RiskProfile.HIGH.name());
        assertEquals(result.size(), mergedClientList.size());
        assertEquals(result, mergedClientList);
    }

    @Test
    public void shouldReturnClientsWithNormalRiskProfileWhenTheHighestRiskProfileIsNormal() {
        existingClients = Arrays.asList(
                new Client(1L, RiskProfile.LOW.name()),
                new Client(2L, RiskProfile.NORMAL.name()),
                new Client(3L, RiskProfile.LOW.name())
        );

        when(clientRepository.findAll()).thenReturn(existingClients);
        when(clientRepository.existsByRiskProfile(RiskProfile.NORMAL.name())).thenReturn(true);

        List<Client> mergedClientList = Arrays.asList(
                new Client(1L, RiskProfile.NORMAL.name()),
                new Client(2L, RiskProfile.NORMAL.name()),
                new Client(3L, RiskProfile.NORMAL.name())
        );

        List<Client> result = clientService.mergeClients();

        verify(clientRepository, times(1)).findAll();
        verify(clientRepository, times(1)).existsByRiskProfile(RiskProfile.HIGH.name());
        verify(clientRepository, times(1)).existsByRiskProfile(RiskProfile.NORMAL.name());
        assertEquals(result.size(), mergedClientList.size());
        assertEquals(result, mergedClientList);
    }

    @Test
    public void shouldReturnClientWhenClientExists() {
        when(clientRepository.findById(2L)).thenReturn(Optional.of(existingClients.get(1)));

        Client client = clientService.getClient(2L);
        assertEquals(client.getId(), existingClients.get(1).getId());
        verify(clientRepository, times(1)).findById(2L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThroughResourceNotFoundExceptionWhenClientDoesNotExist() {
        when(clientRepository.findById(10L)).thenThrow(ResourceNotFoundException.class);

        clientService.getClient(10L);
        verify(clientRepository, times(1)).findById(10L);
    }

    @Test
    public void shouldSaveClientWhenRequestBodyIsValid() {
        ClientDTO clientDTO = new ClientDTO(RiskProfile.LOW.name());

        when(clientRepository.save(new Client(clientDTO.getRiskProfile())))
                .thenReturn(new Client(4L, clientDTO.getRiskProfile()));

        Client newClient = clientService.createClient(clientDTO);

        assertEquals(newClient.getId(), 4L);
        verify(clientRepository, times(1)).save(new Client(clientDTO.getRiskProfile()));
    }

    @Test
    public void shouldUpdateClientWhenRequestBodyIsValid() {
        ClientDTO clientDTO = new ClientDTO(RiskProfile.LOW.name());

        when(clientRepository.findById(2L)).thenReturn(Optional.of(existingClients.get(1)));

        clientService.updateClient(2L, clientDTO);
        Client modifiedClient = clientService.getClient(2L);

        assertEquals(modifiedClient.getId(), existingClients.get(1).getId());
        verify(clientRepository, times(2)).findById(2L);
    }

    @Test(expected = InvalidResourceException.class)
    public void shouldThroughInvalidResourceExceptionWhenRequestBodyIsInvalid() {
        ClientDTO clientDTO = new ClientDTO("DROP TABLE ...");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClients.get(0)));

        clientService.updateClient(1L, clientDTO);
    }

    @Test
    public void deleteById() {
        clientService.deleteClientById(2L);

        verify(clientRepository, times(1)).deleteById(2L);
    }
}
