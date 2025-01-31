package zippyisms.common;

/**
 * Defines constants used through the program.
 */
public class Constants {
    /**
     * Port number listened upon by the controller.
     */
    public static final int SERVER_PORT = 10200;

    /**
     * The server resides on the localhost.
     */
    public static final String LOCAL_HOST = "localhost";

    /**
     * The server response is sent back to the client after
     * initialization.
     */
    public static final String SERVER_RESPONSE = "serverResponse";

    /*
     * These constants identify RSocket endpoint names.
     */
    public static final String SERVER_CONNECT = "serverInitializer";
    public static final String SUBSCRIBE = "subscribe";
    public static final String CANCEL_CONFIRMED = "cancelConfirmed";
    public static final String CANCEL_UNCONFIRMED = "cancelUnconfirmed";
    public static final String GET_ALL_QUOTES = "getQuotes";
    public static final String GET_QUOTES_SUBSCRIBED = "getQuoteSubscribed";
    public static final String GET_QUOTES_UNSUBSCRIBED = "getQuoteUnsubscribed";
    public static final String GET_NUMBER_OF_QUOTES = "getNumberOfQuotes";
}

