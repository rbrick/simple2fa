package io.dreamz.simple2fa.session;

public interface ExpirableSession extends Session {
    long duration();

    long expireTime();

    boolean isExpired();
}
