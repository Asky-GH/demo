package com.example.demo.controller;

import com.example.demo.exception.InvalidResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Client;
import com.example.demo.model.ClientDTO;
import com.example.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clients")
public class ClientRestController {

    private final ClientService clientService;

    @GetMapping
    public List<Client> getAllResources() {
        return clientService.findAllClients();
    }

    @GetMapping("/merge")
    public List<Client> getMergedResources() {
        return clientService.mergeClients();
    }

    @GetMapping(value = "/{id}")
    public Client findResourceById(@PathVariable("id") Long id) {
        return clientService.getClient(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client createResource(@RequestBody ClientDTO clientDTO) {
        return clientService.createClient(clientDTO);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateResource(@PathVariable( "id" ) Long id, @RequestBody ClientDTO clientDTO) {
        clientService.updateClient(id, clientDTO);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteResource(@PathVariable("id") Long id) {
        clientService.deleteClientById(id);
    }

    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<String> handleException(InvalidResourceException e) {
        return new ResponseEntity<>("Invalid risk profile!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleException(ResourceNotFoundException e) {
        return new ResponseEntity<>("Client not found!", HttpStatus.NOT_FOUND);
    }
}
