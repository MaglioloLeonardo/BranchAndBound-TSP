package Source.Datastructures.MST;

import Source.Datastructures.Graph.GraphNode;

import java.util.ArrayList;
import java.util.List;
//Implementation via QuickUnion with union-by-size
//Source.Datastructures.MST.UnionFindSet cannot work with primitive types without pointer references
public class UnionFindSet {
    private Node sets_root;

    public UnionFindSet(){
        sets_root = new Node(null, -1);
    }

    //add lists of objects without repetition an returns the list of nodes associated with objects in set
    public List<Node> makeSet(List<GraphNode> objects){
        List<Object> insert = new ArrayList<>();
        List<Node> elements = new ArrayList<>();
        for (Object obj: objects){
            if(!insert.contains(obj)){
                insert.add(obj);
                Node temp = new Node(sets_root, obj);
                elements.add(temp);
            }
        }
        return elements;
    }

    //Returns node's root key value if root is already in sets list
    public Object findPartition(Node node){
        Node temp = findRoot(node);
        if(temp == null)
            return null;
        else return temp.getKey();
    }

    public void union(Node x, Node y){
        Node root_x = findRoot(x) , root_y = findRoot(y);
        assert root_x != null && root_y != null;
        //root_x contains reference to the partition associated with Source.Datastructures.MST.GraphNode 'x'
        //same procedure is done for Source.Datastructures.MST.GraphNode 'y'
        if (root_x != root_y) {
            if (root_x.getHeight() >= root_y.getHeight()) {
                root_x.add(root_y); //tree union
            } else {
                root_y.add(root_x);
            }
        }
    }

    //Returns node's tree if node's root is already the root of some trees in sets list
    private Node findRoot(Node node){
        if(node == null)return null;
        List<Node> compress_list = new ArrayList<>(); //Contains node's pointers used for compression
        while(node.getFather() != null){
            if(node.getFather() == sets_root)break;
            compress_list.add(node);
            node = node.getFather();
        }
        if(node.getFather() == sets_root){
            for (Node to_compress:compress_list) {
                to_compress.setFather(node); //Compression
            }
            return node;
        } else return null;
    }

    public void makeSet(GraphNode graphNode) {  //add a single node to the set
        Node temp = new Node(sets_root, graphNode);
    }
}
