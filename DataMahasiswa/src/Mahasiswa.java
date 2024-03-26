public class Mahasiswa {
    private String nim;
    private String nama;
    private String jenisKelamin;

    private String jenjang;

    public Mahasiswa(String nim, String nama, String jenisKelamin, String jenjang) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.jenjang = jenjang;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public void setJenjang(String jenjang) {
        this.jenjang = jenjang;
    }

    public String getNim() {
        return this.nim;
    }

    public String getNama() {
        return this.nama;
    }

    public String getJenisKelamin() {
        return this.jenisKelamin;
    }

    public String getJenjang() {
        return this.jenjang;
    }
}
