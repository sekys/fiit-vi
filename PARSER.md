Parsovanie
===

Pre parsovanie freebase dumpu, vo formate RDF boli otestovane tieto techniky.

1. Prve parsovanie prebehlo dekopresiou dumpu. Tato dekopresia trvala 3hod, vysledny subor zaberal 360G. Na tomto subore bol sputeny grep, ktory trval niekolko hodin. Vysledny subor sme este analyzovali 010 Eitorom, ktory je urceny pre takto velke subory. Samotne nacitanie suboru trvalo vyse 2hodin.

2. Druha technika spocivala v pouziti Java programu, kde sa vyuzili regularne vyrazy. Program je v triede Parser.java. Program parsoval povodny GZ subor za pomoci GZIPInputStream a riadky boli parsovane do 3 roznych suborov, podla typu. Parsovanie a dekompresia trvala 3hod.

3. Tretia technika spocivala vo vyuziti "zgrepu", ktory ma podobnu funkcionalitu ako grep s tym,
ze parsuje povodny zazipovany subor. Na tomto subore boli spustene 3 prikazy naraz:


zgrep '\s<http://rdf\.freebase\.com/ns/type\.object\.name' dump.gz | gzip > names.gz <br />
zgrep '\s<http://rdf\.freebase\.com/ns/people\.person\.date_of_birth' dump.gz | gzip > births.gz  <br />
zgrep '\s<http://rdf\.freebase\.com/ns/people\.deceased_person\.date_of_death' dump.gz | gzip > deceased_persons.gz  <br />
zgrep '\s<http://rdf\.freebase\.com/ns/people\.person>' dump.gz | gzip > people.gz <br />


Parsovanie s dekompresiou trvalo 1hod. Pricom riadky boli rozdelene do suborov a boli rovno zakompresovane.

Zda sa, ze najlepsou technikou na parsovanie velkych suborov je teda zgrep.
Dovodom je, ze IO operacie disku su velmy narocne, preto sa oplati parsovat povodny zazipovany subor. Dalej zgrep bezal rychlejsie ako Java program, pretoze zgrep je na nizsej urovni ako Java, ktora bezi na virtualnom stroji.