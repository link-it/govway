/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.lib.queue.costanti.Operazione;

/**
 * OperazioniDaSmistare
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class OperazioneDaSmistare implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// public static final String CREAZIONE = "add";
	// public static final String MODIFICA = "change";
	// public static final String ELIMINAZIONE = "del";

	private long idTable;
	private Operazione operazione;
	private String pdd;
	private String superuser;
	private TipoOggettoDaSmistare oggetto;

	private Map<OperationsParameter, List<String>> params;

	public OperazioneDaSmistare() {
		this.params = new HashMap<OperationsParameter, List<String>>();

	}

	/**
	 * Aggiunge un parametro all'operazione da smistare
	 * 
	 * @param key
	 *            Il nome(key) del parametro
	 * @param value
	 *            Il valore del parametro
	 */
	public void addParameter(OperationsParameter key, String value) {
		// Se esiste gia' una chiave nella tabella
		// allora aggiungo il parametro alla lista di parametri
		// altrimenti creo una nuova lista con associata alla chiave
		List<String> v = null;
		if (this.params.containsKey(key)) {
			v = this.params.get(key);
			v.add(value);

		} else {
			v = new ArrayList<>();
			v.add(value);

		}

		this.params.put(key, v);

	}

	/**
	 * Ritorna la lista di parametri associati alla key
	 * 
	 * @param key
	 *            La chiave di ricerca
	 * @return I valori associati alla key, null altrimenti.
	 */
	public List<String> getParameter(OperationsParameter key) {
		return this.params.get(key);
	}

	public Map<OperationsParameter, List<String>> getParameters() {
		return new HashMap<OperationsParameter, List<String>>(this.params);
	}

	public void setIDTable(long id) {
		this.idTable = id;
	}

	public long getIDTable() {
		return this.idTable;
	}

	public void setOperazione(Operazione op) {
		this.operazione = op;
	}

	public Operazione getOperazione() {
		return this.operazione;
	}

	public void setPdd(String n) {
		this.pdd = n;
	}

	public String getPdd() {
		return this.pdd;
	}

	public void setSuperuser(String s) {
		this.superuser = s;
	}

	public String getSuperuser() {
		return this.superuser;
	}

	public void setOggetto(TipoOggettoDaSmistare obj) {
		this.oggetto = obj;
	}

	public TipoOggettoDaSmistare getOggetto() {
		return this.oggetto;
	}
}

// // private String tipoSoggetto;
// // private String nomeSoggetto;
// // private String oldTipoSoggetto;
// // private String oldNomeSoggetto;
// //
// // private String tipoServizio;
// // private String nomeServizio;
// // private String oldTipoServizio;
// // private String oldNomeServizio;
// // private String nomePA;
// // private String nomePD;
// // private String nomeServizioApplicativo;
// // private String nomeAccordo;
// // private String oldNomeAccordo;
// // private String[] idPoliticheSicurezza;
// // private int fruitore;
// // private String nomeFruitore;
// // private String tipoFruitore;
// // private ArrayList<String> listaServiziApplicativi;
// // private String oldNomePD;
//
// public ArrayList<String> getListaServiziApplicativi()
// {
// return listaServiziApplicativi;
// }
//
// public void setListaServiziApplicativi(ArrayList<String>
// listaServiziApplicativi)
// {
// this.listaServiziApplicativi = listaServiziApplicativi;
// }
//
// public String getNomeFruitore()
// {
// return nomeFruitore;
// }
//
// public void setNomeFruitore(String nomeFruitore)
// {
// this.nomeFruitore = nomeFruitore;
// }
//
// public String getTipoFruitore()
// {
// return tipoFruitore;
// }
//
// public void setTipoFruitore(String tipoFruitore)
// {
// this.tipoFruitore = tipoFruitore;
// }
//
// public int getFruitore()
// {
// return fruitore;
// }
//
// public void setFruitore(int fruitore)
// {
// this.fruitore = fruitore;
// }
//
// public String[] getIdPoliticheSicurezza()
// {
// return idPoliticheSicurezza;
// }
//
// public void setIdPoliticheSicurezza(String[] idPoliticheSicurezza)
// {
// this.idPoliticheSicurezza = idPoliticheSicurezza;
// }
// public void setTipoSoggetto(String s) {
// tipoSoggetto = s;
// }
// public String getTipoSoggetto() {
// return tipoSoggetto;
// }
//
// public void setNomeSoggetto(String s) {
// nomeSoggetto = s;
// }
// public String getNomeSoggetto() {
// return nomeSoggetto;
// }
//
// public void setTipoServizio(String t) {
// tipoServizio = t;
// }
// public String getTipoServizio() {
// return tipoServizio;
// }
//
// public void setNomeServizio(String n) {
// nomeServizio = n;
// }
// public String getNomeServizio() {
// return nomeServizio;
// }
//
// public void setNomePA(String npa) {
// nomePA = npa;
// }
// public String getNomePA() {
// return nomePA;
// }
//
// public void setNomePD(String npd) {
// nomePD = npd;
// }
// public String getNomePD() {
// return nomePD;
// }
//
// public void setNomeServizioApplicativo(String ns) {
// nomeServizioApplicativo = ns;
// }
// public String getNomeServizioApplicativo() {
// return nomeServizioApplicativo;
// }
//
// public void setNomeAccordo(String na) {
// nomeAccordo = na;
// }
// public String getNomeAccordo() {
// return nomeAccordo;
// }
//
// public String getOldNomeAccordo()
// {
// return oldNomeAccordo;
// }
//
// public void setOldNomeAccordo(String oldNomeAccordo)
// {
// this.oldNomeAccordo = oldNomeAccordo;
// }
//
// public String getOldNomeServizio()
// {
// return oldNomeServizio;
// }
//
// public void setOldNomeServizio(String oldNomeServizio)
// {
// this.oldNomeServizio = oldNomeServizio;
// }
//
// public String getOldNomeSoggetto()
// {
// return oldNomeSoggetto;
// }
//
// public void setOldNomeSoggetto(String oldNomeSoggetto)
// {
// this.oldNomeSoggetto = oldNomeSoggetto;
// }
//
// public String getOldTipoServizio()
// {
// return oldTipoServizio;
// }
//
// public void setOldTipoServizio(String oldTipoServizio)
// {
// this.oldTipoServizio = oldTipoServizio;
// }
//
// public String getOldTipoSoggetto()
// {
// return oldTipoSoggetto;
// }
//
// public void setOldTipoSoggetto(String oldTipoSoggetto)
// {
// this.oldTipoSoggetto = oldTipoSoggetto;
// }
//
// public String getOldNomePD() {
// return this.oldNomePD;
// }
//
// public void setOldNomePD(String oldNomePD) {
// this.oldNomePD = oldNomePD;
//
// }

