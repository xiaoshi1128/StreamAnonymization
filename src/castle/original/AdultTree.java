/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package castle.original;

import TreeStructure.Tree;

/**
 *
 * @author Ankhbayar
 */
public class AdultTree {

    public Tree WorkClass;
    public Tree Education;
    public Tree MaritalStatus;
    public Tree Race;
    public Tree Gender;
    
   public AdultTree(){
       
        //Work-class = Private, Self-emp-not-inc, Self-emp-inc, Federal-gov, Local-gov, State-gov, Without-pay, Never-worked
        this.WorkClass= new Tree();
        this.WorkClass.addNode("Work-class");
        this.WorkClass.addNode("Private","Work-class");
        this.WorkClass.addNode("Self-emp-not-inc","Work-class");
        this.WorkClass.addNode("Self-emp-inc","Work-class");
        this.WorkClass.addNode("Federal-gov","Work-class");
        this.WorkClass.addNode("Local-gov","Work-class");
        this.WorkClass.addNode("State-gov","Work-class");
        this.WorkClass.addNode("Without-pay","Work-class");
        this.WorkClass.addNode("Never-worked","Work-class");
        

        /**
         * 
        Compulsory-Education=1st-4th,Middle-School,High-School
                                    Middle-School = 5th-6th , 7th-8th
                                    High-School = 9th, 10th, 11th, 12th

        Higher-Education = Pre-University, College, University
                            Pre-University = HS-grad, Prof-school, 
                            College = Some-college ,Assoc-acdm, Assoc-voc
                            University = Bachelors , GraduateSchool
                                                    GraduateSchool = Masters,Doctorate
         */
        this.Education = new Tree();
        //Education = Preschool,Compulsory-Education,Higher-Education
        this.Education.addNode("Education");
        this.Education.addNode("Preschool","Education");
        this.Education.addNode("Compulsory-Education","Education");
        this.Education.addNode("Higher-Education","Education");
        
        /**
         *  Compulsory-Education=1st-4th,Middle-School,High-School
                                    Middle-School = 5th-6th , 7th-8th
                                    High-School = 9th, 10th, 11th, 12th
         */
        
        this.Education.addNode("1st-4th","Compulsory-Education");
        this.Education.addNode("Middle-School","Compulsory-Education");
        this.Education.addNode("High-School","Compulsory-Education");

        this.Education.addNode("5th-6th","Middle-School");
        this.Education.addNode("7th-8th","Middle-School");
        
        this.Education.addNode("9th","High-School");
        this.Education.addNode("10th","High-School");
        this.Education.addNode("11th","High-School");
        this.Education.addNode("12th","High-School");
        
        
        /**
         * Higher-Education = Pre-University, College, University
                            Pre-University = HS-grad, Prof-school, 
                            College = Some-college ,Assoc-acdm, Assoc-voc
                            University = Bachelors , GraduateSchool
                            GraduateSchool = Masters,Doctorate
         */
        
        this.Education.addNode("Pre-University","Higher-Education");
        this.Education.addNode("College","Higher-Education");
        this.Education.addNode("University","Higher-Education");
        
        this.Education.addNode("HS-grad","Pre-University");
        this.Education.addNode("Prof-school","Pre-University");
        
        this.Education.addNode("Some-college","College");
        this.Education.addNode("Assoc-acdm","College");
        this.Education.addNode("Assoc-voc","College");
        
        this.Education.addNode("Bachelors","University");
        this.Education.addNode("GraduateSchool","University");
        this.Education.addNode("Masters","GraduateSchool");
        this.Education.addNode("Doctorate","GraduateSchool");
        /**
         * Marital-status = Married, Not-married
         * Married = Married-civ-spouse, Married-spouse-absent, Married-AF-spouse
         * Not-married = Divorced, Never-married, Separated, Widowed
         * 
         */     
        this.MaritalStatus = new Tree();
        this.MaritalStatus.addNode("Marital-status");
        this.MaritalStatus.addNode("Married","Marital-status");
        this.MaritalStatus.addNode("Not-married","Marital-status");
        
        this.MaritalStatus.addNode("Married-civ-spouse","Married");
        this.MaritalStatus.addNode("Married-spouse-absent","Married");
        this.MaritalStatus.addNode("Married-AF-spouse","Married");

        this.MaritalStatus.addNode("Divorced","Not-married");
        this.MaritalStatus.addNode("Never-married","Not-married");
        this.MaritalStatus.addNode("Separated","Not-married");
        this.MaritalStatus.addNode("Widowed","Not-married");
        
        // Race = White, Asian-Pac-Islander, Amer-Indian-Eskimo, Black, Other
        this.Race = new Tree();
        this.Race.addNode("Race");
        
        this.Race.addNode("White","Race");
        this.Race.addNode("Asian-Pac-Islander","Race");
        this.Race.addNode("Amer-Indian-Eskimo","Race");
        this.Race.addNode("Black","Race");
        this.Race.addNode("Other","Race");
         
        //Gender = Male Female
        this.Gender = new Tree();
        this.Gender.addNode("Gender");
        this.Gender.addNode("Male","Gender");
        this.Gender.addNode("Female","Gender");
    } 
    
}
