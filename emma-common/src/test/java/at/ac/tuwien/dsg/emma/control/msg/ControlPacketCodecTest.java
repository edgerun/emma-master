package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ControlPacketCodecTest {
    private EmbeddedChannel channel;
    private static final NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);

    @Before
    public void setupChannel() {
        channel = new EmbeddedChannel(new ControlPacketCodec());
    }

    @Test(expected = DecoderException.class)
    public void throws_exception_if_packet_type_0() {
        // write a packet of type 0 with length 0
        ByteBuf in = channel.alloc().buffer();
        in.writeByte(0);
        in.writeInt(0);
        channel.writeInbound(in);

        // should throw DecoderException
        channel.checkException();
    }

    @Test
    public void encodes_register_packet() {
        RegisterMessage message = new RegisterMessage(nodeInfo);

        channel.writeOutbound(message);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.REGISTER);
        assertPacketSize(buffer, 21);
        int hostLength = buffer.readInt();
        assertThat("host length", hostLength, is(equalTo(nodeInfo.getHost().length())));
        assertThat("host", buffer.readCharSequence(hostLength, Charset.forName("UTF-8")), is(equalTo(nodeInfo.getHost())));
        assertThat("port", buffer.readInt(), is(equalTo(nodeInfo.getPort())));
        assertThat("monitoring port", buffer.readInt(), is(equalTo(nodeInfo.getMonitoringPort())));
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_register_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer();
        payload.writeInt(4);
        payload.writeCharSequence(nodeInfo.getHost(), Charset.forName("UTF-8"));
        payload.writeInt(nodeInfo.getPort());
        payload.writeInt(nodeInfo.getMonitoringPort());

        // write to channel and read object
        channel.writeInbound(assemblePacket(ControlPacketType.REGISTER, payload));
        RegisterMessage message = channel.readInbound();

        // verify
        assertThat(message.toNodeInfo(), is(equalTo(nodeInfo)));
    }

    @Test
    public void encodes_unregister_packet() {
        String id = "id";
        UnregisterMessage message = new UnregisterMessage(id);

        channel.writeOutbound(message);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.UNREGISTER);
        assertPacketSize(buffer, 11);
        int idLength = buffer.readInt();
        assertThat("id length", idLength, is(equalTo(id.length())));
        assertThat("id", buffer.readCharSequence(idLength, Charset.forName("UTF-8")), is(equalTo(id)));
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_unregister_packet() {
        String id = "id";

        // prepare payload
        ByteBuf payload = channel.alloc().buffer();
        payload.writeInt(2);
        payload.writeCharSequence(id, Charset.forName("UTF-8"));

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.UNREGISTER, payload));
        UnregisterMessage message = channel.readInbound();

        // verify
        assertThat(message.getId(), is(equalTo(id)));
    }

    @Test
    public void encodes_register_response_success_packet() {
        String id = "id";
        channel.writeOutbound(new RegisterResponseMessage(id));
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.REGISTER_RESPONSE);
        assertPacketSize(buffer, 12);
        assertByte(buffer, "success flag", 1);
        assertThat("id length", buffer.readInt(), is(equalTo(2)));
        assertThat("id", buffer.readCharSequence(id.length(), Charset.forName("UTF-8")), is(equalTo(id)));
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_register_response_success_packet() {
        // prepare payload
        String id = "id";
        ByteBuf payload = channel.alloc().buffer(1);
        payload.writeByte(1);
        payload.writeInt(2);
        payload.writeCharSequence(id, Charset.forName("UTF-8"));

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.REGISTER_RESPONSE, payload));
        RegisterResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.isSuccess(), is(true));
        assertThat(message.getId(), is(equalTo(id)));
    }

    @Test
    public void encodes_unregister_response_success_packet() {
        channel.writeOutbound(UnregisterResponseMessage.SUCCESS);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.UNREGISTER_RESPONSE);
        assertPacketSize(buffer, 6);
        assertByte(buffer, "success flag", 1);
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_unregister_response_success_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer(1);
        payload.writeByte(1);

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.UNREGISTER_RESPONSE, payload));
        UnregisterResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.isSuccess(), is(true));
    }

    @Test
    public void encodes_register_response_error_packet() {
        channel.writeOutbound(RegisterResponseMessage.ERROR_ALREADY_REGISTERED);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.REGISTER_RESPONSE);
        assertPacketSize(buffer, 7);
        assertByte(buffer, "success flag", 0);
        assertByte(buffer, "error code", RegisterResponseMessage.RegisterError.ALREADY_REGISTERED.ordinal());
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_register_response_error_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer(2);
        payload.writeByte(0);
        payload.writeByte(0);

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.REGISTER_RESPONSE, payload));
        RegisterResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.isSuccess(), is(false));
        assertThat(message.getError(), is(equalTo(RegisterResponseMessage.RegisterError.ALREADY_REGISTERED)));
    }

    @Test
    public void encodes_unregister_response_error_packet() {
        channel.writeOutbound(UnregisterResponseMessage.ERROR_NO_REGISTRATION);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.UNREGISTER_RESPONSE);
        assertPacketSize(buffer, 7);
        assertByte(buffer, "success flag", 0);
        assertByte(buffer, "error code", UnregisterResponseMessage.UnregisterError.NO_REGISTRATION.ordinal());
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_unregister_response_error_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer(2);
        payload.writeByte(0);
        payload.writeByte(0);

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.UNREGISTER_RESPONSE, payload));
        UnregisterResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.isSuccess(), is(false));
        assertThat(message.getError(), is(equalTo(UnregisterResponseMessage.UnregisterError.NO_REGISTRATION)));
    }

    @Test
    public void encodes_get_broker_packet() {
        String gatewayId = "id";
        channel.writeOutbound(new GetBrokerMessage(gatewayId));
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.GET_BROKER);
        assertPacketSize(buffer, 11);
        assertThat("gatewayId length", buffer.readInt(), is(equalTo(gatewayId.length())));
        assertThat("gatewayId", buffer.readCharSequence(gatewayId.length(), Charset.forName("UTF-8")).toString(),
                is(equalTo(gatewayId)));
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_get_broker_packet() {
        // prepare payload
        String gatewayId = "id";
        ByteBuf payload = channel.alloc().buffer(6);
        payload.writeInt(gatewayId.length());
        payload.writeCharSequence(gatewayId, Charset.forName("UTF-8"));

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.GET_BROKER, payload));
        GetBrokerMessage message = channel.readInbound();

        // verify
        assertThat(message.getGatewayId(), is(equalTo(gatewayId)));
    }

    @Test
    public void encodes_get_broker_response_success_packet() {
        String brokerUri = "uri";
        channel.writeOutbound(new GetBrokerResponseMessage(brokerUri));
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.GET_BROKER_RESPONSE);
        assertPacketSize(buffer, 13);
        assertByte(buffer, "success flag", 1);
        assertThat("brokerUri length", buffer.readInt(), is(equalTo(brokerUri.length())));
        assertThat("brokerUri", buffer.readCharSequence(brokerUri.length(), Charset.forName("UTF-8")).toString(),
                is(equalTo(brokerUri)));
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_get_broker_response_success_packet() {
        // prepare payload
        String brokerUri = "uri";
        ByteBuf payload = channel.alloc().buffer(8);
        payload.writeByte(1);
        payload.writeInt(brokerUri.length());
        payload.writeCharSequence(brokerUri, Charset.forName("UTF-8"));

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.GET_BROKER_RESPONSE, payload));
        GetBrokerResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.isSuccess(), is(true));
        assertThat(message.getBrokerUri(), is(equalTo(brokerUri)));
    }

    @Test
    public void encodes_get_broker_response_error_unknown_gateway_id_packet() {
        channel.writeOutbound(GetBrokerResponseMessage.ERROR_UNKNOWN_GATEWAY_ID);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.GET_BROKER_RESPONSE);
        assertPacketSize(buffer, 7);
        assertByte(buffer, "success flag", 0);
        assertByte(buffer, "error code", GetBrokerResponseMessage.GetBrokerError.UNKNOWN_GATEWAY_ID.ordinal());
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_get_broker_response_error_unknown_gateway_id_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer(2);
        payload.writeByte(0);
        payload.writeByte(GetBrokerResponseMessage.GetBrokerError.UNKNOWN_GATEWAY_ID.ordinal());

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.GET_BROKER_RESPONSE, payload));
        GetBrokerResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.getBrokerUri(), is(nullValue()));
        assertThat(message.getError(), is(equalTo(GetBrokerResponseMessage.GetBrokerError.UNKNOWN_GATEWAY_ID)));
    }

    @Test
    public void encodes_get_broker_response_error_no_broker_available_packet() {
        channel.writeOutbound(GetBrokerResponseMessage.ERROR_NO_BROKER_AVAILABLE);
        ByteBuf buffer = channel.readOutbound();

        assertPacketType(buffer, ControlPacketType.GET_BROKER_RESPONSE);
        assertPacketSize(buffer, 7);
        assertByte(buffer, "success flag", 0);
        assertByte(buffer, "error code", GetBrokerResponseMessage.GetBrokerError.NO_BROKER_AVAILABLE.ordinal());
        assertEndOfBuffer(buffer);
    }

    @Test
    public void decodes_get_broker_response_error_no_broker_available_packet() {
        // prepare payload
        ByteBuf payload = channel.alloc().buffer(2);
        payload.writeByte(0);
        payload.writeByte(GetBrokerResponseMessage.GetBrokerError.NO_BROKER_AVAILABLE.ordinal());

        // write packet and read object
        channel.writeInbound(assemblePacket(ControlPacketType.GET_BROKER_RESPONSE, payload));
        GetBrokerResponseMessage message = channel.readInbound();

        // verify
        assertThat(message.getBrokerUri(), is(nullValue()));
        assertThat(message.getError(), is(equalTo(GetBrokerResponseMessage.GetBrokerError.NO_BROKER_AVAILABLE)));
    }

    private ByteBuf assemblePacket(ControlPacketType type, ByteBuf payload) {
        ByteBuf packet = channel.alloc().buffer();
        packet.writeByte(type.getId());
        packet.writeInt(payload.readableBytes());
        packet.writeBytes(payload);
        return packet;
    }

    private static void assertByte(ByteBuf buffer, String reason, int value) {
        assertThat(reason, buffer.readByte(), is(equalTo((byte) value)));
    }

    private static void assertEndOfBuffer(ByteBuf buffer) {
        assertThat("end of buffer", buffer.isReadable(), is(false));
    }

    private static void assertPacketSize(ByteBuf buffer, int size) {
        assertThat("packet size", buffer.readInt(), is(equalTo(size)));
    }

    private static void assertPacketType(ByteBuf buffer, ControlPacketType packetType) {
        assertByte(buffer, "packet type", (byte) packetType.getId());
    }
}
