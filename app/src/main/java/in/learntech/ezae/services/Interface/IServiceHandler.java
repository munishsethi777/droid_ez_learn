package in.learntech.ezae.services.Interface;

import org.json.JSONObject;

/**
 * Created by baljeet singh on 2/13/2016.
 */
public interface IServiceHandler {
    public void processServiceResponse(JSONObject response);
    public void setCallName(String call);
}
