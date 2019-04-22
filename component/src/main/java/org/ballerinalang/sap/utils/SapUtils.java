package org.ballerinalang.sap.utils;

import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.ServerDataProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_ERROR;
import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;

/**
 * Utility class for SAP Connector Implementation.
 */
public class SapUtils {

    private static final Log log = LogFactory.getLog(SapUtils.class);

    public static BError createError(Context context, String errorMessage) {
        return BLangVMErrors.createError(context, errorMessage);
    }

    /**
     * Get the server properties.
     *
     * @param serverConfig server config
     */
    public static Properties getServerProperties(Struct serverConfig) {
        Properties serverProperties = new Properties();
        serverProperties.setProperty(ServerDataProvider.JCO_GWHOST,
                serverConfig.getStringField(SapConstants.Serverproperties.JCO_GWHOST));
        serverProperties.setProperty(ServerDataProvider.JCO_GWSERV,
                serverConfig.getStringField(SapConstants.Serverproperties.JCO_GWSERV));
        serverProperties.setProperty(ServerDataProvider.JCO_PROGID,
                serverConfig.getStringField(SapConstants.Serverproperties.JCO_PROGID));
        serverProperties.setProperty(ServerDataProvider.JCO_CONNECTION_COUNT,
                String.valueOf(serverConfig.getIntField(SapConstants.Serverproperties
                        .JCO_CONNECTION_COUNT)));
        serverProperties.setProperty(ServerDataProvider.JCO_TRACE, String.valueOf(serverConfig.
                getIntField(SapConstants.Serverproperties.JCO_TRACE)));
        serverProperties.setProperty(SapConstants.Serverproperties.JCO_SERVER_UNICODE,
                String.valueOf(serverConfig.getIntField(SapConstants.Serverproperties.JCO_UNICODE)));
        if ((String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_REPOSITORY_DEST)) != null
                && !String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_REPOSITORY_DEST))
                .equals(""))) {
            serverProperties.setProperty(ServerDataProvider.JCO_REP_DEST,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_REPOSITORY_DEST));
        }
        if ((String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_LIBRARY)) != null
                && !String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_LIBRARY))
                .equals(""))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_LIBRARY,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_LIBRARY));
        }
        if ((String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_MYNAME)) != null
                && !String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_MYNAME))
                .equals(""))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_MYNAME,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_MYNAME));
        }
        if ((String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_QOP)) != null
                && !String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_QOP))
                .equals(""))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_QOP,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_QOP));
        }
        return serverProperties;
    }

    /**
     * Get the destination properties
     *
     * @param destinationConfig destination config
     */
    public static Properties getDestinationProperties(Struct destinationConfig) {
        Properties destinationProperties = new Properties();
        destinationProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_CLIENT)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_USER,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_USER)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_PASSWD)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_LANG,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_LANG)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_ASHOST)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
                String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SYSNR)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_USE_SAPGUI,
                String.valueOf(destinationConfig.getStringField(
                        SapConstants.Clientproperties.JCO_USE_SAPGUI)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_GETSSO2,
                destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GETSSO2));
        destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
                destinationConfig.getStringField(SapConstants.Clientproperties
                        .JCO_SNC_MODE));
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_EXTID_DATA)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_EXTID_DATA))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_DATA,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_EXTID_DATA)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_EXTID_TYPE)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_EXTID_TYPE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_TYPE,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_EXTID_TYPE)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SAPROUTER)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SAPROUTER))
                .equals(""))) {
            destinationProperties.setProperty(SapConstants.Clientproperties.JCO_SAPROUTER,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SAPROUTER)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWHOST)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_GWHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSHOST)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_ALIAS_USER)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_ALIAS_USER))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_ALIAS_USER,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_ALIAS_USER)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSSERV)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSSERV))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSSERV,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSSERV)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWSERV)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWSERV))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWSERV,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWSERV)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_R3NAME)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_R3NAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_R3NAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_R3NAME)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GROUP)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GROUP))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GROUP,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GROUP)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_CODEPAGE)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_CODEPAGE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
                    String.valueOf(Integer.parseInt(String.valueOf(destinationConfig.getStringField(SapConstants.
                            Clientproperties.JCO_CODEPAGE)))));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
                    destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_PARTNERNAME));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_QOP)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_QOP))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_QOP)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_MYNAME)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_MYNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MYNAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_MYNAME)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_LIBRARY)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_LIBRARY))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_LIBRARY)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPNAME)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPNAME))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPNAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPNAME)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPHOST)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPHOST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPHOST)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TYPE)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TYPE))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TYPE,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TYPE)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_DEST)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_DEST))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_DEST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_DEST)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MYSAPSSO2)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MYSAPSSO2))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MYSAPSSO2,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_MYSAPSSO2)));
        }
        if ((String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_X509CERT)) != null
                && !String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_X509CERT))
                .equals(""))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_X509CERT,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_X509CERT)));
        }
        return destinationProperties;
    }

    /**
     * Invoke the 'onError' resource with provided callback and error.
     *
     * @param sapService that contains resource map
     * @param error      {@link BError} instance which contains the error details
     */
    public static void invokeOnError(Map<String, Resource> sapService, CallableUnitCallback callback, String error,
                                     Context context) {
        try {
            Resource errorResource = sapService.get(RESOURCE_ON_ERROR);
            if (log.isDebugEnabled()) {
                log.info("invokeOnError : " + sapService.get(RESOURCE_ON_ERROR).getName());
            }
            BValue[] bValues = new BValue[1];
            bValues[0] = createError(context, error);
            Executor.submit(errorResource, callback, null, null, bValues);
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
     * @param context     Current context instance
     */
    public static void invokeOnBapiMessage(String output, Map<String, Resource> sapResource,
                                           CallableUnitCallback callback, Context context) {
        try {
            Map<String, Object> properties = new HashMap<>();
            Resource onMessageResource = sapResource.get(RESOURCE_ON_MESSAGE);
            BValue[] bValues = new BValue[1];
            bValues[0] = new BString(output);
            Executor.submit(onMessageResource, callback, properties, null, bValues);
        } catch (BallerinaConnectorException e) {
            SapUtils.invokeOnError(sapResource, callback, e.getMessage(), context);
        }
    }

    /**
     * Invoke the 'onMessage' resource.
     *
     * @param xml           Required for the resource
     * @param sapResource   The resource
     * @param callback      Callback to return the response values
     * @param context       Current context instance
     */
    public static void invokeOnIdocMessage(BXML xml, Map<String, Resource> sapResource, CallableUnitCallback callback,
                                       Context context) {
        try {
            Map<String, Object> properties = new HashMap<>();
            Resource onMessageResource = sapResource.get(RESOURCE_ON_MESSAGE);
            Executor.submit(onMessageResource, callback, properties, null, xml);
        } catch (BallerinaConnectorException e) {
            SapUtils.invokeOnError(sapResource, callback, e.getMessage(), context);
        }
    }
}
