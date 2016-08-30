/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import java.util.Vector;

import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;

/**
 * Verifica le tracce
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerificatoreMessaggi {

	protected Connection con;
	protected String protocollo;
	
	public VerificatoreMessaggi(Connection con,String protocollo){
		this.con = con;
		this.protocollo = protocollo;
	}
	
	
	
	
	/* ----------------------- MESSAGGI --------------------------- */
	
	public long countMsgOpenSPCoop_profiloAsincrono(String idRichiesta,String idRisposta) throws TestSuiteException {
		return countMsgOpenSPCoop(idRichiesta) + countMsgOpenSPCoop(idRisposta);
	}
	public synchronized long countMsgOpenSPCoop(String id) throws TestSuiteException {
		PreparedStatement prep = null;
		ResultSet res = null;
		try {
			prep = this.con
					.prepareStatement("select count(*) as numeroMsg from "+GestoreMessaggi.MESSAGGI+
							" where "+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>'"+TimerGestoreMessaggi.ID_MODULO+"' AND "+
							"("+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? OR "+GestoreMessaggi.MESSAGGI_COLUMN_RIFERIMENTO_MSG+"=?) AND "+
							GestoreMessaggi.MESSAGGI_COLUMN_PROTOCOLLO+"=?");
			prep.setString(1,id);
			prep.setString(2,id);
			prep.setString(3,this.protocollo);
			res = prep.executeQuery();
			if(res.next()){
				long r = res.getLong("numeroMsg");
				res.close();
				prep.close();
				return r;
			}else{
				res.close();
				prep.close();
				throw new Exception("Errore durante il posizionamento del result set");
			}
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"countMsgOpenSPCoop");
		}finally{
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
		}
	}

	
	
	public String getMotivoErroreProcessamentoMessaggio(String id,String tipo) throws TestSuiteException {
		PreparedStatement prep = null;
		ResultSet res = null;
		try {
			prep = null;
			if(Costanti.OUTBOX.equals(tipo)){
				prep = this.con.prepareStatement("select errore_processamento from "+GestoreMessaggi.MESSAGGI+
						" where "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			}else{
				prep = this.con.prepareStatement("select errore_processamento from "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+
						" where "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO+"=?");
			}
			prep.setString(1,id);
			if(Costanti.OUTBOX.equals(tipo)){
				prep.setString(2,tipo);
			}
			res = prep.executeQuery();
			if(res.next()){
				String r = res.getString("errore_processamento");
				res.close();
				prep.close();
				return r;
			}else{
				res.close();
				prep.close();
				throw new Exception("Messaggio non trovato");
			}
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"getMotivoErroreProcessamentoMessaggio");
		}finally{
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
		}
	}

	
	public boolean existsMessaggioInProcessamento(String id,String tipo) throws TestSuiteException {
		PreparedStatement prep = null;
		ResultSet res = null;
		try {
			prep = this.con.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+
					" where "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+
					GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
					GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>? AND "+
					GestoreMessaggi.MESSAGGI_COLUMN_PROTOCOLLO+"=?");
			prep.setString(1,id);
			prep.setString(2,tipo);
			prep.setString(3, TimerGestoreMessaggi.ID_MODULO);
			prep.setString(4,this.protocollo);
			res = prep.executeQuery();
			if(res.next()){
				res.close();
				prep.close();
				return true;
			}else{
				res.close();
				prep.close();
				return false;
			}
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"existsMessaggioInProcessamento");
		}finally{
			try{
				res.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
		}
	}

	
	public void deleteMessage(String id,String tipo,boolean useTransazioni) throws TestSuiteException {
		deleteMessage_engine(id, tipo, false, null, useTransazioni);
	}
	public void deleteMessageByRiferimentoMessaggio(String id,String tipo,boolean useTransazioni) throws TestSuiteException {
		deleteMessage_engine(id, tipo, true, null, useTransazioni);
	}
	public void deleteMessageByRiferimentoMessaggio(String id,String tipo,boolean orderByOraRegistrazioneASC,boolean useTransazioni) throws TestSuiteException {
		deleteMessage_engine(id, tipo, true, orderByOraRegistrazioneASC, useTransazioni);
	}
	private void deleteMessage_engine(String id,String tipo,boolean rifMsg,Boolean orderByOraRegistrazioneASC,boolean useTransazioni) throws TestSuiteException {
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			// Prelevo id
			String idMessaggio = id;
			if(rifMsg){
				String query  = "select "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+" from "+GestoreMessaggi.MESSAGGI+
						" WHERE "+GestoreMessaggi.MESSAGGI_COLUMN_RIFERIMENTO_MSG+"=? AND "
							+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
							GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>? AND "+
							GestoreMessaggi.MESSAGGI_COLUMN_PROTOCOLLO+"=?";
				if(orderByOraRegistrazioneASC!=null){
					query = query +" ORDER BY "+GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE+" ";
					if(orderByOraRegistrazioneASC){
						query = query +"ASC";
					}
					else{
						query = query +"DESC";
					}
				}
				prep = this.con.prepareStatement(query);
				prep.setString(1,id);
				prep.setString(2,tipo);
				prep.setString(3,TimerGestoreMessaggi.ID_MODULO);
				prep.setString(4,this.protocollo);
				rs = prep.executeQuery();
				if(rs.next()){
					idMessaggio = rs.getString(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO);
				}
				rs.close();
				prep.close();
				//System.out.println("PRELEVATO ["+idMessaggio+"]");
			}

			prep = this.con
					.prepareStatement("UPDATE "+GestoreMessaggi.MESSAGGI+
							" SET "+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"=? where "+
							GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+
							GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			prep.setString(1,TimerGestoreMessaggi.ID_MODULO);
			prep.setString(2,idMessaggio);
			prep.setString(3,tipo);
			prep.executeUpdate();
			prep.close();

			if(useTransazioni){
				prep = this.con
						.prepareStatement("UPDATE "+Costanti.REPOSITORY+
								" SET "+Costanti.REPOSITORY_COLUMN_USE_REPOSITORY_ACCESS+"=? where "+
									Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+"=? AND "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				prep.setInt(1,0);
				prep.setString(2,idMessaggio);
				prep.setString(3,tipo);
				prep.executeUpdate();
				prep.close();
			}
			else{
				prep = this.con
						.prepareStatement("UPDATE "+Costanti.REPOSITORY+
								" SET "+Costanti.REPOSITORY_COLUMN_USE_PROFILO+"=?,"+Costanti.REPOSITORY_COLUMN_USE_PDD+
								"=? where "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+"=? AND "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				prep.setInt(1,0);
				prep.setInt(2,0);
				prep.setString(3,idMessaggio);
				prep.setString(4,tipo);
				prep.executeUpdate();
				prep.close();
			}

			if(Costanti.OUTBOX.equals(tipo)){
				prep = this.con
						.prepareStatement("DELETE FROM "+Costanti.PROFILO_ASINCRONO+
								" where "+Costanti.PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO+"=? AND "+Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO+"=?");
				prep.setString(1,idMessaggio);
				prep.setString(2,tipo);
				prep.executeUpdate();
				prep.close();

				prep = this.con
						.prepareStatement("DELETE FROM "+Costanti.RISCONTRI_DA_RICEVERE+
								" where "+Costanti.RISCONTRI_COLUMN_ID_MESSAGGIO+"=?");
				prep.setString(1,idMessaggio);
				prep.executeUpdate();
				prep.close();
			}

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			try{
				prep.close();
			}catch(Exception eClose){}
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"updateProprietarioMessaggio");
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception eClose){}
			try{
				prep.close();
			}catch(Exception eClose){}
		}
	}

	public void deleteUtilizzoProfiloCollaborazione(String id,String tipo, boolean useTransazioni) throws TestSuiteException {
		PreparedStatement prep = null;
		try {
			if(useTransazioni){
				prep = this.con
						.prepareStatement("UPDATE "+Costanti.REPOSITORY+
								" SET "+Costanti.REPOSITORY_COLUMN_USE_REPOSITORY_ACCESS+"=? where "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+"=? AND "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
			}
			else{
				prep = this.con
						.prepareStatement("UPDATE "+Costanti.REPOSITORY+
								" SET "+Costanti.REPOSITORY_COLUMN_USE_PROFILO+"=? where "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+"=? AND "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
			}
			prep.setInt(1,0);
			prep.setString(2,id);
			prep.setString(3,tipo);
			prep.executeUpdate();
			prep.close();

		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			try{
				prep.close();
			}catch(Exception eClose){}
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
					"updateProprietarioMessaggio");
		}finally{
			try{
				prep.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------- CORRELAZIONE APPLICATIVA --------------------------- */

	public void deleteCorrelazioneApplicativa(String idCorrelazione)
			throws TestSuiteException {

		PreparedStatement pstmt = null;
		try {
			pstmt = this.con.prepareStatement("delete from "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" where "+
					GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA_COLUMN_ID_APPLICATIVO+"=?");
			pstmt.setString(1, idCorrelazione);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore deleteCorrelazioneApplicativa: "+e.getMessage(),"deleteCorrelazioneApplicativa");
		}finally{
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRichiestaInviato(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRichiesta(id,Costanti.OUTBOX,correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRichiestaRicevuto(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRichiesta(id,Costanti.INBOX,correlazioneApplicativa);
	}
	private boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRichiesta(String id, String tipo, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con
					.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+" where "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			pstmt.setString(1, id);
			pstmt.setString(2, tipo);
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(GestoreMessaggi.MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA);

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
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedCorrelazioneApplicativaIntoMessaggioRichiesta("+tipo+")");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRispostaInviato(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRisposta(id,Costanti.OUTBOX,correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRispostaRicevuto(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRisposta(id,Costanti.INBOX,correlazioneApplicativa);
	}
	private boolean isTracedCorrelazioneApplicativaRichiestaIntoMessaggioRisposta(String id, String tipo, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con
					.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+" where "+GestoreMessaggi.MESSAGGI_COLUMN_RIFERIMENTO_MSG+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			pstmt.setString(1, id);
			pstmt.setString(2, tipo);
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(GestoreMessaggi.MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA);

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
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedCorrelazioneApplicativaIntoMessaggioRisposta("+tipo+")");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	public boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRichiestaInviato(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRispostaIntoMessaggioRichiesta(id,Costanti.OUTBOX,correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRichiestaRicevuto(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRispostaIntoMessaggioRichiesta(id,Costanti.INBOX,correlazioneApplicativa);
	}
	private boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRichiesta(String id, String tipo, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con
					.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+" where "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			pstmt.setString(1, id);
			pstmt.setString(2, tipo);
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(GestoreMessaggi.MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA);

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
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedCorrelazioneApplicativaIntoMessaggioRichiesta("+tipo+")");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRispostaInviato(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRispostaIntoMessaggioRisposta(id,Costanti.OUTBOX,correlazioneApplicativa);
	}
	public boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRispostaRicevuto(String id, String correlazioneApplicativa)throws TestSuiteException {
		return isTracedCorrelazioneApplicativaRispostaIntoMessaggioRisposta(id,Costanti.INBOX,correlazioneApplicativa);
	}
	private boolean isTracedCorrelazioneApplicativaRispostaIntoMessaggioRisposta(String id, String tipo, String correlazioneApplicativa)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con
					.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+" where "+GestoreMessaggi.MESSAGGI_COLUMN_RIFERIMENTO_MSG+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			pstmt.setString(1, id);
			pstmt.setString(2, tipo);
			res = pstmt.executeQuery();

			boolean presente = false;
			while (res.next()) {
				presente = true;

				String value = res.getString(GestoreMessaggi.MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA);

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
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"isTracedCorrelazioneApplicativaIntoMessaggioRisposta("+tipo+")");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	
	
	
	
	
	
	
	/* ----------------------- CONSEGNA IN ORDINE --------------------------- */

	public int getSequenzaAttesaDaRicevere(String idCollaborazione){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = this.con.prepareStatement("Select * from "+Costanti.SEQUENZA_DA_RICEVERE+" where "+
					Costanti.SEQUENZA_DA_RICEVERE_COLUMN_ID_COLLABORAZIONE+"=?");
			pstmt.setString(1, idCollaborazione);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getInt("sequenza_attesa");
			}else{
				return -1;
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore getSequenzaAttesaDaRicevere: "+e.getMessage(),"getSequenzaAttesaDaRicevere");
		}finally{
			try{
				rs.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	public int getNextSequenzaDaInviare(String idCollaborazione){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = this.con.prepareStatement("Select * from "+Costanti.SEQUENZA_DA_INVIARE+" where "+
					Costanti.SEQUENZA_DA_INVIARE_COLUMN_ID_COLLABORAZIONE+"=?");
			pstmt.setString(1, idCollaborazione);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getInt("prossima_sequenza");
			}else{
				return -1;
			}

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore getNextSequenzaDaInviare: "+e.getMessage(),"getNextSequenzaDaInviare");
		}finally{
			try{
				rs.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	public void aggiornaSequenzaAttesaDaRicevere(String idCollaborazione,int sequenzaAttesa){
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con.prepareStatement("UPDATE "+Costanti.SEQUENZA_DA_RICEVERE+" SET sequenza_attesa=? WHERE "+
					Costanti.SEQUENZA_DA_RICEVERE_COLUMN_ID_COLLABORAZIONE+"=?");
			pstmt.setInt(1, sequenzaAttesa);
			pstmt.setString(2, idCollaborazione);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore aggiornaSequenzaAttesaDaRicevere: "+e.getMessage(),"aggiornaSequenzaAttesaDaRicevere");
		}finally{
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	public void resetSequenzeDaRicevere(){
		PreparedStatement pstmt = null;
		try {
			pstmt = this.con.prepareStatement("DELETE FROM  "+Costanti.SEQUENZA_DA_RICEVERE);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore resetSequenzeDaRicevere: "+e.getMessage(),"resetSequenzeDaRicevere");
		}finally{
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	public void resetSequenzeDaInviare(){
		PreparedStatement pstmt = null;
		try {

			pstmt = this.con.prepareStatement("DELETE FROM  "+Costanti.SEQUENZA_DA_INVIARE);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore resetSequenzeDaInviare: "+e.getMessage(),"resetSequenzeDaInviare");
		}finally{
			try{
				pstmt.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* ----------------------- REPOSITORY --------------------------- */
	
	public long getNumeroDuplicatiRicevuti(String tipoMessaggio,String idMessaggio,boolean richiesta,boolean useTransazioni)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {
			if(useTransazioni){
				if(richiesta){
					pstmt = this.con
							.prepareStatement("select "+CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RICHIESTA+" from "+
									CostantiTestSuite.TABLE_TRANSAZIONI+" where "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA+
										"=? AND "+CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RICHIESTA+">0");
				}
				else{
					pstmt = this.con
							.prepareStatement("select "+CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RISPOSTA+" from "+
									CostantiTestSuite.TABLE_TRANSAZIONI+" where "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RISPOSTA
										+"=? AND "+CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RISPOSTA+">0");
				}
				pstmt.setString(1, idMessaggio);
				res = pstmt.executeQuery();
				if (res.next()) {
					if(richiesta){
						long dup = res.getLong(CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RICHIESTA);
						return dup;
					}
					else{
						long dup = res.getLong(CostantiTestSuite.TABLE_TRANSAZIONI_DUPLICATI_RISPOSTA);
						return dup;
					}
				}
				else{
					return 0;
				}
			}
			else{
			
				String campo = Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO;
				if(!richiesta)
					campo = Costanti.REPOSITORY_COLUMN_RIFERIMENTO_MESSAGGIO;
	
				pstmt = this.con
						.prepareStatement("select "+Costanti.REPOSITORY_COLUMN_DUPLICATI+" from "+Costanti.REPOSITORY+
								" where "+campo+"=? AND "+
								Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				pstmt.setString(1, idMessaggio);
				pstmt.setString(2, tipoMessaggio);
	
				res = pstmt.executeQuery();
	
				if (res.next()) {
	
					long dup = res.getLong("duplicati");
					return dup;
				}
				else{
					return 0;
				}
			}


		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getNumeroDuplicatiRicevuti");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}

	public boolean isBustaRegistrataHistory(String tipoMessaggio,String idMessaggio,boolean richiesta,boolean useTransazioni)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try {

			if(useTransazioni){
				if(richiesta){
					pstmt = this.con
							.prepareStatement("select "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA+" from "+
									CostantiTestSuite.TABLE_TRANSAZIONI+" where "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA+"=?");
				}
				else{
					pstmt = this.con
							.prepareStatement("select "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RISPOSTA+" from "+
									CostantiTestSuite.TABLE_TRANSAZIONI+" where "+CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA+"=?");
				}
				pstmt.setString(1, idMessaggio);
				res = pstmt.executeQuery();
				if (res.next()) {
					//System.out.println("TROVATO");
					if(richiesta){
						String s = res.getString(CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RICHIESTA);	
						return s!=null;
					}
					else{
						String s = res.getString(CostantiTestSuite.TABLE_TRANSAZIONI_ID_MESSAGGIO_RISPOSTA);	
						return s!=null;
					}
				}
				else{
					return false;
				}
			}
			else{
			
				String campo = Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO;
				if(!richiesta)
					campo = Costanti.REPOSITORY_COLUMN_RIFERIMENTO_MESSAGGIO;
	
				pstmt = this.con
						.prepareStatement("select "+Costanti.REPOSITORY_COLUMN_USE_HISTORY+" from "+Costanti.REPOSITORY+
								" where "+campo+"=? AND "+
								Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				pstmt.setString(1, idMessaggio);
				pstmt.setString(2, tipoMessaggio);
	
				res = pstmt.executeQuery();
				if (res.next()) {
					//System.out.println("TROVATO");
					int history = res.getInt("history");	
					return (1 == history);
				}
				else{
					return false;
				}
			}


		} catch (SQLException e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getNumeroDuplicatiRicevuti");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------- UTILITY --------------------------- */
	
	public Vector<String> getTabelleNonCorrettamenteSvuotate(String tipoRepositoryBuste)
			throws TestSuiteException {

		ResultSet res = null;
		PreparedStatement pstmt = null;
		Vector<String> resultsVector = new Vector<String>();
		try {

			// REPOSITORY
			boolean findRepositoryBusteMalformato = false;
			Class<?> c = Class.forName(tipoRepositoryBuste);
			IGestoreRepository repositoryBuste = (IGestoreRepository) c.newInstance();
			pstmt = this.con
					.prepareStatement("select * from "+Costanti.REPOSITORY+" where "+repositoryBuste.createSQLCondition_PdD(true) 
							+" OR "+repositoryBuste.createSQLCondition_ProfiloCollaborazione(true));
			res = pstmt.executeQuery();
			while (res.next()) {
				findRepositoryBusteMalformato = true;
				String entryNonGestitaCorrettamente = 
						Costanti.REPOSITORY+"."+ res.getString(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();

			if(findRepositoryBusteMalformato==false){
				// LISTA_TRASMISSIONE
				// INBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_TRASMISSIONI+" where "+Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.INBOX);
				pstmt.setString(2, Costanti.INBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_TRASMISSIONI+"."+ res.getString(Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
				// OUTBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_TRASMISSIONI+" where "+Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.OUTBOX);
				pstmt.setString(2, Costanti.OUTBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_TRASMISSIONI+"."+ res.getString(Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();

				// LISTA_RISCONTRI
				// INBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_RISCONTRI+" where "+Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.INBOX);
				pstmt.setString(2, Costanti.INBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_RISCONTRI+"."+ res.getString(Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
				// OUTBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_RISCONTRI+" where "+Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.OUTBOX);
				pstmt.setString(2, Costanti.OUTBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_RISCONTRI+"."+ res.getString(Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();

				// LISTA_ECCEZIONI
				// INBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_ECCEZIONI+" where "+Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.INBOX);
				pstmt.setString(2, Costanti.INBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_ECCEZIONI+"."+ res.getString(Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
				// OUTBOX
				pstmt = this.con
						.prepareStatement("select * from "+Costanti.LISTA_ECCEZIONI+" where "+Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" from "+Costanti.REPOSITORY+
								" WHERE "+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?)");
				pstmt.setString(1, Costanti.OUTBOX);
				pstmt.setString(2, Costanti.OUTBOX);
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							Costanti.LISTA_ECCEZIONI+"."+ res.getString(Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
			}

			// RISCONTRI DA RICEVERE
			pstmt = this.con
					.prepareStatement("select * from "+Costanti.RISCONTRI_DA_RICEVERE);
			res = pstmt.executeQuery();
			while (res.next()) {
				String entryNonGestitaCorrettamente = 
						Costanti.RISCONTRI_DA_RICEVERE+"."+ res.getString(Costanti.RISCONTRI_COLUMN_ID_MESSAGGIO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();

			// ASINCRONO
			pstmt = this.con
					.prepareStatement("select * from "+Costanti.PROFILO_ASINCRONO+" where "+Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO+"=? AND "+
							Costanti.PROFILO_ASINCRONO_COLUMN_RICEVUTA_ASINCRONA+"=? AND "+Costanti.PROFILO_ASINCRONO_COLUMN_RICEVUTA_APPLICATIVA+"=? AND "+
							" NOT EXISTS (select "+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+" FROM "+Costanti.REPOSITORY+" where "+
							Costanti.REPOSITORY+"."+Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"="+Costanti.PROFILO_ASINCRONO+"."+Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO+"  AND  "+
							Costanti.REPOSITORY+"."+Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO+"="+Costanti.PROFILO_ASINCRONO+"."+Costanti.PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO+"  AND  "+
							repositoryBuste.createSQLCondition_PdD(false) +" AND "+
							repositoryBuste.createSQLCondition_ProfiloCollaborazione(false)+")");
			pstmt.setString(1, Costanti.OUTBOX);
			pstmt.setInt(2, 0);
			pstmt.setInt(3, 0);
			res = pstmt.executeQuery();
			while (res.next()) {
				String entryNonGestitaCorrettamente = 
						Costanti.PROFILO_ASINCRONO+"."+ res.getString(Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(Costanti.PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();

			// MESSAGGI
			boolean findRepositoryMessaggiMalformato = false;
			pstmt = this.con
					.prepareStatement("select * from "+GestoreMessaggi.MESSAGGI+" where "+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>? OR "+
							GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+" is null");
			pstmt.setString(1, "GestoreMessaggi");
			res = pstmt.executeQuery();
			while (res.next()) {
				findRepositoryMessaggiMalformato = true;
				String entryNonGestitaCorrettamente = 
						GestoreMessaggi.MESSAGGI+"."+ res.getString(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();

			// MSG_SERVIZI_APPLICATIVI
			String cmd = "select * from "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+","+GestoreMessaggi.MESSAGGI +" WHERE "+
					GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO+"="+GestoreMessaggi.MESSAGGI+"."+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+
					" AND ("+GestoreMessaggi.MESSAGGI+"."+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>? OR "+GestoreMessaggi.MESSAGGI+"."+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+" is null)";
			pstmt = this.con.prepareStatement(cmd);
			pstmt.setString(1, "GestoreMessaggi");
			res = pstmt.executeQuery();
			while (res.next()) {
				String entryNonGestitaCorrettamente = 
						GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+"."+ res.getString(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO) +" , SA:"+res.getString(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();
			cmd = "select * from "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO+
					" NOT IN (SELECT "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+" FROM "+GestoreMessaggi.MESSAGGI+" WHERE "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?)";
			pstmt = this.con.prepareStatement(cmd);
			pstmt.setString(1, Costanti.INBOX);
			res = pstmt.executeQuery();
			while (res.next()) {
				String entryNonGestitaCorrettamente = 
						GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+"."+ res.getString(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO) +" , (messaggio non esistente) SA:"+res.getString(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO);
				resultsVector.add(entryNonGestitaCorrettamente);
			}
			res.close();
			pstmt.close();

			if(findRepositoryMessaggiMalformato==false) {
				// DEFINIZIONE_MESSAGGI
				// INBOX
				pstmt = this.con
						.prepareStatement("select * from "+GestoreMessaggi.DEFINIZIONE_MESSAGGI+" where "+GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+" from "+GestoreMessaggi.MESSAGGI+
								" WHERE "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"=?)");
				pstmt.setString(1, Costanti.INBOX);
				pstmt.setString(2, Costanti.INBOX);
				pstmt.setString(3, "GestoreMessaggi");
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							GestoreMessaggi.DEFINIZIONE_MESSAGGI+"."+ res.getString(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
				// OUTBOX
				pstmt = this.con
						.prepareStatement("select * from "+GestoreMessaggi.DEFINIZIONE_MESSAGGI+" where "+GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+
								GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO +" NOT IN ("+"select "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+" from "+GestoreMessaggi.MESSAGGI+
								" WHERE "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"=?)");
				pstmt.setString(1, Costanti.OUTBOX);
				pstmt.setString(2, Costanti.OUTBOX);
				pstmt.setString(3, "GestoreMessaggi");
				res = pstmt.executeQuery();
				while (res.next()) {
					String entryNonGestitaCorrettamente = 
							GestoreMessaggi.DEFINIZIONE_MESSAGGI+"."+ res.getString(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO) +"/"+ res.getString(GestoreMessaggi.DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO);
					resultsVector.add(entryNonGestitaCorrettamente);
				}
				res.close();
				pstmt.close();
			}


		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),"getTabelleNonCorrettamenteSvuotate");
		}finally{
			try{
				res.close();
			}catch(Exception e){}
			try{
				pstmt.close();
			}catch(Exception e){}
		}

		return resultsVector;
	}
}
