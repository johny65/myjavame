package pkgUI;

import java.util.Vector;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

import pkgUniverso.UniversoCliente;

public class MenuCliente extends Form implements ActionListener {

	private BatallaMidlet midlet;
	private TextField tf;
	private Command volver, iniciar, buscar, cancelar;
	private Vector servicios;
	private List choice_servicios;
	private UniversoCliente client;
	
	public MenuCliente(BatallaMidlet bm) {
		super("Cliente");
		midlet = bm;
		
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		volver = new Command("Volver");
		iniciar = new Command("Conectar");
		buscar = new Command("Buscar");
		cancelar = new Command("Cancelar");
		
		LWUITMultiLabel l = new LWUITMultiLabel("Ingresa tu nombre de jugador y luego presiona el botón 'Buscar' para iniciar la búsqueda de una partida.");
		addComponent(l);
		tf = new TextField();
		tf.setMaxSize(10);
		addComponent(tf);
		
		choice_servicios = new List();
		servicios = new Vector();
		
		addCommand(volver);
		addCommand(buscar);
		addCommandListener(this);
		
	}

	public void Mensaje(String msg) {
		addComponent(new Label(msg));
		show();
	}
	
	public void AgregarServicio(String NombreDispositivo, String urlServicio) {
		servicios.addElement(urlServicio);
		choice_servicios.addItem(NombreDispositivo);
//		choice_servicios.setMinElementHeight(servicios.size());
//		choice_servicios.setPreferredH(getLayoutHeight());
		show();
	}
	
	public void BusquedaTerminada() {
		removeAll();
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		addComponent(new Label("Selecciona un juego:"));
		addComponent(choice_servicios);
		removeCommand(cancelar);
		addCommand(volver);
		addCommand(iniciar);
	}
	
	//cuando el cliente ya estableció la conexión y tiene su ID, se llama a este método
	public void ClienteListo() {
		midlet.Inicializar(client);
		Mensaje("La partida empezará pronto...");
	}
	
	//cuando el cliente recibe el START, se llama a este método
	public void ClienteStart() {
		midlet.Jugar();
	}
	
	public void Error(String msg) {
		midlet.MostrarMensaje(msg, null, 1);
		midlet.Terminar();
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getCommand() == volver)
			midlet.Volver();
		else if (a.getCommand() == buscar){
			String nombre = tf.getText();
			if (nombre.length() == 0){
				midlet.MostrarMensaje("Debes ingresar un nombre.", this, 0);
				return;
			}
			//iniciar búsqueda
			addComponent(new Label("Buscando juego..."));
			removeCommand(volver);
			removeCommand(buscar);
			addCommand(cancelar);
			client = new UniversoCliente(this, nombre);
			client.IniciarBusqueda();
		}
		else if (a.getCommand() == cancelar){
			client.CancelarBusqueda();
		}
		else if (a.getCommand() == iniciar){
			if (choice_servicios.size() != 0 && choice_servicios.getSelectedIndex() != -1){
				client.Conectar((String)servicios.elementAt(choice_servicios.getSelectedIndex()));
				Mensaje("Conectándose...");
				removeCommand(iniciar);
			}
		}
		
		
	}
}
