Projekt pre premet "Vyhladavanie informacii".
==

# Zadanie 
Freebase, Sparsovanie entít Person, vytvorenie jednoduchej služby 'mohli sa stretnúť?', ktorá po zadaní dvoch mien určí, či sa mohli dané osoby stretnúť (prekryv času ich života). Ako Hadoop/Pig.

# Ciele
- Stiahnut FREEBASE udaje, analyzovat ich
- Vytvorit parsovanie udajov pre entitu Person
- Nainstalovat Hadoop, pripravit prototyp jednoduchej MapReduce ulohy 
- Vytvorit REST sluzbu na vyhladavanie
- Dopisat dokumentaciu, vytvorit testy

#Freebase dump statistiky
* Kompriomovany subor cca 30G.
* Rozbaleny subor cca 300G.
* Obsahuje 2746142741 riadkov.
* Pocet zaznamov Person cca 3,5 miliona.

# Atributy osoby
Vo freebase kazda osoba ma viacero atributov. Nas bude zaujimat iba: *id, meno, datum narodenia a srmti*

# Priebeh parsovania
Cielom je vyparsovat udaje o ludoch v DUMPE.

Pricom parsovanie ma prebiehat tym sposobom, ze kazdy tribut bude v samostatnom subore.

Tento sposob je velmy vyhodny co sa ukaze neskor.

Pouzili sme rozne [sposoby parsovania](PARSER.md).

# Priebeh spajania atributov

Spajanie atributov prebieha v triede "StructurePeople.java".

Principom je nacitanie vsetkych ID. Zoradenie tychto ID.

Nasledne prechadzame subor, s datumami narodenin, pouzijeme binarne vyhladavanie podla ID, priradime vlastnost, teda datum.

Nasledne prechadzame subor, s datumami smrti, pouzijeme binarne vyhladavanie podla ID, priradime vlastnost, teda datum.

Nasledne prechadzame subor, s menami osob, pouzijeme binarne vyhladavanie podla ID, priradime vlastnost, teda datum.

Kazdy subor sa teda prechadza len raz. Binarne vyhladavanie je zase efektivne. Zaznamov nie je tak vela aby sa nezmestili do RAM.

# Vysledok
Vysledkom spajania atributov je strukturovana trieda Person, ktora obsahuje namapovane atributy.

Zoznam tychto osob je nasledne ulozeny pre dalsiu pracu (je serializovany).

Nasledne je tento zoznam poslany do Apache Lucene, ktory si vytvori index nad menami.

# Vyhladavanie
Vyhladavanie funguje ako REST sluzba. Sluzba bola vytvorena jednoduchym servletom.

Sluzba vyzaduje meno osoby A, meno osoby B. Nasledne tieto 2 osoby vyhlada v Apache Lucene.

Cim ziskame strukturovane, vyfiltrovane udaje o tychto osobach.

Nasledne je vypisany zoznam osob, pre meno A aj B.

Potom su tieto osoby porovname, teda sa skontroluje ci sa tieto osby mohli stretnut.



