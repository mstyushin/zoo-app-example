package com.mstyushin.example.application;

import com.google.common.base.Preconditions;
import com.mstyushin.example.provider.ZookeeperProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a service that handles leader election process in distributed application
 */
@Slf4j
public class ElectionService implements Runnable {
    public static final String ROOT_ZNODE = "/example";
    public static final String APPLICATION_ZNODE_PREFIX = "/app_";

    private final Integer appId;
    private final ZookeeperProvider zkProvider;

    private String appZNodePath;
    private String watchedZNodePath;

    public ElectionService(final Integer appId, final String zkURL) throws IOException {
        this.appId = appId;
        zkProvider = new ZookeeperProvider(zkURL, new ZNodeDeleteEventHandler());
    }

    private void tryPromote() {
        final List<String> childZNodePaths = zkProvider.getZNodeChildren(ROOT_ZNODE, false);

        Collections.sort(childZNodePaths);

        int index = childZNodePaths.indexOf(appZNodePath.substring(appZNodePath.lastIndexOf('/') + 1));
        if(index == 0) {
            log.info("Application[" + appId + "][ElectionService] - I am the new leader!");
        } else {
            final String watchedZNodeShortPath = childZNodePaths.get(index - 1);
            watchedZNodePath = ROOT_ZNODE + "/" + watchedZNodeShortPath;
            log.info("Application[" + appId + "][ElectionService] - Setting watch on node with path: " + watchedZNodePath);
            zkProvider.watchZNode(watchedZNodePath, true);
        }
    }

    @Override
    public void run() {
        log.info("Application[" + appId + "][ElectionService] - starting");
        final String rootZNodePath = zkProvider.createZNode(ROOT_ZNODE, false, false);
        Preconditions.checkNotNull(rootZNodePath, "Error while creating/reading root znode: " + ROOT_ZNODE);

        appZNodePath = zkProvider.createZNode(rootZNodePath + APPLICATION_ZNODE_PREFIX, false, true);
        Preconditions.checkNotNull(appZNodePath, "Error while creating/reading application znode: " + APPLICATION_ZNODE_PREFIX);

        log.info("Application[" + appId + "][ElectionService] - started");

        tryPromote();
    }

    public class ZNodeDeleteEventHandler implements Watcher {
        @Override
        public void process(WatchedEvent watchedEvent) {
            final Event.EventType eventType = watchedEvent.getType();
            log.debug("Application[" + appId + "][ElectionService] - Event received: " + watchedEvent);
            if(Event.EventType.NodeDeleted.equals(eventType)) {
                if(watchedEvent.getPath().equalsIgnoreCase(watchedZNodePath)) {
                    log.info("Application[" + appId + "][ElectionService] - triggering leader election");
                    tryPromote();
                }
            }

        }
    }
}
