/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tagityokalu;

import java.util.ArrayList;

/**
 *
 * @author janne
 */
public class tagiLista extends javax.swing.JPanel {

    ArrayList<tagi> tagit;
    ArrayList<tagi> lisaamattomat;
    /**
     * Creates new form tagiLista
     */
    public tagiLista() {
        initComponents();
        tagit = new ArrayList<>();
        lisaamattomat = new ArrayList<>();
    }
    
    public void addExsistingTagi (tagi kohde)
    {
        tagit.add(kohde);
        this.add(kohde);
    }
    
    public void addNewTagi (tagi kohde)
    {
        lisaamattomat.add(kohde);
        this.add(kohde);
    }
    
    public boolean hasSelected()
    {
        boolean pal = false;
        for(tagi kohde : tagit)
        {

                pal = pal || kohde.isChecked();
        }
        return pal;
    }
    
    public ArrayList<tagi> getSelected()
    {
        
       
        //haetaan valitut tägit, ja palautetaan ne ja vain ne
        ArrayList<tagi> pal = new ArrayList<>();
        for(tagi kohde : tagit)
        {
            if(kohde.isChecked())
            {
                pal.add(kohde);
            }
        }
        return pal;
    }
    
    public void removeAllTags()
    {
        this.removeAll();
        tagit.clear();
    }
    
    //asetetaan tuloksen tägit valituiksi
    public void asetaValitut(ArrayList<String> kohdeTagit)
    {
        for(tagi kohde : tagit)
        {
            kohde.setState(false);
        }
        for(String kohdeId : kohdeTagit)
        {
            for(tagi ehdokas : tagit)
            {
                if(kohdeId.compareTo(ehdokas.getId()+"") == 0)
                {
                    ehdokas.setState(true);
                    break;
                }
            }
        }
        
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridLayout(0, 1));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}