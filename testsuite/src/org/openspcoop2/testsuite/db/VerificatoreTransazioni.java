/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * Verifica le tracce
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerificatoreTransazioni {

	protected Connection con;
	protected String protocollo;
	private TransazioneFieldConverter fieldConverter;
	
	public VerificatoreTransazioni(Connection con,String protocollo){
		this.con = con;
		this.protocollo = protocollo;
		this.fieldConverter = new TransazioneFieldConverter(TipiDatabase.DEFAULT);
	}
	
		
	
	
	// **** PREPARED STATEMENT *******
	
	// Transazione
	protected PreparedStatement prepareStatement(String idTransazione) throws TestSuiteException{
		try{
			PreparedStatement pstmt = this.con
					.prepareStatement("select * from "+CostantiDB.TRANSAZIONI+" where "+this.fieldConverter.toColumn(Transazione.model().ID_TRANSAZIONE, false)+"=?");
			pstmt.setString(1, idTransazione);
			return pstmt;
		}catch(Exception e){
			throw new TestSuiteException(e,e.getMessage());
		}
	}
	
	
	// TRACCIA
	
	
	public boolean isTraced(String idTransazione) throws TestSuiteException {
		return _isTraced(this.prepareStatement(idTransazione));
	}
	private boolean _isTraced(PreparedStatement pstmt) throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean ret = res.next();
			return ret;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTraced");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}


	public String[] getValuesTraced(String idTransazione,String colonna)
			throws TestSuiteException {
		return _getValuesTraced(this.prepareStatement(idTransazione),colonna);
	}
	private String[] _getValuesTraced(PreparedStatement pstmt,String colonna) throws TestSuiteException{
		ResultSet res = null;
		try {
			res = pstmt.executeQuery();
			List<String> out = new ArrayList<>();
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

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"getValuesTracedIntoResponse");
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
	
	public boolean isTracedEsito(String idTransazione, int esitoElaborazione)
			throws TestSuiteException {
		return _isTracedEsito(this.prepareStatement(idTransazione),esitoElaborazione);
	}
	private boolean _isTracedEsito(PreparedStatement pstmt, int esitoElaborazione)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				int esito = res.getInt(this.fieldConverter.toColumn(Transazione.model().ESITO, false));
				return esito==esitoElaborazione;
			}

			return presente;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTracedRuoloPdd");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}


	
	public boolean isTracedCorrelazioneApplicativaRichiesta(String idTransazione, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idTransazione),correlazioneApplicativa);
	}
	private boolean _isTracedCorrelazioneApplicativaRichiesta(PreparedStatement pstmt, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(this.fieldConverter.toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, false));
				if(value==null){
					if(correlazioneApplicativa!=null)
						return false;
				}else{
					if (!(value.equals(correlazioneApplicativa)))
						return false;
				}

			}

			return presente;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean existsTracedCorrelazioneApplicativaRichiesta(String idTransazione)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRichiesta(this.prepareStatement(idTransazione));
	}
	private boolean _existsTracedCorrelazioneApplicativaRichiesta(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(this.fieldConverter.toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, false));
				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	public boolean isTracedCorrelazioneApplicativaRisposta(String idTransazione, String correlazioneApplicativa)
			throws TestSuiteException {
		return _isTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idTransazione),correlazioneApplicativa);
	}
	private boolean _isTracedCorrelazioneApplicativaRisposta(PreparedStatement pstmt, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(this.fieldConverter.toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, false));
				if(value==null){
					if(correlazioneApplicativa!=null)
						return false;
				}else{
					if (!(value.equals(correlazioneApplicativa)))
						return false;
				}

			}

			return presente;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean existsTracedCorrelazioneApplicativaRisposta(String idTransazione)
			throws TestSuiteException {
		return _existsTracedCorrelazioneApplicativaRisposta(this.prepareStatement(idTransazione));
	}
	private boolean _existsTracedCorrelazioneApplicativaRisposta(PreparedStatement pstmt)
			throws TestSuiteException {

		ResultSet res = null;
		try {
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(this.fieldConverter.toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, false));
				if(value==null){
					return false;
				}

			}

			return presente;

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database delle transazioni: "+e.getMessage(),"isTracedCorrelazioneApplicativa");
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
