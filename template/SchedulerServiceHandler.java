// This handler implements the services provided to the client.
package edu.umich.clarity.thrift;

// Java packages
import java.util.List;
import java.util.ArrayList;

// Interface definition
import edu.umich.clarity.thrift.QAService;

/** Implementation of the question-answer interface defined
 * in the question-answer thrift file. A client request to any
 * method defined in the thrift file is handled by the
 * corresponding method here.
 */
public class SchedulerServiceHandler implements SchedulerService.Iface {
  
  public SchedulerServiceHandler()
  {

  }

  /** Echos input */
  public THostPort consultAddress(String serviceType) throws TException {
        LOG.info("receive consulting about service " + serviceType);
        THostPort hostPort = new THostPort();
        hostPort.ip = "clarity04.eecs.umich.edu";
        hostPort.port = 4200;
        return hostPort;
    }

}

