package de.jdynameta.rasptinker.server;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class RestRootResource implements Serializable {

    private static final Logger LOG = Logger.getLogger(RestRootResource.class.getName());
 
    public RestRootResource() {
    }

    @GET
    @Path("hello")
    @Produces("text/plain")
    public String getHello() {
        return "Hello from Root";
    }

    @GET
    @Path("helloSecure")
    @Produces("text/plain")
    public String getHelloSecure(@Context SecurityContext sc, @Context HttpServletRequest req ) {
        
        HttpSession session = req.getSession();
        LOG.log(Level.WARNING, session.getId());
        
        return "Hello from Root User:" + sc.getUserPrincipal();
    }

              
}
