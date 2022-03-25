import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * The type Huffman.
 */
public class Huffman {

    private PriorityQueue<Node> tree;

    /**
     * Create tree structure between nodes using the tree priority queue to sort the lowest frequency nodes first
     *
     * @param fileName the file name
     * @return the node
     * @throws FileNotFoundException the file not found exception
     */
    public Node createTree(String fileName) throws FileNotFoundException {
        //find the frequencies of each character from the text file
        HashMap<Character, Integer> frequencies = getFrequencies("C:\\Uni Work\\Data structures and algorithms\\src\\" + fileName);
        //create all of the node objects using these frequencies
        tree = createNodes(frequencies);

        //go through the priority queue and assign left/right/parent nodes depending on their frequencies
        Node root = null;
        while (tree.size() > 1)  {
            //collect the two lowest frequency nodes and remove from the queue
            Node least1 = tree.poll();
            Node least2 = tree.poll();
            //combine the frequencies to create a new parent node for them both
            Node combined = new Node(least1.getFreq() + least2.getFreq());
            //assign the left/right nodes to this new one
            combined.addRight(least1);
            combined.addLeft(least2);
            //assign the parent node to the lower frequency ones
            least1.addParent(combined);
            least2.addParent(combined);
            tree.add(combined);
            root = combined;
        }
        return root;
    }

    /**
     * Convert original text into binary using the codes found from the frequencies.
     *
     * @param fileName the file name
     * @param codes    the codes
     * @return the string
     */
    public String convertToBinary(String fileName, HashMap<Character, String> codes) {
        File text = new File(fileName);
        StringBuilder binaryText = new StringBuilder();

        //go over each character in the text file and append its corresponding code from the HashMap to the StringBuilder
        try (FileReader fr = new FileReader(text)) {
            BufferedReader bf = new BufferedReader(fr);
            int value;
            while ((value = bf.read()) != -1) {
                char c = (char) value;
                binaryText.append(codes.get(c));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return binaryText.toString();
    }

    /**
     * Recursively traverse the tree starting with the root node to assign the codes to each of the characters
     *
     * @param n           the n
     * @param currentCode the current code
     * @param codes       the codes
     * @return the codes
     */
    public HashMap<Character, String> getCodes(Node n, String currentCode, HashMap<Character, String> codes) {
        //if it isn't a leaf node go to its left and right nodes and repeat, incrementing the current code accordingly
        if (!n.checkIsChar()) {
            getCodes(n.getLeft(), currentCode + "0", codes);
            getCodes(n.getRight(), currentCode + "1", codes);
        }
        else {
            //if it's a leaf node then assign it the current code
            codes.put(n.getCharacter(), currentCode);
        }
        return codes;
    }


    /**
     * Print out the tree to command line
     * <p>
     * Used for testing to see the tree
     *
     * @param n      the n
     * @param dashes the dashes
     */
    public void printTree(Node n, String dashes) {
        // print with colon if leaf node
        if (n.checkIsChar()) {
            System.out.println(dashes + n.getCharacter() + ":" + n.getFreq());
        }
        else {
            System.out.println(dashes + n.getFreq());
        }

        // Start recursive on left child then right
        if (n.getLeft() != null) {
            printTree(n.getLeft(), dashes + "-");
        }
        if (n.getRight() != null) {
            printTree(n.getRight(), dashes + "-");
        }
    }

    /**
     * Create nodes and adds to priority queue so they're in order for the tree creation
     *
     * @param frequencies the frequencies
     * @return the priority queue
     */
    public PriorityQueue<Node> createNodes(HashMap<Character, Integer> frequencies) {
        PriorityQueue<Node> nodes = new PriorityQueue<>();
        int keyCount = 1;
        for (Character key : frequencies.keySet()) {
            nodes.add(new Node(key, frequencies.get(key), keyCount++));
        }
        return nodes;
    }

    /**
     * Iterates over the text file and adds the frequencies to the HashMap
     *
     * @param fileName the file name
     * @return the frequencies
     * @throws FileNotFoundException the file not found exception
     */
    public HashMap<Character, Integer> getFrequencies(String fileName) throws FileNotFoundException {
        HashMap<Character, Integer> frequencies = new HashMap<Character, Integer>();
        File text = new File(fileName);
        try (FileReader fr = new FileReader(text)) {
            BufferedReader bf = new BufferedReader(fr);
            String line;
            int value;
            //loop over the text file and add the frequencies to the character in the HashMap
            while ((value = bf.read()) != -1) {
                char c = (char) value;
                frequencies.merge(c, 1, Integer::sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frequencies;
    }

    /**
     * Wrap the tree and byte array formed by the binary text into the same object and save that object to a new compressed file
     *
     * @param binaryText the binary text
     */
    public void serialize(String binaryText) {
        byte[] converted = BitstringJava.GetBinary(binaryText);
        //create wrapper object using the tree and the converted byte array
        Wrapper outWrapper = new Wrapper(tree, converted);
        //write the wrapper object to a file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("output_file.bin"))) {
            oos.writeObject(outWrapper);
            System.out.println("Compressed file made at output_file.bin");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error has occured");
        }
    }

    /**
     * Deserialize the wrapper object file to decompress
     *
     * @param fileName the file name
     * @return the wrapper
     * @throws FileNotFoundException the file not found exception
     */
    public Wrapper deserialize(String fileName) throws FileNotFoundException {
        Wrapper inWrapper = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("C:\\Uni Work\\Data structures and algorithms\\output_file.bin"))) {
            //create an instance of an object
            Object obj = in.readObject();
            //assign object to wrapper object if it's the same object type
            if (obj instanceof Wrapper) {
                inWrapper = (Wrapper) obj;
                in.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return inWrapper;
    }


    /**
     * Decompress the file to create a copy of the original text file
     *
     * @param wrapper the wrapper
     */
    public void decode(Wrapper wrapper) {
        //collect and convert the byte array back into a string in binary
        byte[] converted = wrapper.getBinaryText();
        String binaryText = BitstringJava.GetString(converted);
        //collect the tree from the root node
        Node root = wrapper.getTree().poll();
        Node n = root;
        StringBuilder sb = new StringBuilder();
        //traverse the tree by travelling left or right depending on the bit value and restarting once at a leaf node
        for (int i = 0; i < binaryText.length(); i++) {
            char c = binaryText.charAt(i);
            if (c == "0".charAt(0)) {
                n = n.getLeft();
            }
            else {
                n = n.getRight();
            }
            if (n.checkIsChar()) {
                sb.append(n.getCharacter());
                n = root;
                }
        }
        writeToFile(sb.toString());
        }

    /**
     * Write the string formed from the decompression to a new text file
     *
     * @param string the string
     */
    public void writeToFile(String string) {
            try {
                FileWriter myWriter = new FileWriter("new_file.txt");
                myWriter.write(string);
                myWriter.close();
                System.out.println("New file made at new_file.txt");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

    /**
     * Gets tree from file.
     *
     * @param fileName the file name
     * @return the tree from file
     * @throws FileNotFoundException the file not found exception
     */
    public Node getTreeFromFile(String fileName) throws FileNotFoundException {
        Wrapper wrapper = deserialize(fileName);
        Node tree = wrapper.getTree().poll();
        return tree;
    }

    /**
     * Print codes from the HashMap
     * <p>
     * Used for testing
     *
     * @param codes the codes
     */
    public void printCodes(HashMap<Character, String> codes) {
        for (Character code : codes.keySet()) {
            System.out.println(code + " | " + codes.get(code));
        }
    }

    /**
     * Compress.
     *
     * @param fileName the file name
     * @param filePath the file path
     * @throws FileNotFoundException the file not found exception
     */
    public void compress(String fileName, String filePath) throws FileNotFoundException {
        System.out.println("Compressing " + filePath + fileName);
        Node root = createTree(fileName);
        HashMap<Character, String> codes = getCodes(root, "", new HashMap<Character, String>());
        String binaryText = convertToBinary(filePath + fileName, codes);
        serialize(binaryText);
    }

    /**
     * Decompress.
     *
     * @param wrapperFileName the wrapper file name
     * @throws FileNotFoundException the file not found exception
     */
    public void decompress(String wrapperFileName) throws FileNotFoundException {
        System.out.println("Decompressing " + wrapperFileName);
        Wrapper inWrapper = deserialize(wrapperFileName);
        decode(inWrapper);
    }

    /**
     * Main.
     *
     * @param args the args
     * @throws FileNotFoundException the file not found exception
     */
    public static void main(String args[]) throws FileNotFoundException {
        Huffman h = new Huffman();

        System.out.println("Would you like to compress, decompress or run example?\n" +
                "\n" +
                "1. Compress\n" +
                "2. Decompress\n" +
                "3. Run example\n");

        Scanner sc=new Scanner(System.in);
        String input=sc.next();

        switch (input) {
            case "1" :
                System.out.println("Enter file name - eg bible.txt");
                String fileName = sc.next();
                System.out.println("Enter file path - eg C:\\Uni Work\\Data structures and algorithms\\src\\");
                String filePath = sc.next();
                h.compress(fileName, filePath);
                break;
            case "2" :
                System.out.println("Enter .bin file directory - eg C:\\Uni Work\\Data structures and algorithms\\output_file.bin");
                String wrapperFileName = sc.next();
                break;
            case "3" :
                //enter the name of the file you wish to compress
                String exampleFileName = "bible.txt";
                //enter the filepath for the file
                String exampleFilePath = "C:\\Uni Work\\Data structures and algorithms\\src\\";
                System.out.println("Compressing " + exampleFilePath + exampleFileName);
                Node root = h.createTree(exampleFileName);
                HashMap<Character, String> codes = h.getCodes(root, "", new HashMap<Character, String>());
                String binaryText = h.convertToBinary(exampleFilePath + exampleFileName, codes);
                h.serialize(binaryText);


                String exampleWrapperFileName = "C:\\Uni Work\\Data structures and algorithms\\output_file.bin";
                System.out.println("Decompressing " + exampleWrapperFileName);
                Wrapper inWrapper = h.deserialize(exampleWrapperFileName);
                h.decode(inWrapper);
        }
    }

}
