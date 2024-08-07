package com.br.domain.model.enums;

public enum TypeMovement{

	CRIACAO(1, "CRIACAO"),
	ASSINATURA_COM_SENHA(2, "ASSINATURA_COM_SENHA"),
	INCLUSAO_DE_COSIGNATARIO(3, "INCLUSAO_DE_COSSIGNATARIO"),
	TRAMITAR(4, "Tramitar para pessoa"),
	FINALIZACAO(5, "FINALIZACAO"),
	TRAMITAR_PARA_LOTACAO(6, "Tramitar para lotação"),
	EXCLUSAO_DOCUMENTO(7,"EXCLUSAO_DOCUMENTO"),
	RECEBIMENTO_DOCUMENTO(8,"RECEBIMENTO_DOCUMENTO");
	
	private final int id;
	private final String descr;

	TypeMovement(int id, String descr) {
		this.id = id;
		this.descr = descr;
	}

	public long getId() {
		return id;
	}
}
