package parser;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class Person {
    private String id;
    private String name;
    private
    @Nullable
    DateTime birth;
    private
    @Nullable
    DateTime death;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public DateTime getBirth() {
        return birth;
    }

    public void setBirth(@Nullable DateTime birth) {
        this.birth = birth;
    }

    @Nullable
    public DateTime getDeath() {
        return death;
    }

    public void setDeath(@Nullable DateTime death) {
        this.death = death;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("birth", birth).append("death", death).append("id", id).append("name", name).toString();
    }
}
