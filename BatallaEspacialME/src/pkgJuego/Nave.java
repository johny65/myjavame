package pkgJuego;

public class Nave {

	private int x, y, id, dir, e, em, p;
	private String a;
	private boolean vivo;
	
	public Nave() {}
	public Nave(int x, int y, int id, int e, boolean vivo) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.e = e;
		this.em = 100; //escudo máximo, inicialmente 100
		this.vivo = vivo;
		this.a = "V";
		this.p = 0; //puntaje
	}
	
	//setters:
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setID(int id) { this.id = id; }
	public void setEscudo(int e) { this.e = e; }
	public void setMaxEscudo(int e) { this.em = e; }
	public void setVivo(boolean v) { this.vivo = v; }
	public void setDir(int d) { this.dir = d; }
	public void setAsesino(String a) { this.a = a; }
	public void sumarPunto() { this.p++; }
	
	//getters:
	public int X() { return x; }
	public int Y() { return y; }
	public int Id() { return id; }
	public int E() { return e; }
	public int MaxE() { return em; }
	public boolean Vivo() { return vivo; }
	public int Dir() { return dir; }
	public String Asesino() { return a; }
	public int Puntos() { return p; }
	
}
