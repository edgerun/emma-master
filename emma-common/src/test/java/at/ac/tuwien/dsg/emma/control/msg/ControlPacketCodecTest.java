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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.REGISTER.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(21)));
        int hostLength = buffer.readInt();
        assertThat("host length", hostLength, is(equalTo(nodeInfo.getHost().length())));
        assertThat("host", buffer.readCharSequence(hostLength, Charset.forName("UTF-8")), is(equalTo(nodeInfo.getHost())));
        assertThat("port", buffer.readInt(), is(equalTo(nodeInfo.getPort())));
        assertThat("monitoring port", buffer.readInt(), is(equalTo(nodeInfo.getMonitoringPort())));
        assertThat(buffer.isReadable(), is(false));
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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.UNREGISTER.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(11)));
        int idLength = buffer.readInt();
        assertThat("id length", idLength, is(equalTo(id.length())));
        assertThat("id", buffer.readCharSequence(idLength, Charset.forName("UTF-8")), is(equalTo(id)));
        assertThat(buffer.isReadable(), is(false));
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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.REGISTER_RESPONSE.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(12)));
        assertThat("success flag", buffer.readByte(), is(equalTo((byte) 1)));
        assertThat("id length", buffer.readInt(), is(equalTo(2)));
        assertThat("id", buffer.readCharSequence(id.length(), Charset.forName("UTF-8")), is(equalTo(id)));
        assertThat(buffer.isReadable(), is(false));
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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.UNREGISTER_RESPONSE.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(6)));
        assertThat("success flag", buffer.readByte(), is(equalTo((byte) 1)));
        assertThat(buffer.isReadable(), is(false));
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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.REGISTER_RESPONSE.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(7)));
        assertThat("success flag", buffer.readByte(), is(equalTo((byte) 0)));
        assertThat("error code", buffer.readByte(), is(equalTo((byte) RegisterResponseMessage.RegisterError.ALREADY_REGISTERED.ordinal())));
        assertThat(buffer.isReadable(), is(false));
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

        assertThat("packet type", buffer.readByte(), is(equalTo((byte) ControlPacketType.UNREGISTER_RESPONSE.getId())));
        assertThat("packet size", buffer.readInt(), is(equalTo(7)));
        assertThat("success flag", buffer.readByte(), is(equalTo((byte) 0)));
        assertThat("error code", buffer.readByte(), is(equalTo((byte) UnregisterResponseMessage.UnregisterError.NO_REGISTRATION.ordinal())));
        assertThat(buffer.isReadable(), is(false));
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

    private ByteBuf assemblePacket(ControlPacketType type, ByteBuf payload) {
        ByteBuf packet = channel.alloc().buffer();
        packet.writeByte(type.getId());
        packet.writeInt(payload.readableBytes());
        packet.writeBytes(payload);
        return packet;
    }
}
