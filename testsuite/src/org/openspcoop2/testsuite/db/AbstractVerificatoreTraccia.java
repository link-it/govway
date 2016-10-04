/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.testsuite.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Verifica le tracce
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractVerificatoreTraccia {

	protected Connection con;
	protected String protocollo;
	
	public AbstractVerificatoreTraccia(Connection con,String protocollo){
		this.con = con;
		this.protocollo = protocollo;
	}
	
	protected abstract String getColumnId();
	protected abstract String getTipoTraccia();
	
	
		
	
	
	// **** PREPARED STATEMENT *******
	
	// Traccia
	protected PreparedStatement prepareStatement(String idMessaggio) throws TestSuiteException{
		try{
			PreparedStatement pstmt = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE+" where "+this.getColumnId()+"=? AND "+
							CostantiDB.TRACCE_COLUMN_PROTOCOLLO+"=?");
			pstmt.setString(1, idMessaggio);
			pstmt.setString(2, this.protocollo);
			return pstmt;
		}catch(SQLException e){
			throw new TestSuiteException(e,e.getMessage());
		}
	}
	protected PreparedStatement prepareStatement(String idMessaggio,IDSoggetto idPortaMessaggio) throws TestSuiteException{
		try{
			PreparedStatement pstmt = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE+" where "+this.getColumnId()+"=? AND "+
							CostantiDB.TRACCE_COLUMN_PDD_CODICE+"=? AND  "+CostantiDB.TRACCE_COLUMN_PDD_TIPO_SOGGETTO+"=? AND "+CostantiDB.TRACCE_COLUMN_PDD_NOME_SOGGETTO+"=? AND "+
							CostantiDB.TRACCE_COLUMN_PROTOCOLLO+"=?");
			pstmt.setString(1, idMessaggio);
			pstmt.setString(2, idPortaMessaggio.getCodicePorta());
			pstmt.setString(3, idPortaMessaggio.getTipo());
			pstmt.setString(4, idPortaMessaggio.getNome());
			pstmt.setString(5, this.protocollo);
			return pstmt;
		}catch(SQLException e){
			throw new TestSuiteException(e,e.getMessage());
		}
	}
	protected PreparedStatement prepareStatement(String idMessaggio,
			DatiServizioAzione datiServizioAzione) throws TestSuiteException {
		try{
			String tipoServizio = datiServizioAzione.getTipoServizio();
			String nomeServizio = datiServizioAzione.getNomeServizio();
			Integer versioneServizio = datiServizioAzione.getVersioneServizio();
			String azione = datiServizioAzione.getAzione();
			
			String query = "select * from "+ CostantiDB.TRACCE+"  where "+this.getColumnId()+"=? AND ";
			if(tipoServizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+" is null AND ";
			if(nomeServizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+" is null AND ";
			if(versioneServizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE+" is null AND ";
			if(azione!=null)
				query = query + CostantiDB.TRACCE_COLUMN_AZIONE+"=? ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_AZIONE+" is null ";
			PreparedStatement pstmt = this.con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, idMessaggio);
			if(tipoServizio!=null){
				pstmt.setString(index++, tipoServizio);
			}
			if(nomeServizio!=null){
				pstmt.setString(index++, nomeServizio);
			}
			if(versioneServizio!=null){
				pstmt.setInt(index++, versioneServizio);
			}
			if(azione!=null){
				pstmt.setString(index++, azione);
			}
			return pstmt;
		}catch(SQLException e){
			throw new TestSuiteException(e,e.getMessage());
		}
	}
	
	// traccia riscontri
	protected PreparedStatement prepareStatementListaRiscontri(String idMessaggioPortaRiscontro,String idRiscontro) throws TestSuiteException {
		try{
			PreparedStatement pstmt = null;
			if(idMessaggioPortaRiscontro!=null){
				pstmt = this.con
						.prepareStatement("select* from "+CostantiDB.TRACCE+","+CostantiDB.TRACCE_RISCONTRI+" where "+
								CostantiDB.TRACCE+"."+this.getColumnId()+"=? AND "+
								CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_RISCONTRI+"."+CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_TRACCIA+" AND "+
								CostantiDB.TRACCE_RISCONTRI+"."+CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO+"=?");
				pstmt.setString(1, idMessaggioPortaRiscontro);
				pstmt.setString(2, idRiscontro);
			}else{
				pstmt = this.con
						.prepareStatement("select* from "+CostantiDB.TRACCE_RISCONTRI+" where "+
								CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO+"=?");
				pstmt.setString(1, idRiscontro);
			}
			return pstmt;
		}catch(SQLException e){
			throw new TestSuiteException(e,e.getMessage());
		}
	}
	
	
	
	
	// TRACCIA
	
	
	public boolean isTraced(String idMessaggio) throws TestSuiteException {
		return _isTraced(this.prepareStatement(idMessaggio));
	}
	public boolean isTraced(String idMessaggio,IDSoggetto idPortaMessaggio) throws TestSuiteException {
		return _isTraced(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean isTraced(String idMessaggio, DatiServizioAzione datiServizioAzione) throws TestSuiteException {
		return _isTraced(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _isTraced(PreparedStatement pstmt) throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean ret = res.next();
			return ret;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTraced");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}


	public boolean isTracedRiferimentoMessaggio(String idMessaggio, String riferimentoMessaggio)
			throws TestSuiteException {
		return _isTracedRiferimentoMessaggio(this.prepareStatement(idMessaggio),riferimentoMessaggio);
	}
	public boolean isTracedRiferimentoMessaggio(String idMessaggio,IDSoggetto idPortaMessaggio, String riferimentoMessaggio)
			throws TestSuiteException {
		return _isTracedRiferimentoMessaggio(this.prepareStatement(idMessaggio,idPortaMessaggio),riferimentoMessaggio);
	}
	public boolean isTracedRiferimentoMessaggio(String idMessaggio,DatiServizioAzione datiServizioAzione, 
			String riferimentoMessaggio)
			throws TestSuiteException {
		return _isTracedRiferimentoMessaggio(this.prepareStatement(idMessaggio,datiServizioAzione),riferimentoMessaggio);
	}
	private boolean _isTracedRiferimentoMessaggio(PreparedStatement pstmt,String riferimentoMessaggio) throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;
				String valoreLetto = res.getString(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO);
				if(valoreLetto==null){
					if(riferimentoMessaggio==null)
						return true;
					else
						return false;
				}
				else{
					if (!(valoreLetto.equals(riferimentoMessaggio)) )
						return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedRiferimentoMessaggio");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public int countTracce(String idMessaggio)
			throws TestSuiteException {
		return _countTracce(this.prepareStatement(idMessaggio));
	}
	public int countTracce(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _countTracce(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public int countTracce(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _countTracce(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private int _countTracce(PreparedStatement pstmt) throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			int  i=0;
			while( res.next()) 
				i++;

			return i;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"countTracce");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public String[] getValuesTraced(String idMessaggio,String colonna)
			throws TestSuiteException {
		return _getValuesTraced(this.prepareStatement(idMessaggio),colonna);
	}
	public String[] getValuesTraced(String idMessaggio,IDSoggetto idPortaMessaggio,String colonna)
			throws TestSuiteException {
		return _getValuesTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),colonna);
	}
	public String[] getValuesTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String colonna)
			throws TestSuiteException {
		return _getValuesTraced(this.prepareStatement(idMessaggio,datiServizioAzione),colonna);
	}
	private String[] _getValuesTraced(PreparedStatement pstmt,String colonna) throws TestSuiteException{
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();
			Vector<String> out = new Vector<String>();
			while(res.next()){
				String colonnaValue = res.getString(colonna);
				if(colonnaValue!=null){
					colonnaValue = colonnaValue.trim();
				}else{
					colonnaValue = "Value is null";
				}
				out.add(colonnaValue);
			}
			if(out.size()==0)
				return null;
			else{
				String [] tmp = new String[1];
				return out.toArray(tmp);
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getValuesTracedIntoResponse");
		} finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	



	// *** DATI GENERALI
	
	public boolean isTracedRuoloPdd(String idMessaggio, String ruoloPdd)
			throws TestSuiteException {
		return _isTracedRuoloPdd(this.prepareStatement(idMessaggio),ruoloPdd);
	}
	public boolean isTracedRuoloPdd(String idMessaggio,IDSoggetto idPortaMessaggio, String ruoloPdd)
			throws TestSuiteException {
		return _isTracedRuoloPdd(this.prepareStatement(idMessaggio,idPortaMessaggio),ruoloPdd);
	}
	public boolean isTracedRuoloPdd(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String ruoloPdd)
			throws TestSuiteException {
		return _isTracedRuoloPdd(this.prepareStatement(idMessaggio,datiServizioAzione),ruoloPdd);
	}
	private boolean _isTracedRuoloPdd(PreparedStatement pstmt, String ruoloPdd)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String valoreLetto = res.getString(CostantiDB.TRACCE_COLUMN_PDD_RUOLO);
				if(valoreLetto==null){
					if(ruoloPdd==null)
						return true;
					else
						return false;
				}
				else{
					if (!(valoreLetto.equals(ruoloPdd)) )
						return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedRuoloPdd");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedTipoTraccia(String idMessaggio, TipoTraccia tipoTraccia)
			throws TestSuiteException {
		return _isTracedTipoTraccia(this.prepareStatement(idMessaggio),tipoTraccia);
	}
	public boolean isTracedTipoTraccia(String idMessaggio,IDSoggetto idPortaMessaggio, TipoTraccia tipoTraccia)
			throws TestSuiteException {
		return _isTracedTipoTraccia(this.prepareStatement(idMessaggio,idPortaMessaggio),tipoTraccia);
	}
	public boolean isTracedTipoTraccia(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			TipoTraccia tipoTraccia)
			throws TestSuiteException {
		return _isTracedTipoTraccia(this.prepareStatement(idMessaggio,datiServizioAzione),tipoTraccia);
	}
	private boolean _isTracedTipoTraccia(PreparedStatement pstmt, TipoTraccia tipoTraccia)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String valoreLetto = res.getString(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO);
				if(valoreLetto==null){
					if(tipoTraccia.toString()==null)
						return true;
					else
						return false;
				}
				else{
					if (!(valoreLetto.equals(tipoTraccia.toString())) )
						return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedRuoloPdd");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedEsito(String idMessaggio, EsitoElaborazioneMessaggioTracciatura esitoElaborazione, String dettaglioEsitoElaborazione)
			throws TestSuiteException {
		return _isTracedEsito(this.prepareStatement(idMessaggio),esitoElaborazione,dettaglioEsitoElaborazione);
	}
	public boolean isTracedEsito(String idMessaggio,IDSoggetto idPortaMessaggio, EsitoElaborazioneMessaggioTracciatura esitoElaborazione, String dettaglioEsitoElaborazione)
			throws TestSuiteException {
		return _isTracedEsito(this.prepareStatement(idMessaggio,idPortaMessaggio),esitoElaborazione,dettaglioEsitoElaborazione);
	}
	public boolean isTracedEsito(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			EsitoElaborazioneMessaggioTracciatura esitoElaborazione, String dettaglioEsitoElaborazione)
			throws TestSuiteException {
		return _isTracedEsito(this.prepareStatement(idMessaggio,datiServizioAzione),esitoElaborazione,dettaglioEsitoElaborazione);
	}
	private boolean _isTracedEsito(PreparedStatement pstmt, EsitoElaborazioneMessaggioTracciatura esitoElaborazione, String dettaglioEsitoElaborazione)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String valoreLetto = res.getString(CostantiDB.TRACCE_COLUMN_ESITO_ELABORAZIONE);
				if(valoreLetto==null){
					if(esitoElaborazione.toString()==null)
						return true;
					else
						return false;
				}
				else{
					if (!(valoreLetto.equals(esitoElaborazione.toString())) )
						return false;
				}

				valoreLetto = res.getString(CostantiDB.TRACCE_COLUMN_DETTAGLIO_ESITO_ELABORAZIONE);
				if(valoreLetto==null){
					if(esitoElaborazione.toString()==null)
						return true;
					else
						return false;
				}
				else{
					if (!(valoreLetto.equals(esitoElaborazione.toString())) )
						return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedRuoloPdd");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}



	// *** MITTENTE

	public boolean isTracedMittente(String idMessaggio, IDSoggetto mittente, String indirizzoMittente)
			throws TestSuiteException {
		return _isTracedMittente(this.prepareStatement(idMessaggio),mittente, indirizzoMittente);
	}
	public boolean isTracedMittente(String idMessaggio,IDSoggetto idPortaMessaggio, IDSoggetto mittente, String indirizzoMittente)
			throws TestSuiteException {
		return _isTracedMittente(this.prepareStatement(idMessaggio,idPortaMessaggio),mittente, indirizzoMittente);
	}
	public boolean isTracedMittente(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			IDSoggetto mittente, String indirizzoMittente)
			throws TestSuiteException {
		return _isTracedMittente(this.prepareStatement(idMessaggio,datiServizioAzione),mittente, indirizzoMittente);
	}
	private boolean _isTracedMittente(PreparedStatement pstmt, IDSoggetto mittente, String indirizzoMittente)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				if(!_isTracedMittente(res, mittente, indirizzoMittente)){
					return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedMittente");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedMittente_checkLocalhost(String idMessaggio, 
			IDSoggetto mittente1, String indirizzoMittente1,
			IDSoggetto mittente2, String indirizzoMittente2) throws TestSuiteException {

		boolean check1 = isTracedMittente(idMessaggio, mittente1, indirizzoMittente1);
		boolean check2 = isTracedMittente(idMessaggio, mittente2, indirizzoMittente2);
		return check1 || check2;

	}
	public boolean isTracedMittente_checkLocalhost(String idMessaggio,IDSoggetto idPortaMessaggio, 
			IDSoggetto mittente1, String indirizzoMittente1,
			IDSoggetto mittente2, String indirizzoMittente2) throws TestSuiteException {

		boolean check1 = isTracedMittente(idMessaggio,idPortaMessaggio, mittente1, indirizzoMittente1);
		boolean check2 = isTracedMittente(idMessaggio,idPortaMessaggio, mittente2, indirizzoMittente2);
		return check1 || check2;

	}
	public boolean isTracedMittente_checkLocalhost(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			IDSoggetto mittente1, String indirizzoMittente1,
			IDSoggetto mittente2, String indirizzoMittente2) throws TestSuiteException {

		boolean check1 = isTracedMittente(idMessaggio,datiServizioAzione, mittente1, indirizzoMittente1);
		boolean check2 = isTracedMittente(idMessaggio,datiServizioAzione, mittente2, indirizzoMittente2);
		return check1 || check2;

	}

	private boolean _isTracedMittente(ResultSet res, IDSoggetto mittente, String indirizzoMittente) throws SQLException{
		// Tipo
		String tipo = res.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO);
		//System.out.println("MITT TIPO TROVATO["+tipo+"] ATTESO["+mittente.getTipo()+"]");
		if(tipo==null){
			if(mittente.getTipo()!=null)
				return false;
		}else{
			if (!(tipo.equals(mittente.getTipo())))
				return false;
		}

		// Nome
		String nome = res.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_NOME);
		//System.out.println("MITT NOME TROVATO["+nome+"] ATTESO["+mittente.getNome()+"]");
		if(nome==null){
			if(mittente.getNome()!=null)
				return false;
		}else{
			if (!(nome.equals(mittente.getNome())))
				return false;
		}

		// IDPorta
		String idPorta = res.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA);
		//System.out.println("MITT IDPORTA TROVATO["+idPorta+"] ATTESO["+mittente.getCodicePorta()+"]");
		if(idPorta==null){
			if(mittente.getCodicePorta()!=null)
				return false;
		}else{
			if (!(idPorta.equals(mittente.getCodicePorta())))
				return false;
		}

		// Indirizzo
		String indirizzo = res.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_INDIRIZZO);
		//System.out.println("MITT INDIRIZZO TELEMATICO TROVATO["+indirizzo+"] ATTESO["+indirizzoMittente+"]");
		if(indirizzo==null){
			if(indirizzoMittente!=null)
				return false;
		}else{
			if (!(indirizzo.equals(indirizzoMittente)))
				return false;
		}
		return true;
	}



	// *** DESTINATARIO
	
	public boolean isTracedDestinatario(String idMessaggio, IDSoggetto destinatario, String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedDestinatario(this.prepareStatement(idMessaggio),destinatario, indirizzoDestinatario);
	}
	public boolean isTracedDestinatario(String idMessaggio,IDSoggetto idPortaMessaggio, IDSoggetto destinatario, String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedDestinatario(this.prepareStatement(idMessaggio,idPortaMessaggio),destinatario, indirizzoDestinatario);
	}
	public boolean isTracedDestinatario(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			IDSoggetto destinatario, String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedDestinatario(this.prepareStatement(idMessaggio,datiServizioAzione),destinatario, indirizzoDestinatario);
	}
	private boolean _isTracedDestinatario(PreparedStatement pstmt, IDSoggetto destinatario, String indirizzoDestinatario)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				if(!_isTracedDestinatario(res, destinatario, indirizzoDestinatario)){
					return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedDestinatario");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedDestinatario_checkLocalhost(String idMessaggio, 
			IDSoggetto destinatario1, String indirizzoDestinatario1,
			IDSoggetto destinatario2, String indirizzoDestinatario2) throws TestSuiteException {

		boolean check1 = isTracedDestinatario(idMessaggio, destinatario1, indirizzoDestinatario1);
		boolean check2 = isTracedDestinatario(idMessaggio, destinatario2, indirizzoDestinatario2);
		return check1 || check2;

	}
	public boolean isTracedDestinatario_checkLocalhost(String idMessaggio,IDSoggetto idPortaMessaggio, 
			IDSoggetto destinatario1, String indirizzoDestinatario1,
			IDSoggetto destinatario2, String indirizzoDestinatario2) throws TestSuiteException {

		boolean check1 = isTracedDestinatario(idMessaggio,idPortaMessaggio, destinatario1, indirizzoDestinatario1);
		boolean check2 = isTracedDestinatario(idMessaggio,idPortaMessaggio, destinatario2, indirizzoDestinatario2);
		return check1 || check2;

	}
	public boolean isTracedDestinatario_checkLocalhost(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			IDSoggetto destinatario1, String indirizzoDestinatario1,
			IDSoggetto destinatario2, String indirizzoDestinatario2) throws TestSuiteException {

		boolean check1 = isTracedDestinatario(idMessaggio,datiServizioAzione, destinatario1, indirizzoDestinatario1);
		boolean check2 = isTracedDestinatario(idMessaggio,datiServizioAzione, destinatario2, indirizzoDestinatario2);
		return check1 || check2;

	}

	private boolean _isTracedDestinatario(ResultSet res, IDSoggetto destinatario, String indirizzoDestinatario) throws SQLException{
		// Tipo
		String tipo = res.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO);
		//System.out.println("DEST TIPO TROVATO["+tipo+"] ATTESO["+destinatario.getTipo()+"]");
		if(tipo==null){
			if(destinatario.getTipo()!=null)
				return false;
		}else{
			if (!(tipo.equals(destinatario.getTipo())))
				return false;
		}

		// Nome
		String nome = res.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME);
		//System.out.println("DEST NOME TROVATO["+nome+"] ATTESO["+destinatario.getNome()+"]");
		if(nome==null){
			if(destinatario.getNome()!=null)
				return false;
		}else{
			if (!(nome.equals(destinatario.getNome())))
				return false;
		}

		// IDPorta
		String idPorta = res.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA);
		//System.out.println("DEST IDPORTA TROVATO["+idPorta+"] ATTESO["+destinatario.getCodicePorta()+"]");
		if(idPorta==null){
			if(destinatario.getCodicePorta()!=null)
				return false;
		}else{
			if (!(idPorta.equals(destinatario.getCodicePorta())))
				return false;
		}

		// Indirizzo
		String indirizzo = res.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_INDIRIZZO);
		//System.out.println("DEST INDIRIZZO TELEMATICO TROVATO["+indirizzo+"] ATTESO["+indirizzoDestinatario+"]");
		if(indirizzo==null){
			if(indirizzoDestinatario!=null)
				return false;
		}else{
			if (!(indirizzo.equals(indirizzoDestinatario)))
				return false;
		}
		return true;
	}

	
	// *** PROFILO COLLABORAZIONE

	public boolean isTracedProfiloDiCollaborazione(String idMessaggio, String profiloCollaborazione, ProfiloDiCollaborazione  profiloCollaborazioneSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloDiCollaborazione(this.prepareStatement(idMessaggio),profiloCollaborazione, profiloCollaborazioneSdkValue);
	}
	public boolean isTracedProfiloDiCollaborazione(String idMessaggio,IDSoggetto idPortaMessaggio, String profiloCollaborazione, ProfiloDiCollaborazione  profiloCollaborazioneSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloDiCollaborazione(this.prepareStatement(idMessaggio,idPortaMessaggio),profiloCollaborazione, profiloCollaborazioneSdkValue);
	}
	public boolean isTracedProfiloDiCollaborazione(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String profiloCollaborazione, ProfiloDiCollaborazione  profiloCollaborazioneSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloDiCollaborazione(this.prepareStatement(idMessaggio,datiServizioAzione),profiloCollaborazione, profiloCollaborazioneSdkValue);
	}
	private boolean _isTracedProfiloDiCollaborazione(PreparedStatement pstmt, String profiloCollaborazione, ProfiloDiCollaborazione  profiloCollaborazioneSdkValue)
			throws TestSuiteException {
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE);
				//System.out.println("PROFILO COLLABORAZIONE TROVATO["+value+"] ATTESO["+profiloCollaborazione+"]");
				if(value==null){
					if(profiloCollaborazione!=null)
						return false;
				}
				else if (!(value.equals(profiloCollaborazione)))
					return false;
				
				
				String sdkValue = res.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT);
//				if(profiloCollaborazioneSdkValue!=null)
//					System.out.println("PROFILO COLLABORAZIONE SDK TROVATO["+sdkValue+"] ATTESO["+profiloCollaborazioneSdkValue.getEngineValue()+"]");
//				else
//					System.out.println("PROFILO COLLABORAZIONE SDK TROVATO["+sdkValue+"] ATTESO[null]");
				if(sdkValue==null){
					if(profiloCollaborazioneSdkValue!=null)
						return false;
				}
				else if ( profiloCollaborazioneSdkValue==null || !(sdkValue.equals(profiloCollaborazioneSdkValue.getEngineValue())))
					return false;
				
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedProfiloDiCollaborazione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	

	// *** SERVIZIO

	public boolean isTracedServizio(String idMessaggio, DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizio(this.prepareStatement(idMessaggio),datiServizio);
	}
	public boolean isTracedServizio(String idMessaggio,IDSoggetto idPortaMessaggio, DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizio(this.prepareStatement(idMessaggio,idPortaMessaggio),datiServizio);
	}
	public boolean isTracedServizio(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizio(this.prepareStatement(idMessaggio,datiServizioAzione),datiServizio);
	}
	private boolean _isTracedServizio(PreparedStatement pstmt, DatiServizio datiServizio)
			throws TestSuiteException {

		String tipo = null;
		String nome = null;
		Integer versione = null;
		if(datiServizio!=null){
			tipo = datiServizio.getTipoServizio();
			nome = datiServizio.getNomeServizio();
			versione = datiServizio.getVersioneServizio();
		}
		
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String tipoRead = res.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO);
				String nomeRead = res.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME);
				int versioneRead = res.getInt(CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE);

				if(tipo==null){
					if(tipoRead!=null)
						return false;
				}else{
					if (!(tipo.equals(tipoRead)))
						return false;
				}

				if(nome==null){
					if(nomeRead!=null)
						return false;
				}else{
					if (!(nome.equals(nomeRead)))
						return false;
				}

				if(versione==null){
					if(versioneRead<=0)
						return false;
				}else{
					if(versioneRead<=0){
						return false;
					}
					if (!(versione.intValue()==versioneRead))
						return false;
				}
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedServizio");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean isTracedServizioCorrelato(String idMessaggio, DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizioCorrelato(this.prepareStatement(idMessaggio),datiServizio);
	}
	public boolean isTracedServizioCorrelato(String idMessaggio,IDSoggetto idPortaMessaggio, DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizioCorrelato(this.prepareStatement(idMessaggio,idPortaMessaggio),datiServizio);
	}
	public boolean isTracedServizioCorrelato(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			DatiServizio datiServizio)
			throws TestSuiteException {
		return _isTracedServizioCorrelato(this.prepareStatement(idMessaggio,datiServizioAzione),datiServizio);
	}
	private boolean _isTracedServizioCorrelato(PreparedStatement pstmt, DatiServizio datiServizio)
			throws TestSuiteException {
		
		String tipoServizioCorrelato = null;
		String nomeServizioCorrelato = null;
		if(datiServizio!=null){
			tipoServizioCorrelato = datiServizio.getTipoServizio();
			nomeServizioCorrelato = datiServizio.getNomeServizio();
		}
		
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String tipo = res.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO);
				if(tipo==null){
					if(tipoServizioCorrelato!=null)
						return false;
				}
				else if (!(tipo.equals(tipoServizioCorrelato)))
					return false;

				String nome = res.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME);
				if(nome==null){
					if(nomeServizioCorrelato!=null)
						return false;
				}
				else if (!(nome.equals(nomeServizioCorrelato)))
					return false;
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedServizioCorrelato");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}


	// *** COLLABORAZIONE

	public boolean isTracedCollaborazione(String idMessaggio, String idCollaborazione)
			throws TestSuiteException {
		return _isTracedCollaborazione(this.prepareStatement(idMessaggio),idCollaborazione);
	}
	public boolean isTracedCollaborazione(String idMessaggio,IDSoggetto idPortaMessaggio, String idCollaborazione)
			throws TestSuiteException {
		return _isTracedCollaborazione(this.prepareStatement(idMessaggio,idPortaMessaggio),idCollaborazione);
	}
	public boolean isTracedCollaborazione(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String idCollaborazione)
			throws TestSuiteException {
		return _isTracedCollaborazione(this.prepareStatement(idMessaggio,datiServizioAzione),idCollaborazione);
	}
	private boolean _isTracedCollaborazione(PreparedStatement pstmt,String idCollaborazione) throws TestSuiteException{
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_COLLABORAZIONE);
				if(value==null){
					if(idCollaborazione!=null)
						return false;
				}
				else if (!(value.equals(idCollaborazione)))
					return false;
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedCollaborazione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}


	// *** AZIONE

	public boolean isTracedAzione(String idMessaggio, String nome)
			throws TestSuiteException {
		return _isTracedAzione(this.prepareStatement(idMessaggio),nome);
	}
	public boolean isTracedAzione(String idMessaggio,IDSoggetto idPortaMessaggio, String nome)
			throws TestSuiteException {
		return _isTracedAzione(this.prepareStatement(idMessaggio,idPortaMessaggio),nome);
	}
	public boolean isTracedAzione(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String nome)
			throws TestSuiteException {
		return _isTracedAzione(this.prepareStatement(idMessaggio,datiServizioAzione),nome);
	}
	private boolean _isTracedAzione(PreparedStatement pstmt, String nome)
			throws TestSuiteException {
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_AZIONE);
				//System.out.println("AZIONE TROVATA["+value+"] ATTESA["+nome+"]");
				if(value==null){
					if(nome!=null)
						return false;
				}
				else if (!(value.equals(nome)))
					return false;
			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedAzione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}



	// *** ORA REGISTRAZIONE
	
	public boolean isTracedOraRegistrazione(String idMessaggio)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio),null,null);
	}
	public boolean isTracedOraRegistrazione(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio,idPortaMessaggio),null,null);
	}
	public boolean isTracedOraRegistrazione(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio,datiServizioAzione),null,null);
	}
	public boolean isTracedOraRegistrazione(String idMessaggio, String tipoOraRegistrazione, TipoOraRegistrazione tipoOraRegistrazioneSdkConstant)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio),tipoOraRegistrazione,tipoOraRegistrazioneSdkConstant);
	}
	public boolean isTracedOraRegistrazione(String idMessaggio,IDSoggetto idPortaMessaggio, String tipoOraRegistrazione, TipoOraRegistrazione tipoOraRegistrazioneSdkConstant)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio,idPortaMessaggio),tipoOraRegistrazione,tipoOraRegistrazioneSdkConstant);
	}
	public boolean isTracedOraRegistrazione(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String tipoOraRegistrazione, TipoOraRegistrazione tipoOraRegistrazioneSdkConstant)
			throws TestSuiteException {
		return _isTracedOraRegistrazione(this.prepareStatement(idMessaggio,datiServizioAzione),tipoOraRegistrazione,tipoOraRegistrazioneSdkConstant);
	}
	private boolean _isTracedOraRegistrazione(PreparedStatement pstmt,String tipoOraRegistrazione, TipoOraRegistrazione tipoOraRegistrazioneSdkConstant)
			throws TestSuiteException {
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presenza = false;
			while (res.next()) {
				presenza = true;

				String ora = res.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE);
				if(ora==null){
					return false;
				}

				String tipoRead = res.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO);
				if(tipoRead==null){
					return false;
				}
				if ( (tipoOraRegistrazione!=null) && (!(tipoRead.equals(tipoOraRegistrazione))) )
					return false;

				String tipoSdkRead = res.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT);
				if(tipoSdkRead==null){
					return false;
				}
				if ( tipoOraRegistrazioneSdkConstant!=null){
					if(!tipoSdkRead.toString().equals(tipoOraRegistrazioneSdkConstant.getEngineValue())){
						return false;
					}
				}
				else{
					// Se puo' essere unknow, deve essere fornito il valore unknow in modo da rientrare nel ramo if
					if(!tipoSdkRead.equals(TipoOraRegistrazione.LOCALE.getEngineValue()) && 
							!tipoSdkRead.equals(TipoOraRegistrazione.SINCRONIZZATO.getEngineValue()))
						return false;
				}


			}
			return presenza;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedOraRegistrazione");
		} finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}

	public boolean isTracedScadenza(String idMessaggio)
			throws TestSuiteException {
		return _isTracedScadenza(this.prepareStatement(idMessaggio));
	}
	public boolean isTracedScadenza(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _isTracedScadenza(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean isTracedScadenza(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _isTracedScadenza(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _isTracedScadenza(PreparedStatement pstmt)
			throws TestSuiteException {
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presenza = false;
			while (res.next()) {
				presenza = true;

				String ora = res.getString(CostantiDB.TRACCE_COLUMN_SCADENZA);
				if(ora==null){
					return false;
				}

			}
			return presenza;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedOraRegistrazione");
		} finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}




	// *** TRASMISSIONE
	
	public boolean isTracedProfiloTrasmissione(String idMessaggio, boolean confermaRicezione, String inoltro, Inoltro inoltroSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloTrasmissione(this.prepareStatement(idMessaggio),confermaRicezione,inoltro,inoltroSdkValue);
	}
	public boolean isTracedProfiloTrasmissione(String idMessaggio,IDSoggetto idPortaMessaggio, boolean confermaRicezione, String inoltro, Inoltro inoltroSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloTrasmissione(this.prepareStatement(idMessaggio,idPortaMessaggio),confermaRicezione,inoltro,inoltroSdkValue);
	}
	public boolean isTracedProfiloTrasmissione(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdkValue)
			throws TestSuiteException {
		return _isTracedProfiloTrasmissione(this.prepareStatement(idMessaggio,datiServizioAzione),confermaRicezione,inoltro,inoltroSdkValue);
	}
	private boolean _isTracedProfiloTrasmissione(PreparedStatement pstmt, boolean confermaRicezione, String inoltro, Inoltro inoltroSdkValue)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				boolean confermaRicezioneDB = false;
				if(res.getInt(CostantiDB.TRACCE_COLUMN_CONFERMA_RICEZIONE)==1)
					confermaRicezioneDB = true;
				//System.out.println("confermaRicezione LETTO["+confermaRicezioneDB+"] ATTESO["+confermaRicezione+"]");
				if(confermaRicezioneDB!=confermaRicezione){
					return false;
				}	
				
				String inoltroDB = res.getString(CostantiDB.TRACCE_COLUMN_INOLTRO);
				//System.out.println("inoltro LETTO["+inoltroDB+"] ATTESO["+inoltro+"]");
				if(inoltroDB==null){
					if(inoltro!=null)
						return false;
				}else{
					if (!(inoltroDB.equals(inoltro)))
						return false;
				}

				String inoltroSdkDB = res.getString(CostantiDB.TRACCE_COLUMN_INOLTRO_SDK_CONSTANT);
//				if(inoltroSdkValue!=null)
//					System.out.println("inoltroSdk LETTO["+inoltroSdkDB+"] ATTESO["+inoltroSdkValue.getEngineValue()+"]");
//				else
//					System.out.println("inoltroSdk LETTO["+inoltroSdkDB+"] ATTESO[null]");
				if(inoltroSdkDB==null){
					if(inoltroSdkValue!=null)
						return false;
				}else{
					if ( inoltroSdkValue==null || !(inoltroSdkDB.equals(inoltroSdkValue.getEngineValue())))
						return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedProfiloTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean isTracedSequenza(String idMessaggio,int sequenza)
			throws TestSuiteException {
		return _isTracedSequenza(this.prepareStatement(idMessaggio),sequenza);
	}
	public boolean isTracedSequenza(String idMessaggio,IDSoggetto idPortaMessaggio,int sequenza)
			throws TestSuiteException {
		return _isTracedSequenza(this.prepareStatement(idMessaggio,idPortaMessaggio),sequenza);
	}
	public boolean isTracedSequenza(String idMessaggio, DatiServizioAzione datiServizioAzione,int sequenza)
			throws TestSuiteException {
		return _isTracedSequenza(this.prepareStatement(idMessaggio,datiServizioAzione),sequenza);
	}
	private boolean _isTracedSequenza(PreparedStatement pstmt,int sequenza) throws TestSuiteException{
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			while (res.next()) {

				int value = res.getInt(CostantiDB.TRACCE_COLUMN_SEQUENZA);
				return value == sequenza;
			}

			return false;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSequenza");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	// *** INTEGRAZIONE
	
	public boolean isTracedLocation(String idMessaggio, String location)
			throws TestSuiteException {
		return _isTracedLocation(this.prepareStatement(idMessaggio),location);
	}
	public boolean isTracedLocation(String idMessaggio,IDSoggetto idPortaMessaggio, String location)
			throws TestSuiteException {
		return _isTracedLocation(this.prepareStatement(idMessaggio,idPortaMessaggio),location);
	}
	public boolean isTracedLocation(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String location)
			throws TestSuiteException {
		return _isTracedLocation(this.prepareStatement(idMessaggio,datiServizioAzione),location);
	}
	private boolean _isTracedLocation(PreparedStatement pstmt,String location) throws TestSuiteException{
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			while (res.next()) {

				String valueRead = res.getString(CostantiDB.TRACCE_COLUMN_LOCATION);
				if(valueRead==null){
					if(location!=null)
						return false;
				}else{
					if (!(valueRead.equals(location)))
						return false;
				}
			}

			return false;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedLocation");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	public boolean isTracedCorrelazioneApplicativaRichiesta(String idMessaggio, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio),correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRichiesta(String idMessaggio,IDSoggetto idPortaMessaggio, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio,idPortaMessaggio),correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRichiesta(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio,datiServizioAzione),correlazioneApplicativa);
	}
	private boolean _isTracedCorrelazioneApplicativaRichiesta(PreparedStatement pstmt, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA);
				if(value==null){
					if(correlazioneApplicativa!=null)
						return false;
				}else{
					if (!(value.equals(correlazioneApplicativa)))
						return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean existsTracedCorrelazioneApplicativaRichiesta(String idMessaggio)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio));
	}
	public boolean existsTracedCorrelazioneApplicativaRichiesta(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsTracedCorrelazioneApplicativaRichiesta(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsTracedCorrelazioneApplicativaRichiesta(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA);
				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean isTracedCorrelazioneApplicativaRisposta(String idMessaggio, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio),correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRisposta(String idMessaggio,IDSoggetto idPortaMessaggio, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio,idPortaMessaggio),correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRisposta(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio,datiServizioAzione),correlazioneApplicativa);
	}
	private boolean _isTracedCorrelazioneApplicativaRisposta(PreparedStatement pstmt, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA);
				if(value==null){
					if(correlazioneApplicativa!=null)
						return false;
				}else{
					if (!(value.equals(correlazioneApplicativa)))
						return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean existsTracedCorrelazioneApplicativaRisposta(String idMessaggio)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio));
	}
	public boolean existsTracedCorrelazioneApplicativaRisposta(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsTracedCorrelazioneApplicativaRisposta(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsTracedCorrelazioneApplicativaRisposta(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA);
				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean isTracedSAFruitore(String idMessaggio, String saFruitore)
			throws TestSuiteException {
		return _isTracedSAFruitore(this.prepareStatement(idMessaggio),saFruitore);
	}
	public boolean isTracedSAFruitore(String idMessaggio,IDSoggetto idPortaMessaggio, String saFruitore)
			throws TestSuiteException {
		return _isTracedSAFruitore(this.prepareStatement(idMessaggio,idPortaMessaggio),saFruitore);
	}
	public boolean isTracedSAFruitore(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String saFruitore)
			throws TestSuiteException {
		return _isTracedSAFruitore(this.prepareStatement(idMessaggio,datiServizioAzione),saFruitore);
	}
	private boolean _isTracedSAFruitore(PreparedStatement pstmt, String saFruitore)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_SA_FRUITORE);

				if(value==null){
					if(saFruitore!=null)
						return false;
				}else{
					if (!(value.equals(saFruitore)))
						return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSAFruitore");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean existsTracedSAFruitore(String idMessaggio)
			throws TestSuiteException {
		return _existsTracedSAFruitore(this.prepareStatement(idMessaggio));
	}
	public boolean existsTracedSAFruitore(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsTracedSAFruitore(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsTracedSAFruitore(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsTracedSAFruitore(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsTracedSAFruitore(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_SA_FRUITORE);

				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSAFruitore");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	

	
	
	public boolean isTracedSAErogatore(String idMessaggio, String saErogatore)
			throws TestSuiteException {
		return _isTracedSAErogatore(this.prepareStatement(idMessaggio),saErogatore);
	}
	public boolean isTracedSAErogatore(String idMessaggio,IDSoggetto idPortaMessaggio, String saErogatore)
			throws TestSuiteException {
		return _isTracedSAErogatore(this.prepareStatement(idMessaggio,idPortaMessaggio),saErogatore);
	}
	public boolean isTracedSAErogatore(String idMessaggio, DatiServizioAzione datiServizioAzione, 
			String saErogatore)
			throws TestSuiteException {
		return _isTracedSAErogatore(this.prepareStatement(idMessaggio,datiServizioAzione),saErogatore);
	}
	private boolean _isTracedSAErogatore(PreparedStatement pstmt, String saErogatore)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_SA_EROGATORE);

				if(value==null){
					if(saErogatore!=null)
						return false;
				}else{
					if (!(value.equals(saErogatore)))
						return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSAErogatore");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	
	
	
	// IS ARRIVED
	
	public int isArrivedCount(String idMessaggio)
			throws TestSuiteException {
		return _isArrivedCount(this.prepareStatement(idMessaggio));
	}
	public int isArrivedCount(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _isArrivedCount(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public int isArrivedCount(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _isArrivedCount(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private int _isArrivedCount(PreparedStatement pstmt) throws TestSuiteException {
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			int number = 0;
			while (res.next()) {
				int isArrived = res.getInt(CostantiDB.TRACCE_COLUMN_IS_ARRIVED);
				number = number + isArrived;
			}

			return number;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isArrivedCount");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	
	
	// SOAP
	
	public boolean existsTracedSOAPElement(String idMessaggio)
			throws TestSuiteException {
		return _existsTracedSOAPElement(this.prepareStatement(idMessaggio));
	}
	public boolean existsTracedSOAPElement(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsTracedSOAPElement(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsTracedSOAPElement(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsTracedSOAPElement(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsTracedSOAPElement(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_SOAP);

				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSAFruitore");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean existsTracedDigest(String idMessaggio)
			throws TestSuiteException {
		return _existsTracedDigest(this.prepareStatement(idMessaggio));
	}
	public boolean existsTracedDigest(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsTracedDigest(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsTracedDigest(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsTracedDigest(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsTracedDigest(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(CostantiDB.TRACCE_COLUMN_DIGEST);

				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database ["+this.getTipoTraccia()+"]: "+e.getMessage(),"isTracedSAFruitore");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	
	
	
	
	
	
	
	
	/* ----------------- RICEVUTE ASINCRONE ----------------------- */

	public boolean isTracedRicevutaAsincrona(String mittente,String tipo_mittente, 
			String destinatario, String tipo_destinatario, 
			String servizio, String tipo_servizio,Integer versione_servizio){
		return isTracedRicevutaAsincrona(mittente,tipo_mittente,destinatario,tipo_destinatario,servizio,tipo_servizio,versione_servizio,null);
	}

	public boolean isTracedRicevutaAsincrona(String mittente,String tipo_mittente, 
			String destinatario,String tipo_destinatario, 
			String servizio, String tipo_servizio,Integer versione_servizio,String azione)
					throws TestSuiteException {

		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			String query = "select * from "+CostantiDB.TRACCE+" where " +
					CostantiDB.TRACCE_COLUMN_MITTENTE_NOME+"=? and "+CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO+"=? and "+
					CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME+" =? and "+CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO+"=? AND ";
			if(servizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+" is null AND ";
			if(tipo_servizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+" is null AND ";
			if(versione_servizio!=null)
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE+"=? AND ";
			else
				query = query + CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE+" is null AND ";
			if(azione!=null)
				query = query + CostantiDB.TRACCE_COLUMN_AZIONE+"=?";
			else
				query = query + CostantiDB.TRACCE_COLUMN_AZIONE+" is null";


			pstmt = this.con.prepareStatement(query);
			pstmt.setString(1, mittente);
			pstmt.setString(2, tipo_mittente);
			pstmt.setString(3, destinatario);
			pstmt.setString(4, tipo_destinatario);
			int count = 5;
			if(servizio!=null){
				pstmt.setString(count,servizio);
				count++;
			}
			if(tipo_servizio!=null){
				pstmt.setString(count,tipo_servizio);
				count++;
			}
			if(versione_servizio!=null){
				pstmt.setInt(count,versione_servizio);
				count++;
			}
			if(azione!=null){
				pstmt.setString(count,azione);
				count++;
			}

			res = pstmt.executeQuery();
			boolean ret = res.next();
			return ret;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedRicevutaAsincrona");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* --------------------- RISCONTRO ------------------------- */
	public boolean isTracedRiscontro(String idRiscontro,String idMessaggioPortaRiscontro) throws TestSuiteException {
		return isTracedRiscontro(idRiscontro,idMessaggioPortaRiscontro, true,null,null);
	}
	public boolean isTracedRiscontro(String idRiscontro,String idMessaggioPortaRiscontro,boolean checkOraRegistrazione) throws TestSuiteException {
		return isTracedRiscontro(idRiscontro,idMessaggioPortaRiscontro, checkOraRegistrazione,null,null);
	}
	public boolean isTracedRiscontro(String idRiscontro,String idMessaggioPortaRiscontro,
			boolean checkOraRegistrazione,String tipoOraRegistrazione, TipoOraRegistrazione tipoOraRegistrazioneSdkConstant) throws TestSuiteException {
	
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			pstmt = this.prepareStatementListaRiscontri(idMessaggioPortaRiscontro, idRiscontro);
			res = pstmt.executeQuery();
			boolean ret = res.next();

			if(ret && checkOraRegistrazione){
				
				String ora = res.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE);
				if(ora==null){
					return false;
				}
				
				String tipoRead = res.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO);
				if(tipoRead==null){
					return false;
				}
				if ( (tipoOraRegistrazione!=null) && (!(tipoRead.equals(tipoOraRegistrazione))) )
					return false;

				String tipoSdkRead = res.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT);
				if(tipoSdkRead==null){
					return false;
				}
				if ( tipoOraRegistrazioneSdkConstant!=null){
					if(!tipoSdkRead.toString().equals(tipoOraRegistrazioneSdkConstant.getEngineValue())){
						return false;
					}
				}
				else{
					// Se puo' essere unknow, deve essere fornito il valore unknow in modo da rientrare nel ramo if
					if(!tipoSdkRead.equals(TipoOraRegistrazione.LOCALE.getEngineValue()) && 
							!tipoSdkRead.equals(TipoOraRegistrazione.SINCRONIZZATO.getEngineValue()))
						return false;
				}
				

			}

			return ret;
		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedRiscontro");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	/* ---------------- ECCEZIONI ------------------------ */

	public boolean existsListaEccezioni(String idMessaggio)
			throws TestSuiteException {
		return _existsListaEccezioni(this.prepareStatement(idMessaggio));
	}
	public boolean existsListaEccezioni(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _existsListaEccezioni(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public boolean existsListaEccezioni(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _existsListaEccezioni(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private boolean _existsListaEccezioni(PreparedStatement pstmt) throws TestSuiteException{
		
		PreparedStatement pstmtListaEccezioni = null;
		ResultSet res = null;
		try {
			// Raccolta id traccia
			res = pstmt.executeQuery();
			if(res.next() == false)
				return false;
			int idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
			res.close();
			pstmt.close();

			// ResultSet
			pstmtListaEccezioni = this.con
					.prepareStatement("select* from "+ CostantiDB.TRACCE_ECCEZIONI+"  where "+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+"=?");
			pstmtListaEccezioni.setInt(1, idTraccia);
			res = pstmtListaEccezioni.executeQuery();
			boolean ret = res.next();
			return ret;
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"existsListaEccezioni");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaEccezioni.close();
			}catch(Exception e){}
		}
	}
	
	
	public boolean isTracedEccezione(String idMessaggio,String faultCode)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, faultCode ,true);
	}
	public boolean isTracedEccezione(String idMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, Utilities.toString(faultCode, this.protocollo) ,true);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,String faultCode)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, faultCode,true);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, Utilities.toString(faultCode, this.protocollo) ,true);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String faultCode)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, faultCode,true);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE, Utilities.toString(faultCode, this.protocollo),true);
	}
	
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),true);
	}
	
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,IDSoggetto idPortaMessaggio,ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio, DatiServizioAzione datiServizioAzione,
			ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),true);
	}
	
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,colonna,value,true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio,IDSoggetto idPortaMessaggio,String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,colonna,value,true);
	}
	public boolean isTracedPerTutteLeEccezioni(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,colonna,value,true);
	}
	
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),false);
	}
	
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			ErroreCooperazione errore)
			throws TestSuiteException, ProtocolException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),false);
	}
	
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,colonna,value,false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,idPortaMessaggio,colonna,value,false);
	}
	public boolean isTracedPerAlmenoUnaEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String colonna,String value)
			throws TestSuiteException {
		return isTracedEccezione(idMessaggio,datiServizioAzione,colonna,value,false);
	}
	
	public boolean isTracedEccezione(String idMessaggio,CodiceErroreCooperazione faultCode,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzione),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,true);
	}
	
	public boolean isTracedEccezione(String idMessaggio,ErroreCooperazione errore,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,ErroreCooperazione errore,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			ErroreCooperazione errore,boolean uniqueValue)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzione),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,true);
	}
	
	public boolean isTracedEccezione(String idMessaggio,String colonna,String value,boolean uniqueValue)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),colonna,value,uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,String colonna,String value,boolean uniqueValue)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),colonna,value,uniqueValue,true);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String colonna,String value,boolean uniqueValue)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzione),colonna,value,uniqueValue,true);
	}
	
	public boolean isTracedEccezione(String idMessaggio,CodiceErroreCooperazione faultCode,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzionee,
			CodiceErroreCooperazione faultCode,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzionee),CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE,Utilities.toString(faultCode, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
	
	public boolean isTracedEccezione(String idMessaggio,ErroreCooperazione errore,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,ErroreCooperazione errore,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			ErroreCooperazione errore,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzione),CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,Utilities.toString(errore, this.protocollo),uniqueValue,checkTutteLeTrasmissioni);
	}
		
	public boolean isTracedEccezione(String idMessaggio,String colonna,String value,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio),colonna,value,uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio,IDSoggetto idPortaMessaggio,String colonna,String value,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,idPortaMessaggio),colonna,value,uniqueValue,checkTutteLeTrasmissioni);
	}
	public boolean isTracedEccezione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String colonna,String value,boolean uniqueValue,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _isTracedEccezione(this.prepareStatement(idMessaggio,datiServizioAzione),colonna,value,uniqueValue,checkTutteLeTrasmissioni);
	}
	
	private boolean _isTracedEccezione(PreparedStatement pstmt,String colonna,String value,boolean uniqueValue,boolean checkTutteLeTrasmissioni) throws TestSuiteException{
		
		ResultSet res = null;
		PreparedStatement pstmtE = null;
		ResultSet resE = null;
		try {
			res = pstmt.executeQuery();

			if(uniqueValue){
				int j=0;
				while (res.next()) {
					int idTraccia=res.getInt(CostantiDB.TRACCE_COLUMN_ID);

					pstmtE = this.con
							.prepareStatement("select "+colonna+" from "+CostantiDB.TRACCE_ECCEZIONI+" where "+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+"=? ");
					pstmtE.setInt(1,idTraccia);
					resE = pstmtE.executeQuery();
					int i=0;
					while(resE.next()){
						if(!resE.getString(colonna).equals(value)){
							return false;		
						}
						i++;
					}
					if(i==0 && checkTutteLeTrasmissioni)
						return false;
					j++;

					resE.close();
					pstmtE.close();
				}
				if(j==0)
					return false;
				return true;
			}
			else{
				while (res.next()) {
					int idTraccia=res.getInt(CostantiDB.TRACCE_COLUMN_ID);

					pstmtE = this.con
							.prepareStatement("select "+colonna+" from "+CostantiDB.TRACCE_ECCEZIONI+" where "+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+"=? ");
					pstmtE.setInt(1,idTraccia);
					resE = pstmtE.executeQuery();
					while(resE.next()){
						if(resE.getString(colonna).equals(value)){
							return true;
						}
					}
					resE.close();
					pstmtE.close();
				}
				return false;
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedEccezione");
		} finally{
			try{
				resE.close();
			}catch(Exception e){}
			try{
				pstmtE.close();
			}catch(Exception e){}
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio),true);
	}
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),true);
	}
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),true);
	}
	
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio),checkTutteLeTrasmissioni);
	}
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),checkTutteLeTrasmissioni);
	}
	public List<EccezioneVO> getEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),checkTutteLeTrasmissioni);
	}
		
	private List<EccezioneVO> _getEccezioniTraced(PreparedStatement pstmt,boolean checkTutteLeTrasmissioni) throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement pstmtE = null;
		ResultSet resE = null;
		try {
			res = pstmt.executeQuery();

			//System.out.println("select* from "+ CostantiDB.TRACCE+"  where "+CostantiDatabase.ID_RIFERIMENTO_MESSAGGIO+"='"+idBusta+"'");
			List<EccezioneVO> out = new ArrayList<EccezioneVO>();

			while (res.next()) {
				int idTraccia=res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				//System.out.println("TROVATA ID TRACCIA ["+idTraccia+"]");
				pstmtE = this.con
						.prepareStatement("select * from "+CostantiDB.TRACCE_ECCEZIONI+" where "+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+"=? ");
				pstmtE.setInt(1,idTraccia);
				//System.out.println("select * from "+CostantiDatabase.TABELLA_ECCEZIONI+" where "+CostantiDatabase.IDENTIFICATORE_TABELLE_LISTE_TRACCIA+"="+idTraccia+" ");
				resE = pstmtE.executeQuery();
				int i=0;
				while(resE.next()){
					
					EccezioneVO eccezioneVO = new EccezioneVO();
					
					eccezioneVO.setContestoCodifica(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA));
					eccezioneVO.setContestoCodificaSdk(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA_SDK_CONSTANT));
					
					eccezioneVO.setCodiceEccezione(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE));
					eccezioneVO.setCodiceEccezioneSdk(resE.getInt(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SDK_CONSTANT));
					eccezioneVO.setSubCodiceEccezioneSdk(resE.getInt(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SUBCOD_SDK_CONSTANT));
					
					eccezioneVO.setRilevanza(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA));
					eccezioneVO.setRilevanzaSdk(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA_SDK_CONSTANT));
					
					eccezioneVO.setPosizione(resE.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE));
					
					out.add(eccezioneVO);
					i++;
				}
				resE.close();
				pstmtE.close();
				if(i==0 && checkTutteLeTrasmissioni){
					return new ArrayList<EccezioneVO>();
				}
			}

			return out;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getEccezioniTracedIntoResponse");
		} finally{
			try{
				resE.close();
			}catch(Exception e){}
			try{
				pstmtE.close();
			}catch(Exception e){}
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	public int countEccezioniTraced(String idMessaggio,String faultCode)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio),true),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio),true),Utilities.toString(faultCode, this.protocollo));
	}
	public int countEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio,String faultCode)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),true),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),true),Utilities.toString(faultCode, this.protocollo));
	}
	public int countEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String faultCode)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),true),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),true),Utilities.toString(faultCode, this.protocollo));
	}
	
	public int countEccezioniTraced(String idMessaggio,String faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio),checkTutteLeTrasmissioni),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio,CodiceErroreCooperazione faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio),checkTutteLeTrasmissioni),Utilities.toString(faultCode, this.protocollo));
	}
	public int countEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio,String faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),checkTutteLeTrasmissioni),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio,IDSoggetto idPortaMessaggio,CodiceErroreCooperazione faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,idPortaMessaggio),checkTutteLeTrasmissioni),Utilities.toString(faultCode, this.protocollo));
	}
	public int countEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),checkTutteLeTrasmissioni),faultCode);
	}
	public int countEccezioniTraced(String idMessaggio, DatiServizioAzione datiServizioAzione,
			CodiceErroreCooperazione faultCode,boolean checkTutteLeTrasmissioni)
			throws TestSuiteException, ProtocolException {
		return _countEccezioniTraced(_getEccezioniTraced(this.prepareStatement(idMessaggio,datiServizioAzione),checkTutteLeTrasmissioni),Utilities.toString(faultCode, this.protocollo));
	}
	
	private int _countEccezioniTraced(List<EccezioneVO> ecc,String faultCode)throws TestSuiteException{
		//System.out.println("VERIFICA TRACCIATURA ECC["+faultCode+"] in lista con size["+ecc.size()+"]");
		int count=0;
		if(ecc!=null){
			for(int i = 0;i<ecc.size();i++){
				//System.out.println("CHECK ATTUALE["+faultCode+"] con CODICE["+ecc.get(i).getCodiceEccezione()+"]");
				if(faultCode.equals(ecc.get(i).getCodiceEccezione()))
					count++;
			}
		}
		return count;
	}
	
	
	
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione){
		return isTracedEccezione(gdo, mittente, destinatario, codiceEccezione, contestoCodificaEccezione, posizioneEccezione, true, null,null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, Utilities.toString(codiceEccezione, this.protocollo), contestoCodificaEccezione, posizioneEccezione, true, null,null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,String codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, codiceEccezione, contestoCodificaEccezione, Utilities.toString(errore, this.protocollo), true, null,null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, Utilities.toString(codiceEccezione, this.protocollo), contestoCodificaEccezione, Utilities.toString(errore, this.protocollo), true, null,null);
	}
	
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione, 
			boolean checkOraRegistrazione){
		return isTracedEccezione(gdo, mittente, destinatario, codiceEccezione, contestoCodificaEccezione, posizioneEccezione, checkOraRegistrazione, null, null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione, 
			boolean checkOraRegistrazione) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, Utilities.toString(codiceEccezione, this.protocollo), contestoCodificaEccezione, posizioneEccezione, checkOraRegistrazione, null, null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,String codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore, 
			boolean checkOraRegistrazione) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, codiceEccezione, contestoCodificaEccezione, Utilities.toString(errore, this.protocollo), checkOraRegistrazione, null, null);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore, 
			boolean checkOraRegistrazione) throws ProtocolException{
		return isTracedEccezione(gdo, mittente, destinatario, Utilities.toString(codiceEccezione, this.protocollo), contestoCodificaEccezione, Utilities.toString(errore, this.protocollo), checkOraRegistrazione, null, null);
	}
	
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,
			CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione, 
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk) throws ProtocolException{
		return isTracedEccezione(gdo, mittente,destinatario,Utilities.toString(codiceEccezione, this.protocollo),contestoCodificaEccezione,posizioneEccezione,checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,
			CodiceErroreCooperazione codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore, 
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk) throws ProtocolException{
		return isTracedEccezione(gdo, mittente,destinatario,Utilities.toString(codiceEccezione, this.protocollo),contestoCodificaEccezione,Utilities.toString(errore, this.protocollo),checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,
			String codiceEccezione,String contestoCodificaEccezione, ErroreCooperazione errore, 
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk) throws ProtocolException{
		return isTracedEccezione(gdo, mittente,destinatario,codiceEccezione,contestoCodificaEccezione,Utilities.toString(errore, this.protocollo),checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	public boolean isTracedEccezione(Date gdo, IDSoggetto mittente,IDSoggetto destinatario,
			String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione, 
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk){

		ResultSet res = null;
		PreparedStatement pstmt =  null;
		try {

			String checkOraRegistrazioneString = "";
			if(checkOraRegistrazione){
				checkOraRegistrazioneString = CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO+" is not null AND ";
				
				if(tipoOraRegistrazione!=null){
					checkOraRegistrazioneString = checkOraRegistrazioneString + CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO+"='"+tipoOraRegistrazione+"' ) AND ";
				}
				
				if(tipoOraRegistrazioneSdk==null){
					checkOraRegistrazioneString = checkOraRegistrazioneString + "( "+CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+TipoOraRegistrazione.LOCALE.getEngineValue()+"' OR "+CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+TipoOraRegistrazione.SINCRONIZZATO.getEngineValue()+"' ) AND ";
				}
				else{
					checkOraRegistrazioneString = checkOraRegistrazioneString + CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+tipoOraRegistrazioneSdk.getEngineValue()+"' ) AND ";
				}
			}

			pstmt = this.con
					.prepareStatement("select * from "+ CostantiDB.TRACCE+", "+CostantiDB.TRACCE_ECCEZIONI+"  where "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+" AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_GDO+">? AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO+"=? AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_NOME+"=? AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO+"=? AND "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME+"=? AND "+
							checkOraRegistrazioneString+
							CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE+"=? AND "+
							CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA+"=?");

			/*System.out.println("select * from "+ CostantiDB.TRACCE+", "+CostantiDB.TRACCE_ECCEZIONI+"  where "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+" AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_GDO+">'"+new Timestamp(gdo.getTime()).toString()+"' AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"='Risposta' AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO+"='"+mittente.getTipo()+"' AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_NOME+"='"+mittente.getNome()+"' AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO+"='"+destinatario.getTipo()+"' AND "+
					CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME+"='"+destinatario.getNome()+"' AND "+
					CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE+"='"+codiceEccezione+"' AND "+
					CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA+"='"+contestoCodificaEccezione+"'");*/
			pstmt.setTimestamp(1, new Timestamp(gdo.getTime()));
			pstmt.setString(2, "Risposta");
			pstmt.setString(3, mittente.getTipo());
			pstmt.setString(4, mittente.getNome());
			pstmt.setString(5, destinatario.getTipo());
			pstmt.setString(6, destinatario.getNome());
			pstmt.setString(7, codiceEccezione);
			pstmt.setString(8, contestoCodificaEccezione);
			res = pstmt.executeQuery();

			boolean trovato = false;
			while(res.next()){
				String posizione = res.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE);
				if(posizioneEccezione.equals(posizione)){
					trovato = true;
					break;
				}
			}
			//System.out.println("trovato : "+trovato);
			return trovato;
		} catch (SQLException e) {
			System.out.println("errore nel db " + e.getMessage());
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"nella fase DBC.getResult");
		} finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	public boolean isTracedEccezione(Date gdo, String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione){
		return isTracedEccezione(gdo, codiceEccezione, contestoCodificaEccezione, posizioneEccezione, true, null,null);
	}
	public boolean isTracedEccezione(Date gdo, String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione,
			boolean checkOraRegistrazione){
		return isTracedEccezione(gdo, codiceEccezione, contestoCodificaEccezione, posizioneEccezione, checkOraRegistrazione, null,null);
	}
	public boolean isTracedEccezione(Date gdo, String codiceEccezione,String contestoCodificaEccezione, String posizioneEccezione,
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk){

		ResultSet res = null;
		PreparedStatement pstmt =  null;
		try {

			String checkOraRegistrazioneString = "";
			if(checkOraRegistrazione){
				checkOraRegistrazioneString = CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO+" is not null AND ";
				
				if(tipoOraRegistrazione!=null){
					checkOraRegistrazioneString = checkOraRegistrazioneString + CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO+"='"+tipoOraRegistrazione+"' ) AND ";
				}
				
				if(tipoOraRegistrazioneSdk==null){
					checkOraRegistrazioneString = checkOraRegistrazioneString + "( "+CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+TipoOraRegistrazione.LOCALE.toString()+"' OR "+CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+TipoOraRegistrazione.SINCRONIZZATO.toString()+"' ) AND ";
				}
				else{
					checkOraRegistrazioneString = checkOraRegistrazioneString + CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT+"='"+tipoOraRegistrazioneSdk.toString()+"' ) AND ";
				}
			}

			pstmt = this.con
					.prepareStatement("select * from "+ CostantiDB.TRACCE+", "+CostantiDB.TRACCE_ECCEZIONI+"  where "+
							CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+" AND "+
							CostantiDB.TRACCE_COLUMN_GDO+">? AND "+
							CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+
							checkOraRegistrazioneString+
							CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE+"=? AND "+
							CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA+"=?");
			pstmt.setTimestamp(1, new Timestamp(gdo.getTime()));
			pstmt.setString(2, "Risposta");
			pstmt.setString(3, codiceEccezione);
			pstmt.setString(4, contestoCodificaEccezione);
			res = pstmt.executeQuery();
			boolean trovato = false;
			while(res.next()){
				String posizione = res.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE);
				if(posizioneEccezione.equals(posizione)){
					trovato = true;
					break;
				}
			}

			return trovato;
		} catch (SQLException e) {
			System.out.println("errore nel db " + e.getMessage());
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"nella fase DBC.getResult");
		} finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* -------------- TRASMISSIONI ----------------------- */
	
	public boolean isTracedTrasmissione(String idMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				true,null,null);
	}
	public boolean isTracedTrasmissione(String idMessaggio,IDSoggetto idPortaMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,idPortaMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				true,null,null);
	}
	public boolean isTracedTrasmissione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,datiServizioAzione),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				true,null,null);
	}
	
	public boolean isTracedTrasmissione(String idMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,null,null);
	}
	public boolean isTracedTrasmissione(String idMessaggio,IDSoggetto idPortaMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,idPortaMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,null,null);
	}
	public boolean isTracedTrasmissione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,datiServizioAzione),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,null,null);
	}
	
	public boolean isTracedTrasmissione(String idMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	public boolean isTracedTrasmissione(String idMessaggio,IDSoggetto idPortaMessaggio,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,idPortaMessaggio),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	public boolean isTracedTrasmissione(String idMessaggio, DatiServizioAzione datiServizioAzione,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk)
			throws TestSuiteException {
		return _isTracedTrasmissione(this.prepareStatement(idMessaggio,datiServizioAzione),
				mittente,indirizzoMittente,destinatario,indirizzoDestinatario,
				checkOraRegistrazione,tipoOraRegistrazione,tipoOraRegistrazioneSdk);
	}
	private boolean _isTracedTrasmissione(PreparedStatement pstmt,
			IDSoggetto mittente,String indirizzoMittente,
			IDSoggetto destinatario,String indirizzoDestinatario,
			boolean checkOraRegistrazione,String tipoOraRegistrazione,TipoOraRegistrazione tipoOraRegistrazioneSdk) throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement pstmtListaTrasmissioni = null;
		int idTraccia = 0;
		try {
			res = pstmt.executeQuery();

			if (res.next()){
				idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				if(idTraccia<=0)
					throw new TestSuiteException("L'id traccia vale null");

			}else{
				return false;
			}
			res.close();
			pstmt.close();

			pstmtListaTrasmissioni = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE_TRASMISSIONI+" where "+CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ID_TRACCIA+"=? ");
			pstmtListaTrasmissioni.setInt(1,idTraccia);
			res = pstmtListaTrasmissioni.executeQuery();
			if(res.next()){

				if(!_isTracedTrasmissione(res, mittente, indirizzoMittente, 
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE, CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO,
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA,CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO)){
					//System.out.println("Mittente non corretto");
					return false;
				}
				
				if(!_isTracedTrasmissione(res, destinatario, indirizzoDestinatario, 
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE, CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO,
						CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA,CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO)){
					//System.out.println("Destinatario non corretto");
					return false;
				}
				
				

				if(checkOraRegistrazione){
					
					String ora = res.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE);
					if(ora==null){
						//System.out.println("Ora non presente");
						return false;
					}

					String tipoRead = res.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO);
					if(tipoRead==null){
						//System.out.println("TipoOra non presente");
						return false;
					}
					if ( (tipoOraRegistrazione!=null) && (!(tipoRead.equals(tipoOraRegistrazione))) ){
						//System.out.println("TipoOra non corretto");
						return false;
					}
				
					String tipoSdkRead = res.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT);
					if(tipoSdkRead==null){
						//System.out.println("TipoOra non corretto (2)");
						return false;
					}
					if ( tipoOraRegistrazioneSdk!=null){
						if(!tipoSdkRead.toString().equals(tipoOraRegistrazioneSdk.getEngineValue())){
							//System.out.println("TipoOra non corretto (3)");
							return false;
						}
					}
					else{
						// Se puo' essere unknow, deve essere fornito il valore unknow in modo da rientrare nel ramo if
						if(!tipoSdkRead.equals(TipoOraRegistrazione.LOCALE.getEngineValue()) && 
								!tipoSdkRead.equals(TipoOraRegistrazione.SINCRONIZZATO.getEngineValue())){
							//System.out.println("TipoOra non corretto (4)");
							return false;
						}
					}
					
				}

				res.close();
				pstmtListaTrasmissioni.close();
				return true;
			}else{
				//System.out.println("ListaTrasmissione non trovata");
				res.close();
				pstmtListaTrasmissioni.close();
				return false;
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaTrasmissioni.close();
			}catch(Exception e){}
		}	
	}
	
	private boolean _isTracedTrasmissione(ResultSet res, IDSoggetto idSoggetto, String indirizzoSoggetto,
			String columnNome,String columnTipo,String columnIdPorta,String columnIndirizzo) throws SQLException{
		// Tipo
		String tipo = res.getString(columnTipo);
		//System.out.println("TIPO TROVATO["+tipo+"] ATTESO["+idSoggetto.getTipo()+"]");
		if(tipo==null){
			if(idSoggetto.getTipo()!=null)
				return false;
		}else{
			if (!(tipo.equals(idSoggetto.getTipo())))
				return false;
		}

		// Nome
		String nome = res.getString(columnNome);
		//System.out.println("NOME TROVATO["+nome+"] ATTESO["+idSoggetto.getNome()+"]");
		if(nome==null){
			if(idSoggetto.getNome()!=null)
				return false;
		}else{
			if (!(nome.equals(idSoggetto.getNome())))
				return false;
		}

		// IDPorta
		String idPorta = res.getString(columnIdPorta);
		//System.out.println("IDPORTA TROVATO["+idPorta+"] ATTESO["+idSoggetto.getCodicePorta()+"]");
		if(idPorta==null){
			if(idSoggetto.getCodicePorta()!=null)
				return false;
		}else{
			if (!(idPorta.equals(idSoggetto.getCodicePorta())))
				return false;
		}

		// Indirizzo
		String indirizzo = res.getString(columnIndirizzo);
		//System.out.println("INDIRIZZO TELEMATICO TROVATO["+indirizzo+"] ATTESO["+indirizzoSoggetto+"]");
		if(indirizzo==null){
			if(indirizzoSoggetto!=null)
				return false;
		}else{
			if (!(indirizzo.equals(indirizzoSoggetto)))
				return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	/* -------------- ALLEGATI ----------------------- */
	public long countTracedAllegati(String idMessaggio)
			throws TestSuiteException {
		return _countTracedAllegati(this.prepareStatement(idMessaggio));
	}
	public long countTracedAllegati(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _countTracedAllegati(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public long countTracedAllegati(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _countTracedAllegati(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private long _countTracedAllegati(PreparedStatement pstmt) throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement pstmtListaAllegati = null;
		int idTraccia = 0;
		try {
			res = pstmt.executeQuery();

			if (res.next()){
				idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				if(idTraccia<=0)
					throw new TestSuiteException("L'id traccia vale null");

			}else{
				return 0;
			}
			res.close();
			pstmt.close();

			pstmtListaAllegati = this.con
					.prepareStatement("select count(*) as tot from "+CostantiDB.TRACCE_ALLEGATI+" where "+CostantiDB.TRACCE_ALLEGATI_COLUMN_ID_TRACCIA+"=? ");
			pstmtListaAllegati.setInt(1,idTraccia);
			res = pstmtListaAllegati.executeQuery();
			
			if(res.next()){
				
				return res.getLong("tot");

			}
			
			res.close();
			pstmtListaAllegati.close();
			return 0;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaAllegati.close();
			}catch(Exception e){}
		}	
	}
	
	
	
	public boolean isTracedAllegato(String idMessaggio,String contentId,String contentLocation, String contentType, boolean checkNotNullDigit)
			throws TestSuiteException {
		return _isTracedAllegato(this.prepareStatement(idMessaggio),contentId,contentLocation,contentType,checkNotNullDigit);
	}
	public boolean isTracedAllegato(String idMessaggio,IDSoggetto idPortaMessaggio,String contentId,String contentLocation, String contentType, boolean checkNotNullDigit)
			throws TestSuiteException {
		return _isTracedAllegato(this.prepareStatement(idMessaggio,idPortaMessaggio),contentId,contentLocation,contentType,checkNotNullDigit);
	}
	public boolean isTracedAllegato(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String contentId,String contentLocation, String contentType, boolean checkNotNullDigit)
			throws TestSuiteException {
		return _isTracedAllegato(this.prepareStatement(idMessaggio,datiServizioAzione),contentId,contentLocation,contentType,checkNotNullDigit);
	}
	private boolean _isTracedAllegato(PreparedStatement pstmt,
			String contentId,String contentLocation, String contentType, boolean checkNotNullDigit) throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement pstmtListaAllegati = null;
		int idTraccia = 0;
		try {
			res = pstmt.executeQuery();

			if (res.next()){
				idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				if(idTraccia<=0)
					throw new TestSuiteException("L'id traccia vale null");

			}else{
				return false;
			}
			res.close();
			pstmt.close();

			pstmtListaAllegati = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE_ALLEGATI+" where "+CostantiDB.TRACCE_ALLEGATI_COLUMN_ID_TRACCIA+"=? ");
			pstmtListaAllegati.setInt(1,idTraccia);
			res = pstmtListaAllegati.executeQuery();
			
			while(res.next()){

				boolean trovato = true;

				String idRead = res.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_ID);
				//System.out.println("ID TROVATO["+normalizeContentInfo(idRead)+"] ATTESO["+normalizeContentInfo(contentId)+"]");
				if(idRead==null){
					if(contentId!=null){
						trovato  = false;
					}
				}else{
					if (!(normalizeContentInfo(idRead).equals(normalizeContentInfo(contentId))))
						trovato  = false;
				}
				//System.out.println("TROVATO ["+trovato+"]");
				
				String locationRead = res.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_LOCATION);
				//System.out.println("LOCATION TROVATO["+normalizeContentInfo(locationRead)+"] ATTESO["+normalizeContentInfo(contentLocation)+"]");
				if(locationRead==null){
					if(contentLocation!=null)
						trovato  = false;
				}else{
					if (!(normalizeContentInfo(locationRead).equals(normalizeContentInfo(contentLocation))))
						trovato  = false;
				}
				//System.out.println("TROVATO ["+trovato+"]");
				
				String typeRead = res.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_TYPE);
				//System.out.println("TYPE TROVATO["+typeRead+"] ATTESO["+contentType+"]");
				if(typeRead==null){
					if(contentType!=null)
						trovato  = false;
				}else{
					if (!(typeRead.equals(contentType)))
						trovato  = false;
				}
				//System.out.println("TROVATO ["+trovato+"]");
				
				//System.out.println("CHECK ["+checkNotNullDigit+"]");
				if(checkNotNullDigit){
					String digest = res.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_DIGEST);
					if(digest==null){
						trovato  = false;
					}
				}
				//System.out.println("TROVATO ["+trovato+"]");
				
				if(trovato){
					return true;
				}
			}
			
			res.close();
			pstmtListaAllegati.close();
			return false;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaAllegati.close();
			}catch(Exception e){}
		}	
	}
	private String normalizeContentInfo(String content){
		if(content == null){
			return null;
		}
		String newS = new String(content);
		if(content.startsWith("<")){
			newS = newS.substring(1);
		}
		if(content.endsWith(">")){
			newS = newS.substring(0,newS.length()-1);
		}
		return newS;
	}
	
	
	
	
	
	
	
	
	/* -------------- EXTENDEND INFO PROTOCOL ----------------------- */

	public boolean isTracedExtInfoProtocol(String idMessaggio,String name,String value)
			throws TestSuiteException {
		return _isTracedExtInfoProtocol(this.prepareStatement(idMessaggio),name,value);
	}
	public boolean isTracedExtInfoProtocol(String idMessaggio,IDSoggetto idPortaMessaggio,String name,String value)
			throws TestSuiteException {
		return _isTracedExtInfoProtocol(this.prepareStatement(idMessaggio,idPortaMessaggio),name,value);
	}
	public boolean isTracedExtInfoProtocol(String idMessaggio, DatiServizioAzione datiServizioAzione,
			String name,String value)
			throws TestSuiteException {
		return _isTracedExtInfoProtocol(this.prepareStatement(idMessaggio,datiServizioAzione),name,value);
	}
	private boolean _isTracedExtInfoProtocol(PreparedStatement pstmt,
			String name,String value) throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement pstmtListaExtInfoProtocol = null;
		int idTraccia = 0;
		try {
			res = pstmt.executeQuery();

			if (res.next()){
				idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				if(idTraccia<=0)
					throw new TestSuiteException("L'id traccia vale null");

			}else{
				return false;
			}
			res.close();
			pstmt.close();

			pstmtListaExtInfoProtocol = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE_EXT_INFO+" where "+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+"=? ");
			pstmtListaExtInfoProtocol.setInt(1,idTraccia);
			res = pstmtListaExtInfoProtocol.executeQuery();
			boolean trovato = false;
			
			while(res.next()){
				
				String nome = res.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME);
				String valore = res.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE);
				
				if(nome.equals(name) && valore.equals(value)){
					trovato = true;
					break;
				}
				
			}

			return trovato;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaExtInfoProtocol.close();
			}catch(Exception e){}
		}	
	}
	
	
	public Hashtable<String,String> readTracedExtInfoProtocol(String idMessaggio)
			throws TestSuiteException {
		return _readTracedExtInfoProtocol(this.prepareStatement(idMessaggio));
	}
	public Hashtable<String,String> readTracedExtInfoProtocol(String idMessaggio,IDSoggetto idPortaMessaggio)
			throws TestSuiteException {
		return _readTracedExtInfoProtocol(this.prepareStatement(idMessaggio,idPortaMessaggio));
	}
	public Hashtable<String,String> readTracedExtInfoProtocol(String idMessaggio, DatiServizioAzione datiServizioAzione)
			throws TestSuiteException {
		return _readTracedExtInfoProtocol(this.prepareStatement(idMessaggio,datiServizioAzione));
	}
	private Hashtable<String,String> _readTracedExtInfoProtocol(PreparedStatement pstmt) throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement pstmtListaExtInfoProtocol = null;
		Hashtable<String,String> extInfos = new Hashtable<String, String>();
		int idTraccia = 0;
		try {
			res = pstmt.executeQuery();

			if (res.next()){
				idTraccia = res.getInt(CostantiDB.TRACCE_COLUMN_ID);
				if(idTraccia<=0)
					throw new TestSuiteException("L'id traccia vale null");

			}else{
				return extInfos;
			}
			res.close();
			pstmt.close();

			pstmtListaExtInfoProtocol = this.con
					.prepareStatement("select * from "+CostantiDB.TRACCE_EXT_INFO+" where "+CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+"=? ");
			pstmtListaExtInfoProtocol.setInt(1,idTraccia);
			res = pstmtListaExtInfoProtocol.executeQuery();
			
			while(res.next()){
				
				String nome = res.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME);
				String valore = res.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE);
				extInfos.put(nome, valore);
				
			}

			return extInfos;

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedTrasmissione");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
			try{
				pstmtListaExtInfoProtocol.close();
			}catch(Exception e){}
		}	
	}
	
	
	
	
	
	
	
	
	/* ------------------------- IS ARRIVED ------------------------ */

	/** 
	 * Metodo cheva a scrivere il numero di volte 
	 * che viene ricevuto un messaggio
	 * 
	 * @param id Identi
	 * */
	public void tracciaIsArrivedIntoDatabase(String id,String destinatario) throws Exception {

		// Raccolata tempo minimo
		PreparedStatement state=null;
		ResultSet res = null;
		java.sql.Timestamp minDate = null;
		int giro = 0;
		int giriMax = 300;
		LoggerWrapperFactory.getLogger("openspcoop2.testsuite").debug("Query in corso...:  select tracce.gdo from tracce where tracce."+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"='"+id+"' AND "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"='0' AND "+CostantiDB.TRACCE_COLUMN_PDD_CODICE+" LIKE '"+destinatario+"%"+"' ORDER BY tracce.gdo");
		while(giro<giriMax){
			try {
				//System.out.println("SELECT [select "+CostantiDB.TRACCE_COLUMN_GDO+" from "+CostantiDB.TRACCE+" where  "+CostantiDB.TRACCE_COLUMN_ID+"="+id+" AND "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"=0 AND "+CostantiDatabase.ID_PORTA+" LIKE "+destinatario+"%"+" ORDER BY "+CostantiDB.TRACCE_COLUMN_GDO+"]");
				state=this.con.prepareStatement("select "+CostantiDB.TRACCE_COLUMN_GDO+" from "+CostantiDB.TRACCE+" where "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=? AND "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"=? AND "+CostantiDB.TRACCE_COLUMN_PDD_CODICE+" LIKE ? ORDER BY "+CostantiDB.TRACCE_COLUMN_GDO);
				state.setString(1,id);
				state.setInt(2,0);
				state.setString(3, destinatario+"%");
				res = state.executeQuery();
				if(res.next()){
					minDate = res.getTimestamp("gdo");
				}else{
					throw new Exception("Data minima per traccia non trovata."); 
				}
				res.close();
				state.close();
			} catch (Exception e) {
				if(giro==(giriMax-1))
					throw new Exception("Impostazione isArrived non riuscita (select Data) [select "+CostantiDB.TRACCE_COLUMN_GDO+" from "+CostantiDB.TRACCE+" where "+
								CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"='"+id+"' AND "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"='0' AND "+CostantiDB.TRACCE_COLUMN_PDD_CODICE+" LIKE '"+destinatario+"%"+"' ORDER BY "+CostantiDB.TRACCE_COLUMN_GDO+"]: "+e.getMessage(),e);
			}
			finally{
				try{
					res.close();
				}catch(Exception sql){}
				try{
					state.close();
				}catch(Exception sql){}
			}

			if(minDate!=null)
				break;

			giro++;
			if(giro==giriMax)
				throw new Exception("Impostazione isArrived non riuscita (select Data) aspettato 1 minuto.");

			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}

		// Raccolata id
		int idTraccia = -1;
		try {
			//System.out.println("SELECT [select tracce.id from tracce where tracce.id="+id+" AND "+CostantiDatabase.IS_ARRIVED+"=0 AND "+CostantiDatabase.ID_PORTA+" LIKE "+destinatario+"%"+" AND tracce.gdo="+minDate+"]");
			state=this.con.prepareStatement("select "+CostantiDB.TRACCE_COLUMN_ID+" from "+CostantiDB.TRACCE+" where "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO
					+"=? AND "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"=? AND "+CostantiDB.TRACCE_COLUMN_PDD_CODICE+" LIKE ? AND "+CostantiDB.TRACCE_COLUMN_GDO+"=?");
			state.setString(1,id);
			state.setInt(2,0);
			state.setString(3, destinatario+"%");
			state.setTimestamp(4,minDate);
			res = state.executeQuery();
			if(res.next()){
				idTraccia = res.getInt("id");
			}else{
				throw new Exception("Identificatore traccia non trovato."); 
			}
			res.close();
			state.close();
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new Exception("Impostazione isArrived non riuscita (select id): "+e.getMessage(),e);
		}
		finally{
			try{
				res.close();
			}catch(Exception sql){}
			try{
				state.close();
			}catch(Exception sql){}
		}


		try {
			//System.out.println("UPDATE [UPDATE tracce set "+CostantiDatabase.IS_ARRIVED+"=1 where id="+idTraccia+"]");
			state=this.con.prepareStatement("UPDATE "+CostantiDB.TRACCE+" set "+CostantiDB.TRACCE_COLUMN_IS_ARRIVED+"=? where "+CostantiDB.TRACCE_COLUMN_ID+"=?");
			state.setInt(1,1);
			state.setInt(2,idTraccia);
			state.execute();
			state.close();
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new Exception("Impostazione isArrived non riuscita: "+e.getMessage(),e);
		}
		finally{
			try{
				state.close();
			}catch(Exception sql){}
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------- UTILITY --------------------------- */
	/**
	 * Ritorna un id che rispetta i parametri
	 * 
	 */
	public String[] getIdMessaggio(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,String tipoServizio,String servizio,String azione,
			long timeAfter)
					throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			String tmpAzione = null;
			if(azione==null){
				tmpAzione = CostantiDB.TRACCE_COLUMN_AZIONE +" is null";
			}else{
				tmpAzione = CostantiDB.TRACCE_COLUMN_AZIONE +"=?";
			}

			pstmt = this.con
					.prepareStatement("select "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+" from "+CostantiDB.TRACCE+" where "+CostantiDB.TRACCE_COLUMN_MITTENTE_NOME+"=? AND "+
							CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO+"=? AND "+
							CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME+"=? AND "+
							CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO+"=? AND "+
							CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+"=? AND "+
							CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+"=? AND "+
							CostantiDB.TRACCE_COLUMN_GDO+">? AND "+tmpAzione);
			pstmt.setString(1, mittente);
			pstmt.setString(2, tipoMittente);
			pstmt.setString(3, destinatario);
			pstmt.setString(4, tipoDestinatario);
			pstmt.setString(5, servizio);
			pstmt.setString(6, tipoServizio);
			pstmt.setTimestamp(7, new Timestamp(timeAfter) );
			if(azione!=null)
				pstmt.setString(8, azione);

			res = pstmt.executeQuery();

			Vector<String> v = new Vector<String>();
			while (res.next()) {

				String value = res.getString(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO);
				v.add(value);

			}

			if(v.size()<=0)
				return null;
			else{
				String [] s = new String[1];
				return v.toArray(s);
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getIdMessaggio");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	

}
