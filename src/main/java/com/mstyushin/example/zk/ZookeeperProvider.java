package com.mstyushin.example.zk;

import com.mstyushin.example.application.ElectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * ZookeeperProvider provides interface for interacting with ZK cluster
 */
@Slf4j
public class ZookeeperProvider {
    public static final int DEFAULT_SESSION_TIMEOUT = 3000;
    private ZooKeeper zooKeeper;

    public ZookeeperProvider(final String url, final ElectionService.ZNodeDeleteEventHandler handler) throws IOException {
        zooKeeper = new ZooKeeper(url, DEFAULT_SESSION_TIMEOUT, handler);
    }

    public String createZNode(final String znode, final boolean watch, final boolean isEphemeral) {
        String createdNodePath;

        try {
            final Stat nodeStat =  zooKeeper.exists(znode, watch);

            if(nodeStat == null) {
                createdNodePath = zooKeeper.create(znode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        (isEphemeral ?  CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.PERSISTENT));
            } else {
                createdNodePath = znode;
            }

        } catch (KeeperException | InterruptedException e) {
            log.error("Something went wrong while creating ZK node: ", e);
            throw new IllegalStateException(e);
        }

        return createdNodePath;
    }

    public boolean watchZNode(final String znode, final boolean watch) {
        boolean watched = false;

        try {
            final Stat nodeStat =  zooKeeper.exists(znode, watch);

            if(nodeStat != null) {
                watched = true;
            }

        } catch (KeeperException | InterruptedException e) {
            log.error("Something went wrong while watching ZK node: ", e);
            throw new IllegalStateException(e);
        }

        return watched;
    }

    public List<String> getZNodeChildren(final String znode, final boolean watch) {
        List<String> childNodes;

        try {
            childNodes = zooKeeper.getChildren(znode, watch);
        } catch (KeeperException | InterruptedException e) {
            log.error("Something went wrong while reading child ZK nodes: ", e);
            throw new IllegalStateException(e);
        }

        return childNodes;
    }
}
