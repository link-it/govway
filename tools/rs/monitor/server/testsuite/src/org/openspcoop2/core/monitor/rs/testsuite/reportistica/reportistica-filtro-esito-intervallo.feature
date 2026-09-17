@parallel=false
Feature: Reportistica - filtro sull'esito e allineamento dell'intervallo temporale

# I report prodotti tramite le API devono comportarsi come quelli della console di monitoraggio:
#
#  - in assenza di filtro sull'esito vengono considerate tutte le transazioni dell'intervallo,
#    comprese quelle che rientrano nel gruppo delle richieste scartate;
#  - i gruppi di esito partizionano il risultato senza perdere ne' duplicare transazioni;
#  - il parametro 'escludi_scartate' deve incidere effettivamente sul risultato;
#  - l'unita' temporale, se non indicata, viene derivata dall'intervallo come avviene nella console;
#    se indicata, gli estremi dell'intervallo vengono allineati all'unita' in uso, altrimenti il primo
#    intervallo di campionamento verrebbe escluso per intero.
#
# Il filtro sull'esito viene tradotto in un unico punto (ReportisticaHelper.overrideFiltroEsito) e
# l'intervallo temporale in un altro (ReportExporter.setParametersInSearchForm), comuni a tutte le
# distribuzioni e ad entrambe le forme di ricerca; gli Scenario Outline le percorrono comunque tutte,
# sia per ricerca semplice (GET) sia per ricerca full (POST), in modo che una eventuale divergenza
# futura fra i percorsi non passi inosservata. La distribuzione per token info viene percorsa su tutti
# i claim disponibili, informazioni PDND comprese.
#
# 'distribuzione-esiti' compare solo negli scenari sull'intervallo temporale: non prevede un filtro
# sull'esito, essendo l'esito la sua dimensione. E' esclusa anche 'distribuzione-errori', che
# normalizza da se' il filtro a 'fallite_e_fault' e rifiuta 'ok'.
#
# I conteggi sono limitati all'API del fixture, il cui nome e' randomizzato ad ogni esecuzione, cosi'
# da isolarli dal traffico delle altre feature; fa eccezione 'distribuzione-api', che non prevede il
# filtro sull'API essendo questa la sua dimensione e che quindi riporta il traffico dell'intero
# intervallo. Gli scenari restano validi: la lock sulla generazione delle statistiche, presa da
# lock_db, congela l'insieme dei dati per tutta la durata della feature, e le verifiche sono sempre
# relazioni fra totali ottenuti nella stessa esecuzione.
#
# Composizione dei gruppi di esito (EsitiProperties):
#   ok               gli esiti completati con successo, fault applicativo escluso
#   fault            il solo esito 2 'Fault Applicativo'
#   fallite          tutti gli esiti non ok, fault applicativo escluso
#   fallite_e_fault  tutti gli esiti non ok, fault applicativo compreso
#   errori_consegna  2, 10, 49, 51, 52, 11, 29, 30, 40
#   scartate         16, 41, 42, 15, 43, 44, 13, 4, 33

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')

    # Aggiunge transazioni per ogni gruppo di esito: deve precedere lock_db
    * callonce read('classpath:prepare_transazioni_esiti.feature')

    * callonce read('classpath:lock_db.feature')

    * def reportGet = 'classpath:report-distribuzione-totale.feature'
    * def reportPost = 'classpath:report-distribuzione-totale-post.feature'
    * def reportisticaUrl = monitorUrl + '/reportistica/analisi-statistica'

    # Intervallo allineato all'ora, come negli altri test della suite
    * def argsBase =
    """
    ({
        data_inizio: setup.dataInizioMinuteZero,
        data_fine: getDate(),
        nome_servizio: setup.erogazione_petstore.api_nome,
        versione_servizio: setup.erogazione_petstore.api_versione,
        unita_tempo: 'orario'
    })
    """

    # Stesso intervallo, ma con inizio a meta' dell'ora corrente: il campionamento e' registrato
    # sull'inizio dell'intervallo (l'ora esatta, la mezzanotte, ...) e resterebbe fuori dal filtro
    # temporale se gli estremi non venissero allineati all'unita' in uso
    * def argsNonAllineato =
    """
    ({
        data_inizio: setup.dataInizio,
        data_fine: getDate(),
        nome_servizio: setup.erogazione_petstore.api_nome,
        versione_servizio: setup.erogazione_petstore.api_versione
    })
    """


@ReportEsitoFixtureTransazioni
Scenario Outline: Il fixture rende visibili transazioni non riuscite nella distribuzione <nome-distribuzione>

    # Precondizione delle verifiche sul filtro dell'esito: se i conteggi fossero nulli, le uguaglianze
    # verificate negli scenari successivi sarebbero soddisfatte in modo banale.
    # La richiesta rifiutata in autenticazione non ha applicativo identificato tramite trasporto,
    # identificativo autenticato ne' soggetto mittente, quindi in quelle tre dimensioni non compare;
    # le transazioni con fault ed errore di consegna, essendo autenticate, compaiono ovunque.

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione> })
    * def args = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsFault = karate.merge(args, { esito: 'fault' })
    * def argsScartate = karate.merge(args, { esito: 'richieste_scartate' })

    * def rFault = call read(reportGet) argsFault
    * def rScartate = call read(reportGet) argsScartate

    * print <nome-distribuzione>, '- fault:', rFault.totale, '- scartate:', rScartate.totale

    * assert rFault.totale > 0
    * assert !<attesa-scartata> || rScartate.totale > 0

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi | attesa-scartata |
    | 'temporale' | 'distribuzione-temporale' | {} | true |
    | 'api' | 'distribuzione-api' | {} | true |
    | 'azione' | 'distribuzione-azione' | {} | true |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} | true |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} | false |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} | true |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } | false |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } | true |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} | false |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } | true |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } | true |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } | true |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } | true |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } | true |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } | true |


@ReportEsitoPerDistribuzione
Scenario Outline: I gruppi di esito partizionano il report <nome-distribuzione> senza perdere transazioni

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione> })
    * def args = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsOk = karate.merge(args, { esito: 'ok' })
    * def argsFault = karate.merge(args, { esito: 'fault' })
    * def argsFallite = karate.merge(args, { esito: 'fallite', escludi_scartate: false })
    * def argsFalliteEFault = karate.merge(args, { esito: 'fallite_e_fault', escludi_scartate: false })
    * def argsErroriConsegna = karate.merge(args, { esito: 'errori_consegna' })
    * def argsScartate = karate.merge(args, { esito: 'richieste_scartate' })
    * def argsEscludiScartate = karate.merge(args, { escludi_scartate: true })
    * def argsFalliteEscludiScartate = karate.merge(args, { esito: 'fallite', escludi_scartate: true })

    * def rDefault = call read(reportGet) args
    * def totaleDefault = rDefault.totale

    * def rOk = call read(reportGet) argsOk
    * def totaleOk = rOk.totale

    * def rFault = call read(reportGet) argsFault
    * def totaleFault = rFault.totale

    * def rFallite = call read(reportGet) argsFallite
    * def totaleFallite = rFallite.totale

    * def rFalliteEFault = call read(reportGet) argsFalliteEFault
    * def totaleFalliteEFault = rFalliteEFault.totale

    * def rErroriConsegna = call read(reportGet) argsErroriConsegna
    * def totaleErroriConsegna = rErroriConsegna.totale

    * def rScartate = call read(reportGet) argsScartate
    * def totaleScartate = rScartate.totale

    * def rEscludiScartate = call read(reportGet) argsEscludiScartate
    * def totaleEscludiScartate = rEscludiScartate.totale

    * def rFalliteEscludiScartate = call read(reportGet) argsFalliteEscludiScartate
    * def totaleFalliteEscludiScartate = rFalliteEscludiScartate.totale

    * print <nome-distribuzione>, '- senza filtro:', totaleDefault, '- ok:', totaleOk, '- fault:', totaleFault, '- fallite:', totaleFallite, '- fallite_e_fault:', totaleFalliteEFault, '- errori_consegna:', totaleErroriConsegna, '- scartate:', totaleScartate

    # Senza filtro il report comprende anche le transazioni non riuscite
    * assert totaleDefault > totaleOk

    # I gruppi partizionano esattamente l'insieme delle transazioni
    * assert totaleDefault == totaleOk + totaleFalliteEFault
    * assert totaleDefault == totaleOk + totaleFault + totaleFallite
    * assert totaleFalliteEFault == totaleFallite + totaleFault

    # I gruppi piu' specifici sono sottoinsiemi di quelli piu' ampi
    * assert totaleErroriConsegna <= totaleFalliteEFault
    * assert totaleScartate <= totaleFallite

    # 'escludi_scartate' rimuove esattamente le transazioni del gruppo 'richieste scartate',
    # sia senza filtro sull'esito sia sul gruppo 'fallite'
    * assert totaleDefault == totaleEscludiScartate + totaleScartate
    * assert totaleFallite == totaleFalliteEscludiScartate + totaleScartate

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi |
    | 'temporale' | 'distribuzione-temporale' | {} |
    | 'api' | 'distribuzione-api' | {} |
    | 'azione' | 'distribuzione-azione' | {} |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } |


@ReportEsitoRichiesteScartateIgnoraEscludiScartate
Scenario Outline: Sul gruppo 'richieste scartate' il parametro 'escludi_scartate' non ha effetto (<nome-distribuzione>)

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione> })
    * def argsParametri = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def args = karate.merge(argsParametri, { esito: 'richieste_scartate' })
    * def argsConFlag = karate.merge(args, { escludi_scartate: true })

    * def rScartate = call read(reportGet) args
    * def rScartateEscluse = call read(reportGet) argsConFlag

    * print <nome-distribuzione>, '- scartate:', rScartate.totale, '- scartate con escludi_scartate:', rScartateEscluse.totale

    * assert rScartateEscluse.totale == rScartate.totale

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi |
    | 'temporale' | 'distribuzione-temporale' | {} |
    | 'api' | 'distribuzione-api' | {} |
    | 'azione' | 'distribuzione-azione' | {} |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } |


@ReportIntervalloPerDistribuzione
Scenario Outline: Gli estremi dell'intervallo vengono rispettati nel report <nome-distribuzione>

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione> })
    * def argsRiferimento = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsNonAllineatoDistribuzione = karate.merge(argsNonAllineato, { path_distribuzione: <path-distribuzione> })
    * def argsSenzaUnita = karate.merge(argsNonAllineatoDistribuzione, <parametri-aggiuntivi>)
    * def argsOrario = karate.merge(argsSenzaUnita, { unita_tempo: 'orario' })

    # Riferimento: finestra allineata all'ora, con unita' temporale indicata
    * def rRiferimento = call read(reportGet) argsRiferimento
    * def totaleRiferimento = rRiferimento.totale
    * assert totaleRiferimento > 0

    # Stessa finestra con inizio a meta' dell'ora, senza indicare l'unita' temporale: viene derivata
    # dall'intervallo, come nella console, e gli estremi vengono allineati
    * def rSenzaUnita = call read(reportGet) argsSenzaUnita
    * def totaleSenzaUnita = rSenzaUnita.totale

    # Stessa finestra con unita' temporale oraria indicata esplicitamente
    * def rOrario = call read(reportGet) argsOrario
    * def totaleOrario = rOrario.totale

    * print <nome-distribuzione>, '- riferimento:', totaleRiferimento, '- senza unita temporale:', totaleSenzaUnita, '- unita oraria:', totaleOrario

    * assert totaleOrario == totaleRiferimento

    # Nelle distribuzioni temporali l'unita' e' una dimensione del report e in sua assenza resta
    # quella giornaliera, come nella console: il confronto con il riferimento orario non si applica
    * assert !<unita-derivata> || totaleSenzaUnita == totaleRiferimento

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi | unita-derivata |
    | 'temporale' | 'distribuzione-temporale' | {} | false |
    | 'esiti' | 'distribuzione-esiti' | {} | false |
    | 'api' | 'distribuzione-api' | {} | true |
    | 'azione' | 'distribuzione-azione' | {} | true |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} | true |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} | true |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} | true |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } | true |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } | true |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} | true |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } | true |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } | true |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } | true |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } | true |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } | true |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } | true |


@ReportIntervalloUnitaTempoGiornaliera
Scenario Outline: Con unita' temporale giornaliera gli estremi dell'intervallo vengono allineati al giorno (<nome-distribuzione>)

    # Lo scenario ha senso solo se il campionamento giornaliero e' gia' stato prodotto per l'API di
    # test: a differenza di quello orario non viene atteso da lock_db
    * def righeGiornaliere = setup.db.readRows("SELECT count(*) AS presenti FROM statistiche_giornaliere WHERE servizio = '" + setup.erogazione_petstore.api_nome + "'")
    * def statisticheGiornalierePresenti = righeGiornaliere[0].presenti
    * eval if (statisticheGiornalierePresenti < 1) karate.log('Campionamento giornaliero non ancora prodotto per API', setup.erogazione_petstore.api_nome, ': scenario non eseguito')
    * eval if (statisticheGiornalierePresenti < 1) karate.abort()

    # Il campionamento giornaliero e' registrato sulla mezzanotte: senza allineamento degli estremi
    # un intervallo che inizia a meta' giornata lo escluderebbe per intero
    * def argsDistribuzione = karate.merge(argsNonAllineato, { path_distribuzione: <path-distribuzione>, unita_tempo: 'giornaliero' })
    * def args = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def rGiornaliero = call read(reportGet) args
    * print <nome-distribuzione>, '- totale su base giornaliera:', rGiornaliero.totale

    * assert rGiornaliero.totale > 0

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi |
    | 'temporale' | 'distribuzione-temporale' | {} |
    | 'esiti' | 'distribuzione-esiti' | {} |
    | 'api' | 'distribuzione-api' | {} |
    | 'azione' | 'distribuzione-azione' | {} |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } |


@ReportPostEsitoPerDistribuzione
Scenario Outline: Tramite ricerca full i gruppi di esito partizionano il report <nome-distribuzione>

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione>, filtro_api: <filtro-api> })
    * def args = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsQualsiasi = karate.merge(args, { esito: 'qualsiasi' })
    * def argsOk = karate.merge(args, { esito: 'ok' })
    * def argsFalliteEFault = karate.merge(args, { esito: 'fallite_e_fault', escludi_scartate: false })
    * def argsScartate = karate.merge(args, { esito: 'richieste_scartate' })
    * def argsEscludiScartate = karate.merge(args, { esito: 'qualsiasi', escludi_scartate: true })

    # Senza alcun filtro sull'esito
    * def rDefault = call read(reportPost) args
    * def totaleDefault = rDefault.totale

    # Con il filtro esplicito 'qualsiasi', esprimibile solo tramite ricerca full
    * def rQualsiasi = call read(reportPost) argsQualsiasi
    * def totaleQualsiasi = rQualsiasi.totale

    * def rOk = call read(reportPost) argsOk
    * def totaleOk = rOk.totale

    * def rFalliteEFault = call read(reportPost) argsFalliteEFault
    * def totaleFalliteEFault = rFalliteEFault.totale

    * def rScartate = call read(reportPost) argsScartate
    * def totaleScartate = rScartate.totale

    * def rEscludiScartate = call read(reportPost) argsEscludiScartate
    * def totaleEscludiScartate = rEscludiScartate.totale

    * print <nome-distribuzione>, '(post) - senza filtro:', totaleDefault, '- qualsiasi:', totaleQualsiasi, '- ok:', totaleOk, '- fallite_e_fault:', totaleFalliteEFault, '- scartate:', totaleScartate

    # Indicare 'qualsiasi' esplicitamente equivale a non indicare alcun filtro
    * assert totaleQualsiasi == totaleDefault

    * assert totaleDefault > totaleOk
    * assert totaleDefault == totaleOk + totaleFalliteEFault
    * assert totaleDefault == totaleEscludiScartate + totaleScartate

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi | filtro-api |
    | 'temporale' | 'distribuzione-temporale' | {} | true |
    | 'api' | 'distribuzione-api' | {} | false |
    | 'azione' | 'distribuzione-azione' | {} | true |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} | true |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} | true |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} | true |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } | true |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } | true |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} | true |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } | true |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } | true |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } | true |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } | true |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } | true |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } | true |


@ReportPostEsitoPersonalizzato
Scenario Outline: Tramite ricerca full il filtro per codici di esito e' coerente con i gruppi (<nome-distribuzione>)

    # L'esito 'personalizzato' e' l'unico ramo che non viene tradotto in un gruppo ma nell'elenco
    # puntuale dei codici, ed e' esprimibile solo tramite ricerca full

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione>, filtro_api: <filtro-api> })
    * def args = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsFault = karate.merge(args, { esito: 'fault' })
    * def argsScartate = karate.merge(args, { esito: 'richieste_scartate' })
    * def argsCodiceFault = karate.merge(args, { codici: [2] })
    * def argsCodiceAutenticazione = karate.merge(args, { codici: [16] })
    * def argsCodiciEntrambi = karate.merge(args, { codici: [2, 16] })

    * def rFault = call read(reportPost) argsFault
    * def rScartate = call read(reportPost) argsScartate
    * def rCodiceFault = call read(reportPost) argsCodiceFault
    * def rCodiceAutenticazione = call read(reportPost) argsCodiceAutenticazione
    * def rCodiciEntrambi = call read(reportPost) argsCodiciEntrambi

    * print <nome-distribuzione>, '(post) - fault:', rFault.totale, '- codice 2:', rCodiceFault.totale, '- codice 16:', rCodiceAutenticazione.totale, '- codici 2+16:', rCodiciEntrambi.totale

    # Il gruppo 'fault' corrisponde esattamente al solo esito 2
    * assert rCodiceFault.totale == rFault.totale

    # L'esito 16 'Autenticazione Fallita' appartiene al gruppo delle richieste scartate
    * assert rCodiceAutenticazione.totale <= rScartate.totale

    # L'elenco dei codici seleziona l'unione degli insiemi
    * assert rCodiciEntrambi.totale == rCodiceFault.totale + rCodiceAutenticazione.totale

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi | filtro-api |
    | 'temporale' | 'distribuzione-temporale' | {} | true |
    | 'api' | 'distribuzione-api' | {} | false |
    | 'azione' | 'distribuzione-azione' | {} | true |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} | true |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} | true |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} | true |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } | true |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } | true |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} | true |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } | true |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } | true |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } | true |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } | true |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } | true |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } | true |


@ReportPostIntervalloPerDistribuzione
Scenario Outline: Tramite ricerca full gli estremi dell'intervallo vengono rispettati nel report <nome-distribuzione>

    * def argsDistribuzione = karate.merge(argsBase, { path_distribuzione: <path-distribuzione>, filtro_api: <filtro-api> })
    * def argsRiferimento = karate.merge(argsDistribuzione, <parametri-aggiuntivi>)

    * def argsNonAllineatoDistribuzione = karate.merge(argsNonAllineato, { path_distribuzione: <path-distribuzione>, filtro_api: <filtro-api> })
    * def argsSenzaUnita = karate.merge(argsNonAllineatoDistribuzione, <parametri-aggiuntivi>)
    * def argsOrario = karate.merge(argsSenzaUnita, { unita_tempo: 'orario' })

    * def rRiferimento = call read(reportPost) argsRiferimento
    * def totaleRiferimento = rRiferimento.totale
    * assert totaleRiferimento > 0

    * def rSenzaUnita = call read(reportPost) argsSenzaUnita
    * def totaleSenzaUnita = rSenzaUnita.totale

    * def rOrario = call read(reportPost) argsOrario
    * def totaleOrario = rOrario.totale

    * print <nome-distribuzione>, '(post) - riferimento:', totaleRiferimento, '- senza unita temporale:', totaleSenzaUnita, '- unita oraria:', totaleOrario

    * assert totaleOrario == totaleRiferimento
    * assert !<unita-derivata> || totaleSenzaUnita == totaleRiferimento

    Examples:
    | nome-distribuzione | path-distribuzione | parametri-aggiuntivi | filtro-api | unita-derivata |
    | 'temporale' | 'distribuzione-temporale' | {} | true | false |
    | 'esiti' | 'distribuzione-esiti' | {} | true | false |
    | 'api' | 'distribuzione-api' | {} | false | true |
    | 'azione' | 'distribuzione-azione' | {} | true | true |
    | 'soggetto-locale' | 'distribuzione-soggetto-locale' | {} | true | true |
    | 'soggetto-remoto' | 'distribuzione-soggetto-remoto' | {} | true | true |
    | 'indirizzo-ip' | 'distribuzione-indirizzo-ip' | {} | true | true |
    | 'applicativo-trasporto' | 'distribuzione-applicativo' | { "tipo_identificazione": "trasporto" } | true | true |
    | 'applicativo-token' | 'distribuzione-applicativo' | { "tipo_identificazione": "token" } | true | true |
    | 'id-autenticato' | 'distribuzione-id-autenticato' | {} | true | true |
    | 'token-info-client-id' | 'distribuzione-token-info' | { "claim": "client_id" } | true | true |
    | 'token-info-subject' | 'distribuzione-token-info' | { "claim": "subject" } | true | true |
    | 'token-info-issuer' | 'distribuzione-token-info' | { "claim": "issuer" } | true | true |
    | 'token-info-username' | 'distribuzione-token-info' | { "claim": "username" } | true | true |
    | 'token-info-email' | 'distribuzione-token-info' | { "claim": "email" } | true | true |
    | 'token-info-pdnd' | 'distribuzione-token-info' | { "claim": "client_id_pdnd_informazioni" } | true | true |
