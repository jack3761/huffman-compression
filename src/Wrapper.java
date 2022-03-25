import java.io.Serializable;
import java.util.PriorityQueue;

public class Wrapper implements Serializable {

    private PriorityQueue<Node> tree;
    private byte[] binaryText;

    public Wrapper(PriorityQueue<Node> tree, byte[] binaryText) {
        this.tree = tree;
        this.binaryText = binaryText;
    }

    public PriorityQueue<Node> getTree() {
        return tree;
    }

    public byte[] getBinaryText() {
        return binaryText;
    }
}
