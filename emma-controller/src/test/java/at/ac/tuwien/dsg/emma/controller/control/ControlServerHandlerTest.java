package at.ac.tuwien.dsg.emma.controller.control;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.control.msg.*;
import at.ac.tuwien.dsg.emma.controller.event.ClientDeregisterEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientRegisterEvent;
import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.ClientRepository;
import at.ac.tuwien.dsg.emma.controller.network.NetworkManager;
import at.ac.tuwien.dsg.emma.controller.network.sel.BrokerSelectionStrategy;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ControlServerHandlerTest {
    @MockBean
    private ApplicationEventPublisher systemEvents;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private BrokerSelectionStrategy brokerSelectionStrategy;
    @MockBean
    private NetworkManager networkManager;
    private EmbeddedChannel channel;

    @Before
    public void setup() {
        ControlServerHandler handler = new ControlServerHandler(systemEvents, clientRepository, brokerSelectionStrategy,
                networkManager);
        channel = new EmbeddedChannel(new ControlServerInboundAdapter(handler));
    }

    @Test
    public void handles_RegisterMessage_successful_registration() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        Client client = new Client(nodeInfo.getHost(), nodeInfo.getPort());
        RegisterMessage registerMessage = new RegisterMessage(nodeInfo);
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
    public void handles_RegisterMessage_with_wildcard_uses_remote_address() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("0.0.0.0", 1234, 2345);
        RegisterMessage registerMessage = new RegisterMessage(nodeInfo);

        // exercise
        channel.writeInbound(registerMessage);

        // verify
        String host = channel.remoteAddress().toString();
        verify(clientRepository).register(new NodeInfo(host, nodeInfo.getPort(), nodeInfo.getMonitoringPort()));
    }

    @Test
    public void handles_RegisterMessage_error_already_registered() {
        // setup
        NodeInfo nodeInfo = new NodeInfo("host", 1234, 2345);
        RegisterMessage registerMessage = new RegisterMessage(nodeInfo);
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
    public void handles_UnregisterMesesage_successfully() {
        // setup
        String id = "id";
        Client client = new Client("host", 1234);
        when(clientRepository.getById(id)).thenReturn(client);
        when(clientRepository.remove(client)).thenReturn(true);
        ArgumentCaptor<ClientDeregisterEvent> publishedEventCaptor = ArgumentCaptor.forClass(ClientDeregisterEvent.class);

        // exercise
        channel.writeInbound(new UnregisterMessage(id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.isSuccess(), is(true));
        verify(clientRepository).remove(client);
        verify(systemEvents).publishEvent(publishedEventCaptor.capture());
        assertThat(publishedEventCaptor.getValue().getHost(), is(equalTo(client)));
    }

    @Test
    public void handles_UnregisterMessage_error_if_no_client() {
        // setup
        String id = "id";
        Client client = new Client("host", 1234);
        when(clientRepository.getById(id)).thenReturn(null);

        // exercise
        channel.writeInbound(new UnregisterMessage(id));
        UnregisterResponseMessage responseMessage = channel.readOutbound();

        // verify
        assertThat(responseMessage.getError(), is(equalTo(UnregisterResponseMessage.UnregisterError.NO_REGISTRATION)));
        verify(clientRepository).getById(id);
        verify(clientRepository, never()).remove(client);
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
}
