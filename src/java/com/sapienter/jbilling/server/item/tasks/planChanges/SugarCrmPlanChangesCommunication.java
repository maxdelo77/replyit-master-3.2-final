/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.server.item.tasks.planChanges;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.event.AbstractPlanEvent;
import com.sapienter.jbilling.server.item.event.NewPlanEvent;
import com.sapienter.jbilling.server.item.event.PlanDeletedEvent;
import com.sapienter.jbilling.server.item.event.PlanUpdatedEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;

/**
 * Implementation of communication with SugarCRM of plan changes via REST
 */
public class SugarCrmPlanChangesCommunication 
        implements IPlanChangesCommunication {

    private static final ParameterDescription PARAM_SERVER = 
    	new ParameterDescription("server", true, ParameterDescription.Type.STR);
    private static final ParameterDescription PARAM_SUGARINSTANCE = 
    	new ParameterDescription("sugarinstance", true, ParameterDescription.Type.STR);
    private static final ParameterDescription PARAM_USER_NAME = 
    	new ParameterDescription("user_name", true, ParameterDescription.Type.STR);
    private static final ParameterDescription PARAM_PASSWORD = 
    	new ParameterDescription("password", true, ParameterDescription.Type.STR);

    public static void addParameters(List<ParameterDescription> descriptions) {
        descriptions.add(PARAM_SERVER);
        descriptions.add(PARAM_SUGARINSTANCE);
        descriptions.add(PARAM_USER_NAME);
        descriptions.add(PARAM_PASSWORD);
    }

    private static final String ENTRY_POINT = "http://{server}/{sugarinstance}/custom/services/tagv0/rest.php";

    private static final FormatLogger LOG = 
            new FormatLogger(Logger.getLogger(SugarCrmPlanChangesCommunication.class));

    private Map<String, String> parameters = null;

    public SugarCrmPlanChangesCommunication(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void process(NewPlanEvent event) throws PluggableTaskException {
        doProcess(event, "insert");
    }

    public void process(PlanUpdatedEvent event) throws PluggableTaskException {
        doProcess(event, "update");
    }

    public void process(PlanDeletedEvent event) throws PluggableTaskException {
        doProcess(event, "delete");
    }

    private void doProcess(AbstractPlanEvent event, String action) 
            throws PluggableTaskException {

        String sessionId = login();

        updatePlan(event.getPlan(), action, sessionId);

        logout(sessionId);
    }

    private String getParameter(String name) throws PluggableTaskException {
        String value = parameters.get(name);
        if (value == null || value.equals("")) {
            String message = "Parameter '" + name + "' required";
            LOG.error(message);
            throw new PluggableTaskException(message);
        }
        return value;
    }

    private String login() throws PluggableTaskException {

        String json = 
"{" +
"  \"user_auth\": {" +
"    \"user_name\": \"" + getParameter(PARAM_USER_NAME.getName()) + "\", " +
"    \"password\": \"" + getParameter(PARAM_PASSWORD.getName()) + "\", " +
"    \"encryption\": \"PLAIN\" " +
"  }, " +
"  \"application_name\": \"JBilling\"," +
"  \"name_value_list\": null " +
"}";

        Map<String, Object> result = makeCall(json, "login");

        if (result.get("id") == null) {
            String message = "No session id. Error calling SugarCRM: " + 
                    result.get("description");
            LOG.error(message);
            throw new PluggableTaskException(message);
        }

        return result.get("id").toString();
    }

    private void logout(String sessionId) throws PluggableTaskException {
        String json = 
"{" +
"  \"session\": \"" + sessionId + "\" " +
"}";

        makeCall(json, "logout");
    }

    private void updatePlan(PlanDTO plan, String action, String sessionId) 
            throws PluggableTaskException {
        ItemDTO item = plan.getItem();
        int itemId = item.getId();
        String itemDescription = item.getDescription(
                item.getEntity().getLanguageId());

        String xml = 
"<updateplans>" +
"  <plan action='" + action + "'>" +
"    <code>" + itemId + "</code>" +
"    <description>" + itemDescription + "</description>" +
"  </plan>"+
"</updateplans>";

        String json = 
"{" +
"  \"session\": \"" + sessionId + "\", " +
"  \"xmldata\": \"" + xml + "\" " +
"}";

        Map<String, Object> result = makeCall(json, "updatePlan");

        String error = null;
        if (result.get("status") != null) {
            if (!result.get("status").toString().equals("OK")) {
                error = result.get("error").toString();
            }
        } else {
            error = result.get("description").toString();
        }

        if (error != null) {
            error = "Error calling SugarCRM: " + error;
            LOG.error(error);
            throw new PluggableTaskException(error);
        }
    }

    private Map<String, Object> makeCall(String json, String method) 
            throws PluggableTaskException {

        LOG.debug("Calling method: '%s', with JSON: %s", method, json);

        MultiValueMap<String, String> postParameters = 
                new LinkedMultiValueMap<String, String>();

        postParameters.add("rest_data", json);
        postParameters.add("input_type", "JSON");
        postParameters.add("method", method);
        postParameters.add("response_type", "JSON");

        RestTemplate restTemplate = new RestTemplate();

        String resultString = restTemplate.postForObject(
                ENTRY_POINT, postParameters, String.class, 
                getParameter(PARAM_SERVER.getName()), 
                getParameter(PARAM_SUGARINSTANCE.getName()));

        LOG.debug("Result contents: %s", resultString);

        // Tried to use Spring MappingJacksonHttpMessageConverter, but
        // server sends text/html mime type. Using Jackson directly:
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = null;
        try {
            result = mapper.readValue(resultString, Map.class);
        } catch (IOException ioe) {
            throw new PluggableTaskException(ioe);
        }

        return result;
    }
}
