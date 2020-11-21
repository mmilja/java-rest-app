package org.example.app.bookmark.javajws;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.utils.IUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Date;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class JavaJws implements IJavaJws {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(JavaJws.class.getSimpleName());

    /**
     * Standard charset encoding assumed for files.
     */
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Name of the jws token issuer.
     */
    public static final String JWS_ISSUER = "BookmarkApplication";

    /**
     * Property used to set a key ID parameter in header.
     */
    public static final String KEY_ID_PARAM = "kid";

    /**
     * Duration of the jws in seconds.
     */
    public static final long JWS_DURATION = 36000L;

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
    private final Map<String, Map.Entry <UUID, PrivateKey>> userKeyMap;

    /**
     * Private constructor.
     *
     * @param utils object containing utility methods.
     */
    private JavaJws(final IUtils utils) {
        this.utils = utils;

        this.userKeyMap = new HashMap<>();
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

        if (userKeyMap.containsKey(username)) {
            javaJwsToken.setJwsStatus(JwsStatus.ALREADY_EXISTS);
        } else {
            UUID userId = UUID.randomUUID();
            KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            Map.Entry<UUID, PrivateKey> userEntry = new AbstractMap.SimpleEntry<>(userId, privateKey);
            this.userKeyMap.put(username, userEntry);

            String jws = Jwts.builder().setIssuer(JWS_ISSUER).setSubject(username).setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plusSeconds(JWS_DURATION)))
                    .setHeaderParam(KEY_ID_PARAM, userId).signWith(publicKey).compact();

            javaJwsToken.setJwsToken(jws);
            javaJwsToken.setJwsStatus(JwsStatus.CREATED);
        }
        return  javaJwsToken;
    }

    @Override
    public boolean abolishJws(final String userName, final String authString) {
        if (userKeyMap.containsKey(userName)) {
            if (decryptJws(userName, authString)) {
                userKeyMap.remove(userName);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean authorizeUser(final String authString, final UUID userUuid) {
        try {
            PrivateKey key = null;
            Iterator<Map.Entry<String, Map.Entry<UUID, PrivateKey>>> mapIterator =
                    this.userKeyMap.entrySet().stream().iterator();
            while(mapIterator.hasNext()) {
                Map.Entry<UUID, PrivateKey> entry = mapIterator.next().getValue();
                if (entry.getKey() == userUuid) {
                    key = entry.getValue();
                }
            }
            if (key == null) {
                return false;
            }
             Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authString);
            return true;
        } catch(JwtException ex) {
            return false;
        }
    }

    /**
     * Decrypt jws and confirm that the subject and the user are the same.
     *
     * @param userName of the session owner.
     * @param authString of the session owner.
     * @return true is decryption is successful.
     */
    private boolean decryptJws(final String userName, final String authString) {
        PrivateKey privateKey = this.userKeyMap.get(userName).getValue();
        try {
            Jwts.parserBuilder().setSigningKey(privateKey)
                    .requireIssuer(JWS_ISSUER)
                    .requireSubject(userName)
                    .build().parseClaimsJws(authString);
            return true;
        } catch(JwtException ex) {
            return false;
        }
    }
}
