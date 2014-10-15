package sk.fiit.vi.parser;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 30. 9. 2014.
 * Datova struktura pre osobu.
 * Zoskupuje atributy.
 */
public class Person implements Serializable, Comparable<Person> {
    private static final long serialVersionUID = 5548062182445352621L;

    private String id;            // Jednoznacne ID
    private List<String> names; // osoba moze mat rozne mena v roznych jazykoch
    private DateTime birth;    // Datum narodenia
    private DateTime death;    // Datum smrti

    public Person(String id) {
        this.id = id;
        names = new ArrayList<>();
    }

    /**
     * Overenie prieniku casoveho bodu s prienikom intervalu
     *
     * @param point Casovy bod.
     * @param another Casovy interval.
     * @return Vysledok prieniku.
     */
    private static Boolean pointIntersection(DateTime point, Person another) {
        if (another.birth == null || another.death == null) {
            return null;
        }

        return (another.birth.isBefore(point) && another.death.isAfter(point));
    }

    /**
     * Porovnanie casoveho intervalu s intervalom
     *
     * @param a Osoba prva
     * @param b Osoba druha
     * @return Vysledok prieniku.
     */
    private static boolean intervalIntersection(Person a, Person b) {
        return pointIntersection(a.birth, b) == true || pointIntersection(a.death, b) == true;
    }

    public String getId() {
        return id;
    }

    public List<String> getNames() {
        return names;
    }

    public void addName(String name) {
        names.add(name);
    }

    public DateTime getBirth() {
        return birth;
    }

    public void setBirth(DateTime birth) {
        this.birth = birth;
    }

    public DateTime getDeath() {
        return death;
    }

    public void setDeath(DateTime death) {
        this.death = death;
    }

    /**
     * Porovnanie dvoch osob je len na zaklade ID
     *
     * @param o Druha osoba.
     * @return Su rovnake.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Person o) {
        return id.compareTo(o.getId());
    }

    /**
     * Kontrola ci osoba sa mohla stretnut s inou osobou.
     * Kontroluje sa len na zaklade datumu narodenia a smrti osob.
     *
     * @param another Druha osoba
     * @return NULL - Neda sa urcite vysledok. Inak vrati True / False
     */
    public Boolean checkIntersection(Person another) {
        if (equals(another)) {
            return true;  // sam so sebou
        }

        if (birth == null && death == null) {
            return null;   // nezistim odpoved
        }

        if (another.birth == null && another.death == null) {
            return null;  // nezistim odpoved
        }

        if (birth != null && death != null) {  // interval intersection
            if (another.birth != null && another.death != null) {
                // interval s intervalom
                return intervalIntersection(this, another);
            }

            if (another.death != null) {
                return pointIntersection(another.death, this);
            }

            if (another.birth != null) {
                return pointIntersection(another.birth, this);
            }
        }

        if (death != null) {
            return pointIntersection(death, another);
        }

        if (birth != null) {
            return pointIntersection(birth, another);
        }

        return false;
    }

    /**
     * Pomocna metoda na vypis atributu
     *
     * @return Retazec s atributmy personu.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("birth=").append(birth);
        sb.append(", death=").append(death);
        sb.append(", id='").append(id).append('\'');
        sb.append(", names=").append(names);
        sb.append('}');
        return sb.toString();
    }
}
