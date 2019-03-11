package org.ballerinalang.sap.utils;

import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.ServerDataProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ProgramFile;
import org.wso2.carbon.kernel.utils.StringUtils;

import java.util.Map;
import java.util.Properties;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;
import static org.ballerinalang.sap.utils.SapConstants.SAP_ERROR_CODE;
import static org.ballerinalang.sap.utils.SapConstants.SAP_NATIVE_PACKAGE;

/**
 * Utility class for SAP Connector Implementation.
 */
public class SapUtils {

    private static final Log log = LogFactory.getLog(SapUtils.class);

    public static BError createError(Context context, String errorMessage) {
        
        return BLangVMErrors.createError(context, errorMessage);
    }

    /**
     * Creates the server properties.
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
        if (!StringUtils.isNullOrEmpty(serverConfig.getStringField(SapConstants.Serverproperties
                .JCO_REPOSITORY_DEST))) {
            serverProperties.setProperty(ServerDataProvider.JCO_REP_DEST,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_REPOSITORY_DEST));
        }
        if (!StringUtils.isNullOrEmpty(serverConfig.getStringField(SapConstants.Serverproperties
                .JCO_SNC_LIBRARY))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_LIBRARY,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_LIBRARY));
        }
        if (!StringUtils.isNullOrEmpty(serverConfig.getStringField(SapConstants.Serverproperties
                .JCO_SNC_MYNAME))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_MYNAME,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_MYNAME));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(serverConfig.getStringField(SapConstants.Serverproperties
                .JCO_SNC_QOP)))) {
            serverProperties.setProperty(ServerDataProvider.JCO_SNC_QOP,
                    serverConfig.getStringField(SapConstants.Serverproperties.JCO_SNC_QOP));
        }
        return serverProperties;
    }

    /**
     * Set the destination properties
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
        destinationProperties.setProperty(DestinationDataProvider.JCO_TRACE,
                destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TRACE));
        destinationProperties.setProperty(DestinationDataProvider.JCO_USE_SAPGUI,
                String.valueOf(destinationConfig.getStringField(
                        SapConstants.Clientproperties.JCO_USE_SAPGUI)));
        destinationProperties.setProperty(DestinationDataProvider.JCO_GETSSO2,
                destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GETSSO2));
        destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
                destinationConfig.getStringField(SapConstants.Clientproperties
                        .JCO_SNC_MODE));
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_EXTID_DATA)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_DATA,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_EXTID_DATA)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_EXTID_TYPE)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_EXTID_TYPE,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_EXTID_TYPE)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_SAPROUTER)))) {
            destinationProperties.setProperty(SapConstants.Clientproperties.JCO_SAPROUTER,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SAPROUTER)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_GWHOST)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_GWHOST)));
        }
        if (!StringUtils.isNullOrEmpty(destinationConfig.getStringField(SapConstants.Clientproperties
                .JCO_MSHOST))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSHOST)));
        }
        if (!StringUtils.isNullOrEmpty(destinationConfig.getStringField(SapConstants.Clientproperties
                .JCO_ALIAS_USER))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_ALIAS_USER,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_ALIAS_USER)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_MSSERV)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSSERV,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_MSSERV)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_GWSERV)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GWSERV,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GWSERV)));
        }
        if (!StringUtils.isNullOrEmpty(destinationConfig.getStringField(SapConstants.Clientproperties
                .JCO_R3NAME))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_R3NAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_R3NAME)));
        }
        if (!StringUtils.isNullOrEmpty(destinationConfig.getStringField(SapConstants.Clientproperties
                .JCO_GROUP))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_GROUP,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_GROUP)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_CODEPAGE)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
                    String.valueOf(Integer.parseInt(String.valueOf(destinationConfig.getStringField(SapConstants.
                            Clientproperties.JCO_CODEPAGE)))));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_SNC_PARTNERNAME)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
                    destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_PARTNERNAME));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_SNC_QOP)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_SNC_QOP)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_SNC_MYNAME)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MYNAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_MYNAME)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_SNC_LIBRARY)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_SNC_LIBRARY)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_TPNAME)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPNAME,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPNAME)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_TPHOST)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TPHOST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TPHOST)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_TYPE)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TYPE,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_TYPE)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_DEST)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_DEST,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties.JCO_DEST)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_MYSAPSSO2)))) {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MYSAPSSO2,
                    String.valueOf(destinationConfig.getStringField(SapConstants.Clientproperties
                            .JCO_MYSAPSSO2)));
        }
        if (!StringUtils.isNullOrEmpty(String.valueOf(destinationConfig.getStringField(SapConstants
                .Clientproperties.JCO_X509CERT)))) {
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
    public static void invokeOnError(Map<String, Resource> sapService, CallableUnitCallback callback, String error) {
        
        try {
            Resource errorResource = sapService.get(RESOURCE_ON_MESSAGE);
            log.info("invokeOnError : " + sapService.get(RESOURCE_ON_MESSAGE).getName());
            BValue[] params = getOnErrorResourceSignature(errorResource, error);
            Executor.submit(errorResource, callback, null, null, params);
        } catch (Throwable e) {
            log.error("Error while executing onError resource", e);
        }
    }

    private static BValue[] getOnErrorResourceSignature(Resource errorResource, String msg) {
        ProgramFile programFile = errorResource.getResourceInfo().getPackageInfo().getProgramFile();
        BError error = createSapError(programFile, msg);
        return new BValue[] { error };
    }

    /**
     * Creates an error message.
     *
     * @param programFile ProgramFile which is used
     * @param errMsg      the cause for the error
     * @return an error which will be propagated to ballerina user
     */
    public static BError createSapError(ProgramFile programFile, String errMsg) {
        
        BMap<String, BValue> errorRecord = BLangConnectorSPIUtil
                .createBStruct(programFile, SAP_NATIVE_PACKAGE, CONSUMER_STRUCT_NAME);
        errorRecord.put("message", new BString(errMsg));
        return BLangVMErrors.createError(SAP_ERROR_CODE, errorRecord);
    }
}
