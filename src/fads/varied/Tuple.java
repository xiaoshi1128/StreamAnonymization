/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fads.varied;

/**
 *
 * @author 77781225
 */
public class Tuple {
    long receivedTime;
    int fhlweight;
    int age; 
    int education_num;
    int hour_per_week;
    String work_class;
    String education;
    String marital_status;
    String race;
    String gender;
    
    public Tuple(){
    
    }

    public Tuple(long receivedTime,int age, int fhlweight,  int education_num, int hour_per_week, 
            String work_class, String education, String marital_status, String race, String gender) {
        
        this.receivedTime=receivedTime;
        this.age = age;
        this.fhlweight = fhlweight;
        this.education_num = education_num;
        this.hour_per_week = hour_per_week;
        this.work_class = work_class;
        this.education = education;
        this.marital_status = marital_status;
        this.race = race;
        this.gender = gender; 
   }
    
    public long getReceivedTime(){
        return receivedTime;
    }

   public int getFhlweight() {
        return fhlweight;
    }
    
   public int getAge(){
        return age;
    }
    
   public  int getEducationNum(){
        return education_num;
    }
    
   public  int getHourPerWeek(){
        return hour_per_week;
    }
    
   public String getWorkClass(){
        return work_class;
    }
    
   public String getEducation(){
        return education;
    }
    
   public String getMaritalStatus(){
        return marital_status;
    }
    
   public String getRace(){
        return race;
    }
    
   public String getGender(){
        return gender;
    }
   
   /**
    * 
    * @param T check Tuple values(except number)
    * @return boolean value 
    */
   public boolean isEqual(Tuple T){
       if(age==T.age && education_num == T.education_num &&
          hour_per_week==T.hour_per_week && work_class.equals(T.work_class) && 
          education.equals(T.education) && marital_status.equals(T.marital_status) &&
          race.equals(T.race) && gender.equals(T.gender)) return true;
       else return false;
   }
   
   public String toString(){
       return "Tuple("+receivedTime +")["+age+"," +fhlweight+","+ education_num+ ", "+
                                              hour_per_week+", "+ work_class+", "+ education+", "+
                                              marital_status+", "+race+","+ gender+"]";
   }
}
