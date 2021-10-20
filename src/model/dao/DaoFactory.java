package model.dao;

import db.DB;
import model.dao.impl.CategoriaDaoJDBC;
import model.dao.impl.SeriesDaoJDBC;

public class DaoFactory {

	public static SeriesDao createSeriesDao() {
		return new SeriesDaoJDBC(DB.getConnection());
	}
	
	public static CategoriaDao createCategoriaDao() {
		return new CategoriaDaoJDBC(DB.getConnection());
	}
}
