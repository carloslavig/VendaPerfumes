import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Locale;

public class MainApp {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private static final String FILE_NAME = "clientes.csv";

    public MainApp() {
        frame = new JFrame("Gerenciador de Clientes e Pagamentos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        String[] columnNames = {"Nome", "Produto", "Valor", "Restante a Pagar"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        loadClientes();

        JButton addButton = new JButton("Adicionar Cliente");
        JButton payButton = new JButton("Registrar Pagamento");
        JButton deleteButton = new JButton("Deletar Cliente");
        JButton saveButton = new JButton("Salvar Progresso"); // Novo botão 📝
        JButton refreshButton = new JButton("Atualizar"); // Novo botão 🔄

        addButton.addActionListener(e -> adicionarCliente());
        payButton.addActionListener(e -> registrarPagamento());
        deleteButton.addActionListener(e -> new ClienteManager(model, table).deletarCliente(frame));
        saveButton.addActionListener(e -> saveClientes()); // Salva os dados manualmente
        refreshButton.addActionListener(e -> loadClientes()); // Recarrega os dados do arquivo

        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(payButton);
        panel.add(deleteButton);
        panel.add(saveButton); // Adiciona botão na interface
        panel.add(refreshButton); // Adiciona botão na interface

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void adicionarCliente() {
        JTextField nomeField = new JTextField(10);
        JTextField produtoField = new JTextField(10);
        JTextField valorField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Produto:"));
        panel.add(produtoField);
        panel.add(new JLabel("Valor:"));
        panel.add(valorField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Adicionar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double valor = Double.parseDouble(valorField.getText().trim());
                String valorFormatado = String.format(Locale.US, "%.2f", valor);
                model.addRow(new Object[]{nomeField.getText().trim(), produtoField.getText().trim(), valorFormatado, valorFormatado});
                saveClientes();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarPagamento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Selecione um cliente", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pagamentoStr = JOptionPane.showInputDialog("Digite o valor do pagamento:");
        if (pagamentoStr == null) return;

        try {
            double pagamento = Double.parseDouble(pagamentoStr.trim());
            double restante = Double.parseDouble(model.getValueAt(selectedRow, 3).toString());
            restante -= pagamento;
            if (restante < 0) restante = 0;
            model.setValueAt(String.format(Locale.US, "%.2f", restante), selectedRow, 3);
            saveClientes();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveClientes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(model.getValueAt(i, 0) + "," +
                        model.getValueAt(i, 1) + "," +
                        model.getValueAt(i, 2) + "," +
                        model.getValueAt(i, 3) + "\n");
            }
            JOptionPane.showMessageDialog(frame, "Progresso salvo com sucesso!", "Salvo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao salvar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadClientes() {
        model.setRowCount(0); // Limpa a tabela antes de recarregar
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    double valor = Double.parseDouble(data[2]);
                    double restante = Double.parseDouble(data[3]);
                    model.addRow(new Object[]{data[0], data[1], String.format(Locale.US, "%.2f", valor), String.format(Locale.US, "%.2f", restante)});
                }
            }
            JOptionPane.showMessageDialog(frame, "Dados atualizados!", "Atualizado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao carregar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
