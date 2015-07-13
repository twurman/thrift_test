
var text = "", ip = "clarity04.eecs.umich.edu", port = "4444";

function updateText(value) {
    text = value;
}

function updateResponseDiv(value) {
    $('#response').empty();
    $('#response').append("<p>" + value + "</p>");
}

function getHostPort() {
    var addr = getAddress('test');
    var transport = new Thrift.TXHRTransport(addr);
    var protocol  = new Thrift.TJSONProtocol(transport);
    var client = new SchedulerServiceClient(protocol);
    var response = client.consultAddress("Sirius");
    var msg = "Host: " + response.ip + ", Port: " + response.port;
    updateResponseDiv(msg);
    console.log(msg);
}

function threadedReq() {
    for(var i = 0; i < 10; i++) {
        getHostPort();
        console.log("sent req");
    }
}
document.getElementById("getHostPort").addEventListener("click",threadedReq);

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

function getAddress(destination) {
    return 'http://' + ip + ':' + port + '/' + destination;
}


