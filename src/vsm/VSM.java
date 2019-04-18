/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsm;

import java.io.*;
import java.util.*;
import java.math.*;
/**
 *
 * @author Naqib
 */
public class VSM {

    /**
     * @param args the command line arguments
     */
    
    public static Map<String, positionalTerm> pDictionary= new HashMap<String, positionalTerm>();
    
    //Cleaning up the string
    public static String clean(String str){         
        str= str.toLowerCase();
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }
    
    //Checking for stop word
    public static boolean isStopword(String str){
        boolean check=false;
        String[] stopwords={"a","is","the","of","all","and","to","can","be","as","once","for","at","am","are","has",
                           "have","had","up","his","her","in","on","no","we","do"}; 
        for(int i=0; i<stopwords.length; i++){
            if(str.compareTo(stopwords[i])==0){
                return true;
            }
        }
        return check;
    }
    
    //Query Evaluation
    public static void queryEvaluate(String query){
       String[] arr= query.split(" ");
       String temp="";
       //removing Stop words
       for(int i=0; i< arr.length; i++){
           if(isStopword(arr[i])== false){
               temp= temp + arr[i] + " ";
           }
       }
       
       arr= temp.split("");
    }
    
    //Printing Index
    public static void positionalIndex( ) {
        for ( Map.Entry<String,  positionalTerm> entry : pDictionary.entrySet() ) {
            System.out.println("<" + entry.getKey() + ", " + entry.getValue().termDocFrequencey);
            
            for ( int i=0; i<entry.getValue().postings.size(); ++i ) {
                System.out.print("D" + entry.getValue().postings.get(i).get(0) + ": ");
                    System.out.print(entry.getValue().postings.get(i).get(1) + " ");
                System.out.println();
            }
            System.out.println(">");
        }
    }
    //Finding cosine similarity
    public static int cosineSimilarity(){
        int simScore=0;
        return simScore;
    }
    public static void main(String[] args) {
        // TODO code application logic here
        //Parsing Document's
        for(int i=1; i<51; i++){
            File x= new File("./"+i+".txt");
            try{
                 int docId=i;
                 Scanner sc= new Scanner(x);
                 sc.nextLine();                  //Escaping title
                 sc.nextLine();                  //Escaping author
                 String str="";
                 while(sc.hasNext()){
                     str= sc.next();
                     str= clean(str);
                     if((isStopword(str)==false) && (str.length()>0)){
                         //System.out.println(str);
                         //For Positional Index
                         if(!(pDictionary.containsKey(str))){
                             //Adding New value in Positional index
                             positionalTerm term= new positionalTerm();
                             term.termDocFrequencey++;  //Adding frequency
                             term.postings.add(new ArrayList<Integer>());
                             term.postings.get(0).add(docId);
                             term.postings.get(0).add(1); //Adding term frequency
                             pDictionary.put(str, term);
                         }
                         else{
                             //Updating old value in pDictionary
                             positionalTerm term= pDictionary.get(str);
                             //For the same document
                             if(term.postings.get(term.postings.size()-1).get(0)==docId){
                                 int hold = term.postings.get(term.postings.size()-1).get(1) + 1;
                                 term.postings.get(term.postings.size()-1).set(1,hold);
                             }
                             //For the new document
                             if(term.postings.get(term.postings.size()-1).get(0)<docId){
                                 term.termDocFrequencey++; //Updating Frequency
                                 term.postings.add(new ArrayList<Integer>()); //Adding new list For new Doc
                                 term.postings.get(term.postings.size()-1).add(docId); //Adding docId first iteration
                                 term.postings.get(term.postings.size()-1).add(1);//Adding corresponding position
                             }
                             pDictionary.replace(str, term);
                         }
                     }
                 }
             }
             catch(Exception e){
                 //System.out.println("You coded wrong");
             }
        }
        
        //positionalIndex();
        
        ArrayList<String> keyList = new ArrayList<String>(pDictionary.keySet()); // list of each item in dictionary
        double[][] vector=new double[51][pDictionary.size()];       //Vectors for each document 
        
        //Generating Vectors
        int count=0;
        for( Map.Entry<String, positionalTerm> entry : pDictionary.entrySet()){
            String term= entry.getKey();
            //Log operation
            double df= entry.getValue().termDocFrequencey;
            double idf= Math.log(50/df);
            
            for(int i=0; i<entry.getValue().postings.size(); i++){
                int docId = entry.getValue().postings.get(i).get(0);
                //Log operation
                double tf=entry.getValue().postings.get(i).get(1);
                tf= 1 + Math.log(tf);
                double value = tf * idf;
                vector[docId][count]= value;
            }
            
            count++;
        }
        
        //Evaluate Query
        Scanner scan=new Scanner(System.in);
        while(true){
            System.out.println("");
            System.out.println("Enter your query");
            
            String query=scan.nextLine();
            String[] arr= query.split(" ");
    
            //Generating query vector
            double[] queryVector = new double[pDictionary.size()];
            for(int i=0; i<arr.length; i++){
                String a = arr[i];
                double df = pDictionary.get(a).termDocFrequencey;
                double idf= Math.log(50/df);
                int pos= keyList.indexOf(a);
                //Log operation to be done
                queryVector[pos]= idf;
            }

            //cosine similarity
            double alpha=0.005;
            //double scorelist
            double[] score= new double[51];
            for(int i=1; i<51; i++){
                double dproduct=0;
                double mag1=0;
                double mag2=0;
                double sim=0;
                for(int j=0; j<pDictionary.size(); j++){
                    //DotProduct
                    if(vector[i][j]==0 || queryVector[j]==0){
                        dproduct+=0;
                    }
                    else{
                        dproduct+=vector[i][j]*queryVector[j];
                    }

                    //Magnitude for mag1
                    if(vector[i][j]==0){
                        mag1+=0;
                    }
                    else{
                        mag1+= Math.pow(vector[i][j], 2);
                    }

                    //Magnitude for mag2
                    if(queryVector[j]==0){
                        mag2+=0;
                    }
                    else{
                        mag2+= Math.pow(queryVector[j], 2);
                    }
                }
                mag1= Math.sqrt(mag1);
                mag2= Math.sqrt(mag2);

                if(dproduct != 0){
                   sim= dproduct/(mag1*mag2); 
                   score[i]=sim;
                }
                else{
                    score[i]=0;
                }
            }
            
            ArrayList<Double>scoreList=new ArrayList<Double>();
            for(int i=0;i<score.length;i++)
            {
                scoreList.add(score[i]);
            }
            
            //printing set in ranked order
            for(int i=1;i<=scoreList.size();i++)
            {
                double maxScore=Collections.max(scoreList);
                int index=scoreList.indexOf(maxScore);
                if(maxScore!=0 && maxScore >= alpha)
                //System.out.println((index)+".txt  "+maxScore);
                System.out.println("Document "+index+" score: "+maxScore);
                scoreList.remove(maxScore);
                scoreList.add(index,0.0);
            }
            
        }
    }
    
}
