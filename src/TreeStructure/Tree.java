/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TreeStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Ankhbayar
 */
public class Tree {
    
   private final static int ROOT=0; 
   private String rootname=null;
   private HashMap<String, Node> nodes;
   private HashMap<String, String> parents;
   
   private TraversalStrategy traversalStrategy;
   
   //Constructors
   public Tree() {
        this(TraversalStrategy.BREADTH_FIRST);
    }

    public Tree(TraversalStrategy traversalStrategy) {
        this.nodes = new HashMap<String, Node>();
        this.parents = new HashMap<String, String>();
        this.traversalStrategy = traversalStrategy;
        
    }
   
    // properties functions
   public HashMap<String, Node> getNodes(){
       return nodes;
   }
   
   public TraversalStrategy getTravelsalStrategy(){
       return this.traversalStrategy;
   }
   
   public void setTraversalStrategy(TraversalStrategy strategy){
       this.traversalStrategy=strategy;
   }
   
   // addNode
   public Node addNode(String data){
        rootname=data;
        return this.addNode(data,null);
   }
   
   // addNode as a child on particular node
   public Node addNode(String data, String parent){
       
       Node node= new Node(data);
       nodes.put(data, node);
       
       if(parent==null){
           parents.put(data,null);
       }
       
       if(parent!=null){
            nodes.get(parent).addChild(data);
            parents.put(data, parent);
       }
       
       return node;
   }
   
   public void printNodes(String data){
        this.printNodes(data,ROOT);
   }
   
   public void printNodes(String data,int depth){
       
       ArrayList<String> children = nodes.get(data).getChildren();
       
       if(depth==ROOT){
           System.out.println(nodes.get(data).getData());
       } 
       else {
           String tabs = String.format("%0" + depth + "d", 0).replace("0", "    "); // 4 spaces
           System.out.println(tabs + nodes.get(data).getData());
       }
       
       depth++;
       
       for(String child: children){
           this.printNodes(child,depth);
       }
   }
   
   
   // get Height
   public int getHeight(String data){
       Node n=nodes.get(data);
       if(n.isLeaf()==true)return 1;
       else {
           int height=0;
           for(String child: n.getChildren()){
               height =Math.max(height, getHeight(child));
           }
      return height+1;
       }
   }
   
   // get number of leafs  
   public double getLeafNumber(String data){
       Node n=nodes.get(data);
       if(n.isLeaf())return 1;
       else {
           int leafnumber=0;
           for(String child: n.getChildren()){
           leafnumber+= getLeafNumber(child);
           }
        return leafnumber+0.0;
       }
   }
   
      public double getLeafNumber(){
          return getLeafNumber(rootname);
       }
   
    public String getParent(String data){
        return parents.get(data);
    }
   
    public String getLCA(String node1,String node2){
        String LCA=null;
        if(node1.equals(node2))LCA=node1;
        else {
                String[] parent1 = new String[10];
                String[] parent2 = new String[10];
                
                String child1 =node1;
                int i=0;   
                while(true){
                    parent1[i]=child1;
                    child1=getParent(child1);
                    if(child1==null)break;
                        i++;
                }
                
                String child2 =node2;
                int j=0; 
                while(true){
                    parent2[j]=child2;
                    child2=getParent(child2);
                    if(child2== null)break;
                        j++;
                }
            ////
             
                for(i=0;parent1[i]!=null;i++){
                        for(j=0;parent2[j]!=null;j++){
                            if(parent1[i].equals(parent2[j]))return parent1[i];
                        }
                }
            }
        
    return LCA;
    }
    
    
   // Last edited
    public double getDistance(String node1, String node2){
        if(node1.equals(node2))return 0.0;
        if(node1=="-1" || node2=="-1")return 1.0;
        String LCA=this.getLCA(node1, node2);
        return this.getLeafNumber(LCA)/this.getLeafNumber(rootname)*1.0;
    }
    
   // iterators
    public Iterator<Node> iterator(String data) {
        return this.iterator(data, traversalStrategy);
    }

    public Iterator<Node> iterator(String data, TraversalStrategy traversalStrategy) {
        return traversalStrategy == TraversalStrategy.BREADTH_FIRST ?
                new BreadthFirstTreeIterator(nodes, data) :
                new DepthFirstTreeIterator(nodes, data);
    }
    
 public String getRootName(){
     return rootname;
 }
 
 public int getOrder(String data){
     int order=0;
     Iterator<Node> iterator = this.iterator(this.rootname);
     Node nd;
     while(iterator.hasNext()){
         nd=iterator.next();
         if(nd.getData().equals(data))return order+1;
         order++;
     } 
       return -1;
 }
 
 public String getData(int order){
     Iterator<Node> iterator = this.iterator(this.rootname);
     Node nd = null;
     for(int i=0;i<order;i++)nd=iterator.next();
     if(nd == null)return "-1";
     else return nd.getData();
 }
}
  