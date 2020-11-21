package org.example.app.bookmark.javajws;

/**
 * Class implementing java jws token object.
 */
public class JavaJwsToken {

    /**
     * String containing the actual jws token.
     */
    private String jwsToken;

    /**
     * Status of the token.
     */
    private JwsStatus jwsStatus;

    /**
     * Wraps jws token and status during creation of the token in a object.
     *
     * @param jwsToken created jws token.
     * @param jwsStatus status of the creation of the jws token.
     */
    public JavaJwsToken(String jwsToken, JwsStatus jwsStatus) {
        this.jwsToken = jwsToken;
        this.jwsStatus = jwsStatus;
    }

    /**
     * Wraps jws token and status during creation of the token in a object.
     * Used when jws token was not created successfully.
     *
     * @param jwsStatus status of the creation of the jws token.
     */
    public JavaJwsToken(JwsStatus jwsStatus) {
        this.jwsToken = "";
        this.jwsStatus = jwsStatus;
    }

    /**
     * Default constructor.
     * Sets the status to failed.
     */
    public JavaJwsToken() {
        this.jwsToken = "";
        this.jwsStatus = JwsStatus.FAILED;
    }

    /**
     * Get the jws token.
     *
     * @return jws token.
     */
    public String getJwsToken() {
        return jwsToken;
    }

    /**
     * Set the jws token.
     *
     * @param jwsToken to set.
     */
    public void setJwsToken(String jwsToken) {
        this.jwsToken = jwsToken;
    }

    /**
     * Get jws token status.
     *
     * @return jws token status.
     */
    public JwsStatus getJwsStatus() {
        return jwsStatus;
    }

    /**
     * Set jws token status.
     *
      * @param jwsStatus to set.
     */
    public void setJwsStatus(JwsStatus jwsStatus) {
        this.jwsStatus = jwsStatus;
    }
}
