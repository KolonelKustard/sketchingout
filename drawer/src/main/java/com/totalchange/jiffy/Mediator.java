package com.totalchange.jiffy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class Mediator {
    private static final long DEFAULT_TIMEOUT = 4000;

    private static Log log = LogFactory.getLog(Mediator.class);

    private static Mediator instance = new Mediator();

    private HashMap mates = new HashMap();
    private long timeout = DEFAULT_TIMEOUT;

    private Mediator() {
        // Does bugger all
    }

    private final class Pair {
        Object mate1 = null;
        Object mate2 = null;
    }

    Object getMate(Object id, Object me) throws MediatorException {
        Pair pair = null;

        if (log.isDebugEnabled()) {
            log.debug("Fetching mate with id: " + id);
        }

        synchronized (this) {
            // First up see if this mate is already here
            pair = (Pair) mates.get(id);
            if (pair != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Mate is already here - returning  "
                            + pair.mate1);
                }

                // Yeee haw - set me in there, let the originator know about
                // me and return the originator
                pair.mate2 = me;
                Object mate = pair.mate1;

                synchronized (pair) {
                    pair.notifyAll();
                }
                return mate;
            }

            // If get here then mate isn't here yet - so need to create a pair
            // in waiting
            if (log.isDebugEnabled()) {
                log.debug("Mate isn't here yet - making somewhere for " + me
                        + " to wait in");
            }
            pair = new Pair();
            pair.mate1 = me;
            mates.put(id, pair);
        }

        try {
            // Have to sit and wait on our pair
            log.debug("Waiting for my mate - ho hum");
            synchronized (pair) {
                pair.wait(timeout);
            }

            // If get here but don't know our mate then we know we timed out
            if (pair.mate2 == null) {
                log.info("Timed out waiting for mate with id: " + id);
                throw new MediatorException("Timed out looking for our mate");
            }

            // Return our mate - job done baby
            if (log.isDebugEnabled()) {
                log.debug("Mate " + pair.mate2 + " has turned up at last");
            }
            return pair.mate2;
        } catch (InterruptedException inEx) {
            log.warn("Argh - interrupted!", inEx);
            throw new MediatorException(inEx);
        } finally {
            // Get rid of this entry - no need to block as is all over for this
            // id.
            mates.remove(id);
        }
    }

    InputStream getInputStream(String id) {
        return null;
    }

    OutputStream getOutputStream(String id) {
        return null;
    }

    long getTimeout() {
        return timeout;
    }

    void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    static Mediator getInstance() {
        return instance;
    }
}

final class MediatorException extends Exception {
    private static final long serialVersionUID = 2917426838805465164L;

    public MediatorException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public MediatorException(String arg0) {
        super(arg0);
    }

    public MediatorException(Throwable arg0) {
        super(arg0);
    }
}
