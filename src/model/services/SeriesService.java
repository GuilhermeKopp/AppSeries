package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SeriesDao;
import model.entities.Series;

public class SeriesService {
	
	private SeriesDao dao = DaoFactory.createSeriesDao();

	public List<Series> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Series obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove (Series obj) {
		dao.deleteById(obj.getId());
	}
}
