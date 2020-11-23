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
