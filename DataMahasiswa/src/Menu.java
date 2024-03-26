import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(460, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;
    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JLabel jenjangLabel;
    private JComboBox jenjangComboBox;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        //database
        database = new Database();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel((jenisKelaminData)));

        // atur isi combo box jenjang
        String[] jenjangdata = {"", "S1", "S2", "S3"};
        jenjangComboBox.setModel(new DefaultComboBoxModel((jenjangdata)));


        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex == -1){
                    insertData();
                } else{
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex >= 0){
                    int option = JOptionPane.showConfirmDialog(null, "Yakin Dihapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        // Kode untuk menghapus data
                        deleteData();
                    }
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedJenjang = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();

                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                jenjangComboBox.setSelectedItem(selectedJenjang);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Jenjang"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");

            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[5];

                row[0] = i+1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("jenjang");
                temp.addRow(row);
                i++;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
         return temp;
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String jenjang = jenjangComboBox.getSelectedItem().toString();

        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || jenjang.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap lengkapi semua input!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Keluar dari metode jika ada input yang kosong
        }

        // Cek apakah NIM sudah ada dalam database
        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = '" + nim + "';";
        int count = database.executeCountQuery(sql);
        if (count > 0) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada dalam database!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Keluar dari metode jika NIM sudah ada
        }

        // tambahkan data ke dalam list
        sql = "INSERT INTO mahasiswa VALUES (null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + jenjang + "');";
        database.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Insert Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
    }


    public void updateData() {
        // Ambil data dari form
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String jenjang = jenjangComboBox.getSelectedItem().toString();

        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || jenjang.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap lengkapi semua input!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Keluar dari metode jika ada input yang kosong
        }

        // Mendapatkan ID dari baris yang sesuai dengan selectedIndex
        int id = 0;
        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            int i = 0;
            while (resultSet.next()) {
                if (i == selectedIndex) {
                    id = resultSet.getInt("id");
                    break;
                }
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = '" + nim + "' AND id != '"+ id + "';";
        int count = database.executeCountQuery(sql);

        if (count > 0) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada dalam database!", "Error", JOptionPane.ERROR_MESSAGE);
            // Isi kembali nimField dengan NIM sebelumnya
            try {
                ResultSet resultSet = database.selectQuery("SELECT nim FROM mahasiswa WHERE id = '" + id + "'");
                if (resultSet.next()) {
                    nimField.setText(resultSet.getString("nim"));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return; // Keluar dari metode jika NIM sudah ada
        }


        // ubah data mahasiswa di database
        sql = "UPDATE `mahasiswa` SET `nim` = '" + nim +"' WHERE `mahasiswa`.`id` = '" + id + "';";
        database.insertUpdateDeleteQuery(sql);
        sql = "UPDATE `mahasiswa` SET `nama` = '" + nama +"' WHERE `mahasiswa`.`id` = '" + id + "';";
        database.insertUpdateDeleteQuery(sql);
        sql = "UPDATE `mahasiswa` SET `jenis_kelamin` = '" + jenisKelamin +"' WHERE `mahasiswa`.`id` = '" + id + "';";
        database.insertUpdateDeleteQuery(sql);
        sql = "UPDATE `mahasiswa` SET `jenjang` = '" + jenjang +"' WHERE `mahasiswa`.`id` = '" + id + "';";
        database.insertUpdateDeleteQuery(sql);



        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Update Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil diubah!");

    }

    public void deleteData() {
        // hapus data dari database
        String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
        String sql = "DELETE FROM mahasiswa WHERE `mahasiswa`.`nim` = '" + selectedNim + "'";
//        System.out.println(selectedNim);
        database.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");

    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        jenjangComboBox.setSelectedItem("");

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
