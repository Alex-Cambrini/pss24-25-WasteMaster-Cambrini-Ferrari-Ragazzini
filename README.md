# Relazione di progetto

## Componenti del gruppo

- **Lorenzo Ferrari** — lorenzo.ferrari27@studio.unibo.it
- **Alex Cambrini** — alex.cambrini@studio.unibo.it
- **Manuel Ragazzini** — manuel.ragazzini3@studio.unibo.it

---

# Analisi

## Requisiti

L'applicazione gestisce le attività di un'azienda di smaltimento rifiuti, fornendo gli strumenti per la pianificazione, il monitoraggio e l'amministrazione dei servizi.  
**Requisiti funzionali:**
- Gestione anagrafiche clienti e operatori (dati dettagliati, ruoli)
- Amministrazione risorse aziendali (mezzi/personale, stato operativo)
- Gestione rifiuti (classificazione, smaltimento, riciclaggio)
- Pianificazione raccolte programmate settimanali (modificabili)
- Gestione raccolte speciali/aggiuntive (prenotazione, annullamento con preavviso)
- Monitoraggio raccolte (stato: completata/fallita, segnalazioni operatori)
- Gestione fatturazione e pagamenti
- Pianificazione e modifica rotte per i camion

**Requisiti opzionali:**
- Dashboard monitoraggio/statistiche
- Notifiche e alert automatici
- Segnalazioni clienti su problemi
- Calendario visuale raccolte
- Gestione manutenzione mezzi

**Challenge principali:**
- Protezione dati sensibili/anagrafiche
- Pianificazione dinamica raccolte/rotte
- Automazione fatturazione
- Notifiche, dashboard, gestione segnalazioni
- Manutenzione preventiva mezzi

---

## Analisi e modello del dominio

**Entità principali:**
- Cliente
- Operatore
- Mezzo
- Rifiuto
- Raccolta
- Rotta
- Fattura
- Segnalazione
- Manutenzione

**Relazioni:**
- Raccolta ↔ Cliente/Operatore/Mezzo/Rifiuto/Rotta/Fattura
- Mezzo ↔ Raccolta/Manutenzione
- Cliente ↔ Segnalazione
- Raccolta → genera Fattura

**Descrizione a parole:**  
Ogni raccolta rappresenta un servizio pianificato o speciale di ritiro rifiuti, coinvolge uno o più clienti, operatori e mezzi, e genera una fattura. I mezzi sono assegnati a rotte dinamiche e sono soggetti a manutenzione. I clienti possono inviare segnalazioni legate al servizio.

**Schema UML del dominio:**

```mermaid
classDiagram
    class Cliente
    class Operatore
    class Mezzo
    class Rifiuto
    class Raccolta
    class Rotta
    class Fattura
    class Segnalazione
    class Manutenzione

    Cliente "1" -- "0..*" Raccolta
    Operatore "1" -- "0..*" Raccolta
    Mezzo "1" -- "0..*" Raccolta
    Mezzo "1" -- "0..*" Manutenzione
    Raccolta "0..*" -- "1" Rotta
    Raccolta "1" -- "0..1" Fattura
    Cliente "1" -- "0..*" Segnalazione
    Raccolta "1" -- "0..*" Rifiuto
```

---

# Design

## Architettura

Il sistema adotta un'architettura modulare ispirata a MVC:

- **Model:** entità di dominio e logica (Cliente, Mezzo, Raccolta, ecc.)
- **View:** interfaccia utente, dashboard, notifiche
- **Controller:** coordinamento richieste, flusso operazioni e orchestrazione

**Schema architetturale:**

```mermaid
classDiagram
    Model <|-- Cliente
    Model <|-- Operatore
    Model <|-- Mezzo
    Model <|-- Rifiuto
    Model <|-- Raccolta
    Model <|-- Rotta
    Model <|-- Fattura
    Model <|-- Segnalazione
    Model <|-- Manutenzione

    Controller o-- Model
    View o-- Controller
```

---

## Design dettagliato

### Gestione anagrafiche

**Problema:** Gestire clienti/operatori in modo sicuro e aggiornabile, con notifica delle modifiche.

**Soluzione:**  
- **Pattern Repository:** separa logica di accesso ai dati e servizi.
- **Pattern Observer:** aggiorna componenti interessate a variazioni.

```mermaid
classDiagram
    interface AnagraficaRepository
    class Cliente
    class Operatore
    class AnagraficaService
    AnagraficaService o-- AnagraficaRepository
    AnagraficaRepository <|.. Cliente
    AnagraficaRepository <|.. Operatore
```

---

### Pianificazione raccolte

**Problema:** Gestire raccolte programmate/speciali, modificabili dinamicamente in base a richieste e disponibilità.

**Soluzione:**  
- **Pattern Strategy:** politiche di pianificazione (standard/speciale)
- **Pattern State:** gestione stato raccolta (pianificata, in corso, completata, fallita)

```mermaid
classDiagram
    class Raccolta {
      +pianifica()
      +cambiaStato()
    }
    interface PianificazioneStrategy
    class RaccoltaStandard
    class RaccoltaSpeciale
    class StatoRaccolta
    class PianificatoreRaccolte

    Raccolta o-- PianificazioneStrategy
    PianificazioneStrategy <|-- RaccoltaStandard
    PianificazioneStrategy <|-- RaccoltaSpeciale
    Raccolta o-- StatoRaccolta
    PianificatoreRaccolte o-- Raccolta
```

---

### Gestione fatturazione e pagamenti

**Problema:** Automatizzare generazione fatture e tracciamento pagamenti.

**Soluzione:**  
- **Pattern Factory Method:** creazione fatture standard/speciali
- **Pattern Observer:** notifica cambiamenti stato pagamento

```mermaid
classDiagram
    class FatturaFactory
    class Fattura
    class FatturaStandard
    class FatturaSpeciale
    class GestorePagamenti
    class Cliente

    FatturaFactory <|-- FatturaStandard
    FatturaFactory <|-- FatturaSpeciale
    GestorePagamenti o-- Fattura
    GestorePagamenti o-- Cliente
    Fattura <|.. Observer
    GestorePagamenti o-- Observer
```

---

### Pianificazione rotte

**Problema:** Ottimizzare e aggiornare dinamicamente le rotte dei mezzi.

**Soluzione:**  
- **Pattern Command:** gestione richieste modifica rotta e undo/redo
- **Pattern Iterator:** scorrimento tappe rotta

```mermaid
classDiagram
    class Rotta
    class Mezzo
    class Tappa
    class ComandoModificaRotta
    class PianificatoreRotte

    Rotta o-- Tappa
    Rotta o-- Mezzo
    PianificatoreRotte o-- Rotta
    PianificatoreRotte o-- ComandoModificaRotta
```

---

### Dashboard e notifiche

**Problema:** Panoramica aggiornata attività e notifiche automatiche su anomalie.

**Soluzione:**  
- **Pattern Observer:** aggiornamento realtime dashboard/notifiche
- **Pattern Decorator:** messaggi alert arricchiti

```mermaid
classDiagram
    class Dashboard
    class Notifica
    class Alert
    class Messaggio
    class DecoratorMessaggio

    Dashboard o-- Notifica
    Notifica <|-- Alert
    Alert o-- Messaggio
    DecoratorMessaggio <|-- Messaggio
```

---

### Gestione manutenzione mezzi

**Problema:** Pianificare e tracciare manutenzioni senza interrompere l’operatività.

**Soluzione:**  
- **Pattern Template Method:** sequenza fasi intervento
- **Pattern Strategy:** tipi diversi di manutenzione (ordinaria, straordinaria)

```mermaid
classDiagram
    class ManutenzioneTemplate {
        <<abstract>>
    }
    class ManutenzioneOrdinaria
    class ManutenzioneStraordinaria
    class Mezzo

    ManutenzioneTemplate <|-- ManutenzioneOrdinaria
    ManutenzioneTemplate <|-- ManutenzioneStraordinaria
    Mezzo o-- ManutenzioneTemplate
```

---

# Sviluppo e Testing automatizzato

Sono stati sviluppati test automatici per:

- Creazione e modifica delle anagrafiche
- Pianificazione e modifica raccolte e rotte
- Generazione fatture e verifica pagamenti
- Integrazione tra raccolte, rotte e disponibilità mezzi

Il testing utilizza JUnit (o equivalente), è automatico e ripetibile, con test di unità e integrazione per le funzionalità core.

---

# Note di sviluppo

## Gestione anagrafiche

- **Uso avanzato di Stream e Optional**  
  *File:* `src/main/java/wastemaster/anagrafica/AnagraficaService.java`  
  ```java
  public Optional<Cliente> cercaClientePerEmail(String email) {
      return clienti.stream()
          .filter(cliente -> cliente.getEmail().equalsIgnoreCase(email))
          .findFirst();
  }
  ```
  *Permette ricerca e gestione sicura (null-safe) dei risultati.*

- **Validazione con annotazioni custom**  
  *File:* `src/main/java/wastemaster/anagrafica/Cliente.java`  
  ```java
  @NotNull
  @Email
  private String email;
  ```
  *Garantisce correttezza dati in input grazie all’uso di Bean Validation.*

- **Uso di generics per repository risorse**  
  *File:* `src/main/java/wastemaster/resources/ResourceRepository.java`  
  ```java
  public class ResourceRepository<T extends Risorsa> { ... }
  ```
  *Permette riuso e tipizzazione sicura nella gestione di diversi tipi di risorse.*

---

## Pianificazione raccolte

- **Algoritmo pianificazione dinamica**  
  *File:* `src/main/java/wastemaster/raccolta/PianificatoreRaccolte.java`  
  ```java
  raccolte.sort(Comparator.comparing(Raccolta::getPriorita).reversed());
  ```
  *Ottimizza l’assegnazione delle risorse in base a priorità e disponibilità.*

- **Integrazione raccolte speciali con calendario**  
  *File:* `src/main/java/wastemaster/raccolta/RaccoltaSpeciale.java`  
  ```java
  calendario.addEvento(this.getData(), "Raccolta Speciale: " + this.getDescrizione());
  ```
  *Gestione unificata eventi e raccolte straordinarie.*

- **Pattern Observer per monitoraggio raccolte**  
  *File:* `src/main/java/wastemaster/raccolta/Raccolta.java`  
  ```java
  private List<RaccoltaObserver> observers = new ArrayList<>();
  public void notificaStato() {
      observers.forEach(o -> o.aggiorna(this));
  }
  ```
  *Notifica automatica di avanzamento stato a dashboard e operatori.*

---

## Fatturazione e rotte

- **Generazione e invio automatico fatture**  
  *File:* `src/main/java/wastemaster/fatturazione/FatturazioneService.java`  
  ```java
  EmailSender.send(emailCliente, "La tua fattura", fattura.toString());
  ```
  *Automatizza comunicazione con il cliente.*

- **Algoritmo calcolo rotte con gestione imprevisti**  
  *File:* `src/main/java/wastemaster/rotte/PianificatoreRotte.java`  
  ```java
  return algoritmoOttimizzazione.calcola(mezzo, raccolte);
  ```
  *Ricalcola percorso in tempo reale in risposta a eventi imprevisti.*

- **Integrazione pagamenti e notifiche cliente**  
  *File:* `src/main/java/wastemaster/fatturazione/PagamentiService.java`  
  ```java
  fattura.setPagata(true);
  Notificatore.avvisaCliente(fattura.getCliente(), "Pagamento ricevuto. Grazie!");
  ```
  *Aggiornamento stato pagamenti e comunicazione tempestiva.*

---

## Dashboard, manutenzione, notifiche

- **Dashboard aggiornata via Observer**  
  *File:* `src/main/java/wastemaster/dashboard/Dashboard.java`  
  ```java
  raccolta.addObserver(this::aggiornaDashboard);
  ```
  *Aggiornamento automatico vista operativa.*

- **Gestione Template Method in manutenzione**  
  *File:* `src/main/java/wastemaster/manutenzione/ManutenzioneTemplate.java`  
  ```java
  public final void eseguiManutenzione() {
      verificaCondizioni();
      eseguiIntervento();
      aggiornaStorico();
  }
  ```
  *Sequenza fasi standard e personalizzazione per ciascun tipo di intervento.*

- **Notifiche avanzate con Decorator**  
  *File:* `src/main/java/wastemaster/notifiche/DecoratorMessaggio.java`  
  ```java
  Messaggio alert = new PrioritaDecorator(new MessaggioBase("Raccolta fallita!"));
  ```
  *Permette arricchimento dei messaggi di allerta.*

---

## Codice di terze parti o adattato

- L’invio email si basa su esempi della documentazione JavaMail.
- La validazione dati utilizza Hibernate Validator.
- Algoritmi di routing ispirati a esempi open source (Dijkstra, A*).
- L’uso di Observer e Decorator segue le best practice Java/GoF.

---

# Commenti finali

*(Sezione da compilare da ciascun membro: autovalutazione, punti di forza/debolezza, ruolo nel gruppo.)*

---

# Guida utente

1. **Accesso:** Login come operatore o amministratore.
2. **Gestione anagrafiche:**  
   - Aggiungi/modifica/elimina clienti e operatori.
3. **Raccolte:**  
   - Pianifica nuove raccolte, visualizza/modifica raccolte esistenti, gestisci raccolte speciali.
4. **Mezzi:**  
   - Visualizza stato, pianifica manutenzione.
5. **Fatture:**  
   - Genera/consulta fatture, registra pagamenti.
6. **Rotte:**  
   - Visualizza/modifica rotte assegnate.
7. **Dashboard:**  
   - Monitora statistiche operative e ricevi notifiche.

*Il sistema guida l’utente con form, messaggi di conferma ed errori. Le funzionalità opzionali (dashboard, notifiche, manutenzione avanzata) sono accessibili tramite l’apposito menu.*

---
