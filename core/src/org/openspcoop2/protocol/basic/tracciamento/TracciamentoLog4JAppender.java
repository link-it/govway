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



package org.openspcoop2.protocol.basic.tracciamento;

import java.io.PrintStream;

/*import javax.naming.Context;
import javax.sql.DataSource;
*/
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
/*import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.utils.GestoreJNDI;
import org.openspcoop2.utils.Loader;
*/
//NOTA: Se lo script viene interrotto a meta', il file con le tracce che hanno dato errore non viene generato e nel file delle tracce restano tutte le tracce. In ogni caso, al giro successivo dello script, le tracce gia' inserite non verranno prese in considerazione, mentre le altre vengono tentate nuovamente.
//La chiave per identificare ogni messaggio e' formata dal GDO della traccia, piu' l'identificatore del messaggio.

/**
 * Log4JAppender personalizzato per la gestione del tracciamento
 * 
 * @author Andrea Manca (manca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TracciamentoLog4JAppender extends AppenderSkeleton {
		
	//private IUnmarshallingContext uctx = null;
	private PrintStream ps = null;
	/** DataSource dove attingere connessioni */
	//private DataSource ds = null;

	private String dburl = "", dbuser = "", dbpw = "";
	private String filerej = "tracce.rejected";
	private String dbdriver = "";
	private String dataSource = null;
	private String provider = null;
	private String tipoDatabase = null; //tipoDatabase

	//DB Url
	public void setDBUrl(String dburl) {
		this.dburl = dburl;
	}
	public String getDBUrl() {
		return this.dburl;
	}

	//DB User
	public void setDBUser(String dbuser) {
		this.dbuser = dbuser;
	}
	public String getDBUser() {
		return this.dbuser;
	}

	//DB Password
	public void setDBPwd(String dbpw) {
		this.dbpw = dbpw;
	}
	public String getDBPwd() {
		return this.dbpw;
	}
	
	//	DB Driver
	public void setDBDriver(String dbdriver) {
		this.dbdriver= dbdriver;
	}
	public String getDBDriver() {
		return this.dbdriver;
	}

	//	DataSource
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getDataSource() {
		return this.dataSource;
	}

	//	ProviderJNDI
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getProvider() {
		return this.provider;
	}

	//File dove inserire le tracce che non si riesce ad inserire nel DB
	public void setFileRejected(String filerej) {
		this.filerej = filerej;
	}
	public String getFileRejected() {
		return this.filerej;
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override public void activateOptions() {
		/* ---- Inizializzazione del contesto di unmarshall ---- */
		/*try {
			IBindingFactory bfact = BindingDirectory.getFactory(Traccia.class);
			this.uctx = bfact.createUnmarshallingContext();
		} catch(org.jibx.runtime.JiBXException e) {
			System.err.println("[TracciamentoAppender] Riscontrato errore durante la creazione del contesto di unmarshall:\n"+e.getMessage());
		}*/

		/* ---- Apertura file per fallimenti ---- */
		try {
			this.ps = new PrintStream(this.filerej);
		} catch (java.io.IOException ioe) {
			System.err.println("[TracciamentoAppender] IOException: "+ioe.getMessage());
		}
		
		/* --- Impostazione Datasource ---- */
		/*if(this.dataSource!=null){
			try {
				java.util.Properties context = new java.util.Properties();
				if(this.provider!=null){
					context.put(Context.PROVIDER_URL, this.provider);
				}
				GestoreJNDI jndi = new GestoreJNDI(context);
				this.ds = (DataSource) jndi.lookup(this.dataSource);
			} catch (Exception e) {
				System.err.println("[TracciamentoAppender] Lookup DataSource Exception: "+e.getMessage());
			}
		}else{
			try {
				Loader.getInstance().newInstance(this.dbdriver);
			} catch (Exception ex) {
				System.err.println ("[TracciamentoAppender] ClassNotFoundException: "+ex.getMessage());
			}
		}*/
	}

	@Override public void append(LoggingEvent event) {
		
		throw new RuntimeException("NotImplemented");
		
		/*
		
		
		Logger log = OpenSPCoopLogger.getLoggerOpenSPCoopCore();
		String singleTrace = (String) event.getMessage();

		boolean procedi = true;
		if (singleTrace != null) {
			// ---- Unmarshall del file di configurazione ---- 
			ByteArrayInputStream sbis = null;
			Traccia tracciaXML = null;
			try {  
				sbis = new ByteArrayInputStream(singleTrace.getBytes());
				tracciaXML = (Traccia) this.uctx.unmarshalDocument(sbis, null);
				try {
					sbis.close();
				} catch (java.io.IOException ioe) {
				}
			} catch(Exception e) {
				try {
					sbis.close();
				} catch (java.io.IOException ioe) {
				}
				if ((singleTrace != null) && !singleTrace.equals("") && !singleTrace.equals(" ")) {
					log.error("[TracciamentoAppender] Riscontrato errore durante l'unmarshall del file di configurazione:\n"+e.getMessage(),e);
					this.ps.println(singleTrace);
				}
				procedi = false;
			}

			Connection con = null;
			if (procedi) {
				//Connessione al DB
				try {
					if(this.ds != null){
						con = this.ds.getConnection();
					}else{				
						con = DriverManager.getConnection (this.dburl, this.dbuser, this.dbpw);
					}
					
					if(con==null)
						throw new Exception("Connection is null");
					
				} catch (Exception ex) {
					log.error("[TracciamentoAppender] GetConnection, SQLException: "+ex.getMessage(),ex);
					this.ps.println(singleTrace);
					procedi = false;
				}
			}
				
			if (procedi) {
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					con.setAutoCommit(false);

					ValidazioneSintattica val = new ValidazioneSintattica(OpenSPCoopLogger.getLoggerOpenSPCoopCore(), new ProtocolFactory(OpenSPCoopLogger.getLoggerOpenSPCoopCore(), "spcoop"));
					
					Date gdo = null, oramess = null, scadenzamess = null;
					boolean confric = false;
					BigInteger numprog = BigInteger.ZERO;
					String idporta = "", tipomess = "", mittente = "", tipomitt = "", indtelmitt = "", destinatario = "", tipodest = "", indteldest = "", profcoll = "", profcollvalue = "", servcorrprofcoll = "", tipoprofcoll = "", collaborazione = "", servizio = "", tiposerv = "", azione = "", messaggio = "", tempomess = "", tempomessvalue="", riferimentomess = "", inoltro = "", inoltrovalue="";

					gdo = tracciaXML.getGDO();
					idporta = tracciaXML.getIdentificativoPorta();
					tipomess = tracciaXML.getTipoMessaggio();

					Intestazione intestazione = tracciaXML.getIntestazione();
					IntestazioneMessaggio intestazioneMsg = intestazione.getIntestazioneMessaggio();

					Mittente classMitt = intestazioneMsg.getMittente();
					IdentificativoParte idMittente = classMitt.getIdentificativoParte(0);
					mittente = idMittente.getBase();
					tipomitt = idMittente.getTipo();
					indtelmitt = idMittente.getIndirizzoTelematico();
					if (indtelmitt == null)
						indtelmitt = "";

					Destinatario classDest = intestazioneMsg.getDestinatario();
					IdentificativoParte idDest = classDest.getIdentificativoParte();
					destinatario = idDest.getBase();
					tipodest = idDest.getTipo();
					indteldest = idDest.getIndirizzoTelematico();
					if (indteldest == null)
						indteldest = "";

					ProfiloCollaborazione classPC = intestazioneMsg.getProfiloCollaborazione();
					if (classPC != null) {
						profcollvalue = classPC.getBase();
						profcoll = val.toProfilo(profcollvalue).toString();
						
						servcorrprofcoll = classPC.getServizioCorrelato();
						if (servcorrprofcoll == null)
							servcorrprofcoll = "";
						tipoprofcoll = classPC.getTipo();
						if (tipoprofcoll == null)
							tipoprofcoll = "";
					}

					collaborazione = intestazioneMsg.getCollaborazione();
					if (collaborazione == null)
						collaborazione = "";

					Servizio classServ = intestazioneMsg.getServizio();
					if (classServ != null) {
						servizio = classServ.getBase();
						tiposerv = classServ.getTipo();
					}

					azione = intestazioneMsg.getAzione();
					if (azione == null)
						azione = "";

					Messaggio classMess = intestazioneMsg.getMessaggio();
					messaggio = classMess.getIdentificatore();
					OraRegistrazione orareg = classMess.getOraRegistrazione();
					oramess = orareg.getBase();
					tempomessvalue = orareg.getTempo();
					tempomess = val.toTipoOra(tempomessvalue).toString();
					riferimentomess = classMess.getRiferimentoMessaggio();
					if (riferimentomess == null)
						riferimentomess = "";
					scadenzamess = classMess.getScadenza();

					ProfiloTrasmissione classPT = intestazioneMsg.getProfiloTrasmissione();
					if (classPT != null) {
						inoltrovalue = classPT.getInoltro();
						inoltro = val.toInoltro(inoltrovalue).toString();
						confric = classPT.getConfermaRicezione();
					}

					Sequenza classSeq = intestazioneMsg.getSequenza();
					if (classSeq != null)
						numprog = classSeq.getNumeroProgressivo();

					
					//Inserimento della traccia nel DB
					if(!TipiDatabase.isAMember(this.tipoDatabase)){
						throw new TracciamentoException("Tipo database ["+this.tipoDatabase+"] non supportato");
					}
					TipiDatabase tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
					// ** Preparazione parametri
					// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
					java.sql.Timestamp gdoT = new java.sql.Timestamp(gdo.getTime()-(2*60*60*1000));
					java.sql.Timestamp oraRegistrazioneT = null;
					java.sql.Timestamp scadenzaT = null;
					if(oramess!=null){
						// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
						oraRegistrazioneT = new java.sql.Timestamp(oramess.getTime()-(2*60*60*1000));
					}
					if(scadenzamess!=null){
						// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
						scadenzaT = new java.sql.Timestamp(scadenzamess.getTime()-(2*60*60*1000));
					}
					int confermaRicezione = -1;
					if(confric)
						confermaRicezione = 1;
					else
						confermaRicezione = 0 ;
					// ** Insert and return generated key
					long idtraccia = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, new DefaultKeyGeneratorObject(TracciamentoLog4JAppender.TRACCIAMENTO,TracciamentoLog4JAppender.TRACCIAMENTO), 
								new InsertAndGeneratedKeyObject("gdo", gdoT , InsertAndGeneratedKeyJDBCType.TIMESTAMP),
								new InsertAndGeneratedKeyObject("gdo_int", gdoT.getTime(), InsertAndGeneratedKeyJDBCType.LONG),
								new InsertAndGeneratedKeyObject("idporta", idporta, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_messaggio", tipomess, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("mittente", mittente, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_mittente", tipomitt, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("indtelematico_mitt", indtelmitt, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("destinatario", destinatario, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_destinatario", tipodest, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("indtelematico_dest", indteldest, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("profilo_collaborazione", profcollvalue, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("profilo_collaborazione_meta", profcoll, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("servizio_correlato", servcorrprofcoll, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_servizio_correlato", tipoprofcoll, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("collaborazione", collaborazione, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("servizio", servizio, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_servizio", tiposerv, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("azione", azione, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("id_messaggio", messaggio, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("ora_registrazione",oraRegistrazioneT,InsertAndGeneratedKeyJDBCType.TIMESTAMP),
								new InsertAndGeneratedKeyObject("tipo_ora_reg", tempomessvalue, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("tipo_ora_reg_meta", tempomess, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("rif_messaggio", riferimentomess, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("scadenza",scadenzaT,InsertAndGeneratedKeyJDBCType.TIMESTAMP),
								new InsertAndGeneratedKeyObject("inoltro", inoltrovalue, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("inoltro_meta", inoltro, InsertAndGeneratedKeyJDBCType.STRING),
								new InsertAndGeneratedKeyObject("conferma_ricezione", confermaRicezione, InsertAndGeneratedKeyJDBCType.INT),
								new InsertAndGeneratedKeyObject("sequenza", numprog.intValue(), InsertAndGeneratedKeyJDBCType.LONG));
					if(idtraccia<=0){
						throw new Exception("ID autoincrementale non ottenuto");
					}
					

					ListaRiscontri listaRisc = intestazione.getListaRiscontri();
					if (listaRisc != null) {
						for (int i = 0; i < listaRisc.sizeRiscontroList(); i++) {
							Riscontro classRisc = listaRisc.getRiscontro(i);
							String riscontro = classRisc.getIdentificatore();
							OraRegistrazione orareg1 = classRisc.getOraRegistrazione();
							Date orarisc = orareg1.getBase();
							String temporisc = orareg1.getTempo();

							//Inserimento nel DB
							String updateString = "INSERT INTO "+TracciamentoLog4JAppender.TRACCIAMENTO_LISTA_RISCONTRI+" (idtraccia, riscontro, ora_registrazione, tipo_ora_reg, tipo_ora_reg_meta) VALUES (?, ?, ?, ?, ?)";
							stmt = con.prepareStatement(updateString);
							stmt.setLong(1, idtraccia);
							JDBCUtilities.setSQLStringValue(stmt,2, riscontro);
							if(orarisc!=null){
								// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
								stmt.setTimestamp(3, new java.sql.Timestamp(orarisc.getTime()-(2*60*60*1000)));
							}else
								stmt.setTimestamp(3,null);
							JDBCUtilities.setSQLStringValue(stmt,4, temporisc);
							JDBCUtilities.setSQLStringValue(stmt,5, val.toTipoOra(temporisc).toString());
							stmt.executeUpdate();
							stmt.close();
						}
					}

					ListaTrasmissioni listaTrasm = intestazione.getListaTrasmissioni();
					if (listaTrasm != null) {
						for (int i = 0; i < listaTrasm.sizeTrasmissioneList(); i++) {
							Trasmissione classTrasm = listaTrasm.getTrasmissione(i);
							Origine classOrig = classTrasm.getOrigine();
							IdentificativoParte idOr = classOrig.getIdentificativoParte();
							String origine = idOr.getBase();
							String tipoorig = idOr.getTipo();
							String indtelorig = idOr.getIndirizzoTelematico();
							if (indtelorig == null)
								indtelorig = "";
							Destinazione classD = classTrasm.getDestinazione();
							IdentificativoParte idD = classD.getIdentificativoParte();
							String destinazione = idD.getBase();
							String tipod = idD.getTipo();
							String indteld = idD.getIndirizzoTelematico();
							if (indteld == null)
								indteld = "";
							OraRegistrazione orareg2 = classTrasm.getOraRegistrazione();
							Date oratrasm = orareg2.getBase();
							String tempotrasm = orareg2.getTempo();

							//Inserimento nel DB
							String updateString = "INSERT INTO "+TracciamentoLog4JAppender.TRACCIAMENTO_LISTA_TRASMISSIONI+" (idtraccia, origine,tipo_origine ,indtelematico_orig , destinazione, tipo_destinazione, indtel_destinazione, ora_registrazione, tipo_ora_registrazione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
							stmt = con.prepareStatement(updateString);
							stmt.setLong(1, idtraccia);
							JDBCUtilities.setSQLStringValue(stmt,2, origine);
							JDBCUtilities.setSQLStringValue(stmt,3, tipoorig);
							JDBCUtilities.setSQLStringValue(stmt,4, indtelorig);
							JDBCUtilities.setSQLStringValue(stmt,5, destinazione);
							JDBCUtilities.setSQLStringValue(stmt,6, tipod);
							JDBCUtilities.setSQLStringValue(stmt,7, indteld);
							if(oratrasm!=null){
								// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
								stmt.setTimestamp(8, new java.sql.Timestamp(oratrasm.getTime()-(2*60*60*1000)));
							}else
								stmt.setTimestamp(8,null);
							JDBCUtilities.setSQLStringValue(stmt,9, tempotrasm);
							JDBCUtilities.setSQLStringValue(stmt,10, val.toTipoOra(tempotrasm).toString());
							stmt.executeUpdate();
							stmt.close();
						}
					}

					ListaEccezioni listaEcc = intestazione.getListaEccezioni();
					if (listaEcc != null) {
						for (int i = 0; i < listaEcc.sizeEccezioneList(); i++) {
							Eccezione classEcc = listaEcc.getEccezione(i);
							String contcod = classEcc.getContestoCodifica();
							String codecc = classEcc.getCodiceEccezione();
							String rilevanza = classEcc.getRilevanza();
							String posizione = classEcc.getPosizione();

							//Inserimento nel DB
							String updateString = "INSERT INTO "+TracciamentoLog4JAppender.TRACCIAMENTO_LISTA_ECCEZIONI+" (idtraccia, contesto_codifica, codice_eccezione, rilevanza, posizione) VALUES (?, ?, ?, ?, ?)";
							stmt = con.prepareStatement(updateString);
							stmt.setLong(1, idtraccia);
							JDBCUtilities.setSQLStringValue(stmt,2, contcod);
							JDBCUtilities.setSQLStringValue(stmt,3, codecc);
							JDBCUtilities.setSQLStringValue(stmt,4, rilevanza);
							JDBCUtilities.setSQLStringValue(stmt,5, posizione);
							stmt.executeUpdate();
							stmt.close();
						}
					}
					
					con.commit();
					con.setAutoCommit(true);
				} catch (Exception ex) {
					//String tmpMess = ex.getMessage();
					//if (tmpMess.indexOf("tracce_singole_pkey") == -1) {
					log.error("[TracciamentoAppender] Gestione traccia: " + ex.getMessage(),ex);
					this.ps.println(singleTrace);
					//}
				}finally{
					try{
						if(rs!=null)
							rs.close();
					}catch(Exception e){}
					try{
						if(stmt!=null)
							stmt.close();
					}catch(Exception e){}
				}

				//Chiusura della connessione al DB
				try {
						con.close();
				} catch (java.sql.SQLException ex) {
					log.error("[TracciamentoAppender] Exception closing connection to DB "+ex.getMessage(),ex);
				}
			}
		}
		*/
	}

	@Override
	public void close() {
		/* ---- Chiusura file tracce.rejected ---- */
		this.ps.close();
	}
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}
	public void setTipoDatabase(String tipoDatabase) {
		this.tipoDatabase = tipoDatabase;
	}

}
