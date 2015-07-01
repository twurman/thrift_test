// This handler implements the services provided to the client.

// Java packages
import java.util.List;
import java.util.ArrayList;

// Interface definition
import qastubs.QAService;

/** Implementation of the question-answer interface defined
 * in the question-answer thrift file. A client request to any
 * method defined in the thrift file is handled by the
 * corresponding method here.
 */
public class QAServiceHandler implements QAService.Iface {
  
  public QAServiceHandler()
  {

  }

  /** Echos input */
  public String echo(String data)
  {
    return data;
  }

}

