package de.htwberlin.mautverwaltung;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.entity.Mautabschnitt;
import de.htwberlin.exceptions.DataException;

/**
 * Die Klasse realisiert die Mautverwaltung.
 * 
 * @author Patrick Dohmeier
 */
public class MautVerwaltungImpl implements IMautVerwaltung {

	private static final Logger L = LoggerFactory.getLogger(MautVerwaltungImpl.class);
	private Connection connection;

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private Connection getConnection() {
		if (connection == null) {
			throw new DataException("Connection not set");
		}
		return connection;
	}

	@Override
	public String getStatusForOnBoardUnit(long fzg_id) {
		String sql = "SELECT status FROM Fahrzeuggerat WHERE fzg_id = ?";
		try(PreparedStatement ps = getConnection().prepareStatement(sql)) {
			ps.setLong(1, fzg_id);
			try(ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("status");
				} else {
					throw new ServiceException("id doesnt exist in db: " + fzg_id);
				}
			}
		}catch (SQLException e) {
			L.error("", e);
			throw new DataException(e);
		}
	}

	@Override
	public int getUsernumber(int maut_id) {
		// TODO Auto-generated method stub
		String sql = "SELECT nutzer_id FROM fahrzeug f INNER JOIN fahrzeuggerat fzg ON f.fz_id = fzg.fz_id INNER JOIN mauterhebung me ON me.fzg_id = fzg.fzg_id " +
				"WHERE me.maut_id = ? ";
		try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
			ps.setInt(1, maut_id);
			try(ResultSet rs = ps.executeQuery()) {
				if(rs.next()){
					return rs.getInt("nutzer_id");
				} else {
					throw new ServiceException("maut_id doesnt exist in db:" + maut_id);
				}
			}
		} catch (SQLException e){
			L.error("", e);
			throw new DataException(e);
		}
	}

	@Override
	public void registerVehicle(long fz_id, int sskl_id, int nutzer_id, String kennzeichen, String fin, int achsen,
			int gewicht, String zulassungsland) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO fahrzeug (fz_id, sskl_id, nutzer_id, kennzeichen, fin, achsen, gewicht,anmeldedatum,abmeldedatum,zulassungsland) VALUES (?,?,?,?,?,?,?,sysdate,null,?)";
		try(PreparedStatement ps = getConnection().prepareStatement(sql)) {
			ps.setLong(1,fz_id);
			ps.setInt(2,sskl_id);
			ps.setInt(3,nutzer_id);
			ps.setString(4, kennzeichen);
			ps.setString(5,fin);
			ps.setInt(6,achsen);
			ps.setInt(7,gewicht);
			ps.setString(8,zulassungsland);
			ps.executeUpdate();
		}
		catch(SQLException e){
			L.error("",e);
			throw new DataException(e);
		}

	}

	@Override
	public void updateStatusForOnBoardUnit(long fzg_id, String status) {
		// TODO Auto-generated method stub
        String sql = "UPDATE fahrzeuggerat SET status = ? WHERE fzg_id = ? ";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
        	ps.setString(1, status);
        	ps.setLong(2,fzg_id);
        	ps.executeUpdate();
		}
        catch (SQLException e){
        	L.error("", e);
        	throw new DataException(e);
		}
	}

	@Override
	public void deleteVehicle(long fz_id) {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM fahrzeug WHERE fz_id = ?";
		try(PreparedStatement ps = getConnection().prepareStatement(sql)){
			ps.setLong(1, fz_id);
			ps.executeUpdate();
		}
		catch (SQLException e){
			L.error("",e);
			throw new DataException(e);
		}

	}

	@Override
	public List<Mautabschnitt> getTrackInformations(String abschnittstyp) {
		// TODO Auto-generated method stub
		List<Mautabschnitt> mautabschnitt = new LinkedList<>();
		String sql = "SELECT * FROM mautabschnitt WHERE abschnittstyp = ?";
		try(PreparedStatement ps = getConnection().prepareStatement(sql)){
			ps.setString(1, abschnittstyp);
			try(ResultSet rs = ps.executeQuery()){
				while(rs.next()){
					Integer id = rs.getInt("abschnitts_id");
					Integer laenge = rs.getInt("laenge");
					String start_koordinate = rs.getString("start_koordinate");
					String ziel_koordinate = rs.getString("ziel_koordinate");
					String name = rs.getString("name");
					Mautabschnitt abschnitt = new Mautabschnitt(id,laenge,start_koordinate,ziel_koordinate,name,abschnittstyp);
					mautabschnitt.add(abschnitt);
				}
			}
            return mautabschnitt;
		}catch (SQLException e){
             L.error("",e);
             throw new DataException(e);
		}
	}

}
