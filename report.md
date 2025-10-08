
# Analisi

Il dominio riguarda la gestione operativa e amministrativa di un’azienda di smaltimento rifiuti. Le principali attività includono la pianificazione dei ritiri, l’organizzazione delle risorse (personale e mezzi), il monitoraggio dell’avanzamento operativo e la rendicontazione economica.

Gli attori principali sono i **Customer** (utenti serviti) e gli **Employee** (personale amministrativo o operativo). Le entità cardine del dominio sono **Schedule**, **Collection**, **Trip**, **Vehicle**, **Waste**, **Invoice**, con supporto di **WasteSchedule** per la calendarizzazione settimanale dei rifiuti e **Notification** per la gestione di segnalazioni operative.

Le pianificazioni dei ritiri possono essere **one-time** o **recurring**, con stati di avanzamento che riflettono la loro evoluzione. Le raccolte effettive (**Collection**) possono essere cancellate entro un limite temporale definito rispetto alla data prevista. I viaggi operativi (**Trip**) raggruppano più raccolte nella stessa area, associando veicoli e operatori in base a vincoli di licenza e capacità.

Il sistema prevede inoltre la possibilità di generare **Invoice** per i Customer aggregando le raccolte completate, distinguendo pagamenti effettuati o pendenti (**Invoice.PaymentStatus**). Le **Notification** vengono utilizzate per segnalare esiti e criticità nel flusso operativo.


## Requisiti

- **Gestione anagrafiche**: registrazione e aggiornamento di **Customer** e **Employee**, con **Employee.Role** (`ADMINISTRATOR | OFFICE_WORKER | OPERATOR`) e **Employee.Licence** (`NONE | B | C1 | C`); indirizzi rappresentati come **Location**.
- **Gestione mezzi**: amministrazione dei **Vehicle** con **Vehicle.RequiredLicence** (`B | C1 | C`), capacità in termini di numero minimo di operatori, stato (**Vehicle.VehicleStatus** = `IN_SERVICE | IN_MAINTENANCE | OUT_OF_SERVICE`) e manutenzioni pianificate.
- **Gestione rifiuti**: definizione dei **Waste** con attributi (`isRecyclable`, `isDangerous`) e pianificazione settimanale tramite **WasteSchedule** (associazione Waste ↔ `DayOfWeek`).
- **Pianificazione dei ritiri**: creazione e amministrazione di **Schedule** per un **Customer** e un **Waste**, in forma **OneTimeSchedule** (**ScheduleCategory.ONE_TIME**) o **RecurringSchedule** (**ScheduleCategory.RECURRING** con `Frequency` = `WEEKLY | MONTHLY`). Ogni Schedule ha uno **ScheduleStatus** (`ACTIVE | CANCELLED | PAUSED | COMPLETED`).
- **Esecuzione dei ritiri**: generazione di **Collection** con `collectionDate`, stato (**CollectionStatus** = `ACTIVE | COMPLETED | CANCELLED`), limite di cancellazione (`cancelLimitDays`) e indicazione di fatturabilità (`isBilled`).
- **Pianificazione dei viaggi**: organizzazione dei **Trip** per area (`postalCode`) con **assignedVehicle**, operatori assegnati, `departureTime`, `expectedReturnTime`, raccolte associate; stato **TripStatus** (`ACTIVE | COMPLETED | CANCELED`).
- **Monitoraggio avanzamento**: aggiornamento coerente degli stati di **Schedule**, **Collection** e **Trip**, con emissione di **Notification** in caso di esiti o criticità.
- **Fatturazione e pagamenti**: emissione di **Invoice** per **Customer**, aggregando le **Collection** associate, con valori come `amount`, `totalRecurring`, `totalOnetime`, `issueDate`, `paymentDate` e **Invoice.PaymentStatus** (`PAID | UNPAID`).
- **Regole trasversali**: controllo automatico della coerenza tra licenze richieste dai mezzi e licenze degli operatori assegnati, rispetto della capacità operativa e assenza di overbooking tra **Trip** e **Collection**.
