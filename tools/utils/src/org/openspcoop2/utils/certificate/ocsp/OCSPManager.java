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

package org.openspcoop2.utils.certificate.ocsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.slf4j.Logger;

/**
 * OCSPManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPManager {

	private static OCSPManager staticInstance;
	public static synchronized void init(File f, boolean throwNotExists, boolean loadDefault, Logger log) throws UtilsException {
		if(staticInstance==null) {
			staticInstance = new OCSPManager(f, throwNotExists, loadDefault, log);
		}
	}
	public static OCSPManager getInstance() {
		return staticInstance;
	}
	
	private HashMap<String, OCSPConfig> hsmOCSPConfigMapIDtoConfig = new HashMap<>();
	
	private HashMap<String, String> hsmOCSPConfigMapTypeToID = new HashMap<>();
	private HashMap<String, String> hsmOCSPConfigMapLabelToID = new HashMap<>();
	
	private SortedMap<String> hsmOCSPConfigSortedMapTypeLabel = new SortedMap<>();
	
	private OCSPManager(File f, boolean throwNotExists, boolean loadDefault, Logger log) throws UtilsException {
		if(!f.exists()) {
			if(throwNotExists) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"' not exists");
			}
			else {
				if(loadDefault) {
					try {
						try(InputStream isDefault = OCSPManager.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/default.properties")){
							if(isDefault!=null) {
								Properties p = new Properties();
								try {
									p.load(isDefault);
								}catch(Throwable t) {
									throw new UtilsException("File '"+f.getAbsolutePath()+"'; initialize error: "+t.getMessage(),t);
								}
								init(p, log);
							}
						}
					}catch(Throwable t) {
						throw new UtilsException("Default configuration; initialize error: "+t.getMessage(),t);
					}
				}
			}
		}
		else {
			if(!f.canRead()) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"' cannot read");
			}
			Properties p = new Properties();
			try {
				try(FileInputStream fin = new FileInputStream(f)){
					p.load(fin);
				}
			}catch(Throwable t) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"'; initialize error: "+t.getMessage(),t);
			}
			init(p, log);
		}
	}
	private OCSPManager(Properties p, Logger log) throws UtilsException {
		init(p, log);
	}
	private void init(Properties p, Logger log) throws UtilsException {
		
		List<String> idKeystore = new ArrayList<>();
		
		if(p!=null && !p.isEmpty()) {
			
			Enumeration<?> enKeys = p.keys();
			while (enKeys.hasMoreElements()) {
				Object object = (Object) enKeys.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					
					if(key.startsWith(OCSPCostanti.PROPERTY_PREFIX) && key.length()>(OCSPCostanti.PROPERTY_PREFIX.length())) {
						String tmp = key.substring(OCSPCostanti.PROPERTY_PREFIX.length());
						if(tmp!=null && tmp.contains(".")) {
							int indeoOf = tmp.indexOf(".");
							if(indeoOf>0) {
								String idK = tmp.substring(0,indeoOf);
								if(!idKeystore.contains(idK)) {
									idKeystore.add(idK);
								}
							}
						}
					}
				}
			}
			
		}
		
		if(!idKeystore.isEmpty()) {
			
			List<String> types = new ArrayList<>();
			List<String> labels = new ArrayList<>();
			
			for (String idK : idKeystore) {
				String prefix = OCSPCostanti.PROPERTY_PREFIX + idK + ".";
				PropertiesReader pReader = new PropertiesReader(p, true);
				Properties pKeystore = pReader.readProperties_convertEnvProperties(prefix);
				OCSPConfig ocspConfig = new OCSPConfig(idK, pKeystore, log);
				
				// type (value è id del ocsp.properties)
				boolean alreadyExists = false;
				for (String type : this.hsmOCSPConfigMapTypeToID.keySet()) {
					if(ocspConfig.getType().equalsIgnoreCase(type)) {
						alreadyExists = true;
					}
				}
				if(alreadyExists) {
					throw new UtilsException("Same ocsp type found for responder '"+this.hsmOCSPConfigMapTypeToID.get(ocspConfig.getType())+"' e '"+idK+"'");
				}
				this.hsmOCSPConfigMapTypeToID.put(ocspConfig.getType(), idK);
				
				// label (value è id del ocsp.properties)
				alreadyExists = false;
				for (String label : this.hsmOCSPConfigMapLabelToID.keySet()) {
					if(ocspConfig.getLabel().equalsIgnoreCase(label)) {
						alreadyExists = true;
					}
				}
				if(alreadyExists) {
					throw new UtilsException("Same ocsp label found for responder '"+this.hsmOCSPConfigMapLabelToID.get(ocspConfig.getLabel())+"' e '"+idK+"'");
				}
				this.hsmOCSPConfigMapLabelToID.put(ocspConfig.getLabel(), idK);
				
				// id di ocsp.properties to config
				this.hsmOCSPConfigMapIDtoConfig.put(idK, ocspConfig);
								
				log.info("OCSP config "+idK+" registrato (OCSP Type:"+ocspConfig.getType()+")");
				
				types.add(ocspConfig.getType());
				labels.add(ocspConfig.getLabel());
			}
			
			// SortedMap by label
			Map<String, String> m = new HashMap<>();
			for (int i = 0; i < types.size(); i++) {
				String type = types.get(i);
				String label = labels.get(i);
				m.put(label, type);
			}
			Collections.sort(labels);
			for (String l : labels) {
				this.hsmOCSPConfigSortedMapTypeLabel.add(m.get(l), l);	
			}
		}
		else {
			log.warn("La configurazione fornita per HSM non contiene alcun keystore");
		}
	}
	
	public OCSPConfig getOCSPConfig(String ocspConfigType) throws UtilsException {
		if(!this.hsmOCSPConfigMapTypeToID.containsKey(ocspConfigType)) {
			throw new UtilsException("OCSP config type '"+ocspConfigType+"' unknown");
		}
		String idK = this.hsmOCSPConfigMapTypeToID.get(ocspConfigType);
		if(!this.hsmOCSPConfigMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("OCSP config type '"+ocspConfigType+"' unknown ? (id:"+idK+")");
		}
		OCSPConfig config = this.hsmOCSPConfigMapIDtoConfig.get(idK);
		return config;
	}
	

	public List<String> getOCSPConfigTypes() {
		List<String> l = new ArrayList<>();
		if(!this.hsmOCSPConfigMapTypeToID.isEmpty()) {
			for (String type : this.hsmOCSPConfigMapTypeToID.keySet()) {
				l.add(type);
			}
		}
		return l;
	}
	public List<String> getOCSPConfigLabels() {
		List<String> l = new ArrayList<>();
		if(!this.hsmOCSPConfigMapLabelToID.isEmpty()) {
			for (String label : this.hsmOCSPConfigMapLabelToID.keySet()) {
				l.add(label);
			}
		}
		return l;
	}
	public SortedMap<String> getOCSPConfigTypesLabels() {
		return this.hsmOCSPConfigSortedMapTypeLabel;
	}
	
	public boolean existsOCSPConfig(String ocspConfigType) {
		if(ocspConfigType==null) {
			return false;
		}
		for (String type : this.hsmOCSPConfigMapTypeToID.keySet()) {
			if(ocspConfigType.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
}
