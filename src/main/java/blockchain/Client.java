package blockchain;

import blockchain.core.Node;
import blockchain.core.Wallet;
import blockchain.db.Context;
import blockchain.networking.MessageSender;
import blockchain.networking.ServerInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Client
{
    public static void main(String[] args)
    {
        Security.addProvider(new BouncyCastleProvider());

        int remotePort = 7001;
        String remoteHost = "127.0.0.1";

        HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();
        serverStatus.put(new ServerInfo(remoteHost, remotePort), new Date());

        try {
            ServerInfo serv = new ServerInfo(remoteHost, remotePort);
            String message = "testowa wiadomosc";
            new Thread(new MessageSender(serv, message)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
