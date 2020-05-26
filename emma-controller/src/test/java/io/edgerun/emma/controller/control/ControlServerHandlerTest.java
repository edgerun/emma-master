package io.edgerun.emma.controller.control;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.edgerun.emma.NodeInfo;
import io.edgerun.emma.control.msg.GetBrokerMessage;
import io.edgerun.emma.control.msg.GetBrokerResponseMessage;
import io.edgerun.emma.control.msg.NodeType;
import io.edgerun.emma.control.msg.OnSubscribeMessage;
import io.edgerun.emma.control.msg.OnUnsubscribeMessage;
import io.edgerun.emma.control.msg.RegisterMessage;
import io.edgerun.emma.control.msg.RegisterResponseMessage;
import io.edgerun.emma.control.msg.UnregisterMessage;
import io.edgerun.emma.control.msg.UnregisterResponseMessage;
import io.edgerun.emma.controller.event.ClientDeregisterEvent;
import io.edgerun.emma.controller.event.ClientRegisterEvent;
import io.edgerun.emma.controller.event.SubscribeEvent;
import io.edgerun.emma.controller.event.UnsubscribeEvent;
import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.BrokerRepository;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import io.edgerun.emma.controller.network.NetworkManager;
import io.edgerun.emma.controller.network.sel.BrokerSelectionStrategy;
import io.netty.channel.embedded.EmbeddedChannel;

@RunWith(SpringRunner.class)
public class ControlServerHandlerTest {
    @MockBean
    private ApplicationEventPublisher systemEvents;
    @MockBean
    private BrokerRepository brokerRepository;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private BrokerSelectionStrategy brokerSelectionStrategy;
    @MockBean
    private NetworkManager networkManager;
    private EmbeddedChannel channel;

    @Before
    public void setup() {
        ControlServerHandler handler = new ControlServerHandler(systemEvents, brokerRepository, clientRepository,
                brokerSelectionStrategy, networkManager);
        channel = new EmbeddedChannel(new ControlServerInboundAdapter(handler));
    }

    @Test
    public void handles_RegisterMessage_successful_gateway_registration() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        Client client = new Client(nodeInfo.getHost(), nodeInfo.getPort());
        RegisterMessage registerMessage = new RegisterMessage(NodeType.CLIENT_GATEWAY, nodeInfo);
        ArgumentCaptor<ClientRegisterEvent> publishedEventCaptor = ArgumentCaptor.forClass(ClientRegisterEvent.class);
        when(clientRepository.register(nodeInfo)).thenReturn(client);

        // exercise
        channel.writeInbound(registerMessage);
        RegisterResponseMessage response = channel.readOutbound();

        // verify
        assertThat(response, is(notNullValue()));
        assertThat(response.isSuccess(), is(true));
        verify(clientRepository).getHost(nodeInfo.getHost(), nodeInfo.getPort());
        verify(clientRepository).register(nodeInfo);
        verify(systemEvents).publishEvent(publishedEventCaptor.capture());
        assertThat(publishedEventCaptor.getValue().getHost(), is(equalTo(client)));
    }

    @Test
    public void handles_RegisterMessage_successful_broker_registration() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        Broker broker = new Broker(nodeInfo.getHost(), nodeInfo.getPort());
        RegisterMessage registerMessage = new RegisterMessage(NodeType.BROKER, nodeInfo);
        when(brokerRepository.register(nodeInfo)).thenReturn(broker);

        // exercise
        channel.writeInbound(registerMessage);
        RegisterResponseMessage response = channel.readOutbound();

        // verify
        assertThat(response, is(notNullValue()));
        assertThat(response.isSuccess(), is(true));
        verify(brokerRepository).getHost(nodeInfo.getHost(), nodeInfo.getPort());
        verify(brokerRepository).register(nodeInfo);
    }

    @Test
    public void handles_RegisterMessage_with_wildcard_uses_remote_address() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("0.0.0.0", 1234, 2345);
        RegisterMessage registerMessage = new RegisterMessage(NodeType.CLIENT_GATEWAY, nodeInfo);

        // exercise
        channel.writeInbound(registerMessage);

        // verify
        String host = channel.remoteAddress().toString();
        verify(clientRepository).register(new NodeInfo(host, nodeInfo.getPort(), nodeInfo.getMonitoringPort()));
    }

    @Test
    public void handles_RegisterMessage_gateway_error_already_registered() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        RegisterMessage registerMessage = new RegisterMessage(NodeType.CLIENT_GATEWAY, nodeInfo);
        when(clientRepository.getHost(nodeInfo.getHost(), nodeInfo.getPort()))
                .thenReturn(new Client(nodeInfo.getHost(), nodeInfo.getPort()));

        // exercise
        channel.writeInbound(registerMessage);
        RegisterResponseMessage response = channel.readOutbound();

        // verify
        assertThat(response, is(notNullValue()));
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getError(), is(equalTo(RegisterResponseMessage.RegisterError.ALREADY_REGISTERED)));
        verify(clientRepository, never()).register((NodeInfo) any());
        verify(systemEvents, never()).publishEvent(any());
    }

    @Test
    public void handles_RegisterMessage_broker_error_already_registered() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        RegisterMessage registerMessage = new RegisterMessage(NodeType.BROKER, nodeInfo);
        when(brokerRepository.getHost(nodeInfo.getHost(), nodeInfo.getPort()))
                .thenReturn(new Broker(nodeInfo.getHost(), nodeInfo.getPort()));

        // exercise
        channel.writeInbound(registerMessage);
        RegisterResponseMessage response = channel.readOutbound();

        // verify
        assertThat(response, is(notNullValue()));
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getError(), is(equalTo(RegisterResponseMessage.RegisterError.ALREADY_REGISTERED)));
        verify(brokerRepository, never()).register((NodeInfo) any());
    }

    @Test
    public void handles_UnregisterMesesage_successfully_gateway() {
        // setup
        String id = "id";
        Client client = new Client("host", 1234);
        when(clientRepository.getById(id)).thenReturn(client);
        when(clientRepository.remove(client)).thenReturn(true);
        ArgumentCaptor<ClientDeregisterEvent> publishedEventCaptor = ArgumentCaptor.forClass(ClientDeregisterEvent.class);

        // exercise
        channel.writeInbound(new UnregisterMessage(NodeType.CLIENT_GATEWAY, id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.isSuccess(), is(true));
        verify(clientRepository).remove(client);
        verify(systemEvents).publishEvent(publishedEventCaptor.capture());
        assertThat(publishedEventCaptor.getValue().getHost(), is(equalTo(client)));
    }

    @Test
    public void handles_UnregisterMesesage_successfully_broker() {
        // setup
        String id = "id";
        Broker broker = new Broker("host", 1234);
        when(brokerRepository.getById(id)).thenReturn(broker);
        when(brokerRepository.remove(broker)).thenReturn(true);

        // exercise
        channel.writeInbound(new UnregisterMessage(NodeType.BROKER, id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.isSuccess(), is(true));
        verify(brokerRepository).remove(broker);
    }

    @Test
    public void handles_UnregisterMessage_error_if_no_client() {
        // setup
        String id = "id";
        Client client = new Client("host", 1234);
        when(clientRepository.getById(id)).thenReturn(null);

        // exercise
        channel.writeInbound(new UnregisterMessage(NodeType.CLIENT_GATEWAY, id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.getError(), is(equalTo(UnregisterResponseMessage.UnregisterError.NO_REGISTRATION)));
        verify(clientRepository).getById(id);
        verify(clientRepository, never()).remove(client);
    }

    @Test
    public void handles_UnregisterMessage_error_if_no_broker() {
        // setup
        String id = "id";
        Broker broker = new Broker("host", 1234);
        when(brokerRepository.getById(id)).thenReturn(null);

        // exercise
        channel.writeInbound(new UnregisterMessage(NodeType.BROKER, id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.getError(), is(equalTo(UnregisterResponseMessage.UnregisterError.NO_REGISTRATION)));
        verify(brokerRepository).getById(id);
        verify(brokerRepository, never()).remove(broker);
    }

    @Test
    public void handles_GetBrokerMessage_successfully() {
        // setup
        String id = "id";
        Client client = new Client("client", 1234);
        when(clientRepository.getById(id)).thenReturn(client);
        Broker broker = new Broker("broker", 2345);
        String brokerUri = "tcp://" + broker.getHost() + ":" + broker.getPort();
        when(brokerSelectionStrategy.select(eq(client), any())).thenReturn(broker);

        // exercise
        channel.writeInbound(new GetBrokerMessage(id));
        GetBrokerResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.isSuccess(), is(true));
        assertThat(responseMessage.getBrokerUri(), is(equalTo(brokerUri)));
    }

    @Test
    public void handles_GetBrokerMessage_error_unknown_gateway_id() {
        // setup
        String id = "id";

        // exercise
        channel.writeInbound(new GetBrokerMessage(id));
        GetBrokerResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.getError(), is(equalTo(GetBrokerResponseMessage.GetBrokerError.UNKNOWN_GATEWAY_ID)));
        assertThat(responseMessage.getBrokerUri(), is(nullValue()));
    }

    @Test
    public void handles_GetBroker_Message_error_no_broker() {
        // setup
        String id = "id";
        Client client = new Client("client", 1234);
        when(clientRepository.getById(id)).thenReturn(client);
        when(brokerSelectionStrategy.select(eq(client), any())).thenThrow(new IllegalStateException("No broker connected"));

        // exercise
        channel.writeInbound(new GetBrokerMessage(id));
        GetBrokerResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.getError(), is(equalTo(GetBrokerResponseMessage.GetBrokerError.NO_BROKER_AVAILABLE)));
        assertThat(responseMessage.getBrokerUri(), is(nullValue()));
    }

    @Test
    public void handles_OnSubscribeMessage() {
        // setup
        String brokerId = "brokerId";
        String topic = "topic";
        Broker broker = new Broker("host", 1234);
        when(brokerRepository.getById(brokerId)).thenReturn(broker);
        ArgumentCaptor<SubscribeEvent> eventCaptor = ArgumentCaptor.forClass(SubscribeEvent.class);

        // exercise
        channel.writeInbound(new OnSubscribeMessage(brokerId, topic));

        // verify
        verify(systemEvents).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getHost(), is(equalTo(broker)));
        assertThat(eventCaptor.getValue().getTopic(), is(equalTo(topic)));
    }

    @Test
    public void handles_OnSubscribeMessage_invalid_brokerId() {
        // setup
        String brokerId = "brokerId";
        String topic = "topic";
        when(brokerRepository.getById(brokerId)).thenReturn(null);

        // exercise
        channel.writeInbound(new OnSubscribeMessage(brokerId, topic));

        // verify
        verify(systemEvents, never()).publishEvent(any());
    }

    @Test
    public void handles_OnUnsubscribeMessage() {
        // setup
        String brokerId = "brokerId";
        String topic = "topic";
        Broker broker = new Broker("host", 1234);
        when(brokerRepository.getById(brokerId)).thenReturn(broker);
        ArgumentCaptor<UnsubscribeEvent> eventCaptor = ArgumentCaptor.forClass(UnsubscribeEvent.class);

        // exercise
        channel.writeInbound(new OnUnsubscribeMessage(brokerId, topic));

        // verify
        verify(systemEvents).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getHost(), is(equalTo(broker)));
        assertThat(eventCaptor.getValue().getTopic(), is(equalTo(topic)));
    }

    @Test
    public void handles_OnUnsubscribeMessage_invalid_brokerId() {
        // setup
        String brokerId = "brokerId";
        String topic = "topic";
        when(brokerRepository.getById(brokerId)).thenReturn(null);

        // exercise
        channel.writeInbound(new OnUnsubscribeMessage(brokerId, topic));

        // verify
        verify(systemEvents, never()).publishEvent(any());
    }
}
