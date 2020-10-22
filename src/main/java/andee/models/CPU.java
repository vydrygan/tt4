package andee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name= "cpu")
public class CPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name="num")
    public double num;
    @Column(name="user")
    public double user;
    @Column(name="`system`")
    public double system;
    @Column(name="idle")
    public double idle;
    @ManyToOne
    @JoinColumn(name="message_id")
    @JsonBackReference
    public Message message;

    /**
     * @return id id of user
     * */
    public int getId() {
        return id;
    }
    /**
     * @param id id of cpu
     * */
    public void setId(int id) {
        this.id = id;
    }

    public double getNum() {
        return num;
    }
    /**
     * @param num mun of cpu
     * */
    public void setNum(double num) {
        this.num = num;
    }
    /**
     * @return user of cpu
     * */
    public double getUser() {
        return user;
    }

    /**
     * @param user of cpu
     * */
    public void setUser(double user) {
        this.user = user;
    }

    /**
     * @return system of cpu
     * */
    public double getSystem() {
        return system;
    }

    /**
     * @param  system set system of cpu
     * */
    public void setSystem(double system) {
        this.system = system;
    }
    /**
     * @return idle set idle of cpu
     * */
    public double getIdle() {
        return idle;
    }

    /**
     * @param idle set idle of cpu
     * */
    public void setIdle(double idle) {
        this.idle = idle;
    }

    /**
     * @return message set message of cpu
     * */
    public Message getMessage() {
        return message;
    }
    /**
     * @param message set message of cpu
     * */
    public void setMessage(Message message) {
        this.message = message;
    }
}
