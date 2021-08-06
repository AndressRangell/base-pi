package com.facephi.soaplibrary;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import com.facephi.soaplibrary.SoapException.SoapExceptionType;

public class Soap {
    public SoapOperationType OPERATION_NAME;
    public String SOAP_ACTION;
    public String WSDL_TARGET_NAMESPACE;

    public String SOAP_ADDRESS;
    public void SetSoapAddres(String soap_address) {
        this.SOAP_ADDRESS = soap_address;
    }

    /**
     * Constructor with default values.
     */
    public Soap() {
        // Change this for your namespace.
        WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    }


    /**
     * Adds a new user in the device.
     * @param name User's name.
     * @param template User's template.
     * @return String with the result of the user creation.
     * @throws Exception
     * @throws SoapFault
     */
    public String Add(String name, String template) throws Exception, SoapFault {
        OPERATION_NAME = (SoapOperationType.Operation_Add);
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + SoapOperationType.getMethodName(OPERATION_NAME);

        return Call(name, null, template)[0];
    }

    /**
     * Authenticates the specified user.
     * @param identifier User's identifier.
     * @param template User's template.
     * @return UserDto with the authenticated user.
     * @throws Exception
     */
    public String Authenticate(String identifier, String template) throws Exception {
        OPERATION_NAME = (SoapOperationType.Operation_Authenticate);
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + SoapOperationType.getMethodName(OPERATION_NAME);

        return Call(null, identifier, template)[0];
    }

    /**
     * Returns a list with the identified user.
     * @return The list of users in the database.
     * @throws Exception
     */
    public String[] GetList() throws Exception {
        OPERATION_NAME = (SoapOperationType.Operation_GetUsers);
        SOAP_ACTION = WSDL_TARGET_NAMESPACE + SoapOperationType.getMethodName(OPERATION_NAME);

        return (Call(null, null, null));
    }

    /**
     * Call to the web service.
     * @param template
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    private String[] Call(String name, String identifier, String template) throws Exception {
        User userParams = new User(name, identifier, template);

        // Executes the call.
        return SendServerRequest(userParams);
    }

    protected String[] SendServerRequest(User user) throws Exception {
        // Creates the request object which call the web service.
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, SoapOperationType.getMethodName(OPERATION_NAME));

        // Process the parameters depending on the operation type selected.
        switch(OPERATION_NAME) {
            case Operation_Add:
                request.addProperty("name", user.getName());
                request.addProperty("template", user.getTemplate());
                break;

            case Operation_Authenticate:
                request.addProperty("identifier", user.getIdentifier());
                request.addProperty("template", user.getTemplate());
                break;
            default:
                break;
        }

        // Serialize the envelope which will be used to carry the parameters for SOAP.
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        // Create the command that manage the connection with the WebServer.
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);

        SoapObject response=null;
        String[] sss = null;

        try {
            // Make the call
            httpTransport.call(SOAP_ACTION, envelope);

            // Get the server response.
            if (envelope.bodyIn instanceof Exception) {
                String str = ((SoapFault) envelope.bodyIn).faultstring;
                String strCode = ((SoapFault) envelope.bodyIn).faultcode;

                throw new SoapException(str, SoapExceptionType.UnhandledSoapException, strCode);
            }
            else {
                if(SoapOperationType.Operation_GetUsers == OPERATION_NAME) {
                    try {
                        response = (SoapObject)envelope.getResponse();
                    }catch(Exception exc) { // Is possible that getResponse launch an exception for some platforms and type of complex objects. Process then with "bodyIn" directly.
                        response = (SoapObject)envelope.bodyIn;
                    }
                } else {
                    response = (SoapObject)envelope.bodyIn;
                }


                // Maybe there is several parameters.
                if(response != null && response.getPropertyCount() > 0) {
                    // Complex objects
                    if(SoapOperationType.Operation_GetUsers == OPERATION_NAME) {
                        // pair(id, name) -> (0, 1), (2, 3), (4, 5)...
                        sss = new String[response.getPropertyCount()*2];
                        for(int pa = 0; pa < response.getPropertyCount(); pa++) {
                            Object property = response.getProperty(pa);
                            if (property instanceof SoapObject) {
                                SoapObject category_list = (SoapObject) property;
                                sss[pa*2] = category_list.getProperty(0).toString();
                                sss[(pa*2)+1] = category_list.getProperty(1).toString();
                            }
                            else { // Only one complex object
                                sss = new String[response.getPropertyCount()];
                                sss[pa] = response.getProperty(0).toString();
                                sss[(pa*2)+1] = response.getProperty(1).toString();
                                break;
                            }

                        }
                    }
                    else {
                        sss = new String[response.getPropertyCount()];
                        for(int pa = 0; pa < response.getPropertyCount(); pa++)
                            sss[pa] = (response.getPropertyAsString(pa));
                    }
                }
            }

            return sss;
        }
        catch (SoapException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw exception;
        }
    }
}
