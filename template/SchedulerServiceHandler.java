// This handler implements the services provided to the client.

// Java packages
import java.util.List;
import java.util.ArrayList;

// Interface definition
import edu.umich.clarity.thrift.LatencyStat;
import edu.umich.clarity.thrift.QueryInput;
import edu.umich.clarity.thrift.QuerySpec;
import edu.umich.clarity.thrift.RegMessage;
import edu.umich.clarity.thrift.THostPort;
import edu.umich.clarity.thrift.SchedulerService;

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

