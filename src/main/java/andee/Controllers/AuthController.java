package andee.Controllers;

import andee.Services.UserService;
import andee.auth.JWTImplementation;
import andee.models.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static andee.Services.AuthService.respondWithToken;
import static andee.Services.UserService.*;

public class AuthController {
    private final static JWTImplementation jwtImplementation = JWTImplementation.getInstance();
    private static final ObjectMapper mapper = new ObjectMapper();


    public static void login(Context ctx) {
        String name = ctx.formParam("name", String.class).check(n -> n.length() > 3 && n.length() < 50).get();
        String password = ctx.formParam("password", String.class).check(p -> p.length() < 50).get();
        User user = getUser(name);
        if (user == null) {
            throw new UnauthorizedResponse("UserName is wrong");
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!result.verified) {
            throw new UnauthorizedResponse("Password is wrong");
        }
        String token = jwtImplementation.generateJwt(user);
        //  Response
        try {
            ctx.result(respondWithToken(user, token));
        } catch (IOException e) {
            ctx.status(500).result("parsing error");
        }
    }

    public static void register(Context ctx) {

        // Добаить email, give token
        String email = ctx.formParam("email", String.class).check(n -> n.length() < 50).get();
        String name = ctx.formParam("name", String.class).check(n -> n.length() > 3 && n.length() < 50).get();
        String password = ctx.formParam("password", String.class).check(p -> p.length() < 100).get();

        User userDb = getUser(name);
        if (userDb != null) {
            throw new BadRequestResponse("User already registered");
        }
        User user = createUser(name, password, email);
        if (user != null) {
            try {
                sendCodeToEmail(user.getEmail(), user.getCode());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.result("Can't send email");
            }
        }
        ObjectNode res = mapper.createObjectNode();
        res.put("token", jwtImplementation.generateJwt(user));
        try {
            ctx.result(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
        } catch (Exception e) {
            ctx.result("server error");
        }
    }

    public static void sendCodeToEmail(String userEmail, String code) throws Exception {
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("monitorserver68@gmail.com", "M?monitorserver68"));
        email.setSSLOnConnect(true);
        email.setFrom("monitorserver68@gmail.com");
        email.setSubject("verification code"); // subject from HTML-form
        email.setMsg("Your code is: " + code); // message from HTML-form
        email.addTo(userEmail);
        email.send();
    }

    public static void sendNewPasswordToEmail(String password, String userEmail) throws Exception {
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("monitorserver68@gmail.com", "M?monitorserver68"));
        email.setSSLOnConnect(true);
        email.setFrom("monitorserver68@gmail.com");
        email.setSubject("new password"); // subject from HTML-form
        email.setMsg("Your password is: " + password); // message from HTML-form
        email.addTo(userEmail);
        email.send();
    }

    public static void verifyEmail(Context ctx) {
        try {
            String name = jwtImplementation.getName(ctx);

            String code = ctx.formParam("code").toString();
            if (UserService.verifyEmail(name, code)) {
                ctx.result("Email successfully verified");
            } else {
                ctx.result("Email isn't verified");
            }
        } catch (Exception e) {
            ctx.result("Server error");
        }
    }

    public static void handleNewPassword(Context ctx) {
        try {
            String name = jwtImplementation.getName(ctx);
            User user = getUser(name);
            if(!user.isIs_verified()) {
                throw new BadRequestResponse("Email isn't verified");
            }
            String password = UserService.saveNewPassword(name);
            if (password != null) {
                sendNewPasswordToEmail(password, user.getEmail());
                ctx.result("Password successfully changed");
            } else {
                ctx.result("Password wasn't changed");
            }
        }
        catch (BadRequestResponse e) {
            throw e;
        }
        catch (Exception e) {
            ctx.result("Server error");
        }
    }

}
