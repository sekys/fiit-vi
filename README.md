Projekt pre premet "Vyhladavanie informacii".
==

# Zadanie 
Freebase, Sparsovanie entít Person, vytvorenie jednoduchej služby 'mohli sa stretnúť?', 
Sktorá po zadaní dvoch mien určí, či sa mohli dané osoby stretnúť (prekryv času ich života). Ako Hadoop/Pig.

# Motivacia
Tento projekt je zaujimavy hlavnou myslienkou. Vytvorenim projektu by sme dokazali rychlo indetifikovat ci sa 2 osoby mohli stretnut.
Podobna sluzba by sa mohla pouzit ako nova funkcionalita pre wikipediu alebo ako ucebna pomocka. 
Podobne overenie stretnutia dvoch osob je mozne za pomoci wikidie vyhladavaca. Prave tym, ze si tie osoby najdeme a manualne porovname.
Nase riesenie je ale praktickejsie a hlavne rychlejsie. Overenie stretnutia dvoch osob moze byt na zaklade viacerych atributov.
My sme sa rozhodli pouzit iba zadany prekryv zivota podla casoveho atributu datumu narodenia a smrti. 

# Ciele
- Stiahnut FREEBASE udaje, analyzovat ich
- Vytvorit parsovanie udajov pre entitu Person, vytvorit testy
- Nainstalovat Hadoop, pripravit prototyp jednoduchej MapReduce ulohy 
- Vytvorit REST sluzbu na vyhladavanie
- Dopisat dokumentaciu, vykonat experiment

# Pouzitie nastroja
- Spustenie predspracovanie, oparsovanie atributov. Viac *Priebeh parsovania*
- Spustenie spajania atributov cez program "StructurePeople", viac *Priebeh spajania atributov*
- Spustenie zaindexovania udajov cez program "Persons2Lucene", viac *Vysledok*
- Nasadenie servletu "Servlet", pre spustenie vyhladavaca osob, viac *Vyhladavanie*

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
Nasledne prechadzame subor, s menami osob, pouzijeme binarne vyhladavanie podla ID, priradime vlastnost, teda meno.
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

# Overenie riesenia 
Parsovanie bolo overene, tym, ze pocet entit vydanych na stranke freebasu a pocet oparsovanych entit sedi.
Nasledne bolo nahodne vybranych 10 mien. Tieto mena boli vyhladane na freebase a v nasom vyhladavaci.
Sledovali sa spravne oparsovane atributy a spravne poradie vo vyhladavani.
Pre tento zoznam mien bolo vyhladavanie spravne a ich atributy tiez.

# Priklady mien
- Susanna Knapp
- Spencer Luxe
- Julija Leanzjuk (ma viac alternatyvnych mien)
- Edmund Hitler
- Elsa Einstein
- Aryan Khan (3 ludia maju to meno)
- Lukas Podolski
- Jozef Bemord (neexistuje)
- Bruce Willis
- Bruce Willis
- Elon Musk

# Zaujimavost
Pri vyhodnoteni projektu sme zistili, ze existuju ludia vo freebase, ktory maju datum a miesto narodenia 
ale su zaradeni do kategorie *common.topic*. Tieto osoby sme sa rozhodli  po konzultacii neindexovat.
Kedze medzi tento typ mozu patrit aj ine objekty, napriklad zvierata.
- Priklad mien: Bedrifelek Kadinefendi, Andrew Prince

# Freebase dump statistiky
* Komprimovany subor cca 30G.
* Rozbaleny subor cca 320G.
* Obsahuje 2,746,142,741 riadkov / tripletov.
* Pocet zaznamov Person 3,376,431 (30.09.2014) / 3,381,906 (17.10.2014) / 3,383,017 (27.10.2014)
* Pocet zaznamov datumu narodenia 1,384,628
* Pocet zaznamov datumu smrti 497,667
* Pocet zaznamov mien entit 65,208,024

# Pouzite nastroje
- Java 7
- Apache hadoop (zgrep)
- Apacha lucene ( pre indexovanie)
- Tomcat server (pre servlet vyhladavania)





