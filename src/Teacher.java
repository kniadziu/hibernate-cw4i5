import java.io.Serializable;
import java.util.Set;

public class Teacher implements Serializable {
    private long id;
    private String name;
    private String subject;
    private Set<SchoolClass> classes;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public Set<SchoolClass> getClasses() {
        return classes;
    }

    public void setId(long id) {

        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setClasses(Set<SchoolClass> classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", classes=" + classes +
                '}';
    }
}
