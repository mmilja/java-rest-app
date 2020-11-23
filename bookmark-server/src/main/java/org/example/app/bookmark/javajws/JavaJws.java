package org.example.app.bookmark.javajws;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JavaJws implements IJavaJws {

    /**
     * Name of the jws token issuer.
     */
    public static final String JWS_ISSUER = "BookmarkApplication";

    /**
     * Singleton instance.
     */
    private static volatile JavaJws instance;

    /**
     * Map containing a user and a private key associated with that user.
     * Contains a list of active sessions.
     */
    private final Map<String, String> authorizedUsers;

    /**
     * Secret key used to verify and sign the jws signature.
     */
    private final SecretKey secretKey;

    /**
     * Private constructor.
     */
    private JavaJws() {
        this.authorizedUsers = new HashMap<>();
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Getter for the singleton.
     *
     * @return IRESTManager instance.
     */
    public static IJavaJws getInstance() {
        if (instance == null) {
            synchronized (JavaJws.class) {
                if (instance == null) {
                    instance = new JavaJws();
                }
            }
        }
        return instance;
    }

    @Override
    public Map.Entry<JwsStatus, String> createJws(final String username) {
        if (this.authorizedUsers.containsKey(username)) {
            return new AbstractMap.SimpleEntry<>(JwsStatus.ALREADY_EXISTS, "");
        } else {
            String uuid = UUID.randomUUID().toString();
            String jws = Jwts.builder().setIssuer(JWS_ISSUER).setSubject(username).setId(uuid)
                    .setIssuedAt(Date.from(Instant.now())).signWith(this.secretKey).compact();

            this.authorizedUsers.put(username, uuid);
            return new AbstractMap.SimpleEntry<>(JwsStatus.CREATED, jws);
        }
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
        if (authString == null || authString.isEmpty()) {
            return "";
        }
        try {
            Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(this.secretKey)
                    .requireIssuer(JWS_ISSUER).build().parseClaimsJws(authString);
            String subject = claim.getBody().getSubject();
            if (claim.getBody().getId().equals(this.authorizedUsers.get(subject))) {
                return subject;
            } else {
                return "";
            }
        } catch (JwtException ex) {
            return "";
        }
    }

    @Override
    public boolean checkUserLoggedIn(final String username) {
        return this.authorizedUsers.containsKey(username);
    }
}
