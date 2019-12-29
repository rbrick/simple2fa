package io.dreamz.simple2fa.session;

public interface Session {

    /**
     * Checks if the session is authenticated
     *
     * @return Whether or not the session has been authenticated
     */
    boolean isAuthenticated();

    /**
     * Checks if the session needs authentication or not
     *
     * @return Whether or not the session needs to be authenticated
     */
    boolean needsAuthentication();


}
