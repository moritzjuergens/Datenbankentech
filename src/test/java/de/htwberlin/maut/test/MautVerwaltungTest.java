package de.htwberlin.maut.test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.OrderedTableNameMap;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.entity.Mautabschnitt;
import de.htwberlin.mautverwaltung.IMautVerwaltung;
import de.htwberlin.mautverwaltung.MautVerwaltungImpl;
import de.htwberlin.test.utils.DbUnitUtils;
import de.htwberlin.utils.DbCred;

/**
 * Die Klasse enthaelt die Testfaelle fuer die Methoden des Mautservice /
 * Mauterhebung.
 * 
 * @author Patrick Dohmeier
 */
public class MautVerwaltungTest {

	private static final Logger L = LoggerFactory.getLogger(MautVerwaltungTest.class);
	private static IDatabaseTester dbTester;
	private static IDatabaseConnection dbTesterCon = null;
	private static String dataDirPath = "de/htwberlin/test/data/jdbc/";
	private static URL dataFeedUrl = ClassLoader.getSystemResource(dataDirPath);
	private static IDataSet feedDataSet = null;

	private static IMautVerwaltung maut = new MautVerwaltungImpl();

	// Wird vor jedem Test ausgefuehrt
	@org.junit.Before
	public void setUp() throws Exception {
		L.debug("start");
		try {

			dbTester = new JdbcDatabaseTester(DbCred.driverClass, DbCred.url, DbCred.user, DbCred.password,
					DbCred.schema);
			dbTesterCon = dbTester.getConnection();
			feedDataSet = new CsvURLDataSet(dataFeedUrl);
			dbTester.setDataSet(feedDataSet);
			DatabaseOperation.CLEAN_INSERT.execute(dbTesterCon,feedDataSet);
			maut.setConnection(dbTesterCon.getConnection());
		} catch (Exception e) {
			DbUnitUtils.closeDbUnitConnectionQuietly(dbTesterCon);
			throw new RuntimeException(e);
		}
	}

	// Wird nach jedem Test ausgefÃ¼hrt
	@org.junit.After
	public void tearDown() throws Exception {
		L.debug("start");
		DbUnitUtils.closeDbUnitConnectionQuietly(dbTesterCon);

	}

	/**
	 * Der Testfall testet im Erfolgsfall, ob der Status des Fahrzeuggerätes
	 * (On-Board-Unit) korrekt zurückgegeben wird.
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung() throws Exception {
		// Der Status des Fahrzeuggerätes mit der ID 5456921154 soll ermittelt
		// und zurückgegeben werden.
		String status = maut.getStatusForOnBoardUnit(5456921154L);
		// Vergleiche zu erwartendes Ergebnis mit dem tatsächlichen Ergebnis
		assertEquals("Der Status des Fahrzeuggerätes stimmt nicht mit dem erwarteten Status überein!", "inactive",
				status);
	}

	/**
	 * Der Testfall testet im Erfolgsfall, ob die Rückgabe der richtigen
	 * Nutzernummer zu einer Mauterhebung im Automatischen Verfahren korrekt ist
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung_1() throws Exception {
		// Im Automatischen Verfahren wurde bereits eine Mauterhebung durch ein
		// Fahrzeuggerät durchgeführt. Für die Mauterhebung mit der Maut_ID 1015
		// soll nun die Nutzernummer ermittelt werden.
		int nutzer_id = maut.getUsernumber(1015);
		// Vergleiche zu erwartendes Ergebnis mit dem tatsächlichen Ergebnis
		assertEquals("Die Nutzernummer stimmt nicht mit der erwarteten überein!", 1000002, nutzer_id);
	}

	/**
	 * Der Testfall testet im Erfolgfall, ob ein Fahrzeug korrekt in die Tabelle
	 * Fahrzeug eingefügt und gespeichert worden ist.
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung_2() throws Exception {
		// Das Fahrzeug mit dem Kennzeichen 935 DGG aus Spanien soll in die
		// Tabelle Fahrzeug hinzugefügt werden. Gleichzeitig muss auch ein
		// Anmeldedatum (Systemdatum) gesetzt werden.
		maut.registerVehicle(100441556794622L, 3, 1000009, "935 GDD", "WD60554718", 5, 12500, "Spanien");

		// Hole Daten aus der Tabelle FAHRZEUG
		QueryDataSet databaseDataSet = new QueryDataSet(dbTesterCon);
		String sql = "select * from FAHRZEUG";
		databaseDataSet.addTable("FAHRZEUG", sql);
		// Vergleiche zu erwartendes Ergebnis mit dem tatsächlichen Ergebnis aus
		// der DB
		ITable actualTable = databaseDataSet.getTable("FAHRZEUG");
		assertEquals("Anzahl der registrierten Fahrzeuge stimmt nicht überein!", 101, actualTable.getRowCount());

	}

	/**
	 * Der Testfall testet im Erfolgsfall, ob der Status eines Fahrzeuggerätes
	 * (On-Board-Unit) korrekt geändert worden ist.
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung_3() throws Exception {
		// Das Fahrzeuggerät mit der ID soll einen anderen Status bekommen. In
		// diesem Fall soll der Status auf inactive gesetzt werden.
		maut.updateStatusForOnBoardUnit(1696502191, "inactive");
		// Vergleiche zu erwartendes Ergebnis mit dem tatsächlichen Ergebnis aus
		// der DB
		assertEquals("Der Status des Fahrzeuggerätes stimmt nicht mit dem erwarteten Status überein!", "inactive",
				maut.getStatusForOnBoardUnit(1696502191));
	}

	/**
	 * Der Testfall testet im Erfolgsfall das Löschen eines Fahrzeugs aus der
	 * Datenbank
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung_4() throws Exception {
		// Das Fahrzeug mir der ID 100441556794623 soll aus der Datenbank
		// gelöscht werden.
		maut.deleteVehicle(100441556794623L);
		// Hole Daten aus der Tabelle FAHRZEUG
		QueryDataSet databaseDataSet = new QueryDataSet(dbTesterCon);
		String sql = "select * from FAHRZEUG where  fz_id <> 100441556794622";
		databaseDataSet.addTable("FAHRZEUG", sql);
		// Vergleiche zu erwartendes Ergebnis mit dem tatsächlichen Ergebnis aus
		// der DB
		ITable actualTable = databaseDataSet.getTable("FAHRZEUG");

		assertEquals("Anzahl der registrierten Fahrzeuge stimmt nicht überein!", 99, actualTable.getRowCount());
	}

	/**
	 * Der Testfall testet im Erfolgsfall das Laden von Mautabschnitten aus der
	 * Datenbank
	 * 
	 */
	@org.junit.Test
	public void testMautverwaltung_5() throws Exception {
		// Abschnittyp B13, also die Bundesstrasse 13 besitzt eine bestimmte
		// Anzahl von Mautabschnittsobjekten, die geladen werden sollen.
		List<Mautabschnitt> m = maut.getTrackInformations("B13");
		assertEquals("Die Anzahl der geladenen Mautabschnittsobjekt stimmt nicht überein!", 3, m.size());
	}
}
