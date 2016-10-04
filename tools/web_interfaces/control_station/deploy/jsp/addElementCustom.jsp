<%--
 * OpenSPCoop - Customizable API Gateway 
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
--%>

<script>

function changePdDType()
{
	var selectedIndex = document.form.tipo.selectedIndex;
	var tipo = document.form.tipo.options[selectedIndex].value;
	//se tipo esterno allora rendo editabili solo le informazioni
	//che interessano
	if(tipo=='esterno')
	{
		$("input[name=ip]").hide("slow");
		$("input[name=password]").hide("slow");
		$("input[name=confpw]").hide("slow");
		$("select[name=protocollo]").hide();
		$("input[name=porta]").hide("slow");
		$("input[name=ip_gestione]").hide("slow");
		$("input[name=porta_gestione]").hide("slow");
		$("select[name=protocollo_gestione]").hide();

	}else
	{
		$("input[name=ip]").show("slow");
		$("input[name=password]").show("slow");
		$("input[name=confpw]").show("slow");
		$("select[name=protocollo]").show();
		$("input[name=porta]").show("slow");
		$("input[name=ip_gestione]").show("slow");
		$("input[name=porta_gestione]").show("slow");
		$("select[name=protocollo_gestione]").show();
	}

};

</script>
