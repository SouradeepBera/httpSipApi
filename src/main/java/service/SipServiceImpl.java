package service;

import beans.SdpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sip.SipClient;
import sip.SipServer;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

@Service
public class SipServiceImpl implements  SipService{

    private final SipClient sipClient;
    private final SipServer sipServer;

    @Autowired
    public SipServiceImpl(SipClient sipClient, SipServer sipServer) {
        this.sipClient = sipClient;
        this.sipServer = sipServer;
    }

    @Override
    public void invite(SdpDTO sdpDTO) throws SipException, InvalidArgumentException, ParseException {
        sipClient.sendInvite(sdpDTO);
    }
}
