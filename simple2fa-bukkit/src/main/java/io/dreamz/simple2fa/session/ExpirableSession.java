package io.dreamz.simple2fa.session;

public interface ExpirableSession extends Session {
    /**
     * The time, in milliseconds when this session will expire.
     * @return The expiration time
     */
    long expireAt();

    /**
     * Checks if the session is expired
     * @return true if the session is expired.
     */
    boolean isExpired();
}
