//Clase Principal:

/*
 * Es la pantalla principal de la aplicación.
 */

package pkgUI;

import pkgUniverso.UnSoloJugadorPrueba;
import pkgUniverso.Universo;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class Principal extends Form implements ActionListener {

	//---------------------------------------------------------------
	//Atributos:
	//---------------------------------------------------------------
	private BatallaMidlet padre;
	private Command salir, unosolo;
	private Button bServidor, bCliente, bAyuda;
	
	
	//---------------------------------------------------------------
	//Constructor:
	//---------------------------------------------------------------
	public Principal(BatallaMidlet bm) {
		
		super("Batalla Espacial ME JJ");
		padre = bm;
		//TODO: poner una imagen
		
		//comandos y botones:
		
		salir = new Command("Salir");
		unosolo = new Command("1 jugador");		
		
		bServidor = new Button("Iniciar partida");
		bServidor.addActionListener(this);
		bCliente = new Button("Unirse a una partida");
		bCliente.addActionListener(this);
		bAyuda = new Button("Ayuda");
		bAyuda.addActionListener(this);
		
		//menú:
		
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		Label l = new Label("Selecciona un modo:");
		l.getStyle().setAlignment(CENTER);
		addComponent(l);
		addComponent(new Label("Crear una nueva partida:"));
		addComponent(bServidor);
		addComponent(new Label("Unirse a una partida existente:"));
		addComponent(bCliente);
		addComponent(bAyuda);
		
		addCommand(salir);
		addCommand(unosolo);
		addCommandListener(this);
		
	}

	
	//---------------------------------------------------------------
	//void actionPerformed(ActionEvent a):
	//---------------------------------------------------------------
	//
	//Para capturar las pulsaciones.
	//
	public void actionPerformed(ActionEvent a) {
		if (a.getCommand() == unosolo){
			Universo u = new UnSoloJugadorPrueba();
			padre.Inicializar(u);
			padre.Jugar();
		}
		else if (a.getCommand() == salir)
			padre.destroyApp(true);
		else if (a.getComponent() == bServidor){
			MenuServidor menu = new MenuServidor(padre);
			menu.show();
		}
		else if (a.getComponent() == bCliente){
			MenuCliente menu = new MenuCliente(padre);
			menu.show();
		}
		else if (a.getComponent() == bAyuda){
			String ayuda = "BatallaEspacial ME JJ v1.0\n\n" +
					"Mejoras de la nave:\n" +
					"La información sobre \"Mejoras\" " +
					"indica cuánto cuesta comprar la " +
					"siguiente mejora disponible para los " +
					"siguientes elementos:\n" +
					"V: velocidad de la nave\n" +
					"E: escudo (máximo y actual)\n" +
					"P: potencia del disparo\n" +
					"C: cristales de la nave (para " +
					"aumentar el rango de visión)\n\n" +
					"Teclas rápidas:\n" +
					"A: mostrar/ocultar radar\n" +
					"B: comprar escudo\n" +
					"C: mostrar/ocultar información sobre Mejoras\n" +
					"D: mostrar/ocultar nombres de los jugadores en el radar";
			padre.MostrarMensaje(ayuda, this, 0);
		}		
	}
}
