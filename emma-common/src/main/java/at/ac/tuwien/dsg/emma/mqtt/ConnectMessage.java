package at.ac.tuwien.dsg.emma.mqtt;

import java.util.Arrays;

/**
 * ConnectMessage.
 */
public class ConnectMessage implements ControlMessage {

    // variable header
    private String protocolName;
    private int protocolLevel;
    private int connectFlags;
    private int keepAlive;

    // payload
    private String clientId;
    private String willTopic;
    private byte[] willMessage;
    private String userName;
    private byte[] password;

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public int getProtocolLevel() {
        return protocolLevel;
    }

    public void setProtocolLevel(int protocolLevel) {
        this.protocolLevel = protocolLevel;
    }

    public int getConnectFlags() {
        return connectFlags;
    }

    public void setConnectFlags(int connectFlags) {
        this.connectFlags = connectFlags;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public byte[] getWillMessage() {
        return willMessage;
    }

    public void setWillMessage(byte[] willMessage) {
        this.willMessage = willMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.CONNECT;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConnectMessage{");
        sb.append("protocolName='").append(protocolName).append('\'');
        sb.append(", protocolLevel=").append(protocolLevel);
        sb.append(", connectFlags=").append(connectFlags);
        sb.append(", keepAlive=").append(keepAlive);
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", willTopic='").append(willTopic).append('\'');
        sb.append(", willMessage=").append(Arrays.toString(willMessage));
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", password=").append(Arrays.toString(password));
        sb.append('}');
        return sb.toString();
    }
}
