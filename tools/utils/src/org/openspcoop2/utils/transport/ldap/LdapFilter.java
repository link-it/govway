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

package org.openspcoop2.utils.transport.ldap;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * classe che implementa i filtri di ricerca
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @version $Rev$, $Date$
 *
 */
public class LdapFilter {

	private String key = null;
	private String value = null;
	private List<LdapFilter> subFilters = null;
	private FilterType type = null;
	
	private enum FilterType {
		AND("&"), OR("|"), NOT("!"), EQUALS("="), PRESENCE("=*"), GTE(">="), LTE("<="), TRUE(null), FALSE(null);
		
		private String operator;
		private FilterType(String op) {
			this.operator = op;
		}
		
		@Override
		public String toString() {
			return this.operator;
		}
		
		public static FilterType fromString(String str) {
			FilterType[] types = FilterType.values();
			for (FilterType type : types)
				if (type.toString().equals(str))
					return type;
			return null;
		}
	}

	private LdapFilter() {}
	
	private LdapFilter(FilterType type) {
		this.type = type;
	}
	
	private LdapFilter(FilterType type, String key, String value) {
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	private LdapFilter(FilterType type, List<LdapFilter> subFilters) {
		this.subFilters = subFilters;
		this.type = type;
	}
	
	private String getOperator() {
		return this.type.toString();
	}
	
	private StringBuilder buildString(StringBuilder ret) {
		switch (this.type) {
		case AND:
		case OR:
		case NOT:
			ret.append("(").append(this.getOperator());
			for (LdapFilter subFilter : this.subFilters) {
				subFilter.buildString(ret);
			}
			ret.append(")");
			break;
		case EQUALS:
		case PRESENCE:
		case GTE:
		case LTE:
			ret.append("(").append(this.key).append(this.getOperator()).append(this.value).append(")");
			break;
		case TRUE:
			ret.append("(&)");
			break;
		case FALSE:
			ret.append("(|)");
			break;
		}
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		return this.buildString(ret).toString();
	}
	
	// operatori
	public static LdapFilter and(LdapFilter ...filters) {
		return new LdapFilter(FilterType.AND, List.of(filters));
	}
	
	public boolean isAndFilter() {
		return this.type.equals(FilterType.AND);
	}
	
	public static LdapFilter or(LdapFilter ...filters) {
		return new LdapFilter(FilterType.OR, List.of(filters));
	}
	
	public boolean isOrFilter() {
		return this.type.equals(FilterType.OR);
	}
	
	public static LdapFilter not(LdapFilter filter) {
		return new LdapFilter(FilterType.NOT, List.of(filter));
	}
	
	public boolean isNotFilter() {
		return this.type.equals(FilterType.NOT);
	}
	
	public static LdapFilter absoluteTrue() {
		return new LdapFilter(FilterType.TRUE);
	}
	
	public boolean isAbsoluteTrueFilter() {
		return this.type.equals(FilterType.TRUE);
	}
	
	public static LdapFilter absoluteFalse() {
		return new LdapFilter(FilterType.FALSE);
	}
	
	public boolean isAbsoluteFalseFilter() {
		return this.type.equals(FilterType.FALSE);
	}
	
	// condizioni di base
	public static LdapFilter isPresent(String attribute) {
		return new LdapFilter(FilterType.PRESENCE, attribute, "");
	}

	public static LdapFilter isEqual(String attribute, String value) {
		return new LdapFilter(FilterType.EQUALS, attribute, value);
	}
	
	public static LdapFilter isAbsent(String attribute) {
		return LdapFilter.not(LdapFilter.isPresent(attribute));
	}
	
	public static LdapFilter isGreaterEqual(String attribute, String value) {
		return new LdapFilter(FilterType.GTE, attribute, value);
	}
	
	public static LdapFilter isLessEqual(String attribute, String value) {
		return new LdapFilter(FilterType.LTE, attribute, value);
	}
	
	// parse del filtro da una stringa
	private static final Pattern operatorsPatern = Pattern.compile("(=|<=|>=|\\&|\\|)");
	private static LdapFilter parseCondition(String raw, int start, int end) throws ParseException {
		
		if (raw.charAt(start) != '(')
			throw new ParseException("la condizione non inizia con una parentesi", start);
		if (raw.charAt(end - 1) != ')')
			throw new ParseException("la condizione non finisce con una parentesi", end - 1);
		
		Matcher matcher = operatorsPatern.matcher(raw).region(start, end);
		if (!matcher.find())
			throw new ParseException("operatore non presente o non riconusciuto operatori possibili: =,<=,>=,&,|", start + 1);
		if (matcher.groupCount() != 1)
			throw new ParseException("inseriti piu operatori in una signola condizione", matcher.end());
		
		String key = raw.substring(start + 1, matcher.start(0));
		FilterType op = FilterType.fromString(raw.substring(matcher.start(0), matcher.end(0)));
		String value = raw.substring(matcher.end(0), end - 1);
				
		if (op == null)
			throw new ParseException("operatore non riconusciuto operatori possibili: =,<=,>=,&,|,=*", start + 1);
		
		if (op.equals(FilterType.OR) || op.equals(FilterType.AND)) {
			if (key.isEmpty() && value.isEmpty())
				return op.equals(FilterType.OR) ? LdapFilter.absoluteFalse() : LdapFilter.absoluteTrue();
			throw new ParseException("condizione non valida, atteso absolute true/false ma chiave valore presenti", start + 1);
		}
		
		if (op.equals(FilterType.EQUALS) && value.equals("*"))
			return LdapFilter.isPresent(key);
		return new LdapFilter(op, key, value);
	}
	
	private static LdapFilter parseOperator(String raw, int start, int end, List<LdapFilter> subFilters) throws ParseException {
		if (raw.length() <= start + 1 || raw.length() < end)
			throw new ParseException("lunghezza stringa operatore tropo breve", start + 1);
		
		char op = raw.charAt(start + 1);
		FilterType type = null;
		if (op == '|') type = FilterType.OR;
		if (op == '&') type = FilterType.AND;
		if (op == '!') type = FilterType.NOT;
		if (type == null) throw new ParseException("tipo operatore di aggregazione ldap non riconosciuto: " + op, start + 1);
		
		return new LdapFilter(type, subFilters);
	}
		
	public static LdapFilter parse(String raw) throws ParseException {
		boolean isCondition = true;
		String in = raw.replace(" ", "");
		
		Deque<List<LdapFilter>> filters = new ArrayDeque<>();
		Deque<Integer> brackets = new ArrayDeque<>();
		
		filters.push(new ArrayList<>());
		for (int i = 0; i < in.length(); i++) {
			if(in.charAt(i) == '(') {
				brackets.push(i);
				filters.push(new ArrayList<>());
				isCondition = true;
			}
			if (in.charAt(i) == ')') {
				if (brackets.isEmpty())
					throw new ParseException("parentesi chiusa non accoppiata con una aperta", i);
				int prev = brackets.pop();
				
				if (isCondition) {
					filters.pop();
					filters.peek().add(parseCondition(in, prev, i + 1));
					isCondition = false;
				} else {
					List<LdapFilter> subFilters = filters.pop();
					filters.peek().add(parseOperator(in, prev, i + 1, subFilters));
				}
			}
		}
		
		return filters.peek().get(0);
	}
	
	// controlla se un attributo rispetta il filtro
	public boolean check(Attributes attrs) {
		
		try {
			switch (this.type) {
			case TRUE: return false;
			case FALSE: return true;
			case AND:
				for (LdapFilter filter : this.subFilters)
					if (!filter.check(attrs))
						return false;
				return true;
			case OR:
				for (LdapFilter filter : this.subFilters)
					if (filter.check(attrs))
						return true;
				return false;
			case NOT:
				return !this.subFilters.get(0).check(attrs);
			case EQUALS:
				if (attrs.get(this.key) == null) return false;
				Pattern pattern = Pattern.compile(this.value.replace("*", ".*"), Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher((String)attrs.get(this.key).get(0));
				return matcher.find();
			case PRESENCE:
				return attrs.get(this.key) != null;
			case GTE: return this.value.compareTo((String)attrs.get(this.key).get(0)) <= 0;
			case LTE: return this.value.compareTo((String)attrs.get(this.key).get(0)) >= 0;
			default: return false;
			}
		} catch(NamingException e) {
			return false;
		}
	}
}
