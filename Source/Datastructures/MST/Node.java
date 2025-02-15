package Source.Datastructures.MST;

public class Node {
    private Node father;
    private Object key;
    int height = 0;
    //_____________________________________________________
    public Node(Node father, Object key){
            if(key == null) throw new Error("Key must be != null");
            this.father = father;
            this.key = key;
    }
    //______________________________________________________

    public void add(Node node){
        if(node != null && node != this) {
            node.setFather(this);
            if(this.height < 1 + node.getHeight()){
                height = 1 + node.getHeight();
            }
        }
    }

    public Node add(Object key){
        if(key != null){
            Node node = new Node(this, key);
            if(height < 1)height = 1;
            return node;
        }
        return null;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public int getHeight() {return height; }
}




