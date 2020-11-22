package org.example.app.bookmark.javajws;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.utils.IUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JavaJws implements IJavaJws {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(JavaJws.class.getSimpleName());

    /**
     * Name of the jws token issuer.
     */
    public static final String JWS_ISSUER = "BookmarkApplication";

    /**
     * Singleton instance.
     */
    private static volatile JavaJws instance;

    /**
     * Object containing utility methods.
     */
    private final IUtils utils;

    /**
     * Map containing a user and a private key associated with that user.
     * Contains a list of active sessions.
     */
    private final Map<String, String> authorizedUsers;

    /**
     * Private key used to verify the jws signature.
     */
    private final PrivateKey privateKey;

    /**
     * Public key used to sign the jws.
     */
    private final PublicKey publicKey;

    /**
     * Private constructor.
     *
     * @param utils object containing utility methods.
     */
    private JavaJws(final IUtils utils) {
        this.utils = utils;

        this.authorizedUsers = new HashMap<>();
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    /**
     * Convenience getter.
     *
     * @return IBookmarkManager instance.
     */
    public static IJavaJws getInstance() {
        return instance;
    }

    /**
     * Getter for the singleton.
     *
     * @param utils object containing utility methods.
     * @return IRESTManager instance.
     */
    public static IJavaJws getInstance(final IUtils utils) {
        if (instance == null) {
            synchronized (JavaJws.class) {
                if (instance == null) {
                    instance = new JavaJws(utils);
                }
            }
        }
        return instance;
    }

    @Override
    public JavaJwsToken createJws(final String username) {
        JavaJwsToken javaJwsToken = new JavaJwsToken();

        if (this.authorizedUsers.containsKey(username)) {
            javaJwsToken.setJwsStatus(JwsStatus.ALREADY_EXISTS);
        } else {
            String uuid = UUID.randomUUID().toString();
            String jws = Jwts.builder().setIssuer(JWS_ISSUER).setSubject(username).setId(uuid)
                    .setIssuedAt(Date.from(Instant.now())).signWith(privateKey).compact();

            this.authorizedUsers.put(username, uuid);
            javaJwsToken.setJwsToken(jws);
            javaJwsToken.setJwsStatus(JwsStatus.CREATED);
        }
        return  javaJwsToken;
    }

    @Override
    public JwsStatus abolishJws(final String userName, final String authString) {
        if (this.authorizedUsers.containsKey(userName)) {
            if (this.authorizeUser(authString).equals(userName)) {
                this.authorizedUsers.remove(userName);
                return JwsStatus.REMOVED;
            } else {
                return JwsStatus.UNAUTHORIZED;
            }
        } else {
            return JwsStatus.NO_SESSION;
        }
    }

    @Override
    public String authorizeUser(final String authString) {
        try {
             Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(this.privateKey)
                     .requireIssuer(JWS_ISSUER).build().parseClaimsJws(authString);
             String subject = claim.getBody().getSubject();
             if (claim.getBody().getId().equals(this.authorizedUsers.get(subject))) {
                 return subject;
             } else {
                 return "";
             }
        } catch(JwtException ex) {
            return "";
        }
    }

    public boolean checkUserLoggedIn(final String username) {
        return this.authorizedUsers.containsKey(username);
    }
}
