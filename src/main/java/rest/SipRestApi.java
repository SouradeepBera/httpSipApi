package rest;

import beans.SdpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.SipService;

@RestController
@RequestMapping(value = "/v1/wap")
public class SipRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SipRestApi.class);
    private final SipService sipService;

    @Autowired
    public SipRestApi(SipService sipService) {
        this.sipService = sipService;
    }

    @PostMapping("/invite")
    public void invite(@RequestBody SdpDTO sdpDTO) {
        try {
            sipService.invite(sdpDTO);
            System.out.println("SUCCESS!");
        } catch (Exception e) {
            LOGGER.error("INVITE failed!", e);
        }
    }
}
