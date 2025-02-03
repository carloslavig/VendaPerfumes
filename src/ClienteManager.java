import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Locale;

class ClienteManager {
    private DefaultTableModel model;
    private JTable table;

    public ClienteManager(DefaultTableModel model, JTable table) {
        this.model = model;
        this.table = table;
    }

    public void deletarCliente(JFrame frame) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Selecione um cliente", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double restante = Double.parseDouble(model.getValueAt(selectedRow, 3).toString());
        int confirm = JOptionPane.showConfirmDialog(frame, "O cliente deve R$" + String.format(Locale.US, "%.2f", restante) + ". Deseja realmente deletá-lo?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(selectedRow);
            saveClientes();
        }
    }

    private void saveClientes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clientes.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(model.getValueAt(i, 0) + "," +
                        model.getValueAt(i, 1) + "," +
                        model.getValueAt(i, 2) + "," +
                        model.getValueAt(i, 3) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
