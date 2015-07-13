import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.util.EntityUtils;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

// Generated code
import edu.umich.clarity.thrift.SchedulerService;

//utils
import java.util.HashMap;

/**
 * Starts the question-answer server and listens for requests.
 */
public class CommandCenterDaemon {

  // Note: all classes in the same directory are automatically imported
  public static SchedulerServiceHandler handler;
  private static ServerSocket serversocket;
  private static HttpParams params;
  private static HttpService httpService;

  public static void main(String [] args) {
    int port = 9091;
    if (args.length == 1) {
      port = Integer.parseInt(args[0].trim());
      System.out.println("Using port: " + port);
    } else {
      System.out.println("Using default port: " + port);
    }

    try {
        serversocket = new ServerSocket(port);
        params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

        // Set up the HTTP protocol processor
        HttpProcessor httpproc = new BasicHttpProcessor();

        // Set up request handlers
        HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
        reqistry.register("*", new HttpReqHandler());

        // Set up the HTTP service
        httpService = new HttpService(httpproc, new NoConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        httpService.setParams(params);
        httpService.setHandlerResolver(reqistry);

    
        while(true) {
          // Set up HTTP connection
          Socket socket = serversocket.accept();
          DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
          System.out.println("Incoming connection from " + socket.getInetAddress());
          conn.bind(socket, params);

          // Start worker thread
          Thread t = new WorkerThread(httpService, conn);
          t.setDaemon(true);
          t.start();
        }
        
    } catch (InterruptedIOException ex) {
        return;
    } catch (IOException e) {
        System.err.println("I/O error initialising connection thread: " + e.getMessage());
        return;
    }

  }

  static class HttpReqHandler implements HttpRequestHandler {

      public HttpReqHandler() {
          super();
      }

      public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {

          String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
          if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
              throw new MethodNotSupportedException(method + " method not supported");
          }
          String target = request.getRequestLine().getUri();

          if (request instanceof HttpEntityEnclosingRequest && target.equals("/test")) {
              HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
              byte[] entityContent = EntityUtils.toByteArray(entity);
              System.out.println("Incoming content: " + new String(entityContent));
              
              final String output = this.thriftRequest(entityContent);
              
              System.out.println("Outgoing content: "+output);
              
              EntityTemplate body = new EntityTemplate(new ContentProducer() {

                  public void writeTo(final OutputStream outstream) throws IOException {
                      OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
                      writer.write(output);
                      writer.flush();
                  }

              });
              body.setContentType("text/html; charset=UTF-8");
              response.setEntity(body);
          }
      }
      
      private String thriftRequest(byte[] input){
          try{
              System.out.println("Thrift request");
              //Input
              TMemoryBuffer inbuffer = new TMemoryBuffer(input.length);           
              inbuffer.write(input);              
              TProtocol  inprotocol   = new TJSONProtocol(inbuffer);                   
              
              //Output
              TMemoryBuffer outbuffer = new TMemoryBuffer(100);           
              TProtocol outprotocol   = new TJSONProtocol(outbuffer);
              
              TProcessor processor = new SchedulerService.Processor(new SchedulerServiceHandler());      
              processor.process(inprotocol, outprotocol);
              
              byte[] output = new byte[outbuffer.length()];
              outbuffer.readAll(output, 0, output.length);
          
              return new String(output,"UTF-8");
          }catch(Throwable t){
              return "Error:"+t.getMessage();
          }
           
                   
      }
        
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    System.out.println("handling request");
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }

    }

}
