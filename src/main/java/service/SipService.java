package service;

import beans.SdpDTO;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

public interface SipService {

    public void invite(SdpDTO sdpDTO) throws SipException, InvalidArgumentException, ParseException;
}
