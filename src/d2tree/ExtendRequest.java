package d2tree;

import p2p.simulator.message.MessageBody;

public class ExtendRequest extends MessageBody {

    private static final long serialVersionUID = -671508466340485564L;

    // private long optimalBucketSize;
    private long              oldOptimalBucketSize;
    private long              initialNode;
    private boolean           left;
    private long              optimalHeight;
    private long currentHeight;

    // public ExtendRequest(long optimalBucketSize, long oldOptimalBucketSize,
    // long initialNode) {
    public ExtendRequest(long oldOptimalBucketSize, boolean left,
            long initialNode, long optimalHeight, long currentHeight) {
        // this.optimalBucketSize = optimalBucketSize;
        this.oldOptimalBucketSize = oldOptimalBucketSize;
        this.initialNode = initialNode;
        this.left = left;
        this.optimalHeight = optimalHeight;
        this.currentHeight = currentHeight;
    }

    long getInitialNode() {
        return initialNode;
    }

    long getOptimalHeight() {
        return this.optimalHeight;
    }
    
    long getCurrentHeight() {
        return this.currentHeight;
    }
    void incrementHeight(){
    	this.currentHeight++;
    }

    boolean buildsLeftLeaf() {
        return left;
    }

    // public long getOptimalBucketSize(){
    // return this.optimalBucketSize;
    // }
    public long getOldOptimalBucketSize() {
        return this.oldOptimalBucketSize;
    }

    @Override
    public int getType() {
        return D2TreeMessageT.EXTEND_REQ;
    }

}
