package com.br.api.v1.controller;

import com.br.api.v1.mapper.MobilModelMapper;
import com.br.api.v1.model.*;
import com.br.api.v1.model.input.*;
import com.br.domain.model.Mobil;
import com.br.domain.service.*;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.br.api.v1.mapper.MovementModelMapper;
import com.br.domain.model.Movement;
import com.br.domain.model.enums.TypeMovement;
import com.br.domain.service.MovementService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v1/movimentacao/")
public class MovementController {
	
	@Autowired
	private MovementService movementService;

	@Autowired
	private MobilService mobilService;
	
	@Autowired
	private MovementModelMapper movementModelMapper;

	@Autowired
	private MobilModelMapper mobilModelMapper;

	@Autowired
	private MarkService markService;
	
	@GetMapping("/filtro")
	public ResponseEntity<Page<?>> findAll(
	        @RequestParam(value = "mobilId", required = false) UUID mobilId,
	        @RequestParam(value = "typeMovement", required = false) TypeMovement typeMovement,
	        @PageableDefault(page = 0, size = 10) Pageable pageable) {

	    // Chama o serviço de busca com os parâmetros fornecidos
	    Page<?> result = movementService.buscarMovimentacoesDoMobilFiltro(mobilId, typeMovement, pageable);
	    
	    return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/filtro-boolean")
	public ResponseEntity<Boolean> findByBoolean(@RequestParam UUID mobilId, @RequestParam TypeMovement typeMovement) {
		Boolean existe = movementService.buscarMovimentacoesDoMobilFiltroBoolean(mobilId, typeMovement);
		return ResponseEntity.status(HttpStatus.OK).body(existe);
	}

	@PostMapping("/assinar-com-senha/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> assinarComSenha(@PathVariable(name = "siglaDocumento") String siglaDocumento,
			@RequestBody MovementAssSenhaInput movementAssSenhaInput) {
		Movement movement = movementService.criarMovimentacaoAssinarComSenha(siglaDocumento, movementAssSenhaInput.getSubscritorId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}

	@PostMapping("/incluir-cossignatario/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> incluirCossinatario(@PathVariable(name = "siglaDocumento") String siglaDocumento,
			@RequestBody MovementAssSenhaInput movementAssSenhaInput) {
		Movement movement = movementService.criarMovimentacaoIncluirCossignatario(siglaDocumento, movementAssSenhaInput.getSubscritorId(), movementAssSenhaInput.getPessoaRecebedoraId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}
	
	@PostMapping("/tramitar-documento/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> tramitarDocumento(@PathVariable(name = "siglaDocumento") String siglaDocumento,
			@RequestBody MovementAssSenhaInput movementAssSenhaInput) {
		Movement movement = movementService.criarMovimentacaoTramitarDocumento(siglaDocumento, movementAssSenhaInput.getSubscritorId(), movementAssSenhaInput.getPessoaRecebedoraId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}

	@PostMapping("/tramitar-documento-para-departamento/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> tramitarDocumentoParaDepartamento(@PathVariable(name = "siglaDocumento") String siglaDocumento,
																					   @RequestBody MovementTramitarParaDapartmentInput movementAssSenhaInput) {
		Movement movement = movementService.criarMovimentacaoTramitarDocumentoParaLotacao(siglaDocumento, movementAssSenhaInput.getSubscritorId(), movementAssSenhaInput.getDepartmentId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}
	
	@PostMapping("/finalizacao-documento/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> finalizarDocumento(@PathVariable(name = "siglaDocumento") String siglaDocumento,
			@RequestBody MovementAssSenhaInput movementAssSenhaInput) {
		Movement movement = movementService.criarMovimentacaoFinalizacaoDocumento(siglaDocumento, movementAssSenhaInput.getSubscritorId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}

	@DeleteMapping("/excluir-movimentacao/{siglaMobil}/{movimentacaoId}")
    public ResponseEntity<Void> verificarEExcluirMovimentacao(
    		@PathVariable(name = "siglaMobil") String siglaMobil, @PathVariable(name = "movimentacaoId") UUID movimentacaoId) {
        movementService.verificarEExcluirMovimentacao(siglaMobil, movimentacaoId);
        return ResponseEntity.noContent().build();
    }
	
    @PostMapping("/excluir-documento/{siglaDocumento}")
    public ResponseEntity<Movement> criarMovimentacaoExcluirDocumento(
            @PathVariable(name = "siglaDocumento") String siglaMobil,
            @RequestBody MovementExclusaoDocumentoInput movementExclusaoDocumentoInput) {
        Movement movimentacaoExclusao = movementService.criarMovimentacaoExcluirDocumento(siglaMobil, movementExclusaoDocumentoInput.getSubscritorId());
        return ResponseEntity.ok(movimentacaoExclusao);
    }

	@PostMapping("/recebimento-documento/{siglaDocumento}")
	public ResponseEntity<MovimentacaoAssinadaModel> recebimentoDocumento(@PathVariable(name = "siglaDocumento") String siglaDocumento,
																		  @RequestBody MovementRecebimentoDocumentoInput movementRecDoc) {
		Movement movement = movementService.criacaoMovimentacaoRecebimentoDocumento(siglaDocumento, movementRecDoc.getPessoaRecebedoraId() ,movementRecDoc.getPessoaRecebedoraId());
		Mobil mobil = mobilService.buscarMobil(movement.getMobil().getMobilId());
		MovimentacaoAssinadaModel movimentacaoAssinadaModel = movementModelMapper.toModelMovAssinada(movement);
		MobilModel mobilModel = mobilModelMapper.toModel(mobil);
		movimentacaoAssinadaModel.setMobilModel(mobilModel);
		return ResponseEntity.status(HttpStatus.OK).body(movimentacaoAssinadaModel);
	}

}
