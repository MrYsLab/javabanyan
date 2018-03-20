package banyan;

import net.asdfa.msgpack.InvalidMsgPackDataException;
import net.asdfa.msgpack.MsgPack;
import org.zeromq.ZMQ;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import static net.asdfa.msgpack.MsgPack.UNPACK_RAW_AS_STRING;


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

@SuppressWarnings({"Since15", "WeakerAccess", "CanBeFinal"})

public class BanyanBase {

    String subscriberPort;
    String publisherPort;
    String processName;
    String backplaneIpAddress;

    // zmq sockets
    String topic;
    ZMQ.Context bpContext;
    ZMQ.Socket publisher;
    ZMQ.Socket subscriber;

    public BanyanBase(String backplaneIpAddress, String subscriberPort,
                      String publisherPort, String processName) {

        if (subscriberPort == null) {
            this.subscriberPort = "43125";
        }
        else {
            this.subscriberPort = subscriberPort;

        }
        // make all parameters available within this class
        if (publisherPort == null) {
            this.publisherPort = "43124";
        }
        else {
            this.publisherPort = publisherPort;
        }
        this.processName = processName;

        // if an Ip address was supplied, use it
        if (backplaneIpAddress != null && !backplaneIpAddress.isEmpty()) {
            this.backplaneIpAddress = backplaneIpAddress;
        }
        // otherwise figure out the ip address of this computer
        else {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                this.backplaneIpAddress = socket.getLocalAddress().getHostAddress();

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        // print a header to the console
        System.out.println("\n************************************************************");
        System.out.println(this.processName + " using Back Plane IP address: " + this.backplaneIpAddress);
        System.out.println("Subscriber Port = " + this.subscriberPort);
        System.out.println("Publisher  Port = " + this.publisherPort);
        System.out.println("************************************************************");

        this.bpContext = ZMQ.context(1);
        this.publisher = bpContext.socket(ZMQ.PUB);
        this.subscriber = bpContext.socket(ZMQ.SUB);

        String pub_string = "tcp://" + this.backplaneIpAddress + ":" + this.publisherPort;
        String sub_string = "tcp://" + this.backplaneIpAddress+ ":" + this.subscriberPort;

        subscriber.connect(sub_string);
        publisher.connect(pub_string);
    }

    public void set_subscriber_topic(String topic) {
        subscriber.subscribe(topic.getBytes(ZMQ.CHARSET));
    }

    public void publish_payload(HashMap payload, String topic) {

        byte[] packedPayload = MsgPack.pack(payload);
        this.publisher.sendMore(topic);
        this.publisher.send(packedPayload);
    }

    public void receive_loop() {
        byte[] payload_raw ;

        while (!Thread.currentThread().isInterrupted()) {
            // Read envelope with topic
            topic = subscriber.recvStr();
            // Read message contents
            payload_raw = subscriber.recv();

            try {
                Object unpacked = MsgPack.unpack(payload_raw, UNPACK_RAW_AS_STRING);
                @SuppressWarnings("unchecked") HashMap<String, Object> payload = (HashMap<String, Object>) unpacked;
                this.incoming_message_processing(topic, payload);
            } catch (InvalidMsgPackDataException ex) {
                //InvalidMsgPackDataException is a type of IOException, so throw it
                //seperately
                throw new RuntimeException("ByteArrayInStream threw an InvalidMsgPackDataException!", ex);
            }
        }
        subscriber.close();
        publisher.close();
        bpContext.term();
    }

    public void incoming_message_processing(String topic, HashMap payload)
    {
        // place holder for derived class
        System.out.println("from base");
        System.out.println(topic);

    }

    public void clean_up() {
        subscriber.close();
        publisher.close();
        bpContext.term();
    }
}

