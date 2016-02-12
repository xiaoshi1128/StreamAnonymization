/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fads.original;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 77781225
 */
public class OFADS {
    
    //Parameters
    int NTuples;// Number of tuples
    int Kanon; // anonymity degree
    int RTuples;
    
    //Time constraints
    int Delta;// allowed time for tuple
    int Omega;// allowed time for k-anonymous cluster
    
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
    // K-anonymous clusters
    Vector<Cluster> KClusters;

    //Output
    Vector<AnonymizationOutput> Output;
    
    
    long startTime;
    /**
     * @param N number of tuple
     * @param K anonymity degree
     * @param D Delta-tuple time
     * @param O Omega-Cluster time
     */
    public OFADS(int N,int K,int D,int O) {
     //main parameters
    this.NTuples=N;    
    this.Kanon=K;
    this.Delta=D;
    this.Omega=O;
        
    //Connect DB
    ConnectDB();
    //Buffer
    Buffer=new Vector<Tuple>();
    
    //K-anonymous clusters
    KClusters=new Vector<Cluster>();
    
    //OUTPUT
    Output= new Vector<AnonymizationOutput>();
    
    //Create Range
    Ranges=new AdultRange();
    
    //Create tree 
    Trees=new AdultTree();
     
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
                RTuples++;
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
                Tuple t=new Tuple(System.currentTimeMillis(),age,fhlweight,education_num,hours_per_week,work_class,education,marital_status,race,gender);
                Buffer.add(t);
                sleep(5);
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
        
        @Override
        public void run() {
            
                try {
                    sleep(Delta-1);
                for(;;){
                
            if(Buffer.size()==0)break;
            t=Buffer.firstElement();
            if(System.currentTimeMillis()-t.getReceivedTime()>=Delta){
                System.out.println("Anonymize: "+t.toString());
                publishTuple();
            }
        }
                } catch (InterruptedException ex) {
                    Logger.getLogger(OFADS.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println("Anonymization finished Anonymization finished Anonymization finished Anonymization finished Anonymization finished Anonymization finished ");
            PublishResult();
        }
    };
   
    }// contructor ends here
    
    
   
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
    
    
public void StartAll(){
    Read.start();
    Anonymize.start();
   // CheckKC.start();
}

public double Distance(Tuple t1,Tuple t2){
    double dis=0.0;
    
    // numeric distance
    dis+= Math.abs(t1.getAge()-t2.getAge())/Ranges.ageRange.getRangeSize();
    dis+= Math.abs(t1.getFhlweight()-t2.getFhlweight())/Ranges.fhlweightRange.getRangeSize();
    dis+= Math.abs(t1.getEducationNum()-t2.getEducationNum())/Ranges.edu_numRange.getRangeSize();
    dis+= Math.abs(t1.getHourPerWeek()-t2.getHourPerWeek())/Ranges.hours_weekRange.getRangeSize();
   
    // Categorical
    dis+=Trees.WorkClass.getDistance(t1.getWorkClass(),t2.getWorkClass());
    dis+=Trees.Education.getDistance(t1.getEducation(),t2.getEducation());
    dis+=Trees.MaritalStatus.getDistance(t1.getMaritalStatus(),t2.getMaritalStatus());
    dis+=Trees.Race.getDistance(t1.getRace(),t2.getRace());
    dis+=Trees.Gender.getDistance(t1.getGender(),t2.getGender());
    
    return dis;
    }

    

//************************ Publishing part **********************************

/**
 * Publish 1st tuple which is expiring
 */
public void publishTuple() {
    
    if(KClusters.size()>0){
        if((System.currentTimeMillis()-KClusters.firstElement().createdTime)>=Omega)KClusters.removeElementAt(0);
    }
    if(Buffer.size()<Kanon){
        for(int i=0;i<Buffer.size();i++){
            outputWithKCorSupress(i);
        }
        Buffer.removeAllElements();
    }
    
    else {
        outputWithKCorNC();
    }
}// publish tuple

public void outputWithKCorSupress(int index){
    Cluster KCluster;
    Tuple T=Buffer.get(index);
    double ILMin=Double.MAX_VALUE;
    int ILMinIndex=-1;
    
    for (int i=1;i<KClusters.size();i++) {
               KCluster=KClusters.get(i);
               if(isClusterCovers(KCluster,T)){
                   double il=InfoLossTupleInCluster(KCluster, T);
                   if(il<ILMin){
                       ILMin=il;
                       ILMinIndex=i;
                    }
           }// min        
       }//find a cluster that covers or supress
    
    if(ILMinIndex==-1){
        
                   Cluster sup=new Cluster(System.currentTimeMillis(), Ranges.ageRange, Ranges.fhlweightRange, 
                                   Ranges.edu_numRange, Ranges.hours_weekRange, 
                                   Trees.WorkClass.getRootName(), Trees.Education.getRootName(), 
                                   Trees.MaritalStatus.getRootName(), Trees.Race.getRootName(),
                                   Trees.Gender.getRootName());
                   
                   AnonymizationOutput Anony=new AnonymizationOutput(T, sup, InfoLossTupleInCluster(sup, T));
                   Output.add(Anony);
    }// if there is no such cluster for T(index)
    else {
           Cluster C=KClusters.get(ILMinIndex);
           AnonymizationOutput Anony=new AnonymizationOutput(T,C, InfoLossTupleInCluster(C, T));
           Output.add(Anony);
           
    }//if cluster found

}


public void outputWithKCorNC() {
    Tuple T=Buffer.firstElement();
    int currentsize=Buffer.size();
    myDistance[] distances=new myDistance[currentsize];
    for(int i=1;i<currentsize;i++){
        distances[i-1]=new myDistance(i,Distance(T, Buffer.get(0)));   
    }
    
    //Distance calculations
    myDistance td;
    for(int i=0;i<currentsize-2;i++){
        for(int j=i+1;j<currentsize-1;j++){
            if(distances[i].infoloss<distances[j].infoloss){
                td=distances[i];
                distances[i]=distances[j];
                distances[j]=td;
            }
        }
    }//selection sort
    
    //1st element in cluster
    long ctime=System.currentTimeMillis();
    Range cage=new Range(T.age,T.age);
    Range cfhlweight=new Range(T.fhlweight,T.fhlweight);
    Range cedunum=new Range(T.education_num,T.education_num);
    Range chours=new Range(T.hour_per_week,T.hour_per_week);
    
    Cluster CT=new Cluster(ctime, cage, cfhlweight, cedunum, chours, 
                 T.work_class, T.education, T.marital_status, T.race, T.gender);
   
    
    //KNN applies here 
    for(int i=0;i<Kanon-1;i++){
            Tuple tt=new Tuple();
            tt=Buffer.get(distances[i].index);
            
        // numeric attributes
        CT.ageRange.enlargeRange(tt.age);
        CT.fhlweightRange.enlargeRange(tt.fhlweight);
        CT.edu_numRange.enlargeRange(tt.education_num);
        CT.hours_weekRange.enlargeRange(tt.hour_per_week);
        
        //categorical attributes
        CT.work_class=Trees.WorkClass.getLCA(CT.work_class,tt.work_class);
        CT.education=Trees.Education.getLCA(CT.education,tt.education);
        CT.marital_status=Trees.MaritalStatus.getLCA(CT.marital_status,tt.marital_status);
        CT.race=Trees.Race.getLCA(CT.race,tt.race);
        CT.gender=Trees.Gender.getLCA(CT.gender,tt.gender);
    } // K-anonymous cluster after KNN approach
    
    
    //find CK anonymized cluster that covers 
    Cluster KCluster;
    double ILMin=Double.MAX_VALUE;
    int ILMinIndex=-1;
    
    for (int i=1;i<KClusters.size();i++) {
               KCluster=KClusters.get(i);
               if(isClusterCovers(KCluster,T)){
                   double il=InfoLossTupleInCluster(KCluster, T);
                   if(il<ILMin){
                       ILMin=il;
                       ILMinIndex=i;
                    }
           }// min        
       }//find a cluster that cove
    
    Tuple[] kt=new Tuple[Kanon-1];
    
    if(ILMinIndex==-1){
        AnonymizationOutput anon=new AnonymizationOutput(T, CT,InfoLossTupleInCluster(CT, T));
        Output.add(anon);
         for(int i=0;i<Kanon-1;i++){
            Tuple tt=new Tuple();
            tt=Buffer.get(distances[i].index);
            kt[i]=tt;
            anon=new AnonymizationOutput(tt, CT, InfoLossTupleInCluster(CT, tt));
            Output.add(anon);
            CT.createdTime=System.currentTimeMillis();
            KClusters.add(CT);
        }//output
        
         Buffer.remove(T);
        for(int i=0;i<Kanon-1;i++){
            Buffer.remove(kt[i]);
        }
    }//if there is no K-anon cluster in KClusterset
    else {
        if(ILMin<InfoLossTupleInCluster(CT, T)){
            Cluster KC=KClusters.get(ILMinIndex);
            AnonymizationOutput anon=new AnonymizationOutput(T, KC,InfoLossTupleInCluster(KC, T));
            Output.add(anon);
            Buffer.remove(T);
        }
        else {
            AnonymizationOutput anon=new AnonymizationOutput(T, CT,InfoLossTupleInCluster(CT, T));
            Output.add(anon);
            for(int i=0;i<Kanon-1;i++){
                Tuple tt=new Tuple();
                tt=Buffer.get(distances[i].index);
                kt[i]=tt;
                anon=new AnonymizationOutput(tt, CT, InfoLossTupleInCluster(CT, tt));
                Output.add(anon);
                CT.createdTime=System.currentTimeMillis();
                KClusters.add(CT);   
            }
                       
            Buffer.remove(T);
            for(int i=0;i<Kanon-1;i++){
                Buffer.remove(kt[i]);
            }
         
    }
}

}

/// ***************** Cluster functions 

public boolean isClusterCovers(Cluster C,Tuple T){
   // numeric
   if(C.ageRange.isBelongs(T.age)==false)return false;
   if(C.fhlweightRange.isBelongs(T.fhlweight)==false)return false;
   if(C.edu_numRange.isBelongs(T.education_num)==false)return false;
   if(C.hours_weekRange.isBelongs(T.hour_per_week)==false)return false;
   
   //categorical
   if(Trees.Education.getLCA(C.education, T.education).equals(C.education)==false)return false;
   if(Trees.MaritalStatus.getLCA(C.marital_status, T.marital_status).equals(C.marital_status)==false)return false;
   if(Trees.WorkClass.getLCA(C.work_class, T.work_class).equals(C.work_class)==false)return false;
   if(Trees.Race.getLCA(C.race, T.race).equals(C.race)==false)return false;
   if(Trees.Gender.getLCA(C.gender, T.gender).equals(C.gender)==false)return false;
   
   return true;
}

public double InfoLossTupleInCluster(Cluster C,Tuple T){

    double IL=0.0;
    
    //numeric
    IL+=C.ageRange.getRangeSize()/Ranges.ageRange.getRangeSize();
    IL+=C.fhlweightRange.getRangeSize()/Ranges.fhlweightRange.getRangeSize();
    IL+=C.edu_numRange.getRangeSize()/Ranges.edu_numRange.getRangeSize();
    IL+=C.hours_weekRange.getRangeSize()/Ranges.hours_weekRange.getRangeSize();
    
    // categoric
    IL+=Trees.Education.getDistance(C.education,T.education);
    IL+=Trees.MaritalStatus.getDistance(C.marital_status,T.marital_status);
    IL+=Trees.WorkClass.getDistance(C.work_class,T.work_class);
    IL+=Trees.Race.getDistance(C.race,T.race);
    IL+=Trees.Gender.getDistance(C.gender,T.gender);
    return IL;
}

public void PublishResult() {
        double il=0.0;
        System.out.println("*************************************************************************************************");
        for(int i=0;i<Output.size();i++){
            System.out.println(Output.get(i).tuple.toString());
            System.out.println(Output.get(i).cluster.toString());
            
            double tt=Output.get(i).InfoLoss;
            il+=tt;
            System.out.println("anony loss:"+tt);
            System.out.println("_______________________________________________________________________________________________________");
        }
        System.out.println("done il="+il);
}

   
        
}

