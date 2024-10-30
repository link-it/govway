package org.openspcoop2.utils.transport.ldap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

/**
 * Classe utility per ldap
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @version $Rev$, $Date$
 *
 */
public class LdapUtility {
	
	private LdapUtility() {}
	
	public static LdapName getBaseFromURI(URI uri) throws InvalidNameException {
		String[] paths = uri.getPath().split("/");
		String base = paths.length > 0 ? paths[paths.length - 1] : "";
		return new LdapName(base);
	}
	
	public static String[] getAttributesFromURI(URI uri) {
		String[] params = uri.getQuery().split("\\?");
		for (String param : params)
			if (!param.isEmpty() && param.charAt(0) != '(')
				return param.split(",");
		return new String[0];
	}
	
	public static URI getBaseUrlFromURI(URI uri) throws URISyntaxException {
		String scheme = Objects.requireNonNullElse(uri.getScheme(), "ldap");
		int port = Objects.requireNonNullElse(uri.getPort(), scheme.equals("ldaps") ? 636 : 389);
		return new URI(scheme + "://" + uri.getHost() + ":" + port);
	}
	
	public static LdapFilter getFilterFromURI(URI uri) throws ParseException {
		String[] params = uri.getQuery().split("\\?");
		for (String param : params)
			if (!param.isEmpty() && param.charAt(0) == '(')
				return LdapFilter.parse(param);
		return LdapFilter.isPresent("cn");
	}
	
	public static LdapQuery getQueryFromURI(URI uri) throws InvalidNameException, ParseException {
		return new LdapQuery()
				.attributes(LdapUtility.getAttributesFromURI(uri))
				.filter(LdapUtility.getFilterFromURI(uri))
				.base(LdapUtility.getBaseFromURI(uri));
	}
	
	public static URI getURIFromQuery(URI baseUrl, LdapQuery query) throws URISyntaxException {
		StringBuilder url = new StringBuilder(getBaseUrlFromURI(baseUrl).toString());
		if (query.getBase() != null)
			url.append("/").append(URLEncoder.encode(query.getBase().toString(), StandardCharsets.UTF_8));
		if (query.getAttributes() != null && !query.getAttributes().isEmpty()) {
			url.append("?");
			List<String> encodeAttr = new ArrayList<>();
			for (String attr : query.getAttributes())
				encodeAttr.add(URLEncoder.encode(attr, StandardCharsets.UTF_8));
			url.append(String.join(",", encodeAttr));
		}
		if (query.getFilter() != null && !query.getFilter().isAbsoluteTrueFilter())
			url.append("?").append(URLEncoder.encode(query.getFilter().toString(), StandardCharsets.UTF_8));
		return new URI(url.toString());
	}
}
