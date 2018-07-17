package win.leizhang.mqcommon.kafka.exception;

public class KafkaCommonException extends RuntimeException {

    private final static long serialVersionUID = 1L;

    public KafkaCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaCommonException(String message) {
        super(message);
    }

    public KafkaCommonException(Throwable cause) {
        super(cause);
    }

    public KafkaCommonException() {
        super();
    }

}
