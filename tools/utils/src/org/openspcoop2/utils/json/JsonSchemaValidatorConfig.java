/**
 * 
 */
package org.openspcoop2.utils.json;

import java.util.List;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 26 ott 2017 $
 * 
 */
public class JsonSchemaValidatorConfig {

	public enum ADDITIONAL {DEFAULT, IF_NULL_DISABLE, IF_NULL_STRING, FORCE_DISABLE, FORCE_STRING }
	
	public enum POLITICA_INCLUSIONE_TIPI {DEFAULT, ANY, ALL}
	
	private ADDITIONAL additionalProperties = ADDITIONAL.DEFAULT;

	private POLITICA_INCLUSIONE_TIPI politicaInclusioneTipi = POLITICA_INCLUSIONE_TIPI.DEFAULT;
	
	private List<String> tipi;

	public ADDITIONAL getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperties(ADDITIONAL defaultAdditionalProperties) {
		this.additionalProperties = defaultAdditionalProperties;
	}

	public POLITICA_INCLUSIONE_TIPI getPoliticaInclusioneTipi() {
		return this.politicaInclusioneTipi;
	}

	public void setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI politica) {
		this.politicaInclusioneTipi = politica;
	}

	public List<String> getTipi() {
		return this.tipi;
	}

	public void setTipi(List<String> tipi) {
		this.tipi = tipi;
	}
	
}
