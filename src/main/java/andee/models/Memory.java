package andee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name= "memory")
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name="wired")
    public int wired;
    @Column(name="free")
    public int free;
    @Column(name="active")
    public int active;
    @Column(name="inactive")
    public int inactive;
    @Column(name="total")
    public int total;
    /*@Column(name="message_id")
    public int message_id;*/

    @OneToOne
    @JoinColumn(name="message_id")
    @JsonBackReference
    public Message message;

    /*@Override
    public String toString() {
        return "Memory{" +
                "id=" + id +
                ", wired=" + wired +
                ", free=" + free +
                ", active=" + active +
                ", inactive=" + inactive +
                ", total=" + total +
                '}';
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWired() {
        return wired;
    }

    public void setWired(int wired) {
        this.wired = wired;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getInactive() {
        return inactive;
    }

    public void setInactive(int inactive) {
        this.inactive = inactive;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    /*@OneToMany
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private List<CPU> cpus;*/
}
