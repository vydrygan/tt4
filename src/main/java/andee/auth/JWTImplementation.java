package andee.auth;

import andee.models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;

import java.util.Optional;

public class JWTImplementation {
    private static JWTImplementation instance;
    private static final Algorithm algorithm = Algorithm.HMAC256("very_secret");

    private static final JWTGenerator<User> generator = (user, alg) -> {
        JWTCreator.Builder token = JWT.create()
                .withClaim("name", user.getName());
        return token.sign(algorithm);
    };
    private static final JWTVerifier verifier = JWT.require(algorithm).build();
    private static final JWTProvider provider = new JWTProvider(algorithm, generator, verifier);

    public Handler validateHandler = context -> {
        Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                .flatMap(provider::validateToken);

        if (!decodedJWT.isPresent()) {
            throw new UnauthorizedResponse("Missing or invalid token");
            //context.status(401).result("Missing or invalid token");
        }
        /*else {
            context.result("Hi " + decodedJWT.get().getClaim("name").asString());
        }*/
    };

    public String getName(Context context) {
        Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                .flatMap(provider::validateToken);

        return decodedJWT.get().getClaim("name").asString();
    };


     /**
      *  Empty constuctor
     * */
    private JWTImplementation() {}

    /**
     * @return isntance instance for generation jwt
     * */
    public static JWTImplementation getInstance() {
        if (instance == null) {
            instance = new JWTImplementation();
        }
        return instance;
    }


    public String generateJwt(User user) {
        return provider.generateToken(user);
    }

}
