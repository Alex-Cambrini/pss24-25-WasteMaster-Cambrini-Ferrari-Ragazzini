# Analisi

## Requisiti

L’applicazione oggetto di analisi è destinata alla gestione completa delle attività di un’azienda che si occupa di smaltimento dei rifiuti. Il sistema mira a supportare l’azienda nelle principali attività operative e amministrative, ottimizzando la gestione delle risorse e fornendo strumenti avanzati per la pianificazione, il monitoraggio e la fatturazione.

### Requisiti funzionali

- **Gestione delle anagrafiche:** Registrazione e gestione dei dati di clienti e operatori, inclusi dati personali, di contatto e ruoli aziendali.
- **Amministrazione delle risorse aziendali:** Monitoraggio e aggiornamento dello stato operativo dei mezzi (camion, veicoli) e del personale, con tracciamento delle disponibilità e assegnazioni.
- **Gestione dei rifiuti:** Classificazione dei rifiuti, gestione delle procedure di smaltimento e riciclaggio, con tracciamento delle tipologie e quantità gestite.
- **Pianificazione delle raccolte:** Programmazione delle raccolte settimanali, possibilità di modificarle e gestione di raccolte speciali su richiesta dei clienti.
- **Gestione delle raccolte speciali:** Prenotazione di raccolte straordinarie e annullamento del ritiro settimanale con almeno 2 giorni di preavviso.
- **Monitoraggio delle raccolte:** Tracciamento dello stato delle raccolte (“Completata”, “Raccolta Fallita”) in base alle segnalazioni degli operatori.
- **Gestione della fatturazione e dei pagamenti:** Generazione di fatture e gestione dello stato dei pagamenti relativi ai servizi erogati.
- **Pianificazione delle rotte:** Pianificazione e assegnazione manuale delle rotte per i camion, con possibilità di modifica in caso di imprevisti.

### Requisiti non funzionali

- **Efficienza operativa:** Migliorare l’efficienza delle attività aziendali e ottimizzare l’uso delle risorse.
- **Sostenibilità:** Favorire pratiche di smaltimento e riciclaggio sostenibili.
- **Sicurezza dei dati:** Protezione e riservatezza dei dati sensibili di clienti e operatori.
- **Scalabilità:** Gestione efficace di un aumento del numero di clienti, risorse e raccolte.

### Requisiti opzionali

- Dashboard di monitoraggio con statistiche sulle raccolte, quantità di rifiuti gestiti e percentuale di completamento.
- Notifiche e alert automatici per segnalare problematiche nelle raccolte.
- Sistema di gestione delle segnalazioni dei clienti.
- Calendario visuale con raccolte evidenziate.
- Gestione della manutenzione dei mezzi aziendali.

### Challenge principali

- Gestione sicura delle anagrafiche e dei ruoli, con autenticazione e protezione dei dati sensibili.
- Pianificazione e modifica dinamica delle raccolte.
- Automazione della fatturazione e dei pagamenti.
- Creazione di una dashboard con statistiche operative.
- Implementazione di notifiche e gestione delle segnalazioni.
- Manutenzione preventiva dei mezzi.

## Analisi e modello del dominio

Il dominio applicativo riguarda l’ambito dello smaltimento e riciclaggio dei rifiuti operato da aziende specializzate. Le entità principali coinvolte nel problema sono:

- **Cliente:** individuo o azienda che usufruisce del servizio di raccolta rifiuti.
- **Operatore:** persona incaricata della raccolta, gestione e organizzazione delle attività operative.
- **Mezzo:** veicolo utilizzato per la raccolta e il trasporto dei rifiuti.
- **Rifiuto:** oggetto della raccolta, classificato per tipologia e modalità di smaltimento.
- **Raccolta:** operazione (programmata o straordinaria) di prelievo dei rifiuti, associata a data, luogo, cliente e uno o più operatori.
- **Rotta:** percorso pianificato per i mezzi aziendali, costituito da una sequenza di tappe o indirizzi da servire.
- **Fattura:** documento relativo ai servizi erogati, collegato ai clienti e alle raccolte effettuate.
- **Segnalazione:** comunicazione di problemi o richieste da parte dei clienti.
- **Manutenzione:** interventi tecnici pianificati e tracciati sui mezzi aziendali.

Le relazioni fondamentali tra le entità includono:

- Ogni **raccolta** è associata a uno o più **clienti**, uno o più **operatori** e a un **mezzo**.
- Ogni **mezzo** può essere coinvolto in una o più **raccolte** e può essere soggetto a **manutenzione**.
- Ogni **raccolta** può generare una **fattura**.
- I **clienti** possono inviare **segnalazioni** relative al servizio ricevuto.
- Le **rotte** sono collegate alle raccolte e ai mezzi.

### Difficoltà principali

- Correlare dinamicamente le richieste dei clienti, la disponibilità delle risorse e la pianificazione delle raccolte.
- Gestire in maniera sicura e conforme la protezione dei dati sensibili.
- Assicurare la flessibilità nella modifica delle rotte e delle raccolte in caso di imprevisti.
- Automatizzare la generazione delle fatture e il tracciamento dei pagamenti.
