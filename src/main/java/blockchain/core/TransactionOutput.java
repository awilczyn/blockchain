package blockchain.core;

import blockchain.util.StringUtil;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionOutput implements Serializable
{
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTransactionId;


    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId)
    {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
                StringUtil.getStringFromKey(recipient)+Float.toString(value)+parentTransactionId
        );
    }

    public boolean isMine(PublicKey publicKey)
    {
        return (publicKey == recipient);
    }
}
