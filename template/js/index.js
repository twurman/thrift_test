
var text = "", ip = "clarity04.eecs.umich.edu", port = "4444", numRuns = 0;

function updateText(value) {
    text = value;
}

function updateResponseDiv(value) {
    $('#response').empty();
    $('#response').append("<p>" + value + "</p>");
}

function sendRequest() {
    //request instance of Sirius Service
    var addr = getAddress(ip, port, 'cc');
    var transport = new Thrift.TXHRTransport(addr);
    var protocol  = new Thrift.TJSONProtocol(transport);
    var client = new SchedulerServiceClient(protocol);
    var response = client.consultAddress("Sirius");
    var msg = "Host: " + response.ip + ", Port: " + response.port;
    console.log(msg);

    //send Sirius Service a request based on the ip and port from CC
    addr = getAddress(response.ip, response.port, 'cc');
    transport = new Thrift.TXHRTransport(addr);
    protocol  = new Thrift.TJSONProtocol(transport);
    client = new IPAServiceClient(protocol);

    response = client.submitRequest("Sirius");
}

var asyncCall;
function getHostPort() {
    console.log("Getting host port");
    var addr = getAddress(ip, port, 'cc');
    var transport = new Thrift.TXHRTransport(addr);
    var protocol  = new Thrift.TJSONProtocol(transport);
    var client = new SchedulerServiceClient(protocol);
    var response = client.consultAddress("Sirius");
    var msg = "Host: " + response.ip + ", Port: " + response.port;
    updateResponseDiv(msg);
    numRuns++;
    console.log(numRuns);
    if(numRuns > 10) {
        console.log("Done with asyncTest");
        window.clearInterval(asyncCall);
    }
}
document.getElementById("getHostPort").addEventListener("click",getHostPort);

function asyncTest() {
    asyncCall = window.setInterval(getHostPort, 100);
    console.log("Started asyncTest");
}
document.getElementById("asyncTest").addEventListener("click",asyncTest);


function askServer() {
    if(text) {
        updateResponseDiv("Sending...");
        var addr = getAddress('test');
        var transport = new Thrift.TXHRTransport(addr);
        var protocol  = new Thrift.TJSONProtocol(transport);
        var client = new QAServiceClient(protocol);

        var response = client.echo(text);
        updateResponseDiv(response);
    } else {
        console.log("Nothing to send!");
    }
}
document.getElementById("askServer").addEventListener("click",askServer);

function getItem(key) {
    return document.getElementById(key).value;
}

function getAddress(ip, port, destination) {
    return 'http://' + ip + ':' + port + '/' + destination;
}


