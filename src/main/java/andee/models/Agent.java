package andee.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name= "agents")
public class Agent {
    @Id
    public String id;

    @Column(name="agent_version")
    public String agent_version;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="agent", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    @JsonManagedReference
    private Set<Message> messageSet = new HashSet<Message>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "agent_user",
            joinColumns = { @JoinColumn(name = "agent_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> users = new HashSet<>();

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgent_version() {
        return agent_version;
    }

    public void setAgent_version(String agent_version) {
        this.agent_version = agent_version;
    }

    public Set<Message> getMessageSet() {
        return messageSet;
    }

    public void setMessageSet(Set<Message> messageSet) {
        this.messageSet = messageSet;
    }
}
