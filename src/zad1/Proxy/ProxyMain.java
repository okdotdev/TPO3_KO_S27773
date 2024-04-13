package zad1.Proxy;



public class ProxyMain {
    public static void main(String[] args) {
        int port = 8080;
        ProxyServer proxyServer = new ProxyServer(port);
        proxyServer.run();
    }
}
