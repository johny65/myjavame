//Clase Cronometro:

/*
 * Se encarga de controlar un tiempo aleatorio para agregar nuevas
 * monedas. Usada por el servidor.
 */

package pkgJuego;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import pkgUniverso.UniversoServidor;

public class Cronometro {

	private UniversoServidor padre;
	private Timer t;
	private TimerTask tt;
	private long tiempo;
	private Random r;
	private static int max = 60000, min = 5000; //entre 5 segundos y 1 minuto
	
	public Cronometro(UniversoServidor u) {
		padre = u;
		r = new Random();
		tiempo = r.nextInt(max) + min;
	}
	
	//Para arrancar el timer hay que llamar a esta función. Después
	//se encarga solo de volver a programarse con un tiempo aleatorio.
	public void Iniciar() {
		tt = new TimerTask() {
			public void run() {
				padre.agregar_moneda_aleatoria();
				tiempo = r.nextInt(max) + min;
				Iniciar();
			}
		};
		t = new Timer();
		t.schedule(tt, tiempo);
	}
	
	//Devuelve un objeto Random para poder generar números aleatorios
	//(para las coordenadas de las nuevas monedas)
	public Random rand() {
		return r;
	}
	
}
