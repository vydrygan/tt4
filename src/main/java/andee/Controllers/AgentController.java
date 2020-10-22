package andee.Controllers;

import andee.Factory;
import andee.Main;
import andee.Services.AgentService;
import andee.auth.JWTImplementation;
import andee.models.Agent;
import andee.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static andee.Services.AgentService.getAgents;

public class AgentController {
    private final static JWTImplementation jwtImplementation = JWTImplementation.getInstance();
    private static final Set<String> AGENTS = new HashSet<String>(Arrays.asList(
            "t1-tss","t2-tss","t3-tss","t5-tss", "t6-tss", "gor-tss"));
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    public static void loadingHandler(Context ctx) {
        try{
            List l = getAgents();
            if(l != null) {
                String responseString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(l);
                ctx.result(responseString);
            } else {
                ctx.status(500).result("Database error occur");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
    public static void handleSingleAgent(Context ctx) {
        String agentName = ctx.pathParam("server", String.class)
                .check(AgentController::validAgent).get();
        try {
            ctx.result(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(AgentService.getSingleAgentMessages(agentName)));
        } catch (JsonProcessingException e) {
            ctx.result("parsing errror");
        }
    }

    public static void addAgent(Context ctx) {
        try {
            String public_key = ctx.formParam("public_key", String.class).get();
            String private_key = ctx.formParam("private_key", String.class).get();
            String name = jwtImplementation.getName(ctx);
            boolean is_successful = AgentService.addAgent(name, public_key, private_key);
            if(is_successful) {
                Object[] objects = AgentService.getAgentsListForUser(name);
                System.out.println("objects" + objects);
                ctx.result(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objects));
            } else {
                ctx.result(mapper.writerWithDefaultPrettyPrinter().writeValueAsString("Is not created"));
            }
        } catch (Exception e){
            e.printStackTrace();
            ctx.result("Parsing error");
        }

    }

    public static boolean validAgent(String agent) {
        return AGENTS.contains(agent.toString());
    }
}
