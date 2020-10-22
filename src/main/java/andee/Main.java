package andee;

import andee.Controllers.AgentController;
import andee.Controllers.AuthController;
import andee.Services.AgentService;
import andee.auth.JWTImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;


import static andee.Services.MessageService.addMessage;

public class Main {

    private final static JWTImplementation jwtImplementation = JWTImplementation.getInstance();
    private static final ObjectMapper mapper = new ObjectMapper();



    private static SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(Main.class.getResource("/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }

    public static void main(String[] args) {
        /**
         * @param con configuration of server
         * */
        Javalin app = Javalin.create(con -> {
            con.addStaticFiles("/public");
            con.addSinglePageRoot("/", "/public/index.html");
            con.addSinglePageRoot("/", "/public/index.html");

            con.server(() -> {
                Server server = new Server();
                ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                sslConnector.setPort(443);
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(7000);
                server.setConnectors(new Connector[]{sslConnector, connector});
                return server;
                /*Server server = new Server();
                connector.setPort(7000);
                server.addConnector(connector);
                return server;*/
            });
        }).start();

        /**
         * @param path path string
         * @param ctx context of app
         * */
        app.post("/login", AuthController::login);
        /**
         * @param path path string
         * @param ctx context of app
         * */
        app.post("/register", AuthController::register);
        app.post("/email", AuthController::verifyEmail);
        app.post("/password", AuthController::handleNewPassword);


        /**
         * @param path path string
         * @param ctx context of app
         * */
        app.post("/api/endpoint", ctx -> {
            addMessage(ctx.body());
            ctx.result("ok");
        });

        /**
         * @param string loading string validator

         * */
        app.before("/email", jwtImplementation.validateHandler);
        app.before("/loading", jwtImplementation.validateHandler);
        app.before("/password", jwtImplementation.validateHandler);
        app.before("/agents", jwtImplementation.validateHandler);
        app.before("/addagent", jwtImplementation.validateHandler);

        /**
         * @param path path string
         * @param ctx context of app
         * */
        app.get("/loading", AgentController::loadingHandler);

        app.get("/loading/:server", AgentController::handleSingleAgent);

        app.post("/addagent", AgentController::addAgent);


        app.get("/agents", ctx -> {
            String name = jwtImplementation.getName(ctx);
            Object[] objects = AgentService.getAgentsListForUser(name);
            if(objects != null) {
                ctx.result(mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(objects));
            } else {
                ctx.result("[]");
            }
        });
    }




}