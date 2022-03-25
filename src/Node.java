import java.io.Serializable;

public class Node  implements Comparable<Node>, Serializable {

    private char character;
    private int freq;
    private Node parent;
    private boolean isChar;
    private Node left = null;
    private Node right = null;

    public Node(int freq){
        this.freq = freq;
        isChar = false;
    }

    public Node(Character character, Integer freq, int key) {
        this.character = character;
        this.freq = freq;
        isChar = true;
    }

    public int compareTo(Node n) {
        if (freq < n.freq) {
            return -1;
        }
        else if(freq > n.freq) {
            return 1;
        }
        return 0;
    }

    public String toString() {
        String string = Character.toString(character) + ":" + Integer.toString(freq);
        return string;
    }

    public void addLeft(Node left) {
        this.left = left;
    }

    public void addRight(Node right) {
        this.right = right;
    }

    public void addFreq(int leftFreq, int rightFreq) {
        this.freq = leftFreq + rightFreq;
    }

    public int getFreq() {
        return freq;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void addParent(Node parent) {
        this.parent = parent;
    }

    public char getCharacter() {
        return character;
    }

    public boolean checkIsChar() {
        return isChar;
    }

}



