package sip.requestCreator;

import beans.SdpDTO;
import gov.nist.javax.sip.header.AllowList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static sip.SipFactories.*;

public class SipInviteRequestCreator implements SipRequestCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SipInviteRequestCreator.class);
    private final SipProvider sipProvider;
    private static final List<String> SIP_ALLOWED_METHODS = Collections.unmodifiableList(Arrays.asList(Request.INVITE, Request.BYE, Request.CANCEL, Request.ACK));
    private static final AllowList ALLOW_LIST;
    static{
        ALLOW_LIST = new AllowList();
        try {
            ALLOW_LIST.setMethods(SIP_ALLOWED_METHODS);
        } catch (ParseException e) {
            LOGGER.error("ParseException, cannot create Allow Header. Empty header will be created. {}", e.toString());
        }
    }

    public SipInviteRequestCreator(SipProvider sipProvider) {
        this.sipProvider = sipProvider;
    }

    @Override
    public Request createRequest(SdpDTO sdpDTO) throws ParseException, InvalidArgumentException {

        ListeningPoint listeningPoint = sipProvider.getListeningPoint("udp");
        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        FromHeader fromHeader = getFromHeader();

        ToHeader toHeader = getToHeader();

        SipURI inviteURI = ADDRESS_FACTORY.createSipURI("wapPOC", "127.0.0.1:5070");

        // Create ViaHeaders - this figures out where the responses to this request are to be sent
        // If via headers are already existing, add your current via header on top of list.
        ViaHeader viaHeader = HEADER_FACTORY.createViaHeader(listeningPoint.getIPAddress(), listeningPoint.getPort(), "udp", null);
        List<ViaHeader> viaHeaders = Collections.singletonList(viaHeader);

        // Create a new Cseq header
        CSeqHeader cSeqHeader = HEADER_FACTORY.createCSeqHeader(cseq.getAndIncrement(), Request.INVITE);

        // Create a new MaxForwardsHeader (convention is 70, but can be anything)
        MaxForwardsHeader maxForwards = HEADER_FACTORY.createMaxForwardsHeader(70);

        // Create the request.
        Request request = MESSAGE_FACTORY.createRequest(inviteURI, Request.INVITE, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

        // Create Contact header after creating the contact address.
        //where to contact, differs from FROM header, refer https://stackoverflow.com/questions/31034422/what-is-the-difference-in-contact-and-from-header
        ContactHeader contactHeader = getContactHeader();
        request.addHeader(contactHeader);

        // Create Allow header
        request.addHeader(ALLOW_LIST);

        ContentTypeHeader contentTypeHeader = HEADER_FACTORY.createContentTypeHeader("application", "sdp");
        request.setContent(sdpDTO.getSdp(), contentTypeHeader);

        System.out.println(request);
        return request;
    }

    public ContactHeader getContactHeader() throws ParseException {
        SipURI contactURI = ADDRESS_FACTORY.createSipURI("wapPOC", "127.0.0.1:5015");
        Address contactAddress = ADDRESS_FACTORY.createAddress("wapPOCName", contactURI);
        return HEADER_FACTORY.createContactHeader(contactAddress);
    }

    public ToHeader getToHeader() throws ParseException {
        SipURI toAddress = ADDRESS_FACTORY.createSipURI("fs", "local");
        Address toNameAddress = ADDRESS_FACTORY.createAddress("fsName", toAddress);
        return HEADER_FACTORY.createToHeader(toNameAddress, null);
    }

    public FromHeader getFromHeader() throws ParseException {
        SipURI fromAddress = ADDRESS_FACTORY.createSipURI("wapPOC", "local");
        Address fromNameAddress = ADDRESS_FACTORY.createAddress("wapPOCName", fromAddress);
        return HEADER_FACTORY.createFromHeader(fromNameAddress, null);
    }
}
