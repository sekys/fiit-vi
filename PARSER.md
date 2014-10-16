Parsovanie
===

Pre oparsovanie freebase dumpu, ktory je vo formate RDF boli otestovane tieto techniky.

1. Prve oparsovanie prebehlo dekopresiou dumpu. Tato dekopresia trvala 3hod, vysledny subor zaberal 360G. Na tomto subore bol sputeny grep, ktory trval niekolko hodin. Vysledny subor sme este analyzovali 010 Editorom, ktory je urceny pre takto velke subory. Samotne nacitanie suboru do editoru trvalo vyse 2hodin.

2. Druha technika spocivala v pouziti Java programu, kde sa vyuzili regularne vyrazy. Program je v triede ParseDump2Parts.java. Program parsoval povodny komprimovany subor za pomoci GZIPInputStream a riadky boli oparsovane do 4 roznych suborov, podla typu. Parsovanie a dekompresia trvala 3hod.

3. Tretia technika vyuziva nastroj grep v systeme Hadoop. Prikaz na parsovanie datumov je uvdeny nizsie. Tento prikaz trval dlhsie
ako predchadzajuce prikazy. Zgrep prikaz nie je implementovany a tak sa musi parsovat dekomprimovany subor.

```Shell

%HADOOP_HOME%/bin/hadoop org.apache.hadoop.examples.Grep data out ".*<http://rdf\.freebase\.com/ns/people\.person\.date_of_birth>.*"

```

4. Dalsia technika spocivala vo vyuziti "zgrepu", ktory ma podobnu funkcionalitu ako grep s tym,
ze parsuje povodny zazipovany subor. Na tomto subore boli spustene 4 prikazy naraz:

```Shell

zgrep '\s<http://rdf\.freebase\.com/ns/type\.object\.name' dump.gz | gzip > names.gz
zgrep '\s<http://rdf\.freebase\.com/ns/people\.person\.date_of_birth' dump.gz | gzip > births.gz
zgrep '\s<http://rdf\.freebase\.com/ns/people\.deceased_person\.date_of_death' dump.gz | gzip > deceased_persons.gz
zgrep '\s<http://rdf\.freebase.com/ns/type\.object\.type>\s<http://rdf\.freebase.com/ns/people\.person>' people.gz | gzip > people2.gz

```

Parsovanie priamo s dekompresiou trvalo 1hod, pre vsetky prikazy spolu. Pricom vystup bol rozdeleny do suborov a boli komprimovane.
Zda sa, ze najlepsou technikou na parsovanie velkych suborov je teda zgrep.
Dovodom je, ze IO operacie disku su velmy narocne, preto sa oplati parsovat povodny komprimovany subor. 
Dalej zgrep bezal rychlejsie ako Java program, pretoze zgrep je na nizsej urovni ako Java, ktora bezi na virtualnom stroji.