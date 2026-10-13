
package com.mycompany.tallerultimoarranque;

import com.mycompany.tallerultimoarranque.vista.FrmPrincipal;
import javax.swing.SwingUtilities;

public class TallerUltimoArranque {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            FrmPrincipal principal = new FrmPrincipal();

            principal.setLocationRelativeTo(null);

            principal.setVisible(true);
            
            

        });
    }
}
