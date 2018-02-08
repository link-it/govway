/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.plugins.servlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettoreConverter;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettoreItem;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**     
 * ServletExtendedConnettoreUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletExtendedConnettoreUtils {

	public static List<ExtendedConnettore> getExtendedConnettore(org.openspcoop2.core.config.Connettore connettore,
			ConnettoreServletType servletType, ConsoleHelper consoleHelper,
			boolean first,String endpointType) throws ExtendedException{
		return getExtendedConnettore(connettore, servletType, consoleHelper, null, first, endpointType);
	}
	public static List<ExtendedConnettore> getExtendedConnettore(org.openspcoop2.core.config.Connettore connettore,
			ConnettoreServletType servletType, ConsoleHelper consoleHelper, Properties parametersPOST,
			boolean first,String endpointType) throws ExtendedException{
		
		boolean connettoreDisabilitato = true;
		String tipoConnettore = TipiConnettore.DISABILITATO.getNome();
		if(ConnettoreServletType.SERVIZIO_APPLICATIVO_ADD.equals(servletType)){
			// default diverso
			connettoreDisabilitato = false;
			tipoConnettore = TipiConnettore.HTTP.getNome();
		}
		if(first){
			if(connettore!=null){
				connettoreDisabilitato = TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo());
				tipoConnettore = connettore.getTipo();
			}
		}
		else{
			connettoreDisabilitato = TipiConnettore.DISABILITATO.getNome().equals(endpointType);
			tipoConnettore = endpointType;
		}
		
		List<ExtendedConnettore> l = getExtendedConnettore(servletType, consoleHelper, connettoreDisabilitato, tipoConnettore);
		if(l!=null && l.size()>0){
			if(first)
				ExtendedConnettoreConverter.readExtendedInfoFromConnettore(l, connettore);
			else 
				updateInfo(consoleHelper, parametersPOST, l);
		}
		return l;
		
	}
	
	public static List<ExtendedConnettore> getExtendedConnettore(org.openspcoop2.core.registry.Connettore connettore,
			ConnettoreServletType servletType, ConsoleHelper consoleHelper, 
			boolean first,String endpointType) throws Exception{
		return getExtendedConnettore(connettore, servletType, consoleHelper, null, first, endpointType);
	}
	public static List<ExtendedConnettore> getExtendedConnettore(org.openspcoop2.core.registry.Connettore connettore,
			ConnettoreServletType servletType, ConsoleHelper consoleHelper, Properties parametersPOST, 
			boolean first,String endpointType) throws Exception{
		
		boolean connettoreDisabilitato = true;
		String tipoConnettore = TipiConnettore.DISABILITATO.getNome();
		if(first){
			if(connettore!=null){
				connettoreDisabilitato = TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo());
				tipoConnettore = connettore.getTipo();
			}
		}
		else{
			connettoreDisabilitato = TipiConnettore.DISABILITATO.getNome().equals(endpointType);
			tipoConnettore = endpointType;
		}
					
		List<ExtendedConnettore> l = getExtendedConnettore(servletType, consoleHelper, connettoreDisabilitato, tipoConnettore);
		if(l!=null && l.size()>0){
			if(first)
				ExtendedConnettoreConverter.readExtendedInfoFromConnettore(l, connettore);
			else
				updateInfo(consoleHelper, parametersPOST, l);
		}
		return l;
		
	}
	private static List<ExtendedConnettore> getExtendedConnettore(ConnettoreServletType servletType, ConsoleHelper consoleHelper,
			boolean connettoreDisabilitato, String tipoConnettore) throws ExtendedException{
		List<ExtendedConnettore> list = new ArrayList<ExtendedConnettore>();
		if(consoleHelper.getCore().getExtendedConnettore()!=null && consoleHelper.getCore().getExtendedConnettore().size()>0){
		
			boolean interfacciaAvanzata =consoleHelper.isModalitaAvanzata();
			
			for (IExtendedConnettore ext : consoleHelper.getCore().getExtendedConnettore()) {
				
				List<ExtendedConnettore> l = ext.getExtendedConnettore(servletType,interfacciaAvanzata,connettoreDisabilitato,tipoConnettore);
				if(l!=null && l.size()>0){
					list.addAll(l);
				}

			}
		}
		return list;
	}
	
	public static void addToDatiEnabled(Vector<DataElement> dati,List<ExtendedConnettore> list){
		
		if(list!=null){
			for (ExtendedConnettore extendedConnettore : list) {
		
				DataElement de = new DataElement();
				de.setName(extendedConnettore.getId());
				de.setValue(extendedConnettore.isEnabled()+"");
				de.setSelected(extendedConnettore.isEnabled());
				de.setLabel(extendedConnettore.getLabel());
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true);
				dati.add(de);
				
				de = new DataElement();
				de.setName(extendedConnettore.getIdForOldValue());
				de.setValue(extendedConnettore.isEnabled()+"");
				de.setType(DataElementType.HIDDEN);
				dati.add(de);

			}
		}
		
	}
	
	public static void addToDatiExtendedInfo(Vector<DataElement> dati,List<ExtendedConnettore> list){
		
		if(list!=null){
			for (ExtendedConnettore extendedConnettore : list) {
				
				if(extendedConnettore.isEnabled()){
				
					DataElement de = new DataElement();
					de.setName("ExtendedSection-"+extendedConnettore.getId());
					de.setLabel(extendedConnettore.getSection());
					de.setValue(extendedConnettore.getSection()+"");
					de.setType(DataElementType.TITLE);
					dati.add(de);
									
					if(extendedConnettore.getListItem()!=null && extendedConnettore.getListItem().size()>0){
						for (ExtendedConnettoreItem extendedConnettoreItem : extendedConnettore.getListItem()) {
							
							String id = ExtendedConnettoreConverter.buildId(extendedConnettore.getId(), extendedConnettoreItem.getId());
							
							de = new DataElement();
							de.setName(id);
							de.setLabel(extendedConnettoreItem.getLabel());
							de.setNote(extendedConnettoreItem.getNote());
							de.setValue(extendedConnettoreItem.getValue());
							de.setRequired(extendedConnettoreItem.isRequired());
							de.setType(DataElementType.TEXT_EDIT);
							dati.add(de);
							
						}
					}
					
				}
				
			}
		}
		
	}
	
	public static void updateInfo(ConsoleHelper consoleHelper,Properties parametersPOST, List<ExtendedConnettore> list) throws ExtendedException{
		try {
			if(list!=null){
				for (ExtendedConnettore extendedConnettore : list) {
					
					String tmp = consoleHelper.getParameter(extendedConnettore.getId());
					if(parametersPOST!=null && parametersPOST.size()>0){
						tmp = getValueFromPropertiesPOST(parametersPOST, extendedConnettore.getId());
					}
					extendedConnettore.setEnabled(ServletUtils.isCheckBoxEnabled(tmp));
					
					String tmpOldValue = consoleHelper.getParameter(extendedConnettore.getIdForOldValue());
					if(parametersPOST!=null && parametersPOST.size()>0){
						tmpOldValue = getValueFromPropertiesPOST(parametersPOST, extendedConnettore.getIdForOldValue());
					}
					boolean oldValue = ServletUtils.isCheckBoxEnabled(tmpOldValue);
					boolean changeRidefinizione = oldValue != extendedConnettore.isEnabled(); 
					
					if(extendedConnettore.isEnabled() && !changeRidefinizione){ // altrimenti devo mantenere i valori di default
						if(extendedConnettore.getListItem()!=null && extendedConnettore.getListItem().size()>0){
							for (ExtendedConnettoreItem extendedConnettoreItem : extendedConnettore.getListItem()) {
						
								String id = ExtendedConnettoreConverter.buildId(extendedConnettore.getId(), extendedConnettoreItem.getId());
								tmp = consoleHelper.getParameter(id);
								if(parametersPOST!=null && parametersPOST.size()>0){
									tmp = getValueFromPropertiesPOST(parametersPOST, id);
								}
								extendedConnettoreItem.setValue(tmp);
															
							}
						}
					}
					
				}
			}
		}catch(Exception e) {
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
	private static String getValueFromPropertiesPOST(Properties parametersPOST, String idParameter){
		if(parametersPOST!=null && parametersPOST.size()>0){
			Enumeration<?> enKeys = parametersPOST.keys();
			while (enKeys.hasMoreElements()) {
				Object object = (Object) enKeys.nextElement();
				if(object instanceof String){
					String s = (String) object;
					if (s.indexOf("\""+idParameter+"\"") != -1) {
						return parametersPOST.getProperty(s);
					}
				}
			}
		}
		return null;
	}
	
	public static void checkInfo(List<ExtendedConnettore> list) throws ExtendedException{
		if(list!=null){
			for (ExtendedConnettore extendedConnettore : list) {
				
				if(extendedConnettore.isEnabled()){
					if(extendedConnettore.getListItem()!=null && extendedConnettore.getListItem().size()>0){
						for (ExtendedConnettoreItem extendedConnettoreItem : extendedConnettore.getListItem()) {
					
							if(extendedConnettoreItem.getValue()==null || "".equals(extendedConnettoreItem.getValue())){
								if(extendedConnettoreItem.isRequired()){
									throw new ExtendedException("Deve essere indicato un valore in '"+extendedConnettoreItem.getLabel()+"'");
								}
							}
							else{
								if(extendedConnettoreItem.getRegularExpression()!=null){
									try{
										if(!RegularExpressionEngine.isMatch(extendedConnettoreItem.getValue(),extendedConnettoreItem.getRegularExpression())){
											throw new ExtendedException("Il valore indicato in '"+extendedConnettoreItem.getLabel()+"' non è valido");
										}
									}catch(Exception e){
										throw new ExtendedException(e.getMessage(),e);
									}
								}
							}
							
						}
					}
				}
				
			}
		}
	}
	
}
