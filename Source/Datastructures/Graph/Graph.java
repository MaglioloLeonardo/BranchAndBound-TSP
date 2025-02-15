package Source.Datastructures.Graph;

import Source.Datastructures.Graph.Exceptions.NodeNotFoundExceptionGraph;

import java.util.*;

public class Graph { //Source.Datastructures.Graph G
    private boolean is_direct;
    HashMap<GraphNode, List<Source.Datastructures.Graph.Adjacency>> hashMap = new HashMap<>();

    public Graph(Boolean is_direct){
        this.is_direct = is_direct;
    }

    public void addNode(GraphNode graphNode){
        if(!hashMap.containsKey(graphNode)){
            hashMap.put(graphNode, new ArrayList<Source.Datastructures.Graph.Adjacency>());
        }
    }
/*
    public void addEdge(GraphNode graphNode_u, GraphNode graphNode_v, float weight){
        List<Source.Datastructures.Graph.Adjacency> list_u = hashMap.get(graphNode_u);
        if(list_u == null){
            addNode(graphNode_u); //if u doesn't exist add it on the hashmap
            list_u = hashMap.get(graphNode_u);
        }

        List<Source.Datastructures.Graph.Adjacency> list_v = hashMap.get(graphNode_v);
        if(list_v == null){
            addNode(graphNode_v); //if v doesn't exist add it on the hashmap
            list_v = hashMap.get(graphNode_v);
        }

        if(!List_constains(list_u, graphNode_v)){
            list_u.add(new Source.Datastructures.Graph.Adjacency(graphNode_v, weight));//add (u,v)
        }

        if(!is_direct){ //if G is not direct then add also (v,u)
            if(!List_constains(list_v, graphNode_u)){
                list_v.add(new Source.Datastructures.Graph.Adjacency(graphNode_u, weight));
            }
        }
    }*/

    public void addEdge(GraphNode graphNode_u, GraphNode graphNode_v, float weight) throws IllegalArgumentException {
        List<Adjacency> list_u = hashMap.get(graphNode_u);
        if (list_u == null) {
            addNode(graphNode_u); // if u doesn't exist add it to the hashmap
            list_u = hashMap.get(graphNode_u);
        }

        List<Adjacency> list_v = hashMap.get(graphNode_v);
        if (list_v == null) {
            addNode(graphNode_v); // if v doesn't exist add it to the hashmap
            list_v = hashMap.get(graphNode_v);
        }

        // Check if the edge (u, v) already exists
        if (listContains(list_u, graphNode_v)) {
            throw new IllegalArgumentException("Edge from " + graphNode_u.getID() + " to " + graphNode_v.getID() + " already exists");
        }

        // Check if the edge (v, u) already exists in case of an undirected graph
        if (!is_direct && listContains(list_v, graphNode_u)) {
            throw new IllegalArgumentException("Edge from " + graphNode_v.getID() + " to " + graphNode_u.getID() + " already exists");
        }

        list_u.add(new Adjacency(graphNode_v, weight)); // add (u, v)

        if (!is_direct) { // if G is not directed then add also (v, u)
            list_v.add(new Adjacency(graphNode_u, weight));
        }
    }

    private boolean listContains(List<Adjacency> list, GraphNode node) {
        for (Adjacency adj : list) {
            if (adj.getNode().equals(node)) {
                return true;
            }
        }
        return false;
    }

    //add edge method that takes in input a edge object throw NodeMissingExceptionGraph if the node is not in the graph
    public void addEdge(Source.Datastructures.Graph.Edge edge) throws NodeNotFoundExceptionGraph {
        if (!Contains_node(edge.getU()) || !Contains_node(edge.getV())) {
            throw new NodeNotFoundExceptionGraph("GraphNode not found in the graph");
        }

        addEdge(edge.getU(), edge.getV(), edge.getWeight());
    }

    public boolean Is_direct() {
        return is_direct;
    }

    public boolean Contains_node(GraphNode v){
        return hashMap.containsKey(v);
    }

    public boolean Contains_edge(Object node_u, Object node_v){
        List<Source.Datastructures.Graph.Adjacency> list_u = hashMap.get(node_u);
        if(list_u == null || !List_constains(list_u, node_v)){
            return  false;
        }else{
            return true;
        }
    }

    public void deleteNode(GraphNode graphNode){
        hashMap.remove(graphNode);
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;
        //for each vertex u on G
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            List<Source.Datastructures.Graph.Adjacency> list_vertex = entry.getValue();//list adj[u]
            Source.Datastructures.Graph.Adjacency toRemove = List_find(list_vertex, graphNode);
            //if(u, graphNode) exists then it delete it
            if(toRemove != null)list_vertex.remove(toRemove);
        }
    }

    //get list of edges from a given graphNode using getEdges() implemntation
    public List<Source.Datastructures.Graph.Edge> getEdgesFromNode(GraphNode graphNode){
        List<Source.Datastructures.Graph.Edge> toReturn = new ArrayList<>();
        List<Source.Datastructures.Graph.Adjacency> temp = hashMap.get(graphNode);
        if(temp != null){
            for (Source.Datastructures.Graph.Adjacency adj: temp) {
                toReturn.add(new Source.Datastructures.Graph.Edge(graphNode, adj.getNode(), adj.getWeight()));
            }
        }
        return toReturn;
    }


    public void Delete_node_from_int(int key){
        GraphNode graphNode = getNodeById(key);
        if(graphNode != null) deleteNode(graphNode);
    }


    //get degree of a graphNode in the graph throw NodeMissingExceptionGraph if the graphNode is not in the graph
    public int getDegree(GraphNode graphNode) throws NodeNotFoundExceptionGraph {
        if(!Contains_node(graphNode)){
            throw new NodeNotFoundExceptionGraph("GraphNode not found in the graph");
        }
        return hashMap.get(graphNode).size();
    }

    public void Delete_edge(Object node_u, Object node_v){
        List<Source.Datastructures.Graph.Adjacency> list_u = hashMap.get(node_u);
        List<Source.Datastructures.Graph.Adjacency> list_v = hashMap.get(node_v);
        if(list_u != null && list_v != null){
            Source.Datastructures.Graph.Adjacency toRemove = List_find(list_u, node_v);
            if(toRemove != null)list_u.remove(toRemove); //remove (u,v) if exists
            if(!is_direct){
                toRemove = List_find(list_v, node_u);
                //remove (v,u) if exists and G is not direct
                if(toRemove != null)list_v.remove(toRemove); 
            }
        }
    }

    public int Nodes_number(){
        return hashMap.size();
    }

    public int Edge_number(){
        int edges = 0;
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;
        //for each vertex u on G
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            List<Source.Datastructures.Graph.Adjacency> list_vertex = entry.getValue();//adj v list of u
            edges += list_vertex.size();
        }
        return edges;
    }

    //returns the list of nodes contained in Hashmap 'keys' 
    public List<GraphNode> getNodes(){
        List<GraphNode> graphNodes = new ArrayList<>();
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;
        //for each vertex u on G
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            graphNodes.add(entry.getKey());
        }
        return graphNodes;
    }

    //implement GetNode method that take in input a key value as int and search a node in the graph with that value for all the Integer in graph
    public GraphNode getNodeById(int key){
        for (GraphNode graphNode : getNodes()) {
            //before cast to int check if the object is an Integer
            if(graphNode.getID() == key)return graphNode;
        }
        return null;
    }


    //returns the list of edges cointained on hashMap
    public List<Source.Datastructures.Graph.Edge> getEdges(){
        List<Source.Datastructures.Graph.Edge> toReturn = new ArrayList<>();
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;
        //foreach vertex u on G
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            List<Source.Datastructures.Graph.Adjacency> temp = entry.getValue();
            //foreach adj v on vertex u
            for (Source.Datastructures.Graph.Adjacency adj: temp) {
                //add to the list (u,v)
                toReturn.add(new Source.Datastructures.Graph.Edge(entry.getKey(), adj.getNode(), adj.getWeight()));
            }
        }
        return toReturn;
    }

    //given vertex returns the list of adj nodes
    public List<GraphNode> Adj_nodes(GraphNode vertex){
        List<GraphNode> result = new ArrayList<>();
        List<Source.Datastructures.Graph.Adjacency> temp = hashMap.get(vertex); //list of Source.Datastructures.Adjacency to vertex u
        if(temp != null) {
            for (Source.Datastructures.Graph.Adjacency adj : temp) {
                result.add(adj.getNode()); //add (u,v)
            }
        }
        return result;
    }

    //get list of edges from a GraphNode in graph
    public List<Source.Datastructures.Graph.Edge> getEdges(GraphNode graphNode){
        List<Source.Datastructures.Graph.Edge> toReturn = new ArrayList<>();
        List<Source.Datastructures.Graph.Adjacency> temp = hashMap.get(graphNode);
        if(temp != null){
            for (Source.Datastructures.Graph.Adjacency adj: temp) {
                toReturn.add(new Source.Datastructures.Graph.Edge(graphNode, adj.getNode(), adj.getWeight()));
            }
        }
        return toReturn;
    }

    @Override
    public Graph clone() {
        Graph clone = new Graph(is_direct);
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;

        // for each vertex u in the original graph
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            clone.addNode(entry.getKey());
            List<Source.Datastructures.Graph.Adjacency> temp = entry.getValue();

            // for each adjacency in the vertex u's adjacency list
            for (Source.Datastructures.Graph.Adjacency adj : temp) {
                GraphNode u = entry.getKey();
                GraphNode v = adj.getNode();

                // Add edge only if the graph is directed or if the edge (v, u) does not exist
                if (is_direct || !clone.Contains_edge(v, u)) {
                    clone.addEdge(u, v, adj.getWeight());
                }
            }
        }
        return clone;
    }


    //get the edge from a couple of nodes in the graph
    public Source.Datastructures.Graph.Edge getEdge(GraphNode graphNode_u, GraphNode graphNode_v){
        List<Source.Datastructures.Graph.Adjacency> list_u = hashMap.get(graphNode_u);
        Source.Datastructures.Graph.Adjacency result = List_find(list_u, graphNode_v);
        if(result == null){
            return  null;
        }else return new Source.Datastructures.Graph.Edge(graphNode_u, graphNode_v, result.getWeight());
    }


    public List<Edge> findDuplicatedEdges() {
        List<Edge> duplicatedEdges = new ArrayList<>();
        Set<Edge> uniqueEdges = new HashSet<>();

        for (Edge edge : getEdges()) {
            Edge reverseEdge = new Edge(edge.getV(), edge.getU(), edge.getWeight());
            if (uniqueEdges.contains(edge) || uniqueEdges.contains(reverseEdge)) {
                duplicatedEdges.add(edge);
            } else {
                uniqueEdges.add(edge);
            }
        }

        return duplicatedEdges;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int totalWeight = 0;

        for (Edge edge : getEdges()) {
            result.append("(")
                    .append(edge.getU().getID())
                    .append(", ")
                    .append(edge.getV().getID())
                    .append(", ")
                    .append(edge.getWeight())
                    .append(") ");
            totalWeight += edge.getWeight();
        }

        result.append("\nTotal weight: ").append(totalWeight);
        return result.toString();
    }

    /*
    //define a function toString that take in consideration is directed or not
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<GraphNode, List<Source.Datastructures.Graph.Adjacency>> map = hashMap;
        //for each vertex u on G
        for (Map.Entry<GraphNode, List<Source.Datastructures.Graph.Adjacency>> entry : map.entrySet()) {
            List<Source.Datastructures.Graph.Adjacency> list_vertex = entry.getValue();
            //for each adj v on vertex u
            for (Source.Datastructures.Graph.Adjacency adj: list_vertex) {
                //if G is direct print (u,v)
                if(is_direct){
                    sb.append(entry.getKey().getID()).append(" -> ").append(adj.getNode().getID()).append(" [label=\"").append(adj.getWeight()).append("\"]\n");
                }else{
                    //if G is not direct print (u,v) and (v,u)
                    sb.append(entry.getKey().getID()).append(" -- ").append(adj.getNode().getID()).append(" [label=\"").append(adj.getWeight()).append("\"]\n");
                }
            }
        }
        return sb.toString();
    }*/

    private boolean List_constains(List<Source.Datastructures.Graph.Adjacency> list, Object vertex){
        for (Source.Datastructures.Graph.Adjacency adj: list) {
            if(adj.getNode() == vertex)return true;
        }
        return false;
    }

    private Source.Datastructures.Graph.Adjacency List_find(List<Source.Datastructures.Graph.Adjacency> list, Object vertex){
        for (Source.Datastructures.Graph.Adjacency adj: list) {
            if(adj.getNode() == vertex)return adj;
        }
        return null;
    }



}
