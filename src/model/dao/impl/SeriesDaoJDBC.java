package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SeriesDao;
import model.entities.Categoria;
import model.entities.Series;

public class SeriesDaoJDBC implements SeriesDao {

	private Connection conn;
	
	public SeriesDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Series obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO series "
					+ "(Nome, Temporadas, AdicionadoEm, Nota, CategoriaId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getTemporadas());
			st.setDate(3, new java.sql.Date(obj.getAdicionadoEm().getTime()));
			st.setDouble(4, obj.getNota());
			st.setInt(5, obj.getCategoria().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Series obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE series "
					+ "SET Nome = ?, Temporadas = ?, AdicionadoEm = ?, Nota = ?, CategoriaId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getTemporadas());
			st.setDate(3, new java.sql.Date(obj.getAdicionadoEm().getTime()));
			st.setDouble(4, obj.getNota());
			st.setInt(5, obj.getCategoria().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM series WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Series findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT series.*,categoria.Nome as CatNome "
					+ "FROM series INNER JOIN categoria "
					+ "ON series.CategoriaId = caategoria.Id "
					+ "WHERE series.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Categoria cat = instantiateCategoria(rs);
				Series obj = instantiateSeries(rs, cat);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Series instantiateSeries(ResultSet rs, Categoria cat) throws SQLException {
		Series obj = new Series();
		obj.setId(rs.getInt("Id"));
		obj.setNome(rs.getString("Nome"));
		obj.setTemporadas(rs.getInt("Temporadas"));
		obj.setNota(rs.getDouble("Nota"));
		obj.setAdicionadoEm(new java.util.Date(rs.getTimestamp("AdicionadoEm").getTime()));
		obj.setCategoria(cat);
		return obj;
	}

	private Categoria instantiateCategoria(ResultSet rs) throws SQLException {
		Categoria cat = new Categoria();
		cat.setId(rs.getInt("CategoriaId"));
		cat.setNome(rs.getString("CatNome"));
		return cat;
	}

	@Override
	public List<Series> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT series.*,categoria.Nome as CatNome "
					+ "FROM series INNER JOIN categoria "
					+ "ON series.CategoriaId = categoria.Id "
					+ "ORDER BY Nome");
			
			rs = st.executeQuery();
			
			List<Series> list = new ArrayList<>();
			Map<Integer, Categoria> map = new HashMap<>();
			
			while (rs.next()) {
				
				Categoria cat = map.get(rs.getInt("CategoriaId"));
				
				if (cat == null) {
					cat = instantiateCategoria(rs);
					map.put(rs.getInt("CategoriaId"), cat);
				}
				
				Series obj = instantiateSeries(rs, cat);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Series> findByCategoria(Categoria categoria) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT series.*,categoria.Nome as CatNome "
					+ "FROM series INNER JOIN categoria "
					+ "ON series.CategoriaId = categoria.Id "
					+ "WHERE CategoriaId = ? "
					+ "ORDER BY Nome");
			
			st.setInt(1, categoria.getId());
			
			rs = st.executeQuery();
			
			List<Series> list = new ArrayList<>();
			Map<Integer, Categoria> map = new HashMap<>();
			
			while (rs.next()) {
				
				Categoria cat = map.get(rs.getInt("CategoriaId"));
				
				if (cat == null) {
					cat = instantiateCategoria(rs);
					map.put(rs.getInt("CategoriaId"), cat);
				}
				
				Series obj = instantiateSeries(rs, cat);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
