package com.mstyushin.example.provider;

import com.mstyushin.example.entity.StatusEntity;
import com.mstyushin.example.protocol.ProtocolMessage;

public class StatusEntityProvider {
    private ProtocolMessage protocolMessage;
    public StatusEntityProvider(ProtocolMessage protocolMessage) {
        this.protocolMessage = protocolMessage;
    }

    public StatusEntity get() {
        // TODO: read status from current leader or just return that from memory if we are the leader
        return new StatusEntity("");
    }
}
