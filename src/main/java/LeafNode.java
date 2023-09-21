public class LeafNode extends Node {
    private Record[] records= new Record[39];
    private LeafNode nextLeafNode;


    public LeafNode(int[] keys, Record[] records){
        super(keys, null);
        this.records = records;
        this.nextLeafNode = null;

    }
    public LeafNode(int[] keys, Record[] records, Node parent){
        super(keys, null, parent);
        this.records = records;
        this.nextLeafNode = null;
    }

    //getters
    public Record[] getRecords(){
        return records;
    }
    public LeafNode getNextLeafNode(){
        return nextLeafNode;
    }
    @Override public Node[] getChildren(){
        System.out.println("Leaf node, no child nodes.");
        return null;
    }

    //setters
    public void setRecords(Record[] records){
        this.records = records;
    }
    public void setNextLeafNode(LeafNode nexLeafNode){
        this.nextLeafNode = nexLeafNode;
    }
    @Override public void setChildren(Node[] children){
        System.out.println("No children for leaf nodes.");
    }
    @Override public Node getChild(int key){
        System.out.println("No children for leaf nodes.");
        return null;
    }
    
    //useful methods
    public Record getRecord(int key){
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
        return getRecords()[index];
    }
    public void deleteRecord(int key){
        int[] keys = getKeys();
        Record[] records = getRecords();
        int[] updatedKeys = new int[keys.length];
        Record[] updatedRecords = new Record[39];

        int index = -1;
        for(int i = 0; i < keys.length; i++){
            if(keys[i] == key){
                index = i;
                break;
            }else{
                updatedKeys[i] = keys[i];
                updatedRecords[i] = records[i];
            }
        }
        for(int i = index; i < keys.length - 1; i++){
            updatedKeys[i] = keys[i + 1];
            updatedRecords[i] = records[i + 1];

        }
        this.setKeys(updatedKeys);
        setRecords(updatedRecords);
    }
}