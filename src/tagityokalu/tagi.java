/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tagityokalu;

import java.awt.Color;

/**
 *
 * @author janne
 */
public class tagi extends javax.swing.JPanel {

    private int id;
    private String name;
    /**
     * Creates new form tagi
     */
    public tagi() {
        initComponents();
    }
    
    public tagi(int aid, String aname, String kategoria) {
        initComponents();
        id = aid;
        name = aname;
        chbTagi.setText(name);
        lblKategoria.setText(kategoria);
    }
    
    public String getBoxName()
    {
        return name;
    }
    
    public int getId()
    {
        return id;
    }
    
    
    public boolean isChecked()
    {
        return chbTagi.isSelected();
    }
    
    public void setState(boolean state)
    {
        chbLukitse.setBackground(Color.gray);
        if(chbLukitse.isSelected())
        {
            //kenttä on lukittu. SEn arvoa ei muuteta
            if(chbTagi.isSelected() != state)
            {
                chbLukitse.setBackground(Color.red);
            }
        }else
        {
            chbTagi.setSelected(state);
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

        chbTagi = new javax.swing.JCheckBox();
        lblKategoria = new javax.swing.JLabel();
        chbLukitse = new javax.swing.JCheckBox();

        chbTagi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbTagiActionPerformed(evt);
            }
        });

        lblKategoria.setText("jLabel1");

        chbLukitse.setText("Lukitse");
        chbLukitse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbLukitseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblKategoria))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chbTagi)
                        .addGap(74, 74, 74)
                        .addComponent(chbLukitse)))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chbTagi)
                    .addComponent(chbLukitse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(lblKategoria)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chbTagiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbTagiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chbTagiActionPerformed

    private void chbLukitseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbLukitseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chbLukitseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chbLukitse;
    private javax.swing.JCheckBox chbTagi;
    private javax.swing.JLabel lblKategoria;
    // End of variables declaration//GEN-END:variables
}