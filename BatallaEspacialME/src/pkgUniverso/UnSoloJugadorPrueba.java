package pkgUniverso;

import java.util.Hashtable;
import java.util.Vector;

import pkgJuego.Nave;
import pkgJuego.TiroPos;
import pkgUtils.Utils;

public class UnSoloJugadorPrueba implements Universo {

	private Nave yo;

	public UnSoloJugadorPrueba() {
		yo = new Nave(70, 70, 0, 100, true);
	}
	
	public Nave[] getPosiciones() {
		Nave pp[] = {yo};
		return pp;
	}

	public void setMisDatos(int x, int y, int dir, int E, int FR) {
		yo.setX(x);
		yo.setY(y);
		yo.setDir(dir);
	}

	public int getMiID() {
		return 0;
	}

	public void Destruir() {}
	
	public int[] getMapa() {
		return Utils.MapaInt();
	}

	public int getCantidadJugadores() {
		return 1;
	}

	public TiroPos[] getTiros() {
		TiroPos t[] = {new TiroPos(0, false)};
		return t;
	}

	public void setMiTiro(int x, int y, int pot) {}

	public int getFrameRate() {
		return 30;
	}

	public void setMonedaAgarrada(int id) {}

	public void setMiAsesino(int id) {}

	public Vector getMonedasNuevas() {
		return new Vector();
	}

	public Vector getMonedasEliminadas() {
		return new Vector();
	}

	public Hashtable getMonedas() {
		return new Hashtable();
	}

	public int getCantVivos() {
		return 2; //para que no aparezca el cartel de ganador
	}

	public String[] getNombresJugadores() {
		String s[] = {"Prueba"};
		return s;
	}

}