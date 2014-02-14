/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package d2tree;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import p2p.simulator.message.Message;
import p2p.simulator.message.MessageBody;
import d2tree.RoutingTable.Role;

/**
 * 
 * @author Pavlos Melissinos
 */
public class PrintMessage extends MessageBody {

    // private boolean down;
    private long         initialNode;
    private int          msgType;
    public static String logDir      = "D:/logs/";
    // public static String logDir = "../logs/";
    static String        allLogFile  = logDir + "main.txt";
    static String        treeLogFile = logDir + "tree.txt";

    // public PrintMessage(boolean down, int msgType, long initialNode) {
    // this.down = down;
    public PrintMessage(int msgType, long initialNode) {
        this.initialNode = initialNode;
        this.msgType = msgType;
    }

    public long getInitialNode() {
        return initialNode;
    }

    public int getSourceType() {
        return this.msgType;
    }

    @Override
    public int getType() {
        return D2TreeMessageT.PRINT_MSG;
    }

    static public synchronized void print(Message msg, String printText,
            String logFile) {
        try {
            if (!logFile.equals(PrintMessage.logDir + "errors.txt") &&
                    !logFile.equals(PrintMessage.logDir + "conn-disconn.txt") &&
                    !logFile.equals(PrintMessage.logDir + "messages.txt")) {
                System.out.println("Saving log to " + logFile);
            }
            PrintWriter out = new PrintWriter(new FileWriter(logFile, true));

            // PrintMessage data = (PrintMessage) msg.getData();
            out.format("\n%s(MID = %d): %s",
                    D2TreeMessageT.toString(msg.getType()), msg.getMsgId(),
                    printText);
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static public synchronized void print(Message msg, String printText,
            String logFile, long initialNode) {
        try {
            if (!logFile.equals(PrintMessage.logDir + "errors.txt")) {
                System.out.println("Saving log to " + logFile);
            }
            PrintWriter out = new PrintWriter(new FileWriter(logFile, true));

            // PrintMessage data = (PrintMessage) msg.getData();
            out.format("\n%s(MID = %d) %d: %s",
                    D2TreeMessageT.toString(msg.getType()), msg.getMsgId(),
                    initialNode, printText);
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static ArrayList<ArrayList<Long>> getPeerIdsByTreeLevel(
            HashMap<Long, RoutingTable> myPeers) {
        // group peers by tree level
        ArrayList<Long> currentLevelNodes = new ArrayList<Long>();
        ArrayList<Long> nextLevelNodes = new ArrayList<Long>();
        currentLevelNodes.add(1L);

        ArrayList<ArrayList<Long>> allPeers = new ArrayList<ArrayList<Long>>();
        while (!currentLevelNodes.isEmpty()) {
            allPeers.add(currentLevelNodes);
            for (Long peerId : currentLevelNodes) {
                RoutingTable peerRT = myPeers.get(peerId);
                assert peerRT != null;
                if (!peerRT.isLeaf() && !peerRT.isBucketNode()) {
                    nextLevelNodes.add(peerRT.get(Role.LEFT_CHILD));
                    nextLevelNodes.add(peerRT.get(Role.RIGHT_CHILD));
                }

            }
            currentLevelNodes = new ArrayList<Long>(nextLevelNodes);
            nextLevelNodes.clear();
        }
        return allPeers;
    }

    static void printPBT(Message msg, HashMap<Long, RoutingTable> peerRTs,
            String logFile) throws IOException {

        ArrayList<ArrayList<Long>> pbtPeers = PrintMessage
                .getPeerIdsByTreeLevel(peerRTs);
        for (ArrayList<Long> peerIds : pbtPeers) {

            PrintWriter out = null;
            out = new PrintWriter(new FileWriter(logFile, true));
            out.println(peerIds);
            out.close();

            out = new PrintWriter(new FileWriter(allLogFile, true));
            out.println(peerIds);
            out.close();
            // for (Long peerId : peerIds) {
            //
            // RoutingTable peerRT = peerRTs.get(peerId);
            // if (peerRT == null) continue;
            // PrintWriter out = null;
            // out = new PrintWriter(new FileWriter(logFile, true));
            // out.format("\nId=%3d,", peerId);
            // peerRT.print(out);
            // out.close();
            //
            // out = new PrintWriter(new FileWriter(allLogFile, true));
            // out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peerId);
            // peerRT.print(out);
            // out.close();
            //
            // out = new PrintWriter(new FileWriter(allLogFile, true));
            // out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peerId);
            // peerRT.print(out);
            // out.close();
            // }
        }
        if (pbtPeers.isEmpty()) throw new IllegalStateException();

    }

    static void printBuckets(HashMap<Long, RoutingTable> peerRTs, String logFile)
            throws IOException {

        ArrayList<ArrayList<Long>> pbtPeers = PrintMessage
                .getPeerIdsByTreeLevel(peerRTs);
        HashMap<Long, ArrayList<Long>> bucketIds = DataExtractor
                .getBucketNodes(peerRTs);

        HashMap<Long, ArrayList<Long>> properBucketIds = new HashMap<Long, ArrayList<Long>>();
        ArrayList<Long> leaves = pbtPeers.get(pbtPeers.size() - 1);
        for (Long leaf : leaves) {
            properBucketIds.put(leaf,
                    DataExtractor.getOrderedBucketNodes(peerRTs, leaf));
        }

        PrintWriter out = new PrintWriter(new FileWriter(logFile, true));
        PrintWriter out1 = new PrintWriter(new FileWriter(allLogFile, true));
        PrintWriter out2 = new PrintWriter(new FileWriter(treeLogFile, true));

        for (Long leaf : leaves) {
            String properText = String
                    .format("\nPrinting Bucket of %d (navigation via bucket nodes' routing tables, starting from %d)\n%s",
                            leaf, leaf, properBucketIds.get(leaf));
            String text = String
                    .format("\nPrinting Bucket of %d (show all nodes with %d as a representatives)\n%s",
                            leaf, leaf, bucketIds.get(leaf));
            out.format(text);
            out.format(properText);

            out1.format(text);
            out1.format(properText);

            out2.format(text);
            out2.format(properText);
        }
        out.close();
        out1.close();
        out2.close();
        // for (Long leafId : bucketIds.keySet()) {
        //
        // ArrayList<Long> bucket = bucketIds.get(leafId);
        // out.format("\nId=%3d,", leafId);
        // out.print(bucket);
        // out.close();
        //
        // out1 = new PrintWriter(new FileWriter(allLogFile, true));
        // out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peerId);
        // peerRT.print(out);
        // out.close();
        //
        // out2 = new PrintWriter(new FileWriter(treeLogFile, true));
        // out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peerId);
        // peerRT.print(out);
        // out.close();
        // }
    }

    static void printTreeByIndex(List<D2TreeCore> peers, Message msg,
            String logFile) throws IOException {
        PrintMessage data = (PrintMessage) msg.getData();
        long id = msg.getDestinationId();
        if (id == msg.getSourceId()) {
            System.out.println("Saving log to " + logFile);
            // TODO Could the removal of a peer (contract) cause problems in the
            // loop? - test this case
            for (int index = 0; index < peers.size(); index++) {
                D2TreeCore peer = peers.get(index);
                RoutingTable peerRT = peer.getRT();
                PrintWriter out = null;
                try {
                    out = new PrintWriter(new FileWriter(logFile, true));
                    out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peer.id);
                    peerRT.print(out);
                    out.close();

                    out = new PrintWriter(new FileWriter(allLogFile, true));
                    out.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peer.id);
                    peerRT.print(out);
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (data.getSourceType() == D2TreeMessageT.JOIN_REQ ||
                data.getSourceType() == D2TreeMessageT.JOIN_RES) return;
        // String logFile = logDir + "main" + data.getInitialNode() + ".log";
        PrintWriter out2 = null;
        try {
            out2 = new PrintWriter(new FileWriter(treeLogFile, true));
            for (int index = 0; index < peers.size(); index++) {
                D2TreeCore peer = peers.get(index);
                String msgType = peer.isRoot() ? D2TreeMessageT.toString(data
                        .getSourceType()) + "\n" : "";
                out2.format("\n%s MID=%5d, Id=%3d,", msgType, msg.getMsgId(),
                        peer.id);
                peer.getRT().print(out2);
                if (data.getSourceType() == D2TreeMessageT.PRINT_ERR_MSG &&
                        peer.id == id) {
                    out2.format(" <-- DISCREPANCY DETECTED");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        out2.close();
        // PrintMessage data = (PrintMessage) msg.getData();
        //
        // PrintWriter indieLog = new PrintWriter(new FileWriter(logFile,
        // true));
        // PrintWriter allLog = new PrintWriter(new FileWriter(allLogFile,
        // true));
        // PrintWriter treeLog = new PrintWriter(new FileWriter(treeLogFile,
        // true));
        // for (int index = 0; index < peers.size(); index++) {
        // D2TreeCore peer = peers.get(index);
        // if (peer == null) continue;
        // RoutingTable peerRT = peer.getRT();
        //
        // indieLog.format("\nId=%3d,", peer.id);
        // peerRT.print(indieLog);
        //
        // allLog.format("\nMID=%3d, Id=%3d,", msg.getMsgId(), peer.id);
        // peerRT.print(allLog);
        //
        // String msgType = peer.isRoot() ? D2TreeMessageT.toString(data
        // .getSourceType()) + " \n" : "";
        // treeLog.format("\n%sMID=%5d, Id=%3d,", msgType, msg.getMsgId(),
        // peer.id);
        // peerRT.print(treeLog);
        // // if (data.getSourceType() == D2TreeMessageT.PRINT_ERR_MSG &&
        // // peer.id == id) {
        // // out3.format(" <-- DISCREPANCY DETECTED");
        // // out4.format(" <-- DISCREPANCY DETECTED");
        // // }
        // }
    }

    static void serializePeers(long msgId, String objectFile,
            HashMap<Long, RoutingTable> routingTables) {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(objectFile));
            oos.writeLong(msgId);
            oos.writeObject(routingTables);
            oos.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = -6662495188045778809L;
}
