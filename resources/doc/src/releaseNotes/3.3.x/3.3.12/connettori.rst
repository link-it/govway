Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

È adesso possibile configurare GovWay per utilizzare una configurazione https differente da quella ereditata dalla jvm, oltre che tramite la configurazione specifica di un connettore https, attraverso un repository di configurazioni definite tramite file di proprietà.

Il nome e la posizione del file di proprietà è configurabile a livello di singola API.

Il nome del file indicato può contenere delle macro, risolte a runtime dal gateway, per creare dei path dinamici (es. un keystore differente per ogni applicativo).
