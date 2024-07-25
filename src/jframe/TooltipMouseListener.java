/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import rojeru_san.complementos.RSTableMetro;

/**
 *
 * @author anonu
 */
public class TooltipMouseListener extends MouseInputAdapter {
    private RSTableMetro table;

    public TooltipMouseListener(RSTableMetro table) {
        this.table = table;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point point = e.getPoint();
        int row = table.rowAtPoint(point);
        int column = table.columnAtPoint(point);

        if (row >= 0 && column >= 0) {
            Object cellValue = table.getValueAt(row, column);
            String tooltipText = (cellValue != null) ? cellValue.toString() : "";
            table.setToolTipText(tooltipText);
        } else {
            table.setToolTipText(null);
        }
    }
}