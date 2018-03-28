/*
   Copyright 2017 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.subscriptionhandler;

import com.ericsson.ei.jmespath.JmesPathInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This class is responsible to send notification through REST POST to the
 * recipient of the Subscription Object.
 *
 */

@Component
public class SpringRestTemplate {

    static Logger log = (Logger) LoggerFactory.getLogger(SpringRestTemplate.class);

    private RestOperations rest;

    public SpringRestTemplate(RestTemplateBuilder builder) {
        rest = builder.build();
    }

    /**
     * This method is responsible to notify the subscriber through REST POST With raw body and form parameters.
     *
     * @param notificationMeta
     * @param mapNotificationMessage
     * @param headerContentMediaType
     * @return integer
     */
    public int postDataMultiValue(String notificationMeta, MultiValueMap<String, String> mapNotificationMessage, String headerContentMediaType) {
        ResponseEntity<JsonNode> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(headerContentMediaType));
            if(headerContentMediaType.equals(MediaType.APPLICATION_FORM_URLENCODED.toString())){ //"application/x-www-form-urlencoded"
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(mapNotificationMessage, headers);
                response = rest.postForEntity(notificationMeta, request , JsonNode.class );
            }
            else{
                HttpEntity<String> request = new HttpEntity<String>(String.valueOf(((List<String>) mapNotificationMessage.get("")).get(0)), headers);
                response = rest.postForEntity(notificationMeta, request, JsonNode.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpStatus.NOT_FOUND.value();
        }
        HttpStatus status = response.getStatusCode();
        log.info("The response code after POST is : " + status);
        if (status == HttpStatus.OK) {
            JsonNode restCall = response.getBody();
            log.info("The response Body is : " + restCall);
        }
        return response.getStatusCode().value();
    }
}
