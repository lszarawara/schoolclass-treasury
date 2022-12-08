# Skarbnik klasowy
> Aplikacja do obsługi budżetów klasowych. Dla skarbników i rodziców - umożliwia wysyłanie informacji o zbiórkach, rozliczanie wpłat i wydatków, podgląd aktualnych sald i składających się na nie transakcji.

## Informacje ogólne
- Projekt końcowy kursu Java od podstaw realizowanego w 2022 (marzec - listopad) roku w SDA
- Zainspirowana potrzebami skarbnika klasowego

## Wykorzytsane technologie
- Spring Boot - version 2.7.4
- Bootstrap - version 5.2.0
- Thymeleaf - version 3.0.11


## Funkcjonalności
Wdrożone funkcjonalności:
- Tworzenie konta skarbnika -> Tworzenie klas -> Tworzenie dzieci -> Tworzenie / dołączanie rodziców
- Mailowe powiadamienie o utworzeniu konta rodzica / utworzeniu składki
- Rozliczanie wpłat
- Rozliczanie wydatków w różnych scenariuszach


## Wykorzystanie
Po pobraniu projektu sugeruję uruchomienie konfiguracji demo wykorzystującej bazę H2.
Po uruchomieniu aplikacji (dla demo: http://localhost:8083/) proponuję zalogowanie:

a) jako admin login i hasło: admin,

b) jako skarbnik login i hasło: drugi,

c) jako rodzic login i hasło: trzeci.

Do wykorzystania funkcjonalności wysyłki mailowych powiadomień konieczne jest skonfigurowanie konta  w application-properties


## Status projektu
Projekt na etapie ukończenia implementacji wg wstępnych założeń, do dalszego rozwoju


## Podziękowania
- Trenerom z SDA za ukazanie świata javy i okolic
- Patrykowi Marciszowi za wsparcie w stworzeniu tego projektu


## Kontakt
Aplikację napisał [lukasz.szarawara@gmail.com](mailto:lukasz.szarawara@gmail.com) - zapraszam do kontaktu!

