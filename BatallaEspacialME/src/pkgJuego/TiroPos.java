package pkgJuego;

public class TiroPos {

	private int x, y;
	private int potencia;
	private boolean existe;
	
	public TiroPos(int pot, boolean existe) {
		this.x = -1;
		this.y = -1;
		this.potencia = pot;
		this.existe = existe;
	}
	
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setPotencia(int p) { this.potencia = p; }
	public void setExiste(boolean e) { this.existe = e; }
	
	public int X() { return x; }
	public int Y() { return y; }
	public int Potencia() { return potencia; }
	public boolean Existe() { return existe; }
	
}
