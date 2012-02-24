//Clase MsgDialog:

/*
 * Formulario para mostrar mensajes al usuario.
 */

package pkgUI;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

public class MsgDialog extends Form implements ActionListener {

	//---------------------------------------------------------------
	//Comandos:
	//
	//ok: para aceptar el mensaje y volver a la pantalla anterior.
	//volver: vuelve al menú principal de la aplicación.
	//salir: no se puede continuar con el juego y se debe finalizar
	//la aplicación.
	//---------------------------------------------------------------
	private Command ok, volver, salir;
	
	
	//---------------------------------------------------------------
	//Otros objetos:
	//---------------------------------------------------------------
	private Form caller;
	private BatallaMidlet midlet;
	private LWUITMultiLabel label;
	
	
	//---------------------------------------------------------------
	//Constructor:
	//---------------------------------------------------------------
	public MsgDialog(BatallaMidlet bm) {
		midlet = bm;
		
		//inicializar comandos:
		ok = new Command("OK");
		volver = new Command("Salir");
		salir = new Command("Salir");
		addCommandListener(this);
		
		//inicializar formulario:
		setLayout(new BorderLayout());
		((BorderLayout)getLayout()).setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER);
		label = new LWUITMultiLabel("");
		label.getStyle().setAlignment(CENTER);
		label.getStyle().setFgColor(0xFF0000);		
		addComponent(BorderLayout.CENTER, label);
	}
	
	
	//---------------------------------------------------------------
	//void Mensaje(String msg, Form d, int c):
	//---------------------------------------------------------------
	//
	//Muestra el mensaje msg por pantalla. En d se pasa la pantalla
	//que llamó a esta función para volver a ella.
	//En c se pasa un número de 0 a 2 que indica el comportamiento
	//deseado:
	//0: agregar el comando ok para volver a la pantalla que lo llamó
	//1: agregar el comando volver para ir a la pantalla principal
	//2: agregar el comando salir para terminar la aplicación.
	//
	public void Mensaje(String msg, Form d, int c) {
		caller = d;
		label.setText(msg);
		switch (c){
		case 0: {
			removeCommand(volver);
			removeCommand(salir);
			addCommand(ok);
			break;
		}
		case 1: {
			removeCommand(ok);
			removeCommand(salir);
			addCommand(volver);
			break;
		}
		case 2: {
			removeCommand(ok);
			removeCommand(volver);
			addCommand(salir);
			break;
		}
		}
		show();
	}

	
	//---------------------------------------------------------------
	//void actionPerformed(ActionEvent a):
	//---------------------------------------------------------------
	//
	//Para capturar cuando se presiona un comando.
	//
	public void actionPerformed(ActionEvent a) {
		if (a.getCommand() == ok)
			caller.showBack();
		else if (a.getCommand() == volver)
			midlet.Volver();
		else if (a.getCommand() == salir)
			midlet.destroyApp(true);
	}

}
