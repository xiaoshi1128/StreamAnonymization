/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fads.original;

/**
 *
 * @author 77781225
 */
public class Cluster {
    
    long createdTime;
    Range ageRange;
    Range fhlweightRange;
    Range edu_numRange;
    Range hours_weekRange;
    
    String work_class;
    String education;
    String marital_status;
    String race;
    String gender;

    Cluster(long createdTime, Range ageRange, 
                   Range fhlweightRange, Range edu_numRange, 
                   Range hours_weekRange, String work_class, 
                   String education, String marital_status, 
                   String race, String gender) {
        
        this.createdTime = createdTime;
        this.ageRange = ageRange;
        this.fhlweightRange = fhlweightRange;
        this.edu_numRange = edu_numRange;
        this.hours_weekRange = hours_weekRange;
        this.work_class = work_class;
        this.education = education;
        this.marital_status = marital_status;
        this.race = race;
        this.gender = gender;
    }

    @Override
    public String toString() {
         return "Tuple("+createdTime +")["+ageRange.toString()+"," +fhlweightRange.toString()+","+ edu_numRange.toString()+ ", "+
                                              hours_weekRange.toString()+", "+ work_class+", "+ education+", "+
                                              marital_status+", "+race+","+ gender+"]";
    }
}
