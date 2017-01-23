package br.com.petrobras.sime.entity;

public enum TipoRelEnum {

	PDF(1, "PDF", "application/pdf", ".pdf"), EXCEL(2, "EXCEL", "vnd.ms-excel", ".xls");

	private Integer id;
	private String descricao;
	private String contentType;
	private String extensao;

	TipoRelEnum(Integer id, String descricao, String contentType, String extensao) {
		this.id = id;
		this.descricao = descricao;
		this.contentType = contentType;
		this.extensao = extensao;
	}

	public Integer getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getContentType() {
		return contentType;
	}

	public String getExtensao() {
		return extensao;
	}

}
