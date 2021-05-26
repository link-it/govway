#!/bin/bash

echo -e ""
echo "Building RateLimiting Built-In Policy ..."

DIR="RateLimiting"

cat list.txt | while read LINE
do
        if [ "${LINE:0:1}" == "#" ]
        then
                continue
        fi
	risorsa=$(echo $LINE |cut -d "|" -f 1)
	simultanee=$(echo $LINE |cut -d "|" -f 2)
	intervallo=$(echo $LINE |cut -d "|" -f 3)
	valore=$(echo $LINE |cut -d "|" -f 4)
	congestione=$(echo $LINE |cut -d "|" -f 5)
	degrado=$(echo $LINE |cut -d "|" -f 6)

	descrizione=""
	identificativoPolicy="${risorsa}"

	richiesteSimultanee=false
	if [ "${risorsa}" == "NumeroRichieste" -a "${simultanee}" == "true" ]
	then
		richiesteSimultanee=true
		descrizione="La policy limita il numero totale massimo di richieste simultanee consentite."
		identificativoPolicy="${identificativoPolicy}Simultanee"
	elif [ "${risorsa}" == "NumeroRichieste" ]
	then
		descrizione="La policy limita il numero totale massimo di richieste consentite"
	elif [ "${risorsa}" == "DimensioneMassimaMessaggio" ]
	then
		descrizione="La policy limita la dimensione massima, in KB, consentita per una richiesta e/o per una risposta"
	elif [ "${risorsa}" == "OccupazioneBanda" ]
	then
		descrizione="La policy limita il numero totale massimo di KB consentiti"
	elif [ "${risorsa}" == "TempoComplessivoRisposta" ]
	then
		descrizione="La policy limita il numero totale massimo di secondi consentiti"
	elif [ "${risorsa}" == "TempoMedioRisposta" ]
	then
		descrizione="La policy blocca ogni successiva richiesta se viene rilevato un tempo medio di risposta elevato"
	elif [ "${risorsa}" == "NumeroRichiesteCompletateConSuccesso" ]
	then
		descrizione="La policy conteggia il numero di richieste completate con successo; raggiunto il limite, ogni successiva richiesta viene bloccata"
	elif [ "${risorsa}" == "NumeroRichiesteFallite" ]
	then
		descrizione="La policy conteggia il numero di richieste fallite; raggiunto il limite, ogni successiva richiesta viene bloccata"
	elif [ "${risorsa}" == "NumeroFaultApplicativi" ]
	then
		descrizione="La policy conteggia il numero di richieste che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata"
	elif [ "${risorsa}" == "NumeroRichiesteFalliteOFaultApplicativi" ]
	then
		descrizione="La policy conteggia il numero di richieste fallite o che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata"
	fi

	if [ "${richiesteSimultanee}" == "false" -a "${risorsa}" != "DimensioneMassimaMessaggio" ]
	then
		if [ "${intervallo}" == "minuti" ]
		then
			descrizione="${descrizione} durante l'intervallo temporale di 1 minuto (campionamento real-time, finestra corrente)."
			identificativoPolicy="${identificativoPolicy}-ControlloRealtimeMinuti"
		elif [ "${intervallo}" == "orario" ]
		then
			descrizione="${descrizione} durante l'intervallo temporale di 1 ora (campionamento real-time, finestra corrente)."
			identificativoPolicy="${identificativoPolicy}-ControlloRealtimeOrario"
		elif [ "${intervallo}" == "giornaliero" ]
		then
			descrizione="${descrizione} durante l'intervallo temporale di 1 giorno (campionamento real-time, finestra corrente)."
			identificativoPolicy="${identificativoPolicy}-ControlloRealtimeGiornaliero"
		fi
	fi

	if [ "${congestione}" == "true" -o "${degrado}" == "true" ]
	then
		descrizione="${descrizione}\nLa policy viene applicata solamente se"
		if [ "${congestione}" == "true" ]
		then
			descrizione="${descrizione} la PdD risulta Congestionata dalla richieste"
			identificativoPolicy="${identificativoPolicy}-Congestione"
		fi
		if [ "${congestione}" == "true" -a "${degrado}" == "true" ]
		then
			descrizione="${descrizione} ed"
		fi
		if [ "${degrado}" == "true" ]
		then
			descrizione="${descrizione} il tempo medio di risposta del servizio (campionamento realtime, intervallo di tempo specificato in ore, finestra precedente, tipo latenza servizio) risulta superiore ai livelli di soglia impostati"
			identificativoPolicy="${identificativoPolicy}-Degrado"
		fi
		descrizione="${descrizione}."
	fi



        # Directory
        mkdir -p ${DIR}
	nomeFile=$(echo "${identificativoPolicy}" | perl -p -e "s#-#_#g")
	file="${DIR}/${nomeFile}.xml"
	#echo "FILE [${file}]"

	
	# Inizio File
	echo -e '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' > ${file}
	echo -e '<configurazione-policy xmlns="http://www.openspcoop2.org/core/controllo_traffico">' >>  ${file}

	# Policy
	echo -e "\t<id-policy>_built-in_${identificativoPolicy}</id-policy>" >> ${file}
	echo -e "\t<built-in>true</built-in>" >> ${file}
	echo -e "\t<descrizione>${descrizione}</descrizione>" >> ${file}
	echo -e "\t<risorsa>${risorsa}</risorsa>" >> ${file}
	echo -e "\t<simultanee>${simultanee}</simultanee>" >> ${file}

	# Valore
	echo -e "\t<valore>${valore}</valore>" >> ${file}
	if [ "${risorsa}" == "DimensioneMassimaMessaggio" ]
	then
		echo -e "\t<valore2>${valore}</valore2>" >> ${file}
	fi
	if [ "${risorsa}" == "OccupazioneBanda" ]
	then
		echo -e "\t<valore-tipo-banda>complessiva</valore-tipo-banda>" >> ${file}
	fi
	if [ "${risorsa}" == "TempoComplessivoRisposta" ]
	then
		echo -e "\t<valore-tipo-latenza>servizio</valore-tipo-latenza>" >> ${file}
	fi
	if [ "${risorsa}" == "TempoMedioRisposta" ]
	then
		echo -e "\t<valore-tipo-latenza>servizio</valore-tipo-latenza>" >> ${file}
	fi
	echo -e "\t<modalita-controllo>realtime</modalita-controllo>" >> ${file}
	if [ "${richiesteSimultanee}" == "false" ]
	then
		echo -e "\t<tipo-intervallo-osservazione-realtime>${intervallo}</tipo-intervallo-osservazione-realtime>" >> ${file}
		echo -e "\t<intervallo-osservazione>1</intervallo-osservazione>" >> ${file}
		echo -e "\t<finestra-osservazione>corrente</finestra-osservazione>" >> ${file}
	fi

	# Condizionale
	if [ "${congestione}" == "true" -o "${degrado}" == "true" ]
	then
		echo -e "\t<tipo-applicabilita>condizionale</tipo-applicabilita>" >> ${file}

		if [ "${congestione}" == "true" ]
		then
			echo -e "\t<applicabilita-con-congestione>true</applicabilita-con-congestione>" >> ${file}
		fi

		if [ "${degrado}" == "true" ]
		then
		    echo -e "\t<applicabilita-degrado-prestazionale>true</applicabilita-degrado-prestazionale>" >> ${file}
		    echo -e "\t<degrado-avg-time-modalita-controllo>statistic</degrado-avg-time-modalita-controllo>" >> ${file}
		    echo -e "\t<degrado-avg-time-tipo-intervallo-osservazione-statistico>orario</degrado-avg-time-tipo-intervallo-osservazione-statistico>" >> ${file}
		    echo -e "\t<degrado-avg-time-intervallo-osservazione>1</degrado-avg-time-intervallo-osservazione>" >> ${file}
		    echo -e "\t<degrado-avg-time-finestra-osservazione>scorrevole</degrado-avg-time-finestra-osservazione>" >> ${file}
		    echo -e "\t<degrado-avg-time-tipo-latenza>servizio</degrado-avg-time-tipo-latenza>" >> ${file}
		fi

	else
		echo -e "\t<tipo-applicabilita>sempre</tipo-applicabilita>" >> ${file}
	fi

	# File File
	echo -e '</configurazione-policy>' >> ${file}

done
if [ $? -eq 1 ]
then
        exit 1
fi

cd RateLimiting
zip -rq ../RateLimiting.zip *
cd ..

rm -rf RateLimiting

echo "Building RateLimiting Built-In Policy completed."
