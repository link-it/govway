/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.transazioni.mbean;

import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;

/****
 * 
 * Bean per che incapsula un oggetto di tipo Busta, per la visualizzazione nella pagina web. 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BustaBean extends Busta {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IProtocolFactory<?> protocolFactory;
	private transient ITraduttore traduttore;

	private List<EccezioneBean> listaEccezioniBean;

	private List<RiscontroBean> listaRiscontriBean;

	private List<TrasmissioneBean> listaTrasmissioniBean;

	public BustaBean(String protocollo) {
		super(protocollo);
	}

	public BustaBean(Busta busta, IProtocolFactory<?> protocolFactory) {
		super(busta.getProtocollo());
		this.protocolFactory = protocolFactory;

		if(protocolFactory==null){
			throw new RuntimeException("protocolFactory is null (verificare che sia presente il jar del protocollo "+busta.getProtocollo()+")");
		}
		
		try {
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch (ProtocolException e) {
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi
				.add(new BlackListElement("setListaEccezioni", List.class));
		metodiEsclusi
				.add(new BlackListElement("setListaRiscontri", List.class));
		metodiEsclusi.add(new BlackListElement("setListaTrasmissioni",
				List.class));
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));
		metodiEsclusi.add(new BlackListElement("setTipoOraRegistrazioneValue",
				String.class));
		metodiEsclusi.add(new BlackListElement(
				"setProfiloDiCollaborazioneValue", String.class));
		metodiEsclusi.add(new BlackListElement("setVersioneServizio",
				String.class));

		BeanUtils.copy(this, busta, metodiEsclusi);

		this.setTipoOraRegistrazione(busta.getTipoOraRegistrazione(), busta.getTipoOraRegistrazioneValue());
		this.setProfiloDiCollaborazione(busta.getProfiloDiCollaborazione(), busta.getProfiloDiCollaborazioneValue());
		this.setInoltro(busta.getInoltro(), busta.getInoltroValue());
		
		
		
		this.listaEccezioniBean = new ArrayList<EccezioneBean>(0);
		// mapping delle eccezioni
		for (Eccezione eccezione : busta.getListaEccezioni()) {
			EccezioneBean eccezioneBean = new EccezioneBean(eccezione,
					protocolFactory);
			this.listaEccezioniBean.add(eccezioneBean);
		}

		this.listaRiscontriBean = new ArrayList<RiscontroBean>(0);
		for (Riscontro riscontro : busta.getListaRiscontri()) {
			RiscontroBean riscontroBean = new RiscontroBean(riscontro,
					protocolFactory);
			this.listaRiscontriBean.add(riscontroBean);
		}

		this.listaTrasmissioniBean = new ArrayList<TrasmissioneBean>(0);
		for (Trasmissione trasmissione : busta.getListaTrasmissioni()) {
			TrasmissioneBean trasmissioneBean = new TrasmissioneBean(
					trasmissione, protocolFactory);
			this.listaTrasmissioniBean.add(trasmissioneBean);
		}
	}

	@Override
	public String getTipoOraRegistrazioneValue() {
		if(super.getTipoOraRegistrazioneValue()!=null){
			return super.getTipoOraRegistrazioneValue();
		}
		else if (this.traduttore != null) {
			if(this.getTipoOraRegistrazione()!=null){
				return this.traduttore.toString(this.getTipoOraRegistrazione());
			}
		}
		return null;
	}

	public String getTipoOraRegistrazioneRicavato() {
		if(super.getTipoOraRegistrazioneValue()!=null){
			return super.getTipoOraRegistrazioneValue();
		} else if(this.getTipoOraRegistrazione()!=null){
			return this.getTipoOraRegistrazione().getEngineValue();
		} else 
			return null;
	}

	public String getInoltroRicavato() {
		if(super.getInoltroValue()!=null){
			return super.getInoltroValue();
		} else if(this.getInoltro()!=null){
			return this.getInoltro().getEngineValue();
		} else 
			return null;
	}

	public String getProfiloDiCollaborazioneRicavato() {
		if(super.getProfiloDiCollaborazioneValue()!=null){
			return super.getProfiloDiCollaborazioneValue();
		} else if(this.getProfiloDiCollaborazione()!=null){
			return this.getProfiloDiCollaborazione().getEngineValue();
		} else 
			return null;
	}
	
	@Override
	public String getProfiloDiCollaborazioneValue() {
		if(super.getProfiloDiCollaborazioneValue()!=null){
			return super.getProfiloDiCollaborazioneValue();
		}
		else if (this.traduttore != null) {
			if(this.getProfiloDiCollaborazione()!=null){
				return this.traduttore.toString(this.getProfiloDiCollaborazione());
			}
		}
		return null;
	}

	public List<EccezioneBean> getListaEccezioniBean() {
		return this.listaEccezioniBean;
	}

	public List<RiscontroBean> getListaRiscontriBean() {
		return this.listaRiscontriBean;
	}

	public List<TrasmissioneBean> getListaTrasmissioniBean() {
		return this.listaTrasmissioniBean;
	}

//	@Override
	public Hashtable<String, String> getProperties(){
		Hashtable<String, String> map = new Hashtable<String, String>();
		
		java.util.ArrayList<String> listKeys = new ArrayList<String>();
		String [] propertiesNames = super.getPropertiesNames();
		if(propertiesNames!=null){
			for (int i = 0; i < propertiesNames.length; i++) {
				listKeys.add(propertiesNames[i]);
			}
		}
		java.util.Collections.sort(listKeys);
		for (String key : listKeys) {
			String value = super.getProperty(key);
			if(key!=null && value!=null){
				map.put(key, value);
			}
		}
		
		return map;
	}
	
//	@Override
	public List<Map.Entry<String, String>> getPropertiesAsList(){
		
		List <Map.Entry<String, String>> toRet = new ArrayList<Map.Entry<String, String>>();
			
		if(super.sizeProperties()>0){
			java.util.ArrayList<String> keys = new ArrayList<String>();
			String [] pNames = super.getPropertiesNames();
			if(pNames!=null && pNames.length>0){
				for (int i = 0; i < pNames.length; i++) {
					keys.add(pNames[i]);
				}
			}
			
			java.util.Collections.sort(keys);
			for (String key : keys) {
				Map.Entry<String, String> entry = new MapEntry<String, String>(key, super.getProperty(key));
				toRet.add(entry);
			}
		}
		
		return toRet;
	}
}

final class MapEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
