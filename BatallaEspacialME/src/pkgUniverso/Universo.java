package pkgUniverso;

import java.util.Hashtable;
import java.util.Vector;

import pkgJuego.Nave;
import pkgJuego.TiroPos;

public interface Universo {
	
	public Nave[] getPosiciones();
	public TiroPos[] getTiros();
	public void setMisDatos(int x, int y, int dir, int E, int FR);
	public void setMiTiro(int x, int y, int pot);
	public void setMonedaAgarrada(int id);
	public void setMiAsesino(int id);
	public int getMiID();
	public int[] getMapa();
	public int getCantidadJugadores();
	public String[] getNombresJugadores();
	public void Destruir();
	public int getFrameRate();
	public int getCantVivos();
	public Vector getMonedasNuevas();
	public Vector getMonedasEliminadas();
	public Hashtable getMonedas();
	
	
	
	
}
