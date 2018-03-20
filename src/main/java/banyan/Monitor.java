package banyan;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 This class subscribes to all messages on the back plane and prints out both topic and payload.
 */
public class Monitor extends BanyanBase {
    /**
     *This class subscribes to all messages on the back plane and prints out both topic and payload.
     * @param backplaneIpAddress: IP address of the currently running backplane
     * @param subscriberPort: subscriber port number - matches that of backplane
     * @param publisherPort: publisher port number - matches that of backplane
     * @param processName: identifier string
     */
    @SuppressWarnings("WeakerAccess")
    Monitor(String backplaneIpAddress, String subscriberPort,
            String publisherPort, String processName) {

        super(backplaneIpAddress, subscriberPort, publisherPort, processName);


        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("interrupted exception");
        }

        this.set_subscriber_topic("");

        this.receive_loop();
    }

    /**
     * This method overrides the inherited method from BanyanBase
     * @param topic: message topic
     * @param payload: message payload
     */
    public void incoming_message_processing(String topic, HashMap payload) {

        System.out.print(topic);

        System.out.print("{" );

        for (Object o : payload.keySet()) {
            String key = o.toString();
            Object value = payload.get(key);

            System.out.print(" " + key + "," + value);
        }
        System.out.println(" }");

    }

    /**
     *
     * @param args: command line arguments
     *            -b Backplane IP Address
     *            -n Montior Name
     *            -p Publisher IP Port
     *            -s Subscriber IP Port
     * @throws ArgumentParserException:
     */
    public static void main(String[] args)throws ArgumentParserException {



        ArgumentParser parser = ArgumentParsers.newArgumentParser("monitor")
                .description("Java Banyan Monitor");
        parser.addArgument("-b").help("Backplane IP Address");
        parser.addArgument("-n").setDefault("Banyan Monitor").help("Monitor Name");
        parser.addArgument("-p").help("Publisher Port");
        parser.addArgument("-s").help("Subscriber Port");

        try {
            Namespace res = parser.parseArgs(args);
            Map m = res.getAttrs();
            new Monitor((String) m.get("b"),(String) m.get("s") ,(String) m.get("p"),(String) m.get("n"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }
}

