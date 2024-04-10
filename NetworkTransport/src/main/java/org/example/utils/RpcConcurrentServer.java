package org.example.utils;

import org.example.IService;
import org.example.rpcProtocol.ClientWorker;

import java.net.Socket;

public class RpcConcurrentServer extends AbstractConcurrentServer
{
    private IService service;

    public RpcConcurrentServer(int port, IService service)
    {
        super(port);
        this.service = service;
        System.out.println("Transport- RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client)
    {
        ClientWorker worker = new ClientWorker(service, client);

        Thread tw = new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
