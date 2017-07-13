package com.paritytrading.parity.net.pmr;

import static com.paritytrading.parity.net.pmr.PMR.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages.
 */
public class PMRParser implements MessageListener {

    private Version      version;
    private OrderEntered orderEntered;
    private Cancel       cancel;
    private Trade        trade;

    private PMRListener listener;

    /**
     * Create a parser for inbound messages.
     *
     * @param listener the message listener
     */
    public PMRParser(PMRListener listener) {
        this.version      = new Version();
        this.orderEntered = new OrderEntered();
        this.cancel       = new Cancel();
        this.trade        = new Trade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_VERSION:
            version.get(buffer);
            listener.version(version);
            break;
        case MESSAGE_TYPE_ORDER_ENTERED:
            orderEntered.get(buffer);
            listener.orderEntered(orderEntered);
            break;
        case MESSAGE_TYPE_CANCEL:
            cancel.get(buffer);
            listener.cancel(cancel);
            break;
        case MESSAGE_TYPE_TRADE:
            trade.get(buffer);
            listener.trade(trade);
            break;
        default:
            throw new PMRException("Unknown message type: " + (char)messageType);
        }
    }

}
