package org.wso2.ei.module.sap.utils;

import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.ServerDataProvider;

import java.util.Map;
import java.util.Properties;

import org.ballerinalang.jvm.BallerinaErrors;
import org.ballerinalang.jvm.types.AttachedFunction;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.XMLValue;
import org.ballerinalang.jvm.values.connector.CallableUnitCallback;
import org.ballerinalang.jvm.values.connector.Executor;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.wso2.ei.module.sap.utils.SapConstants.RESOURCE_ON_ERROR;
import static org.wso2.ei.module.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;

/**
 * Utility class for SAP Connector Implementation.
 */
public class SapUtils {

    private static Logger log = LoggerFactory.getLogger("ballerina");

    /**
     * Creates and returns an ErrorValue object.
     *
     * @param errorMessage The error message to be used for the ErrorValue object.
     * @return ErrorValue object.
     */
//    public static ErrorValue createError(String errorMessage) {
//
//        return BallerinaErrors.createError(errorMessage);
//    }

    /**
     * Get the server properties.
     *
     * @param serverConfig MapValue containing the server configuration.
     * @return A Properties object with the server properties.
     */
    public static Properties getServerProperties(MapValue serverConfig) throws BallerinaSapException {

        Properties serverProperties = new Properties();
        try {
            serverProperties.setProperty(ServerDataProvider.JCO_GWHOST,
                    serverConfig.getStringValue(SapConstants.Serverproperties.JCO_GWHOST));
            serverProperties.setProperty(ServerDataProvider.JCO_GWSERV,
                    serverConfig.getStringValue(SapConstants.Serverproperties.JCO_GWSERV));
            serverProperties.setProperty(ServerDataProvider.JCO_PROGID,
                    serverConfig.getStringValue(SapConstants.Serverproperties.JCO_PROGID));
            serverProperties.setProperty(ServerDataProvider.JCO_CONNECTION_COUNT,
                    String.valueOf(serverConfig.getIntValue(SapConstants.Serverproperties
                            .JCO_CONNECTION_COUNT)));
            serverProperties.setProperty(ServerDataProvider.JCO_TRACE, String.valueOf(serverConfig.
                    getIntValue(SapConstants.Serverproperties.JCO_TRACE)));
            serverProperties.setProperty(SapConstants.Serverproperties.JCO_SERVER_UNICODE,
                    String.valueOf(serverConfig.getIntValue(SapConstants.Serverproperties.JCO_UNICODE)));
            if ((String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_REPOSITORY_DEST)) != null
                    && !String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_REPOSITORY_DEST))
                    .equals(""))) {
                serverProperties.setProperty(ServerDataProvider.JCO_REP_DEST,
                        serverConfig.getStringValue(SapConstants.Serverproperties.JCO_REPOSITORY_DEST));
            }
            if ((String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_LIBRARY)) != null
                    && !String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_LIBRARY))
                    .equals(""))) {
                serverProperties.setProperty(ServerDataProvider.JCO_SNC_LIBRARY,
                        serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_LIBRARY));
            }
            if ((String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_MYNAME)) != null
                    && !String.valueOf(serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_MYNAME))
                    .equals(""))) {
                serverProperties.setProperty(ServerDataProvider.JCO_SNC_MYNAME,
                        serverConfig.getStringValue(SapConstants.Serverproperties.JCO_SNC_MYNAME));
            }
            if ((String.valueOf(serverConfig.getIntValue(SapConstants.Serverproperties.JCO_SNC_QOP)) != null
                    && !String.valueOf(serverConfig.getIntValue(SapConstants.Serverproperties.JCO_SNC_QOP))
                    .equals(""))) {
                serverProperties.setProperty(ServerDataProvider.JCO_SNC_QOP,
                        serverConfig.getIntValue(SapConstants.Serverproperties.JCO_SNC_QOP).toString());
            }
        } catch (Exception e) {
            throw new BallerinaSapException("Error while obtaining the server properties." + e);
        }

        return serverProperties;
    }

    /**
     * Get the destination properties.
     *
     * @param destinationConfig
     * @return
     */
    public static Properties getDestinationProperties(MapValue destinationConfig) {

        Properties destinationProperties = new Properties();
        destinationProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_CLIENT)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_USER,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_USER)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_PASSWD)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_LANG,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_LANG)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_ASHOST)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
                String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SYSNR)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_USE_SAPGUI,
                destinationConfig.getIntValue(SapConstants.Clientproperties.JCO_USE_SAPGUI).toString());
        destinationProperties.setProperty(DestinationDataProvider.JCO_GETSSO2,
                destinationConfig.getIntValue(SapConstants.Clientproperties.JCO_GETSSO2).toString());
        destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
                destinationConfig.getIntValue(SapConstants.Clientproperties
                        .JCO_SNC_MODE).toString());
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_EXTID_DATA)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_EXTID_DATA))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_DATA,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_EXTID_DATA)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_EXTID_TYPE)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_EXTID_TYPE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_TYPE,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_EXTID_TYPE)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SAPROUTER)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SAPROUTER))
                .equals(""))) {
            destinationProperties.setProperty(SapConstants.Clientproperties.JCO_SAPROUTER,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_SAPROUTER)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GWHOST)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GWHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWHOST,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_GWHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSHOST)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSHOST,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_ALIAS_USER)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_ALIAS_USER))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_ALIAS_USER,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_ALIAS_USER)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSSERV)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSSERV))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSSERV,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MSSERV)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GWSERV)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GWSERV))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWSERV,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GWSERV)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_R3NAME)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_R3NAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_R3NAME,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_R3NAME)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GROUP)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GROUP))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GROUP,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_GROUP)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_CODEPAGE)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_CODEPAGE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
                    String.valueOf(Integer.parseInt(String.valueOf(destinationConfig.getStringValue(SapConstants.
                            Clientproperties.JCO_CODEPAGE)))));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
                    destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_SNC_PARTNERNAME));
        }
        if ((String.valueOf(destinationConfig.getIntValue(SapConstants.Clientproperties.JCO_SNC_QOP)) != null
                && !String.valueOf(destinationConfig.getIntValue(SapConstants.Clientproperties.JCO_SNC_QOP))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP,
                    String.valueOf(destinationConfig.getIntValue(SapConstants.Clientproperties.JCO_SNC_QOP)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_MYNAME)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_MYNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MYNAME,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_SNC_MYNAME)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_LIBRARY)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_SNC_LIBRARY))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_SNC_LIBRARY)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPNAME)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPNAME,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPNAME)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPHOST)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPHOST,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TPHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TYPE)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TYPE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TYPE,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_TYPE)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_DEST)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_DEST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_DEST,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_DEST)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MYSAPSSO2)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_MYSAPSSO2))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MYSAPSSO2,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_MYSAPSSO2)));
        }
        if ((String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_X509CERT)) != null
                && !String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties.JCO_X509CERT))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_X509CERT,
                    String.valueOf(destinationConfig.getStringValue(SapConstants.Clientproperties
                            .JCO_X509CERT)));
        }
        return destinationProperties;
    }

    /**
     * Invoke the 'onError' resource with provided callback and error.
     *
     * @param sapService that contains resource map
     * @param error      {@link String} instance which contains the error details
     */
    public static void invokeOnError(Map<String, AttachedFunction> sapService, CallableUnitCallback callback,
                                     String error) {

        try {
            AttachedFunction errorResource = sapService.get(RESOURCE_ON_ERROR);
            Executor.submit(null, (ObjectValue) errorResource, error, callback, null, null,
                    BallerinaErrors.createError(error));
        } catch (Throwable e) {
            log.error("Error while executing onError resource", e);
        }
    }

    /**
     * Invoke the 'onMessage' resource.
     *
     * @param output      Required for the resource
     * @param sapResource The resource
     * @param callback    Callback to return the response values
     */
    public static void invokeOnBapiMessage(String output, Map<String, AttachedFunction> sapResource,
                                           CallableUnitCallback callback) {

        try {
            log.info("invokeOnBapiMessage");
            AttachedFunction onMessageResource = sapResource.get(RESOURCE_ON_MESSAGE);
            String serviceName = onMessageResource.getName();
            Executor.submit(null, (ObjectValue) onMessageResource, serviceName, callback, null,
                    null, output, true);
        } catch (BallerinaException e) {
            SapUtils.invokeOnError(sapResource, callback, e.getMessage());
        }
    }

    /**
     * Invoke the 'onMessage' resource.
     *
     * @param xml         Required for the resource
     * @param sapResource The resource
     * @param callback    Callback to return the response values
     */
    public static void invokeOnIdocMessage(XMLValue xml, Map<String, AttachedFunction> sapResource,
                                           CallableUnitCallback callback) {

        try {
            log.info("invokeOnIdocMessage");
            AttachedFunction onMessageResource = sapResource.get(RESOURCE_ON_MESSAGE);
            Executor.submit(null, null, onMessageResource.getName(), callback, null,
                    null, xml);
        } catch (BallerinaException e) {
            SapUtils.invokeOnError(sapResource, callback, e.getMessage());
        }
    }
}
