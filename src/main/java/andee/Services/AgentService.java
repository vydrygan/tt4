package andee.Services;

import andee.Factory;
import andee.models.Agent;
import andee.models.User;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class AgentService {
    private static SessionFactory factory = Factory.getInstance();
    private final static JSONParser parser = new JSONParser();
    public static Map<String, String> articleMapOne;

    static {
        articleMapOne = new HashMap<>();
        articleMapOne.put("t1-tss", "6c3e621d1d3fc0d67a8697701d29e4eb555dd812a666e877bead6271bc68bb94");
        articleMapOne.put("t2-tss", "d2dd252f7a3a52b44b2f1e3bc9a47d001ad6e43e5792e02642ef7b62f15522ff");
        articleMapOne.put("t3-tss", "bb96444315bf637340d8e8c3d9ad53e983c34ac4f6427be9bef99f20787c392a");
        articleMapOne.put("t5-tss", "4d7d3d71d9273838f7cf6dde50dd7760ab248b70490fa8b183ced1702fa8a054");
        articleMapOne.put("t6-tss", "a82af4e96b5b8d825a6910b908eccdc4401af1e663db5edf2d7dfb712db96529");
        articleMapOne.put("gor-tss", "D96B6E76CB921F0AA7E981C55BC88C3D184D3C0232D1B1C297B9260C6387263B");

    }

    public static Agent getSingleAgentMessages(String agentName) {
        Session session = factory.openSession();
        Transaction tx = null;
        Agent agent = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(Agent.class);
            agent = (Agent) criteria.add(Restrictions.eq("id", agentName)).uniqueResult();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agent;
    }
    public static List getAgents() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Agent> agents = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(Agent.class);
            agents =  criteria.list();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agents;
    }
    public static Agent getAgent(String body) {
        Session session = factory.openSession();
        Transaction tx = null;
        Agent agent = null;
        try {
            tx = session.beginTransaction();
            JSONObject json = (JSONObject) parser.parse(body);
            String host = json.get("host").toString();
            Criteria criteria = session.createCriteria(Agent.class);
            agent = (Agent) criteria.add(Restrictions.eq("id", host)).uniqueResult();
            if(agent == null) {
                agent = new Agent();
                agent.setId(host);
                agent.setAgent_version(json.get("agent_version").toString());
                session.save(agent);
                tx.commit();
            }

        } catch (HibernateException | ParseException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agent;
    }

    public static Object[] getAgentsListForUser(String userName) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        Object[] objects = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(User.class);
            user = (User) criteria.add(Restrictions.eq("name",
                    userName)).uniqueResult();
            if(user != null ) {
                objects = user.getAgents().stream().map((agent1 -> agent1.id)).toArray();
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return objects;
    }

    public static boolean addAgent(String userName, String public_key, String private_key) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        Agent agent = null;
        Object[] objects = null;
        try {
            tx = session.beginTransaction();
            String str = articleMapOne.get(public_key);
            if(private_key.equals(str)) {
                Criteria criteria = session.createCriteria(User.class);
                user = (User) criteria.add(Restrictions.eq("name", userName)).uniqueResult();

                Criteria criteriaa = session.createCriteria(Agent.class);
                agent = (Agent) criteriaa.add(Restrictions.eq("id", public_key)).uniqueResult();
                Set<User> set = agent.getUsers();
                if(set == null) {
                    set = new HashSet<>();
                }
                set.add(user);
                agent.setUsers(set);
                tx.commit();
                return true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }
}
