Miglioramenti alla funzionalità di Tracciatura su File
------------------------------------------------------

È adesso possibile configurare una 'DenyList' o una 'WhiteList' che consente di personalizzare gli header HTTP ottenibili tramite la chiamate delle primitive: 'getInRequestHeaders', 'getOutRequestHeaders', 'getInResponseHeaders', 'getOutResponseHeaders'.

È inoltre stato corretto un bug presente nell'uso dell'istruzione '${logBase64:xx}' su tali primitive: il valore codificato in base64 restituito conteneva una lista di header in cui i nomi e i valori erano nuovamente codificati in base64.

È infine adesso possibile abilitare il dump binario solamente per gli headers o per il payload dei messaggi scambiati.
