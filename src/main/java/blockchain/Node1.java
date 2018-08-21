package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Node1
{

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<>();

    public static void main(String[] args)
    {
        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7001;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();
        Wallet wallet = new Wallet();

        Node localNode = new blockchain.core.Node(context, wallet);
        localNode.start();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new HeartBeatReceiver(clientSocket, serverStatus, localPort)).start();

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public static void prepareNodeList()
    {
        serverStatus.put(new ServerInfo("127.0.0.1", 7002), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7003), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());
    }
}

