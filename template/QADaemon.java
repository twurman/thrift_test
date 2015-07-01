// Thrift java libraries 
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

// Generated code
import qastubs.QAService;

/**
 * Starts the question-answer server and listens for requests.
 */
public class QADaemon {
  // Note: all classes in the same directory are automatically imported
  /** 
   * An object whose methods are implementations of the question-answer thrift
   * interface.
   */
  public static QAServiceHandler handler;

  // /**
  //  * An object responsible for communication between the handler
  //  * and the server. It decodes serialized data using the input protocol,
  //  * delegates processing to the handler, and writes the response
  //  * using the output protocol.
  //  */
  // public static QAService.Processor<QAServiceHandler> processor;

  /** 
   * Entry point for question-answer.
   * @param args the argument list. Provide the port number
   * as the first and only argument.
   */
  public static void main(String [] args) {
    try {
      int tmp_port = 9091;
      if (args.length == 1) {
        tmp_port = Integer.parseInt(args[0].trim());
        System.out.println("Using port: " + tmp_port);
      } else {
        System.out.println("Using default port: " + tmp_port);
      }

      // Inner classes receive copies of local variables to work with.
      // Local vars must not change to ensure that inner classes are synced.
      final int port = tmp_port;
      
      // The handler implements the generated java interface
      // that was originally specified in the thrift file.
      // When it's called, an OpenEphyra object is created.
      handler = new QAServiceHandler();

      // processor = new QAService.Processor<QAServiceHandler>(handler);
      TMultiplexedProcessor processor = new TMultiplexedProcessor();
      processor.registerProcessor("test", new QAService.Processor(handler));

      try {
        // Start the question-answer server
        TServerTransport serverTransport = new TServerSocket(port);
        TServer server = new TSimpleServer(processor, serverTransport);
        System.out.println("Starting the question-answer server at port " + port + "...");
        server.serve();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

}
