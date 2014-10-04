package sk.fiit.vi.parser;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class Person implements Serializable, Comparable<Person> {
    private static final long serialVersionUID = 5548062182445352621L;

    private String id;
    private List<String> names;
    private DateTime birth;
    private DateTime death;

    public Person(String id) {
        this.id = id;
        names = new ArrayList<>();
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


    private static Boolean pointIntersection(DateTime point, Person another) {
        if (another.birth == null || another.death == null) {
            return null;
        }

        return (another.birth.isBefore(point) && another.death.isAfter(point));
    }

    private static boolean intervalIntersection(Person a, Person b) {
        return pointIntersection(a.birth, b) == true || pointIntersection(a.death, b) == true;
    }

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
