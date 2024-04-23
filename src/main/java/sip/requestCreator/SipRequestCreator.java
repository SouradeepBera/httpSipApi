package sip.requestCreator;

import beans.SdpDTO;

import javax.sip.InvalidArgumentException;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public interface SipRequestCreator {

    AtomicLong cseq = new AtomicLong(1);

    public Request createRequest(SdpDTO sdpDTO) throws ParseException, InvalidArgumentException;
}
