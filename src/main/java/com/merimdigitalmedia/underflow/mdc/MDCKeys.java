package com.merimdigitalmedia.underflow.mdc;

import java.util.Optional;

/**
 * MDCKeys.
 *
 * @author Pierre Adam
 * @since 21.09.28
 */
public interface MDCKeys {

    /**
     * The interface Queryable mdc key.
     */
    interface QueryableMDCKey extends MDCContext {
        /**
         * Gets key.
         *
         * @return the key
         */
        String getKey();

        /**
         * Gets from mdc.
         *
         * @return the from mdc
         */
        default Optional<String> getFromMDC() {
            return MDCContext.getInstance().getMDC(this.getKey());
        }

        /**
         * Put to mdc.
         *
         * @param value the value
         */
        default void putToMDC(final String value) {
            MDCContext.getInstance().putMDC(this.getKey(), value);
        }
    }

    /**
     * The enum Connection.
     */
    enum Connection implements QueryableMDCKey {
        /**
         * Io thread connection.
         */
        IO_THREAD("connection.ioThread"),
        /**
         * Peer address connection.
         */
        PEER_ADDRESS("connection.peerAddress");

        /**
         * The Key.
         */
        private final String key;

        /**
         * Instantiates a new Connection.
         *
         * @param key the key
         */
        Connection(final String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }

    /**
     * The enum Request.
     */
    enum Request implements QueryableMDCKey {
        /**
         * Uid request.
         */
        UID("request.uid"),
        /**
         * Method request.
         */
        METHOD("request.method"),
        /**
         * Url request.
         */
        URL("request.url"),
        /**
         * Query string request.
         */
        QUERY_STRING("request.queryString"),
        /**
         * Host name request.
         */
        HOST_NAME("request.hostName"),
        /**
         * Host port request.
         */
        HOST_PORT("request.hostPort"),
        /**
         * Body request.
         */
        BODY("request.body");

        /**
         * The Key.
         */
        private final String key;

        /**
         * Instantiates a new Request.
         *
         * @param key the key
         */
        Request(final String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }
}
