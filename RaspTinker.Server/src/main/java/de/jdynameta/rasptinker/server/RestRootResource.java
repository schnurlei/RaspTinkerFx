package de.jdynameta.rasptinker.server;


import de.jdynameta.rasptinker.model.Sensor;
import de.jdynameta.rasptinker.model.SensorType;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class RestRootResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(RestRootResource.class.getName());
    private final Sensor temperatuSensor1;
    
    public RestRootResource() {
        
        this.temperatuSensor1 = new Sensor();
        temperatuSensor1.setName("Temperatur1");
        SensorType temparaturType = new SensorType();
        temparaturType.setUnit("°C");
        temparaturType.setType("Temparatur");
        temparaturType.setMinValue(-30);
        temparaturType.setMaxValue(50);
        temperatuSensor1.setType(temparaturType);
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getHello() {
//        return "test";
//    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor getHello() {
        return this.temperatuSensor1;
    }
              
}
