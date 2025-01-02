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

package org.openspcoop2.core.registry.driver;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.TipologiaServizio;

/**
 * IDServizioFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDServizioFactory {

	private static final String TIPO_NON_FORNITO = "Tipo non fornito";
	private static final String NOME_NON_FORNITO = "Nome non fornito";
	private static final String SOGGETTO_EROGATORE_NON_FORNITO = "Soggetto erogatore non fornito";
	private static final String TIPO_SOGGETTO_EROGATORE_NON_FORNITO = "Tipo soggetto erogatore non fornito";
	private static final String NOME_SOGGETTO_EROGATORE_NON_FORNITO = "Nome soggetto erogatore non fornito";
	private static final String SINTASSI_NON_CORRETTA = "sintassi non corretta, atteso tipoSoggetto/nomeSoggetto:tipoServizio/nomeServizio:versione";
	
	private static IDServizioFactory factory = null;
	private static synchronized void init(){
		if(IDServizioFactory.factory==null){
			IDServizioFactory.factory = new IDServizioFactory();
		}
	}
	public static IDServizioFactory getInstance(){
		if(IDServizioFactory.factory==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED'
			synchronized (IDServizioFactory.class) {
				IDServizioFactory.init();
			}
		}
		return IDServizioFactory.factory;
	}
	
	private IDServizioFactory() {}

	@SuppressWarnings("deprecation")
	private IDServizio build(String tipo,String nome,IDSoggetto soggettoErogatore,int versione){
		IDServizio idServizio = new IDServizio();
		idServizio.setTipo(tipo);
		idServizio.setNome(nome);
		idServizio.setSoggettoErogatore(soggettoErogatore);
		idServizio.setVersione(versione);
		return idServizio;
	}
	
	public String normalizeUri(String uri) throws DriverRegistroServiziException{
		// La uri può non contenere la versione, che invece nella 3.0 è obbligatoria.
		// Facendo la doppia conversione, viene aggiunta la versione di default
		IDServizio idServizio = this.getIDServizioFromUri(uri);
		return this.getUriFromIDServizio(idServizio);
	}
	
	public String getUriFromIDServizio(IDServizio idServizio) throws DriverRegistroServiziException{
		if(idServizio==null){
			throw new DriverRegistroServiziException("IDServizio non fornito");
		}
		if(idServizio.getTipo()==null){
			throw new DriverRegistroServiziException(TIPO_NON_FORNITO);
		}
		if(idServizio.getNome()==null){
			throw new DriverRegistroServiziException(NOME_NON_FORNITO);
		}
		IDSoggetto soggettoErogatore = idServizio.getSoggettoErogatore();
		if(soggettoErogatore==null){
			throw new DriverRegistroServiziException(SOGGETTO_EROGATORE_NON_FORNITO);
		}
		if(soggettoErogatore.getTipo()==null){
			throw new DriverRegistroServiziException(TIPO_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		if(soggettoErogatore.getNome()==null){
			throw new DriverRegistroServiziException(NOME_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		return soggettoErogatore.toString() + ":" + idServizio.getTipo()+"/"+idServizio.getNome() + ":" + idServizio.getVersione();
	}
	
	public String getUriFromAccordo(AccordoServizioParteSpecifica accordo)  throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDServizio idServizio = this.build(accordo.getTipo(),accordo.getNome(),
				BeanUtilities.getSoggettoErogatore(accordo),accordo.getVersione());
		return this.getUriFromIDServizio(idServizio);
	}
	
	public String getUriFromValues(String tipo, String nome,String tipoSoggettoErogatore,String nomeSoggettoErogatore,int ver)  throws DriverRegistroServiziException{
		if(tipo==null){
			throw new DriverRegistroServiziException(TIPO_NON_FORNITO);
		}
		if(nome==null){
			throw new DriverRegistroServiziException(NOME_NON_FORNITO);
		}
		if(tipoSoggettoErogatore==null){
			throw new DriverRegistroServiziException(TIPO_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		if(nomeSoggettoErogatore==null){
			throw new DriverRegistroServiziException(NOME_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		IDSoggetto soggettoErogatore = new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore);
		IDServizio idServizio = this.build(tipo,nome,soggettoErogatore,ver);
		return this.getUriFromIDServizio(idServizio);
	}
	
	public String getUriFromValues(String tipo, String nome,IDSoggetto soggettoErogatore,int ver)  throws DriverRegistroServiziException{
		if(soggettoErogatore==null){
			throw new DriverRegistroServiziException(SOGGETTO_EROGATORE_NON_FORNITO);
		}
		return getUriFromValues(tipo, nome, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), ver);
	}
	
	public IDServizio getIDServizioFromUri(String uriAccordo) throws DriverRegistroServiziException{
		try{
		
			// sintassi
			// tipoSoggetto/nomeSoggetto:tipoServizio/nomeServizio:versione
			
			if(uriAccordo==null){
				throw new DriverRegistroServiziException("Uri accordo non fornita");
			}
			int primoMarcatore = uriAccordo.indexOf(":");
			int secondoMarcatore = -1;
			if(primoMarcatore>=0){
				secondoMarcatore = uriAccordo.indexOf(":",primoMarcatore+1);
			}
			int terzoMarcatore = -1;
			if(secondoMarcatore>0){
				terzoMarcatore = uriAccordo.indexOf(":",secondoMarcatore+1);
				if(terzoMarcatore>0){
					throw new DriverRegistroServiziException(SINTASSI_NON_CORRETTA);
				}
			}
			
			if(primoMarcatore<0){
				throw new DriverRegistroServiziException(SINTASSI_NON_CORRETTA);
			}
			if(secondoMarcatore<=0){
				throw new DriverRegistroServiziException(SINTASSI_NON_CORRETTA);
			}
			
			String tmp1 = null; 
			String tmp2 = null;
			String tmp3 = null;
			tmp1 = uriAccordo.substring(0, primoMarcatore); //soggetto erogatore
			tmp2 = uriAccordo.substring((primoMarcatore+1), secondoMarcatore); // servizio
			tmp3 = uriAccordo.substring((secondoMarcatore+1),uriAccordo.length()); // versione
			
			int divisorioSoggettoErogatore = tmp1.indexOf("/");
			if(divisorioSoggettoErogatore<=0){
				throw new DriverRegistroServiziException("sintassi del soggetto erogatore non corretta, l'uri deve essere definita con la seguente forma: tipoSoggetto/nomeSoggetto:tipoServizio/nomeServizio:versione");
			}
			String tipoSoggettoErogatore = tmp1.substring(0,divisorioSoggettoErogatore);
			String nomeSoggettoErogatore = tmp1.substring((divisorioSoggettoErogatore+1),tmp1.length());
			IDSoggetto soggettoErogatore = new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore);
			
			int divisorioServizio = tmp2.indexOf("/");
			if(divisorioServizio<=0){
				throw new DriverRegistroServiziException("sintassi del servizio non corretta, l'uri deve essere definita con la seguente forma: tipoSoggetto/nomeSoggetto:tipoServizio/nomeServizio:versione");
			}
			String tipoServizio = tmp2.substring(0,divisorioServizio);
			String nomeServizio = tmp2.substring((divisorioServizio+1),tmp2.length());
			
			int versione = 1;
			try{
				versione = Integer.parseInt(tmp3);
			}catch(Exception e){
				throw new DriverRegistroServiziException("sintassi della versione non corretta: "+e.getMessage(),e);
			}
			
			return this.build(tipoServizio,nomeServizio,soggettoErogatore,versione);
	
		}catch(Exception e){
			throw new DriverRegistroServiziException("Parsing uriAccordo["+uriAccordo+"] non riusciuto: "+e.getMessage());
		}
	}
	
	public IDServizio getIDServizioFromAccordo(AccordoServizioParteSpecifica accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDServizio idServizio = this.build(accordo.getTipo(), accordo.getNome(),
				new IDSoggetto(accordo.getTipoSoggettoErogatore(),accordo.getNomeSoggettoErogatore()),
				accordo.getVersione());
		if(TipologiaServizio.CORRELATO.equals(accordo.getTipologiaServizio())){
			idServizio.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.CORRELATO);
		}
		else{
			idServizio.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.NORMALE);
		}
		idServizio.setUriAccordoServizioParteComune(accordo.getAccordoServizioParteComune());
		idServizio.setPortType(accordo.getPortType());
		return idServizio;
	}
	
	public IDServizio getIDServizioFromValues(String tipo,String nome,String tipoSoggettoErogatore,String nomeSoggettoErogatore,int ver) throws DriverRegistroServiziException{
		if(tipo==null){
			throw new DriverRegistroServiziException(TIPO_NON_FORNITO);
		}
		if(nome==null){
			throw new DriverRegistroServiziException(NOME_NON_FORNITO);
		}
		if(tipoSoggettoErogatore==null){
			throw new DriverRegistroServiziException(TIPO_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		if(nomeSoggettoErogatore==null){
			throw new DriverRegistroServiziException(NOME_SOGGETTO_EROGATORE_NON_FORNITO);
		}
		IDSoggetto soggettoErogatore = new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore);
		return this.build(tipo,nome,soggettoErogatore,ver);
	}
	public IDServizio getIDServizioFromValuesWithoutCheck(String tipo,String nome,String tipoSoggettoErogatore,String nomeSoggettoErogatore,int ver) {
		IDSoggetto soggettoErogatore = new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore);
		return this.build(tipo,nome,soggettoErogatore,ver);
	}
	public IDServizio getIDServizioFromValues(String tipo,String nome,IDSoggetto soggettoErogatore,int ver) throws DriverRegistroServiziException{
		if(soggettoErogatore==null){
			throw new DriverRegistroServiziException(SOGGETTO_EROGATORE_NON_FORNITO);
		}
		IDServizio idServ = this.getIDServizioFromValues(tipo, nome, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), ver);
		idServ.getSoggettoErogatore().setCodicePorta(soggettoErogatore.getCodicePorta());
		return idServ;
	}
	
	
}
