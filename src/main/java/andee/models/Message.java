package andee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name= "message")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name="host")
    public String host;
    @Column(name="at")
    @Temporal(value= TemporalType.TIMESTAMP)
    public Date at;
    @Column(name="boot_time")
    @Temporal(value= TemporalType.TIMESTAMP)
    public Date boot_time;
    @Column(name="public_key")
    public String public_key;

    @OneToOne(fetch = FetchType.EAGER, mappedBy="message", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Memory memory;//= new HashSet<Memory>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="message", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<CPU> cpuSet = new HashSet<CPU>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="message", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Disk> disks = new HashSet<Disk>();

    @ManyToOne
    @JoinColumn(name="agent_id")
    @JsonBackReference
    public Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Set<CPU> getCpuSet() {
        return cpuSet;
    }

    public void setCpuSet(Set<CPU> cpuSet) {
        this.cpuSet = cpuSet;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    /*@Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", at=" + at +
                ", boot_time=" + boot_time +
                ", public_key='" + public_key + '\'' +
                '}';
    }*/

    /*@OneToMany(cascade = CascadeType.ALL,
            mappedBy="message_id")
    private List<Memory> memories;


    /*@ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "ID")
    private CPU teacher;*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public Date getBoot_time() {
        return boot_time;
    }

    public void setBoot_time(Date boot_time) {
        this.boot_time = boot_time;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public Set<Disk> getDisks() {
        return disks;
    }

    public void setDisks(Set<Disk> disks) {
        this.disks = disks;
    }
}
