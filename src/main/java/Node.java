public class Node {
    private Node parent;
    private int[] keys = new int[39];
    private boolean isLeafNode;
    private Node[] children = new Node[40];

    //initialise
    public Node(int[] keys, Node[] children){
        this.keys = keys;
        this.children = children;
        this.isLeafNode = false;
        this.parent = null;
    }
    public Node(int[] keys, Node[] children, Node parent){
        this.keys = keys;
        this.children = children;
        this.isLeafNode = false;
        this.parent = parent;
    }

    //getters
    public Node getParent(){
        return this.parent;
    }
    public Node[] getChildren(){
        return this.children;
    }
    public boolean getIsLeafNode(){
        return this.isLeafNode;
    }   
    public int[] getKeys(){
        return this.keys;
    }
    
    //setters
    public void setParent(Node parent){
        this.parent = parent;
    }
    public void setChildren(Node[] children){
        this.children = children;
    }
    public void setKeys(int[] keys){
        this.keys = keys;
    }
    //functions to get stuff
    public Node getChild(int key){
        int[] keys = getKeys();
        int index = -1;
        for(int i = 0; i < keys.length; i++){
            if(keys[i] == key){
                index = i;
                break;
            }
        }
        if(index == -1){
            System.out.println("Key does not exist!");
            return null;
        }
        return getChildren()[index];
    }

}