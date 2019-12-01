package com.mstyushin.example.resource;

import com.mstyushin.example.protocol.*;
import com.mstyushin.example.provider.StatusEntityProvider;

public class StatusResource extends BaseResource {
    public StatusResource(ProtocolMessage protocolMessage) {
        super(protocolMessage);
    }

    public Result get() {

        StatusEntityProvider statusEntityProvider = new StatusEntityProvider(protocolMessage);
        StatusInfo statusInfo    = new StatusInfo(statusEntityProvider.get());

        return new Result<>(statusInfo);
    }

    public Result post() {
        return new Result<>(new ResponseInfo());
    }

    public Result patch() {
        return success(202);
    }

    public Result delete() {
        return success(203);
    }
}
