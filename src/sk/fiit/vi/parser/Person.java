package sk.fiit.vi.parser;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class Person implements Serializable, Comparable<Person> {
    private String id;
    private String name;
    private
    DateTime birth;
    private
    DateTime death;

    public Person(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("birth", birth).append("death", death).append("id", id).append("name", name).toString();
    }

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
}
