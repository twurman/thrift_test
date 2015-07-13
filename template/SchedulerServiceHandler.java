// This handler implements the services provided to the client.

// Java packages
import java.util.List;
import java.util.ArrayList;

// Interface definition
import edu.umich.clarity.thrift.*;
import org.apache.thrift.TException;

/** Implementation of the question-answer interface defined
 * in the question-answer thrift file. A client request to any
 * method defined in the thrift file is handled by the
 * corresponding method here.
 */
public class SchedulerServiceHandler implements SchedulerService.Iface {
  
	public SchedulerServiceHandler()
	{

	}

	@Override
	public THostPort consultAddress(String serviceType) throws TException {
	    System.out.println("receive consulting about service " + serviceType);
	    THostPort hostPort = new THostPort();
	    hostPort.ip = "clarity04.eecs.umich.edu";
	    hostPort.port = 4200;
	    return hostPort;
	}

	@Override
	public void registerBackend(RegMessage message) throws TException {

	}

	@Override
	public void updateLatencyStat(String name, LatencyStat latencyStat) throws TException {

	}

}

