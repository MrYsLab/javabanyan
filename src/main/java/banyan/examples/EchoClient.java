package banyan.examples;

import banyan.BanyanBase;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings({"WeakerAccess", "unchecked"})
public class EchoClient extends BanyanBase {

    Integer messageNumber;
    Integer numberOfMessages;
    HashMap m = new HashMap();

    EchoClient(String backplaneIpAddress, String subscriberPort,
            String publisherPort, String processName) {
        super(backplaneIpAddress, subscriberPort, publisherPort, processName);


        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("interrupted exception");
        }

        this.set_subscriber_topic("reply");
        messageNumber = numberOfMessages = 10;

        m.put("message_number", messageNumber);
        this.publish_payload(m, "echo");
        messageNumber -= 1;

        this.receive_loop();
    }

    public void incoming_message_processing(String topic, HashMap payload){
        if( (Integer) payload.get("message_number") == 0) {
            System.out.println(numberOfMessages + " messages sent and received. ");

            System.out.println("Press Enter To Exit.");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = null;
            try {
                br.readLine();
                this.clean_up();
                System.exit(0);
                //this code will be executed after user pressed enter
            } catch (IOException e) {
                //handle exception
            }
        }
        else{
            messageNumber -= 1;
            if(messageNumber >= 0){
                m.put("message_number", messageNumber);
                this.publish_payload(m,"echo" );
            }
        }
    }

    public static void main(String[] args)throws ArgumentParserException {

            ArgumentParser parser = ArgumentParsers.newArgumentParser("EchoClient")
                    .description("Echo Client");
            parser.addArgument("-b").help("Backplane IP Address");
            parser.addArgument("-n").setDefault("EchoClient").help("Process Name");
            parser.addArgument("-p").help("Publisher Port");
            parser.addArgument("-s").help("Subscriber Port");

            try {
                Namespace res = parser.parseArgs(args);
                Map m = res.getAttrs();
                new EchoClient((String) m.get("b"),(String) m.get("s") ,(String) m.get("p"),(String) m.get("n"));
            } catch (ArgumentParserException e) {
                parser.handleError(e);
            }
        }
}
