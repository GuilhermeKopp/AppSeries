package model.dao;

import java.util.List;

import model.entities.Categoria;
import model.entities.Series;

public interface SeriesDao {

	void insert(Series obj);
	void update(Series obj);
	void deleteById(Integer id);
	Series findById(Integer id);
	List<Series> findAll();
	List<Series> findByCategoria(Categoria categoria);
}
