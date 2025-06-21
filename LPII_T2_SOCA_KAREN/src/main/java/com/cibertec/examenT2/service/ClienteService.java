package com.cibertec.examenT2.service;

import com.cibertec.examenT2.model.Cliente;
import com.cibertec.examenT2.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Transactional
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }
}