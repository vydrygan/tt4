package andee.Services;

import andee.Factory;
import andee.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import org.hibernate.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;

public class MessageService {
    private static SessionFactory factory = Factory.getInstance();
    private final static JSONParser parser = new JSONParser();

    public static Message getMessage () {
        Session session = factory.openSession();
        Transaction tx = null;
        Message message = null;

        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("from Message order by id DESC");
            query.setMaxResults(1);
            message = (Message) query.uniqueResult();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return message;
    }
    public static void addMessage (String body) {
        Session session = factory.openSession();
        Transaction tx = null;
        Agent agent = null;

        try {
            tx = session.beginTransaction();
            JSONObject json = (JSONObject) parser.parse(body);
            agent = AgentService.getAgent(body);
            if(agent == null) {
                throw new BadRequestResponse();
            }

            Message message = new Message();
            message.setAgent(agent);
            //message.setHost(json.get("host").toString());
            message.setPublic_key(json.get("public_key").toString());
            message.setBoot_time(new Date());
            message.setAt(new Date());
            session.save(message);
            JSONArray cpuJsonArray = (JSONArray) json.get("cpu");
            for (Object object: cpuJsonArray) {
                if ( object instanceof JSONObject ) {
                    JSONObject cpu = (JSONObject)object;
                    ObjectMapper mapper = new ObjectMapper();
                    CPU cpuModel = new CPU();
                    //memory.setInactive((Integer) );
                    cpuModel.setMessage(message);
                    //System.out.println(cpu.get("system"));
                    System.out.println(Double.valueOf(cpu.get("system").toString()));
                    cpuModel.setSystem(Double.parseDouble(cpu.get("system").toString()));
                    cpuModel.setNum(Double.parseDouble(cpu.get("num").toString()));
                    cpuModel.setUser(Double.parseDouble(cpu.get("user").toString()));
                    cpuModel.setIdle(Double.parseDouble(cpu.get("idle").toString()));
                    cpuModel.setMessage(message);
                    session.save(cpuModel);
                }
            }

            // load
            JSONArray disksJson = (JSONArray) json.get("disks");
            for (Object object: disksJson) {
                if ( object instanceof JSONObject ) {
                    JSONObject diskJsonObject = (JSONObject)object;
                    Disk disk = new Disk();
                    disk.setOrigin(diskJsonObject.get("origin").toString());
                    Double d = (Double.parseDouble(diskJsonObject.get("free").toString()));
                    disk.setFree(d.intValue());
                    Double dd = (Double.parseDouble(diskJsonObject.get("total").toString()));
                    disk.setTotal(dd.intValue());
                    disk.setMessage(message);
                    session.save(disk);
                }
            }

            JSONObject memoryJson = (JSONObject) json.get("memory");
            Memory memory = new Memory();
            memory.setMessage(message);
            memory.setWired(((Long)memoryJson.get("wired")).intValue());
            memory.setFree(((Long)memoryJson.get("free")).intValue());
            memory.setActive(((Long)memoryJson.get("active")).intValue());
            memory.setInactive(((Long)memoryJson.get("inactive")).intValue());
            memory.setTotal(((Long)memoryJson.get("total")).intValue());

            session.save(memory);
            tx.commit();

        } catch (HibernateException | ParseException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
