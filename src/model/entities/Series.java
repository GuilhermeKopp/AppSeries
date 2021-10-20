package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Series implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nome;
	private Integer temporadas;
	private Date adicionadoEm;
	private Double nota;
	
	private Categoria categoria;
	
	public Series() {
	}

	public Series(Integer id, String nome, Integer temporadas, Date adicionadoEm, Double nota, Categoria categoria) {
		this.id = id;
		this.nome = nome;
		this.temporadas = temporadas;
		this.adicionadoEm = adicionadoEm;
		this.nota = nota;
		this.categoria = categoria;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getTemporadas() {
		return temporadas;
	}

	public void setTemporadas(Integer temporadas) {
		this.temporadas = temporadas;
	}

	public Date getAdicionadoEm() {
		return adicionadoEm;
	}

	public void setAdicionadoEm(Date adicionadoEm) {
		this.adicionadoEm = adicionadoEm;
	}

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Series other = (Series) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Series [id=" + id + ", nome=" + nome + ", temporadas=" + temporadas + ", adicionadoEm=" + adicionadoEm + ", nota="
				+ nota + ", categoria=" + categoria + "]";
	}
}
