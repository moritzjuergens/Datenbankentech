package de.htwberlin.mautverwaltung;

import java.sql.Connection;
import java.util.List;

import de.htwberlin.entity.Mautabschnitt;

/**
 * Die Schnittstelle enth�lt die Methoden f�r eine Mautverwaltung.
 * 
 * @author Patrick Dohmeier
 */
public interface IMautVerwaltung {




	/**
	 * Liefert den Status eines Fahrzeuger�tes zur�ck.
	 * 
	 * @param fzg_id
	 *            - die ID des Fahrzeugger�tes
	 * @return status - den Status des Fahrzeugger�tes
	 **/
	String getStatusForOnBoardUnit(long fzg_id);






	/**
	 * Liefert die Nutzernummer f�r eine Mauterhebung, die durch ein Fahrzeug im
	 * Automatischen Verfahren ausgel�st worden ist.
	 * 
	 * @param maut_id
	 *            - die ID aus der Mauterhebung
	 * @return nutzer_id - die Nutzernummer des Fahrzeughalters
	 **/
	int getUsernumber(int maut_id);







	/**
	 * Registriert ein Fahrzeug in der Datenbank f�r einen bestimmten Nutzer.
	 * 
	 * @param fz_id
	 *            - die eindeutige ID des Fahrzeug
	 * @param sskl_id
	 *            - die ID der Schadstoffklasse mit dem das Fahrzeug angemeldet
	 *            wird
	 * @param nutzer_id
	 *            - der Nutzer auf dem das Fahrzeug angemeldet wird
	 * @param kennzeichen
	 *            - das amtliche Kennzeichen des Fahrzeugs
	 * @param fin
	 *            - die eindeutige Fahrzeugindentifikationsnummer
	 * @param achsen
	 *            - die Anzahl der Achsen, die das Fahrzeug hat
	 * @param gewicht
	 *            - das zul�ssige Gesamtgewicht des Fahrzeugs
	 * @param zulassungsland
	 *            - die Landesbezeichnung f�r das Fahrzeug in dem es offiziell
	 *            angemeldet ist
	 * 
	 * **/
	void registerVehicle(long fz_id, int sskl_id, int nutzer_id,
			String kennzeichen, String fin, int achsen, int gewicht,
			String zulassungsland);




	/**
	 * Aktualisiert den Status eines Fahrzeugger�tes in der Datenbank.
	 * 
	 * @param fzg_id
	 *            - die ID des Fahrzeugger�tes
	 * @param status
	 *            - der Status auf dem das Fahrzeugger�t aktualisiert werden
	 *            soll
	 */
	void updateStatusForOnBoardUnit(long fzg_id, String status);



	/**
	 * L�scht ein Fahrzeug in der Datenbank.
	 * 
	 * @param fz_id
	 *            - die eindeutige ID des Fahrzeugs
	 */
	void deleteVehicle(long fz_id);




	/**
	 * liefert eine Liste von Mautabschnitten eines bestimmten Abschnittstypen
	 * zur�ck. z.B. alle Mautabschnitte der Autobahn A10
	 * 
	 * @param abschnittsTyp
	 *            - der AbschnittsTyp kann bspw. eine bestimmte Autobahn (A10)
	 *            oder Bundesstrasse (B1) sein
	 * @return List<Mautabschnitt> - eine Liste des Abschnittstypen, bspw. alle
	 *         Abschnitte der Autobahn A10
	 **/
	List<Mautabschnitt> getTrackInformations(String abschnittstyp);

	 /**
   * Speichert die uebergebene Datenbankverbindung in einer Instanzvariablen.
   * 
   * @author Ingo Classen
   */
  void setConnection(Connection connection);
}
