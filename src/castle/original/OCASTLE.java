/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package castle.original;

import fads.original.OFADS;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 77781225
 */
public class OCASTLE {
    
     //Parameters
    int NTuples;// Number of tuples
    int Kanon; // anonymity degree
    int RTuples;// Total red tuples
    
    //Time constraints
    int Delta;// allowed time for tuple
// allowed time for tuple
    int Beta;// allowed time for k-anonymous cluster
    double Tau;
    
    
    // Threads for the stream
    Thread Read;
    Thread Anonymize;
    Thread CheckKC;
    
    //Connection 
    Connection con;
    // trees and ranges
    AdultRange Ranges;
    AdultTree Trees;
    
    //Buffer
    Vector<Tuple> Buffer;
    //Non-K anon clusters
    Vector<Cluster> Clusters;
    // K-anonymous clusters
    Vector<Cluster> KClusters;
    //Output
    Vector<AnonymizationOutput> Output;
    
    long startTime;
 

    
    /**
     * @param N number of tuple
     * @param K anonymity degree
     * @param D Delta-windows size
     * @param B Beta- KCluster number
     */
    public OCASTLE(int N, int K, int D, int B) {
       
    
    //main parameters
    this.NTuples=N;    
    this.Kanon=K;
    this.Delta=D;
    this.Beta=B;
    // TAU
    this.Tau = 0.0;
    
    //Connect DB
    ConnectDB();
    //Buffer
    Buffer=new Vector<Tuple>();
    
    //Non-K anon clusters 
    Clusters=new Vector<Cluster>();
    
    //K-anonymous clusters
    KClusters=new Vector<Cluster>();
    
    //Create Range
    Ranges=new AdultRange();
    
    //Create tree 
    Trees=new AdultTree();
    
    //Output
    Output=new Vector<AnonymizationOutput>();
   // Reading phase
    Read= new Thread(){
        @Override
        public void run() {
            String query= "select * from dataset limit "+NTuples+";";
        try {
            RTuples=0;
            Statement statement= con.createStatement();
            ResultSet rs= statement.executeQuery(query);
            System.out.println(NTuples+" data loaded from database");
            while(rs.next()){
                
                int age=rs.getInt(2);
                int fhlweight=rs.getInt(3);
                int education_num=rs.getInt(4);
                int hours_per_week=rs.getInt(5);
                // englarge Ranges;
                Ranges.ageRange.enlargeRange(age);
                Ranges.fhlweightRange.enlargeRange(fhlweight);
                Ranges.edu_numRange.enlargeRange(education_num);
                Ranges.hours_weekRange.enlargeRange(hours_per_week);
                
                String work_class= rs.getString(6);
                String education= rs.getString(7);
                String marital_status= rs.getString(8);
                String race= rs.getString(9);
                String gender= rs.getString(10);
                Tuple t=new Tuple(RTuples,RTuples,age,fhlweight,education_num,hours_per_week,work_class,education,marital_status,race,gender);
                Buffer.add(t);
                sleep(5);
                RTuples++;
                System.out.println(t.toString());
            }
          
        } catch (Exception ex) {
            Logger.getLogger(OFADS.class.getName()).log(Level.SEVERE, null, ex);
        }  
            System.out.println("reading fnished reading fnished reading fnished reading fnishedreading fnishedreading fnishedreading fnishedreading fnished");
        }       
    };// read phase
    
    //Anonymizing phase
    Anonymize= new Thread(){
        
        Tuple t=new Tuple();
        Cluster C;
        @Override
        public void run() {
            
            while(Buffer.size()!=0){
                t=Buffer.firstElement();
                C=BestSelection(t);
                if(C==null){
                    Cluster Cl=new Cluster(Trees, Ranges, t);
                }
                else {
                    C.addTuple(t);
                }     
            }
            
            Tuple t=Buffer.get(0);
            
            if(RTuples-t.receivedOrder>=Delta){
                Buffer.remove(0);
                DelayConstraint(t);
            }
            
            
        
        }    
    };
    }//Constructor ends here...
    
    
    
      /**
     * Connect DB with XAMPP server Configuration
     */
    private void ConnectDB(){
        try
            {
            Class.forName("com.mysql.jdbc.Driver");
            con = null;
            con = DriverManager.getConnection("jdbc:mysql://localhost/Adult","root", "");
            System.out.print("Database is connected !");
            }
            catch(ClassNotFoundException | SQLException e)
            {
            System.out.print("Do not connect to DB - Error:"+e);
            }
    } 
     
   
    /**
     * Find best Cluster for tuple T which has less Information loss that Tau
     * @param T Tuple
     * @return Best cluster for Tuple or NULL
     */
    Cluster BestSelection(Tuple T){
        int currentsize=Clusters.size();
        enlargeCost[] E=new enlargeCost[currentsize];
        double e;
        
        double mine=Double.MAX_VALUE;
        for(int i=0;i<currentsize;i++){
            e=Clusters.get(i).Enlargement(T);
            E[i]=new enlargeCost(i,e);
            if(e<mine)mine=e;
        }
        
        ArrayList<Integer> SetCmin=new ArrayList<Integer>();
        for(int i=0;i<currentsize;i++){
            if(E[i].cost==mine)SetCmin.add(E[i].index);            
        }
        
        ArrayList<Integer> SetOk=new ArrayList<Integer>();
        for(int i=0;i<SetCmin.size();i++){
            double IL_Cj=Clusters.get(SetCmin.get(i)).ILAfterAddTuple(T);
            if(IL_Cj<=Tau){
                SetOk.add(SetCmin.get(i));
            }
        }
        
        
        Random rd=new Random();
        Cluster cl=null;
        if(SetOk.isEmpty()){
            if(KClusters.size()>=Beta){

                int index=rd.nextInt(SetCmin.size());
                cl=Clusters.get(index);
            }
        }
        else {
            int index=rd.nextInt(SetOk.size());
            cl=Clusters.get(index);
        } 
        return cl;
    }//Best selection
   
    
    /**
     * Output expiring tuple 
     * @param T Tuple
     */
    public void DelayConstraint(Tuple T){
        Cluster C=FindClusterOfTuple(T);
        int size=C.getSize();
        
        if(size>=Kanon){
            OutputCluster(C);
        }
        else {
            ArrayList<Cluster> KC_set=new ArrayList<Cluster>();
            for(Cluster Cl:KClusters){
                if(Cl.isCovers(T))KC_set.add(Cl);
            }
            
            if(!KC_set.isEmpty()){
                 Random rd=new Random();
                 int index=rd.nextInt(KC_set.size());
                 Cluster Cl=KC_set.get(index);
                 AnonymizationOutput Anony=new AnonymizationOutput(T, Cl);
                 Output.add(Anony);
                 C.tuples.remove(T);// After anonymizing should remove tuple from cluster
                 return;
            }//Anonymize with existing Ks cluster
            else {
                int m=0;
                int totalsize=0;// size of the buffer (Summa(nonKS.size))
                for(Cluster Cl: Clusters){
                    if(C.getSize()<Cl.getSize())m++;
                    size+=Cl.getSize();
                }
         
                if(m>Clusters.size()/2 || totalsize<Kanon){
                    Cluster sup=getSuppressCluster();
                    AnonymizationOutput Anony=new AnonymizationOutput(T, sup);
                    C.tuples.remove(T);// After anonymizing should remove tuple from cluster
                }//supress and anonymize;
                else {
                    MergeClusters();                    
                    OutputCluster(Clusters.firstElement());
                }
            }
            
        }
    }
    
    /**
     * Find cluster of tuple T 
     * @param T Tuple
     * @return Cluster which contains T;
     */
    public Cluster FindClusterOfTuple(Tuple T){
        Cluster cl=null;
        int order=T.receivedOrder;
        for (Cluster Cluster : Clusters) {
             if(Cluster.isContains(order))return Cluster;
        }
        return cl;
    }

    /**
     * Output Cluster C
     * @param C Cluster 
     */
    public void OutputCluster(Cluster C){
        ArrayList<Cluster> SC=new ArrayList<Cluster>();
        if(C.getSize()>=2*Kanon){
            SC=Split(C);
        }
        else SC.add(C);        
        
        AnonymizationOutput anony=null;
        for(Cluster Cj: SC){
            for(Tuple T: Cj.tuples){
                anony=new AnonymizationOutput(T, Cj);
                Output.add(anony);
            }
            double IL=Cj.InfoLoss();
            if(IL>Tau)Tau=IL;
            if(Tau>IL){
                KClusters.add(Cj);
            }
        }
        Clusters.remove(C);//Remove anonymized cluster
    }
    
    /**
     * Split big clusters into several clusters 
     * @param C Cluster
     * @return cluster set
     */
    private ArrayList<Cluster> Split(Cluster C) {
        ArrayList<Cluster> AC=new ArrayList<Cluster>();
        ArrayList<Bucket> BS=new ArrayList<Bucket>();
        ArrayList<Tuple> TS=C.tuples;//tuples of C
        
        Tuple t=TS.get(0);
        Bucket b=new Bucket(t);
        BS.add(b);
        
        // assign buckets 
        for(int i=1;i<TS.size();i++){
            int assigned=0;
            Tuple td=TS.get(i);
            for(Bucket B: BS){
                if(B.canInclude(td)){
                    B.addTuple(td);
                    assigned=1;
                }
            }
            if(assigned==0){
                 b=new Bucket(td);
                 BS.add(b);
            }
        }// finished grouping tuples by pid into Buckets BS 
        Random rd=new Random();
        int index;
        while(BS.size()>=Kanon){
            index=rd.nextInt(BS.size());
            Bucket B=BS.get(index);// select random bucket
            Tuple Tk;//Tk- find KNN of Tk
            if(B.getSize()==1){
                Tk=B.tuples.get(0);
                BS.remove(B);
            }
            else {
                Tk=B.tuples.get(rd.nextInt(B.getSize()));
                B.removeTuple(Tk);
            }
            
            
            
            
        }
        
        
        return AC;
    }
    
    
    /**
     * @return return suppressed cluster 
     */
    public Cluster getSuppressCluster(){
            Cluster SupCl=new Cluster(Trees,Ranges);
            SupCl.SuppressCluster();
            return SupCl;
    }

    /**
     * Merge remaining cluster into single cluster
     */
    private void MergeClusters() {
            Cluster MC=Clusters.get(0);
            Cluster TC=null;
            Clusters.remove(0);
            while(!Clusters.isEmpty()){
                TC=Clusters.firstElement();
                for(Tuple t:TC.tuples){
                    MC.addTuple(t); 
                }
                Clusters.remove(0);
            }
            Clusters.add(MC);
    }

    
}