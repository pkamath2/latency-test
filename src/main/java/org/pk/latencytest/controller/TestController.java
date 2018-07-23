package org.pk.latencytest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: purnimakamath
 */
@RestController
public class TestController {

    @RequestMapping(path = "/test")
    public String restTest(@RequestParam(name = "id") int requestId){

        try {
            if(requestId % 500 == 0 && requestId % 1000 != 0){
                Thread.sleep((10000));
            }else {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
           //Do Nothing
        }
        return requestId + " - Successful!";
    }
}
