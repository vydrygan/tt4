package andee.Services;

import andee.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class AuthService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String respondWithToken(User user, String token) throws IOException {
        ObjectNode responseObject = mapper.createObjectNode();
        responseObject.put("token", token);
        //responseObject.set("user", mapper.readTree(mapper.writeValueAsString(user)));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseObject);
    }
}
