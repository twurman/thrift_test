
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

function getHostPort() {
    numRuns++;
    var addr = getAddress(ip, port, 'cc');
    var transport = new Thrift.TXHRTransport(addr);
    var protocol  = new Thrift.TJSONProtocol(transport);
    var client = new SchedulerServiceClient(protocol);
    var response = client.consultAddress("Sirius");
    var msg = "Host: " + response.ip + ", Port: " + response.port;
    updateResponseDiv(msg);
    console.log(numRuns);
}
document.getElementById("getHostPort").addEventListener("click",getHostPort);

function asyncTest() {
    var asyncCall = setTimeout(function() { getHostPort(); }, 1);
    while(numRuns < 10) {
        //do nothing
    }
    clearTimout(asyncCall);
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


