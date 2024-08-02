package com.br.domain.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.br.domain.exception.EntidadeNaoExisteException;
import com.br.domain.model.Departamento;
import com.br.domain.repository.DepartamentoRepository;
import com.br.domain.service.DepartamentoService;
import com.br.infrastructure.external.service.governmentagency.GovernmentAgencyFeignClient;
import com.br.infrastructure.external.service.governmentagency.model.Orgao;

@Service
public class DepartamentoServiceImpl implements DepartamentoService {
	
	@Autowired
	DepartamentoRepository departamentoRepository;
	
	@Autowired
	private GovernmentAgencyFeignClient governmentAgencyFeignClient;

	@Override
	public Departamento activaDepartamento(UUID id, Boolean active) {
		Departamento departamento = departamentoRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
		departamento.setActive(active);
		return departamentoRepository.save(departamento);
	}

    @Override
	public Departamento deactivateDepartamento (UUID id) {
		Departamento departamento = departamentoRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
		departamento.setActive(false);
		return departamentoRepository.save(departamento);
	}
    
    @Override
    public Departamento save(Departamento departamento) {
        // Gerar sigla
        String nomeCompleto = departamento.getNome();
        String[] palavras = nomeCompleto.split("\\s+");
        int indiceUltimaPalavra = palavras.length - 1;
        String ultimaPalavra = palavras[indiceUltimaPalavra].substring(0, 2);
        String primeiraPalavra = nomeCompleto.substring(0, 2);
        String sigla = primeiraPalavra + ultimaPalavra;
        departamento.setSigla(sigla);

        // Setar active para novos departamentos
        if (departamento.getDepartamentoId() == null) {
            departamento.setActive(true);
        }

        // Validar orgao_id
        UUID orgaoId = departamento.getOrgaoId();
        if (orgaoId != null) {
            try {
                Orgao orgao = governmentAgencyFeignClient.getOrgaoId(orgaoId);
                if (orgao == null) {
                    throw new IllegalArgumentException("Orgao com ID " + orgaoId + " não encontrado.");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Falha ao validar orgao_id: " + e.getMessage());
            }
        }

        return departamentoRepository.save(departamento);
    }

	@Override
	public Page<Departamento> findAll(Specification<Departamento> spec, Pageable pageable) {
		return departamentoRepository.findAll(spec, pageable);
	}
	
	@Override
	public Departamento findById(UUID id) {
		Optional<Departamento> departamento = departamentoRepository.findById(id);
		if(departamento.isEmpty()) {
			throw new EntidadeNaoExisteException("Usuário informado não existe: " + id);
		}
		return departamento.get();
	}
	
	
}
