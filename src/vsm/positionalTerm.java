/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsm;

import java.util.ArrayList;

/**
 *
 * @author Naqib
 */
public class positionalTerm {
    public int termDocFrequencey = 0;               //Shows in how many doc the term appear
    public ArrayList<ArrayList<Integer>> postings;  //docId and term frequncey in that document
    
    public positionalTerm(){
        postings = new ArrayList<ArrayList<Integer>>();
    }
}
