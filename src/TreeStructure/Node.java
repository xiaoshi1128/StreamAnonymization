/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TreeStructure;

import java.util.ArrayList;

/**
 *
 * @author Ankhbayar */
public class Node{
        
        private String data;
        private ArrayList<String> children;
        
        public Node(String data){
            this.data=data;
            this.children = new ArrayList<String>();
        }
        
        public String getData(){
            return this.data;
        }
        
        public ArrayList<String> getChildren(){
            return this.children;
        }
        
        public void addChild(String data){
            children.add(data);
        }
        
        public boolean isLeaf(){
            return this.children.size()==0;
        }
       
}