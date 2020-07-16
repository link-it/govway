# GovWay - API Gateway per la Pubblica Amministrazione
[![Build Status](https://jenkins.link.it/govway/buildStatus/icon?job=GovWay&style=plastic)](https://jenkins.link.it/govway/job/GovWay/)
[![Docker](https://github.com/link-it/govway/blob/master/resources/images/docker.svg)](https://hub.docker.com/r/linkitaly/govway)
[![Documentation Status](https://readthedocs.org/projects/govway/badge/?version=latest&style=plastic)](https://govway.readthedocs.io/it/latest/?badge=latest)
[![License](https://github.com/link-it/govway/blob/master/resources/images/license.svg)](https://raw.githubusercontent.com/link-it/govway/master/LICENSE)

Dall’esperienza della Porta di Dominio italiana, l’API Gateway conforme alle normative della Pubblica Amministrazione:
- Conformità agli standard di mercato: gestione protocolli standard di mercato, come SOAP 1.1 e 1.2, API restful serializzate in Json o XML o semplici dati binari su Http.
- Conformità alle specifiche italiane per l'interoperabilità: supporto delle nuove linee guida per l'interoperabilità di AGID (ModI PA). Viene inoltre assicurata la retrocompatibilità con il protocollo SPCoop, ancora ampiamente adottato per i servizi della PA. 
- Conformità alle specifiche dell'interoperabilità europea: supporto supporto del protocollo AS4, tramite integrazione con il Building Block eDelivery del progetto europeo CEF (Connecting European Facilities).
- Conformità alle specifiche per la fatturazione elettronica sul canale SdiCoop.

## Contributi

È possibile partecipare attraverso la creazione di [issues](https://github.com/link-it/govway/issues) e [pull requests](https://github.com/link-it/govway/pulls).

Il progetto possiede un backlog di supporto tecnico costituito dalle [Issue di tipo 'Support'](https://github.com/link-it/govway/issues?utf8=%E2%9C%93&q=is%3Aissue+label%3A%22support%22+).


## Documentazione

Sito WEB del Progetto: 
* https://govway.org

Documentazione online:
* [Read the docs](https://govway.readthedocs.io/it/latest/) ([download](https://readthedocs.org/projects/govway/downloads/htmlzip/latest/))
* Backlog di supporto tecnico costituito dalle [Issue di tipo 'Support'](https://github.com/link-it/govway/issues?utf8=%E2%9C%93&q=is%3Aissue+label%3A%22support%22+)

Documentazione PDF:
* [Manuale di Installazione](./resources/doc/pdf/GovWay-ManualeInstallazione.pdf)
* [Manuale Utente](./resources/doc/pdf/GovWay-ManualeUtente.pdf)
* [Manuale di Monitoraggio](./resources/doc/pdf/GovWay-ManualeMonitoraggio.pdf)


## Docker

Ambienti di prova delle versioni stabili di GovWay, containerizzati in formato Docker, sono disponibile su [Docker Hub
](https://hub.docker.com/r/linkitaly/govway).

Il progetto [govway-docker](https://github.com/link-it/govway-docker) fornisce tutto il necessario per produrre un nuovo ambiente di prova per GovWay funzionante, containerizzato in formato Docker, a partire anche da versioni non stabili o dai sorgenti.

## Govlet

* [Fatturazione Attiva](./resources/doc/govlet/Fatturazione/Govlet_FatturaPA_FatturazioneAttiva.pdf)
* [Fatturazione Passiva](./resources/doc/govlet/Fatturazione/Govlet_FatturaPA_FatturazionePassiva.pdf)
* [PagoPA](./resources/doc/govlet/pagoPA/Govlet_pagoPA.pdf)
* [SIOPE+](./resources/doc/govlet/SiopePlus/Govlet_SiopePlus.pdf)
* [ANPR](./resources/doc/govlet/ANPR/Govlet_ANPR.pdf)

## Contatti

- Mailing list: [Utenti GovWay](https://govway.org/mailing)
- Segnalazioni: [GitHub Issues](https://github.com/link-it/GovWay/issues)

# Le funzionalità principali
## Standard di Mercato
Supporto API conformi ai protocolli standard di mercato, come SOAP 1.1 e 1.2, API restful serializzate in Json o XML o semplici dati binari su Http. L'integrazione avviene sempre tramite le API applicative native, indipendentemente dai profili di interoperabilità adottati, gestiti in maniera trasparente dal gateway.
## Conformità alle specifiche di interoperabilità italiane ed europee
Supporto delle nuove linee guida per l'interoperabilità di AGID (ModI PA) e del "building block" eDelivery del progetto europeo CEF (Connecting European Facilities). Viene inoltre assicurata la retrocompatibilità con il protocollo SPCoop, ancora ampiamente adottato per i servizi della PA.
## Registro delle API
La registrazione delle API può avvenire manualmente o tramite caricamento dei descrittori delle interfacce (OpenAPI 3.0, Swagger e WADL per i servizi REST, WSDL per i servizi SOAP, Accordi di Servizio per i servizi SPCoop).
## Gestione Govlet
GovWay introduce il concetto di Govlet, un formato di archivio caricabile direttamente dalle Console del prodotto, per una rapida configurazione dei servizi della Pubblica Amministrazione. Ad oggi la libreria di GovLet disponibili per GovWay include i servizi di Fatturazione Elettronica (attiva e passiva), PagoPA e SIOPE+ ed è in rapida espansione.
## Gestione token (JWT, OAuth2, OIDC)
Gestione di token di autenticazione conformi agli standard JWT, OAuth2 e OIDC. Supporto della validazione dei token e dell'acquisizione dei claim interni al token per le successive fasi di autenticazione e autorizzazione, anche interagendo con Authorization Server esterni tramite funzionalità di Introspection e UserInfo.
## Rate Limiting
Regolazione del traffico in ingresso su GovWay, limitando il numero di richieste o la dimensione di banda occupata per specifiche erogazioni o fruizioni, anche in funzione di parametri come il tempo medio di elaborazione, la quantità di errori o in base a caratteristiche delle specifiche richieste applicative.
## Autenticazione
Gestione dell'autenticazione delle richieste applicative in ingresso e in uscita dal proprio dominio, tramite supporto nativo dei protocolli HTTP-Basic, TLS e ApiKey o tramite integrazione di sistemi esterni di Identity Management.
## Autorizzazione
Gestione dell'autorizzazione delle richieste applicative, tramite registrazione dei fruitori delle API gestite e dei loro ruoli, o tramite integrazione con sistemi esterni di Identity Management. Supporto dei protocolli di Autorizzazione Oauth2 e XACML, con la possibilità di gestire la valutazione delle policy XACML localmente o utilizzando un Policy Decision Point esterno.
## Trasformazione Messaggi
È possibile modificare i messaggi in transito, sia per quanto riguarda il Protocollo, da SOAP a REST o viceversa, sia per quanto riguarda la trasformazione dei contenuti dei messaggi (header, properties o payload).
##  Validazione
Validazione dei contenuti delle richieste applicative, con verifica dei messaggi XML per i servizi SOAP e JSON o XML per i servizi REST. La validazione viene effettuata rispetto alle descrizioni delle API (OpenAPI, Swagger, WSDL, JSON Schema, XSD) registrate in fase di configurazione del servizio.
## Caching della Risposta
Per ogni API è possibile abilitare la funzionalità di cache delle risposte in modo che successive richieste, con le medesime caratteristiche (uri, http header, payload), vengano servite direttamente dal gateway fino alla scadenza di un timeout configurabile.
## Gestione CORS
Possibilità di gestire il cross-origin HTTP request (CORS) sia globalmente, in modo che sia valido per tutte le API, che puntualmente, perfezionandolo sulla singola erogazione o fruizione.
## Tracciamento
Emissione di tracce conformi alle normative per ogni richiesta applicativa gestita. Oltre ai metadati riguardanti la richiesta di servizio (id transazione, mittente, destinatario, ...) è possibile riportare nelle tracce elementi identificativi estratti dai messaggi in transito; la modalità di estrazione supportate dal prodotto sono: XPath, Espressioni Regolari e JSONPath.
## Sicurezza dei contenuti
Il gateway può intervenire per introdurre o verificare la sicurezza delle richieste applicative. Nel caso di API SOAP è supportato lo standard WS-Security. Nel caso di API REST sono supportati i protocolli XMLEncryption e XMLSignature, per i messaggi XML, e JOSE (JWS/JWE) per i messaggi JSON.
## Gestione del formato MTOM
Il gateway è in grado di imbustare o sbustare in accordo al protocollo MTOM il messaggio in transito. In caso di validazione di un messaggio MTOM, il gateway potrà normalizzare il messaggio prima di effettuarne la validazione per poi ripristinare il formato originale una volta completato il processo di validazione.
## Routing della richiesta
Consegna della richiesta ai servizi di backend, con supporto nativo per i seguenti protocolli di connessione: http, https con mutua autenticazione, jms e scrittura su file. Ulteriori connettori possono essere realizzati come semplici plugin.
## Console di Gestione
Cruscotto web per la registrazione di API (interfacce), Erogazioni (implementazioni), Fruizioni (subscription) e delle varie policy che le regolano. La gestione di vari profili di utenza permette di selezionare le funzioni di gestione sulla base dei ruoli dei diversi gestori. Tutte le operazioni sono sottoposte ad auditing, in modo da poter sempre individuare gli autori delle modifiche effettuate alle configurazioni.
## Console di Monitoraggio
Cruscotto web rivolto alla diagnostica ed al monitoraggio del traffico gestito dall'API gateway; ai gestori dell'infrastruttura assicura un controllo totale sui messaggi in transito, aiutando a diagnosticare e prevenire qualunque tipo di anomalia; ai responsabili di progetto offre la possibilità di analizzare i flussi di utilizzo, gli esiti e l'efficienza complessiva delle API utilizzate nel proprio progetto.


# Licenza
GovWay - A customizable API Gateway 
https://govway.org

Copyright (c) 2005-2020 Link.it srl (https://link.it). 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, as published by
the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
