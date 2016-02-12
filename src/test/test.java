/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import fads.original.AdultTree;

/**
 *
 * @author 77781225
 */
public class test {
    
    
    public AdultTree at;
    
    public void oo(){
        at=new AdultTree();
        System.out.println(at.MaritalStatus.getOrder("Marital-status"));
        System.out.println(at.MaritalStatus.getData(3));
    }
    public static void main(String[] args){
        test a=new test();
        a.oo();
    }
}
