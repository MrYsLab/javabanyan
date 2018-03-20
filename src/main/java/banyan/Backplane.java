package banyan;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.net.DatagramSocket;

/**
 Copyright (c) 2016-2017 Alan Yorinks All right reserved.;

 Python Banyan is free software; you can redistribute it and/or
 modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 Version 3 as published by the Free Software Foundation; either
 or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENSE
 along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


@SuppressWarnings("Since15")
class Backplane {

    private Backplane(String backplane_name, String publisher_port, String subscriber_port) {
        String ipAddress = null;
        String topic;
        byte[] payload;
        ZMQ.Context bpContext;
        ZMQ.Socket publisher;
        ZMQ.Socket subscriber;

        if(publisher_port == null) {
            publisher_port = "43124";
        }


        if(subscriber_port == null) {
            subscriber_port = "43125";
        }

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ipAddress = socket.getLocalAddress().getHostAddress();

        } catch (SocketException e){
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("******************************************");
        if( backplane_name == null) {
            System.out.println("Backplane IP address: " + ipAddress);
        }
        else {
            System.out.println(backplane_name + " Backplane IP address: " + ipAddress);
        }
        System.out.println("Subscriber Port = " + subscriber_port);
        System.out.println("Publisher  Port = " + publisher_port);
        System.out.println("******************************************");

        bpContext = ZMQ.context(1);
        publisher = bpContext.socket(ZMQ.PUB);
        subscriber = bpContext.socket(ZMQ.SUB);

        // this looks backwards, but is correct. the backplane
        //accepts messages and re-sends them.
        String pub_string = "tcp://" + ipAddress + ":" + subscriber_port;
        String sub_string = "tcp://" + ipAddress + ":" + publisher_port;

        subscriber.bind(sub_string);
        publisher.bind(pub_string);

        // subscribe to all messages
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));

        while (!Thread.currentThread().isInterrupted()) {
            // Read envelope with topic
            topic = subscriber.recvStr();
            // Read message contents
            payload = subscriber.recv();

            // now republish so that message is available for all subscribers
            publisher.sendMore(topic);
            publisher.send(payload);
        }
        subscriber.close();
        publisher.close();
        bpContext.term();

    }

    public static void main(String[] args)throws ArgumentParserException {


        ArgumentParser parser = ArgumentParsers.newArgumentParser("backplane")
                .description("Banyan Backplane");
        parser.addArgument("-n").help("Backplane Name");
        parser.addArgument("-p").help("Publisher Port");
        parser.addArgument("-s").help("Subscriber Port");

        try {
        Namespace res = parser.parseArgs(args);
        Map m = res.getAttrs();
        new Backplane((String) m.get("n"),(String) m.get("p"),
                (String) m.get("s"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }
}
