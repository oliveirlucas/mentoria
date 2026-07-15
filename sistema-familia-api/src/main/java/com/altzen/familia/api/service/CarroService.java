package com.altzen.familia.api.service;

import com.altzen.familia.api.dto.CarroRequest;
import com.altzen.familia.api.exception.BusinessException;
import com.altzen.familia.api.exception.NotFoundException;
import com.altzen.familia.api.model.Carro;
import com.altzen.familia.api.model.Pessoa;
import com.altzen.familia.api.repository.CarroRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CarroService {

    private final CarroRepository carroRepository;
    private final PessoaService pessoaService;

    public CarroService(CarroRepository carroRepository, PessoaService pessoaService) {
        this.carroRepository = carroRepository;
        this.pessoaService = pessoaService;
    }

    public Carro cadastrar(CarroRequest request) {
        Pessoa dono = null;
        if (request.getDonoId() != null) {
            Pessoa candidato = pessoaService.buscar(request.getDonoId());
            if (!candidato.podeSerProprietarioDeBemImovelOuVeiculo()) {
                throw new BusinessException(candidato.getNome()
                        + " e menor de idade e nao pode ser proprietario de carro.");
            }
            dono = candidato;
        }
        Carro carro = new Carro(
                request.getNome().trim(),
                request.getIdade(),
                request.getMarca().trim(),
                request.getValor(),
                dono);
        return carroRepository.salvar(carro);
    }

    public List<Carro> listar() {
        return carroRepository.listar();
    }

    public Carro buscar(Long id) {
        return carroRepository.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Carro com id " + id + " nao encontrado."));
    }
}
