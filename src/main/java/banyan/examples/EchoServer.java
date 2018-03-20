package banyan.examples;

import banyan.BanyanBase;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings({"WeakerAccess", "unchecked"})
public class EchoServer extends BanyanBase {

    Integer messageNumber;
    Integer numberOfMessages;
    HashMap m = new HashMap();

    EchoServer(String backplaneIpAddress, String subscriberPort,
               String publisherPort, String processName) {
        super(backplaneIpAddress, subscriberPort, publisherPort, processName);


        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("interrupted exception");
        }

        this.set_subscriber_topic("echo");
        this.receive_loop();
    }

    public void incoming_message_processing(String topic, HashMap payload){
        this.publish_payload(payload, "reply");
    }
    public static void main(String[] args)throws ArgumentParserException {

        ArgumentParser parser = ArgumentParsers.newArgumentParser("EchoServer")
                .description("Echo Server");
        parser.addArgument("-b").help("Backplane IP Address");
        parser.addArgument("-n").setDefault("EchoServer").help("Process Name");
        parser.addArgument("-p").help("Publisher Port");
        parser.addArgument("-s").help("Subscriber Port");

        try {
            Namespace res = parser.parseArgs(args);
            Map m = res.getAttrs();
            new EchoServer((String) m.get("b"),(String) m.get("s") ,(String) m.get("p"),(String) m.get("n"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }
}
