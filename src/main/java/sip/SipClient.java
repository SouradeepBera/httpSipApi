package sip;

import beans.SdpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sip.requestCreator.SipInviteRequestCreator;
import sip.requestCreator.SipRequestCreator;

import javax.sip.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Properties;
import java.util.TooManyListenersException;

import static sip.SipFactories.SIP_FACTORY;

@Service
public class SipClient {
    private final SipProvider sipProvider;
    private final SipServer sipServer;

    @Autowired
    public SipClient(SipServer sipServer) throws TransportNotSupportedException, TooManyListenersException, InvalidArgumentException, PeerUnavailableException, ObjectInUseException {
        this.sipServer = sipServer;
        sipProvider = getSipProvider();
    }

    public void sendInvite(SdpDTO sdpDTO) throws InvalidArgumentException, ParseException, SipException {
        SipRequestCreator inviteRequestCreator = new SipInviteRequestCreator(sipProvider);
        ClientTransaction inviteTransaction = sipProvider.getNewClientTransaction(inviteRequestCreator.createRequest(sdpDTO));
        inviteTransaction.sendRequest();
    }

    private SipProvider getSipProvider() throws TooManyListenersException, ObjectInUseException, TransportNotSupportedException, InvalidArgumentException, PeerUnavailableException {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "wapSipStack");
        SipStack sipStack = SIP_FACTORY.createSipStack(properties);
        ListeningPoint listeningPoint = sipStack.createListeningPoint("127.0.0.1", 5015, "udp");
        SipProvider sipProvider = sipStack.createSipProvider(listeningPoint);
        sipProvider.addSipListener(sipServer);
        return sipProvider;
    }
}
