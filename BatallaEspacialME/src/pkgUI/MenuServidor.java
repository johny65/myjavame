package pkgUI;

import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

import pkgUniverso.UniversoServidor;

public class MenuServidor extends Form implements ActionListener {

	private BatallaMidlet midlet;
	private Command volver, iniciar, jugar, cancelar;
	private ButtonGroup op;
	private TextField tf;
	private UniversoServidor serv;
	
	public MenuServidor(BatallaMidlet bm) {
		super("Servidor");
		midlet = bm;
		volver = new Command("Volver");
		iniciar = new Command("Iniciar");
		jugar = new Command("¡Jugar!");
		cancelar = new Command("Cancelar");
		
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		addComponent(new LWUITMultiLabel("Ingresa tu nombre:"));
		
		tf = new TextField();
		tf.setMaxSize(10);
		addComponent(tf);
		
		addComponent(new Label("Cantidad de jugadores:"));
		

		op = new ButtonGroup();
		for (int i=2; i<=4; ++i){
			op.add(new RadioButton(""+i));
			addComponent(op.getRadioButton(i-2));
		}
		op.setSelected(0);
		
		addCommand(volver);
		addCommand(iniciar);
		addCommandListener(this);
	}
	
	public void Mensaje(String msg) {
		addComponent(new Label(msg));
		show();
	}
	
	public void Error(String msg) {
		midlet.MostrarMensaje(msg, null, 1);
		midlet.Terminar();
	}
	
	//cuando todos los clientes terminaron de conectarse se llama a este método
	public void ServidorListo() {
		midlet.Inicializar(serv); //crear todo el juego
		addCommand(jugar);
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getCommand() == volver)
			midlet.Volver();
		else if (a.getCommand() == jugar){
			serv.IniciarComunicacion();
			midlet.Jugar();
		}
		else if (a.getCommand() == cancelar){
			//cerrar todas las conexiones y destruir el servidor
			serv.Destruir();
			serv = null;
			midlet.Volver();
		}
		else if (a.getCommand() == iniciar){
			String nombre = tf.getText();
			if (nombre.length() == 0){
				midlet.MostrarMensaje("Debes ingresar un nombre.", this, 0);
				return;
			}
			int cant = op.getSelectedIndex() + 2;
			removeAll();
			serv = new UniversoServidor(cant, nombre, this);
			serv.IniciarServidor(); //esperar las conexiones
			setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			removeCommand(iniciar);
			removeCommand(volver);
			addCommand(cancelar);
		}
		
	}
}
