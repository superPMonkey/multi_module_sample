﻿package xulingbo.zookeeper.locks;

import xulingbo.zookeeper.TestMainClient;
import xulingbo.zookeeper.TestMainServer;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;
import java.util.List;

/**
 * locks
 * <p/>
 * Author By: junshan
 * Created Date: 2010-9-7 16:49:40
 */
public class Locks extends TestMainClient {
    private static final Logger logger = Logger.getLogger(Locks.class);
    private String myZnode;

    private Locks(String connectString, String root) {
        super(connectString);
        this.root = root;
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException | InterruptedException e) {
                logger.error(e);
            }
        }
    }

    private void getLock() throws KeeperException, InterruptedException {
        List<String> list = zk.getChildren(root, false);
        String[] nodes = list.toArray(new String[list.size()]);
        Arrays.sort(nodes);
        if (myZnode.equals(root + "/" + nodes[0])) {
            doAction();
        } else {
            waitForLock(nodes[0]);
        }
    }

    private void check() throws InterruptedException, KeeperException {
        myZnode = zk.create(root + "/lock_", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        getLock();
    }

    private void waitForLock(String lower) throws InterruptedException, KeeperException {
        Stat stat = zk.exists(root + "/" + lower, true);
        if (stat != null) {
            mutex.wait();
        } else {
            getLock();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("得到通知");
            super.process(event);
            doAction();
        }
    }

    /**
     * 执行其他任务
     */
    private void doAction() {
        System.out.println("同步队列已经得到同步，可以开始执行后面的任务了");
    }

    public static void main(String[] args) {
        TestMainServer.start();
        String connectString = "localhost:" + TestMainServer.CLIENT_PORT;

        Locks lk = new Locks(connectString, "/locks");
        try {
            lk.check();
        } catch (InterruptedException | KeeperException e) {
            logger.error(e);
        }
    }


}