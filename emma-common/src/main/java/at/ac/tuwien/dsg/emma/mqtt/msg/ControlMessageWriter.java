package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.nio.ByteBuffer;
import java.util.List;

import at.ac.tuwien.dsg.emma.mqtt.Encode;
import at.ac.tuwien.dsg.emma.mqtt.QoS;

/**
 * ControlMessageWriter.
 */
public class ControlMessageWriter {

    // TODO

    public void put(ByteBuffer buf, PacketIdentifierMessage message) {
        buf.put(message.getControlPacketType().toHeader());
        buf.put((byte) 0x02);
        int pos = buf.position();
        Encode.writeTwoByteInt(buf, message.getPacketIdentifier());
        int posnow = buf.position();

        System.out.println("wrote " + (posnow - pos));
    }

    public void put(ByteBuffer header, ByteBuffer buf, SubscribeMessage message) {
        // DATA
        int pos = buf.position();
        List<String> topics = message.getFilter();
        List<QoS> qos = message.getRequestedQos();

        for (int i = 0; i < topics.size(); i++) {
            Encode.writeLengthEncodedString(buf, topics.get(i));
            buf.put((byte) (qos.get(i).ordinal() & 0b00000011));
        }

        // HEADER
        int len = buf.position() - pos;
        header.put(message.getControlPacketType().toHeader());
        Encode.writeVariableInt(header, len);
    }

}
