package blockchain.core;

import blockchain.core.genesis.GenesisBlock;
import blockchain.db.Context;
import blockchain.networking.MessageSender;
import blockchain.networking.ServerInfo;
import blockchain.serialization.PublicKeyDeserizlizer;
import blockchain.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.security.PublicKey;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Node implements Runnable
{
    private Context context;

    private Wallet minerWallet;

    private Block genesisBlock;

    private Block currentBlock;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<byte [], TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    /** peer to peer data */

    private Thread miningThread;
    private boolean shouldMine;

    private Wallet testWallet;

    HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static HashMap<byte [], Transaction> pool = new HashMap();

    public Node(Context context, Wallet wallet, Block genesisBlock){
        this.context = context;
        this.minerWallet = wallet;
        this.genesisBlock = genesisBlock;
        this.testWallet = new Wallet();
    }

    public Node(Context localContext, Wallet localWallet)
    {
        this(localContext, localWallet, GenesisBlock.getInstance(localContext, localWallet).getBlock());
    }

    public void start()
    {
        if(miningThread == null){
            miningThread = new Thread(this);
        }

        if(shouldMine){
            Log.log(Level.INFO, "Node already running");
            return;
        }

        shouldMine = true;
        miningThread.start();
    }

    public void run()
    {
        mine();
    }

    private void mine()
    {
        Wallet walletB = new Wallet();
        while(shouldMine) {
            blockchain.add(genesisBlock);
            UTXOs.put(genesisBlock.transactions.get(0).outputs.get(0).id, genesisBlock.transactions.get(0).outputs.get(0));

            if (!pool.isEmpty()) {


            }
            Block block1 = new Block(genesisBlock.hash);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("\nLocal wallet is Attempting to send funds (40) to WalletB...");

            Transaction transaction1 = minerWallet.sendFunds(walletB.getPublicKey(), 40f);
            block1.addTransaction(transaction1);
            addBlock(block1);
            //context.putBlock(block1);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block2 = new Block(block1.hash);
            System.out.println("\nMiner wallet attempting to send more funds (1000) than it has...");
            block2.addTransaction(minerWallet.sendFunds(walletB.getPublicKey(), 1000f));
            addBlock(block2);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block3 = new Block(block2.hash);
            System.out.println("\nWalletB is Attempting to send funds (20) to Miner wallet...");
            block3.addTransaction(walletB.sendFunds( minerWallet.getPublicKey(), 20));
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
            System.out.println("\nThe block chain: ");
            System.out.println(blockchainJson);
        }
    }

    public static void addBlock(Block newBlock)
    {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public void broadcast(String message) {
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        for (ServerInfo info: this.serverStatus.keySet()) {
            Thread thread = new Thread(new MessageSender(info, message));
            thread.start();
            threadArrayList.add(thread);
        }
    }

    public void multicast(ArrayList<ServerInfo> toPeers, String message) {
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        for (int i = 0; i < toPeers.size(); i++) {
            Thread thread = new Thread(new MessageSender(toPeers.get(i), message));
            thread.start();
            threadArrayList.add(thread);
        }
    }

    public void addTransactionToPool(float value) throws IOException {
        Transaction transaction1 = minerWallet.sendFunds(testWallet.getPublicKey(), value);
        pool.put(transaction1.getTransactionId(), transaction1);

        String transactionJson = new GsonBuilder().setPrettyPrinting().create().toJson(transaction1);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PublicKey.class, new PublicKeyDeserizlizer());
        Gson gson = gsonBuilder.create();
        Transaction test = gson.fromJson(transactionJson, Transaction.class);
        broadcast(transaction1.toString());
    }
}
