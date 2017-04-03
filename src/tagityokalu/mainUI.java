/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tagityokalu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author janne
 */
public class mainUI extends javax.swing.JFrame {

    private ArrayList<TietoPaneli> tiedot;
    private int tiedotIndeksi = -1;
    private HashMap<String,String> idKentat;
    
    private String tagiTaulu;
    private String linkkiTaulu;
    /**
     * Creates new form mainUI
     */
    public mainUI() {
        initComponents();
        tiedot = new ArrayList<>();
        idKentat = new HashMap<>();
        Connection c = null;
        FileInputStream is = null;
        BufferedReader br;
        try {
            //Alustetaan kombo boksi
            is = new FileInputStream("TableNames.txt");
            br = new BufferedReader(new InputStreamReader(is));
            String luettuRivi = br.readLine();
            while(luettuRivi != null)
            {
                String[] tmpRivi = luettuRivi.split("#");
                cmbTaulu.addItem(tmpRivi[0]);
                idKentat.put(tmpRivi[0],tmpRivi[1]);
                luettuRivi = br.readLine();
            }
            br.close();
            is.close();
            luoTietoPanelit();
            haeTagiTaulut();
            alustaTagiLista();
            lblTotal.setText("/" + tiedot.size());
        
            //asetetaan listeneri comboboksille niin että luetaan siinä valitun taulun tiedot,tägit ja linkkeri taulu.
            cmbTaulu.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                    tiedotIndeksi = -1;
                    tiedot.clear();
                    haeTagiTaulut();
                    luoTietoPanelit();
                    alustaTagiLista();
                    pnlIsanta.removeAll();
                    pnlIsanta.add(jumpTo(0));
                    lblTotal.setText("/" + tiedot.size());
                    pnlIsanta.repaint();
                    pnlIsanta.revalidate();
                }
            });
      
        }catch (FileNotFoundException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        pnlIsanta.add(jumpTo(0));
        
    }
    
    //ai katos... tää on olemassa jo alempana, findStructure!
    private void haeTagiTaulut()
    {
        try {
            String[] tmp = findStructure(cmbTaulu.getSelectedItem().toString());
            tagiTaulu = tmp[2];
            linkkiTaulu = tmp[3];
        } catch (SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void alustaTagiLista()
    {
        tglLista.removeAllTags();
        try(Connection c = getConnection("Kaavapp.db")) {

                String querry =  "SELECT * from " + tagiTaulu;
                System.out.println("alustuksessa " + querry);
                Statement stmt = c.createStatement();
                ResultSet tagit = stmt.executeQuery(querry);
                while(tagit.next())
                {
                    //lisätään tägi tägi listaan
                    tglLista.addExsistingTagi(new tagi(tagit.getInt("_tagid"), tagit.getString("nimi"), tagit.getString("kategoria"))); 
            }
        } catch (SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void luoTietoPanelit()
    {
        Connection c = null;
        try {
            
            
            c = getConnection("Kaavapp.db");
            //luetaan tiedot
            String querry = "SELECT * FROM " + cmbTaulu.getSelectedItem();
            Statement stmt = c.createStatement();
            
            ResultSet rsMain = stmt.executeQuery(querry);
            while(rsMain.next())
            {
                TietoPaneli tmpPaneli = new TietoPaneli();
                HashMap<String,String> tmpTiedot = new HashMap<>();
                ResultSetMetaData metDat = rsMain.getMetaData();
                for(int i = 1; i <= metDat.getColumnCount(); i++)
                {
                    tmpTiedot.put(metDat.getColumnName(i),rsMain.getString(i));
                }
                tmpPaneli.addData(tmpTiedot,idKentat.get(cmbTaulu.getSelectedItem().toString()));
                tiedot.add(tmpPaneli);
               
            }
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private TietoPaneli jumpTo(int indeksi)
    {
        if(indeksi  < 0 || indeksi >= tiedot.size())
        {
          JPanel panel = new JPanel();
          JOptionPane.showMessageDialog(panel, indeksi +  " on ulkona alkioden alueesta", "Error", JOptionPane.ERROR_MESSAGE);   
          return null;
        }
        if(indeksi == 0)
        {
             btnEdellinen.setEnabled(false);
        }else{
            btnEdellinen.setEnabled(true);
        }
        if(indeksi == tiedot.size()-1)
        {
            btnSeuraava.setEnabled(false);
        }else{
            btnSeuraava.setEnabled(true);
        }
        asetaTagit(tiedot.get(indeksi).getId());
        txfWarp.setText((indeksi + 1) + "");
        tiedotIndeksi = indeksi;
        return tiedot.get(indeksi);
    }
        
    private void asetaTagit(String kohteenId)
    {
        try {
            Connection c = getConnection("Kaavapp.db");
            //luetaan tiedot
            String[] structure = findStructure(cmbTaulu.getSelectedItem().toString());
            String querry = "SELECT * FROM " + structure[3] + " WHERE " + structure[4] + " = " + kohteenId;
            System.out.println(querry);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(querry);
            
            ArrayList<String> tagiIdt = new ArrayList<>();
            while(rs.next())
            {
                tagiIdt.add(rs.getString("_tagid"));
            }
            tglLista.asetaValitut(tagiIdt);
            c.close();
            tglLista.repaint();
            tglLista.revalidate();
            
        } catch (SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //palautuksessa defaultFieldsin 4 kenttää ja indeksissä 4 taulun primary key (tai ainakin ensimmäinen kenttä)
    private String[] findStructure(String tName) throws SQLException
    {
        String[] pal = new String[5];
        //luodaan querry
        String querry = "SELECT * FROM defaultFields WHERE _tname like '" + tName + "'";
        Connection c = getConnection("Kaavapp.db");
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(querry);
        for(int i = 0; i < 4; i++)
        {
            pal[i] = rs.getString(i+1);
        }
        //etsitään primary key
        querry = "pragma table_info(" + tName + ")"; //onko tämä muka primary key?
        rs = stmt.executeQuery(querry);
        pal[4] = rs.getString(2);
        c.close();
        return pal;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txfLisaaTagi = new javax.swing.JTextField();
        btnLisaa = new javax.swing.JButton();
        btnExcecute = new javax.swing.JButton();
        btnEdellinen = new javax.swing.JButton();
        btnSeuraava = new javax.swing.JButton();
        cmbTaulu = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txfKategoria = new javax.swing.JTextField();
        btnSeurTagiton = new javax.swing.JButton();
        txfWarp = new javax.swing.JTextField();
        txfMene = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tglLista = new tagityokalu.tagiLista();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlIsanta = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnLisaa.setText("lisaa");
        btnLisaa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLisaaActionPerformed(evt);
            }
        });

        btnExcecute.setText("Excecute");
        btnExcecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcecuteActionPerformed(evt);
            }
        });

        btnEdellinen.setText("Edellinen");
        btnEdellinen.setEnabled(false);
        btnEdellinen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdellinenActionPerformed(evt);
            }
        });

        btnSeuraava.setText("Seuraava");
        btnSeuraava.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeuraavaActionPerformed(evt);
            }
        });

        cmbTaulu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTauluActionPerformed(evt);
            }
        });

        jLabel1.setText("nimi");

        jLabel2.setText("kategoria");

        txfKategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfKategoriaActionPerformed(evt);
            }
        });

        btnSeurTagiton.setText("Seuraava tägitön");
        btnSeurTagiton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeurTagitonActionPerformed(evt);
            }
        });

        txfMene.setText("Mene");
        txfMene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfMeneActionPerformed(evt);
            }
        });

        lblTotal.setText("/");

        jScrollPane1.setViewportView(tglLista);

        pnlIsanta.setLayout(new java.awt.GridLayout(1, 0));
        jScrollPane2.setViewportView(pnlIsanta);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEdellinen)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(txfWarp, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnSeuraava, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txfMene, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(btnSeurTagiton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addComponent(cmbTaulu, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 134, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txfKategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addComponent(txfLisaaTagi))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLisaa, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnExcecute, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSeuraava)
                        .addComponent(btnEdellinen)
                        .addComponent(cmbTaulu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnExcecute))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txfLisaaTagi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(txfWarp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txfMene)
                            .addComponent(lblTotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txfKategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSeurTagiton))
                        .addGap(0, 19, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLisaa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLisaaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLisaaActionPerformed

            
            luoUusiTagi(txfLisaaTagi.getText(),txfKategoria.getText());
            txfLisaaTagi.setText("");
            tglLista.repaint();
            tglLista.revalidate();
            
    }//GEN-LAST:event_btnLisaaActionPerformed

    private void btnExcecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcecuteActionPerformed
        ArrayList<tagi> tmp = tglLista.getSelected();
        try{
        Connection c = getConnection("Kaavapp.db");
            Statement stmt = c.createStatement();
            TietoPaneli kohdeTietoPaneli = (TietoPaneli)pnlIsanta.getComponent(0);
            //poistetaan olemassa olevat tägit
            String deleteQuerry = "DELETE FROM " + linkkiTaulu +" WHERE " + idKentat.get(cmbTaulu.getSelectedItem()) + " = " + kohdeTietoPaneli.getId();
            stmt.executeUpdate(deleteQuerry);
            
            String querryBase = "INSERT INTO " + linkkiTaulu + "(" + idKentat.get(cmbTaulu.getSelectedItem()) + ",_tagid) VALUES";
            for(tagi kohde : tmp)
        {
            String querry = querryBase + "(" + kohdeTietoPaneli.getId() + "," + kohde.getId() + ")";
            stmt.executeUpdate(querry);
            
        }
        c.commit();
        c.close();
        } catch (SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception ex){
            }
      if(tiedotIndeksi != tiedot.size()-1)
      {
          System.out.println("Ollaan menosssa seuraavaan");
      btnSeuraavaActionPerformed(null);
      }
        
    }//GEN-LAST:event_btnExcecuteActionPerformed

    private void btnEdellinenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdellinenActionPerformed
        pnlIsanta.removeAll();
        pnlIsanta.add(jumpTo(tiedotIndeksi-1));
        pnlIsanta.repaint();
        pnlIsanta.revalidate();
    }//GEN-LAST:event_btnEdellinenActionPerformed

    private void btnSeuraavaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeuraavaActionPerformed
        pnlIsanta.removeAll();
        pnlIsanta.add(jumpTo(tiedotIndeksi+1));
        pnlIsanta.repaint();
        pnlIsanta.revalidate();
    }//GEN-LAST:event_btnSeuraavaActionPerformed

    private void txfKategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfKategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txfKategoriaActionPerformed

    private void txfMeneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfMeneActionPerformed
        int kohde =Integer.parseInt(txfWarp.getText()) - 1;
        JPanel tmp = jumpTo(kohde);
        if(tmp != null)
        {
            pnlIsanta.removeAll();
            pnlIsanta.add(tmp);
        }
        pnlIsanta.repaint();
        pnlIsanta.revalidate();
    }//GEN-LAST:event_txfMeneActionPerformed

    private void btnSeurTagitonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeurTagitonActionPerformed
        int alkuPiste = tiedotIndeksi;
        TietoPaneli kohde;
        while(tglLista.hasSelected())
        {
            if(tiedotIndeksi == tiedot.size()-1)
            {
                //mennään alkuun
                tiedotIndeksi = -1;
            }
            kohde = jumpTo(tiedotIndeksi+1);
            if(tiedotIndeksi == alkuPiste)
            {
                //mentiin ympäri, ei osumaa.
                JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Kaikilla riveillä on jo tägi", "Error", JOptionPane.ERROR_MESSAGE);
                
            }
            pnlIsanta.removeAll();
            pnlIsanta.add(kohde);
            pnlIsanta.repaint();
            pnlIsanta.revalidate();
        }
    }//GEN-LAST:event_btnSeurTagitonActionPerformed

    private void cmbTauluActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTauluActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTauluActionPerformed

    private void luoUusiTagi(String tagiNimi, String kategoria)
    {
        String query = "INSERT INTO "+ tagiTaulu +" (nimi,kategoria) VALUES('" + tagiNimi + "', '" + kategoria + "')";
        tagi pal = null;
        Connection c = getConnection("Kaavapp.db");
        try{
            Statement stmt = c.createStatement();
            //tarkistetaan että saman nimistä tägie ei ole jo olemassa
            String tarkistus = "SELECT * FROM "+ tagiTaulu +" WHERE nimi = '"+tagiNimi + "'";
            ResultSet rs = stmt.executeQuery(tarkistus);
            if(rs.next())
            {
                 JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Tagi " + tagiNimi +" on jo olemassa", "Error", JOptionPane.ERROR_MESSAGE);
                c.close();
                return;
            }
            stmt.executeUpdate(query);
            c.commit();
            rs = stmt.executeQuery(tarkistus);
        while(rs.next())
        {
            //lisätään tägi tägi listaan
            tglLista.addExsistingTagi(new tagi(rs.getInt("_tagid"), rs.getString("nimi"), rs.getString("kategoria")));   
        } 
          c.close();  
            
        }
        catch(SQLException ex) {
             Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private Connection getConnection(String kohde)
    {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Kaavapp.db");
            c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
            return c;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainUI().setVisible(true);
            }
        });
        
         
        
    }
    
    private static void addData(String querry, Connection c) throws SQLException
    {
         Statement stmt = null;
         stmt = c.createStatement();
         stmt.executeUpdate(querry);
         c.commit();
         stmt.close();
         
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdellinen;
    private javax.swing.JButton btnExcecute;
    private javax.swing.JButton btnLisaa;
    private javax.swing.JButton btnSeurTagiton;
    private javax.swing.JButton btnSeuraava;
    private javax.swing.JComboBox cmbTaulu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlIsanta;
    private tagityokalu.tagiLista tglLista;
    private javax.swing.JTextField txfKategoria;
    private javax.swing.JTextField txfLisaaTagi;
    private javax.swing.JButton txfMene;
    private javax.swing.JTextField txfWarp;
    // End of variables declaration//GEN-END:variables
}
