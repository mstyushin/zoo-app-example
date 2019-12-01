package com.mstyushin.example.protocol;

import com.mstyushin.example.entity.StatusEntity;

public class StatusInfo extends ResponseInfo {
    private StatusEntity status;

    public StatusInfo(StatusEntity status) {
        this.status = status;
    }

    public StatusEntity getStatus() {
        return status;
    }
}
