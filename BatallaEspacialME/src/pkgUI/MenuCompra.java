package pkgUI;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import pkgJuego.Heroe;
import pkgJuego.Juego;

public class MenuCompra implements CommandListener {

	private Command c_vel, c_escudo, c_pot, c_vision;
	private Command salir;
	private Heroe player; //para actualizar su estado
	private Juego juego;
	private BatallaMidlet midlet;
	
	//precios
	private int p_vel, p_escudo, p_pot, p_vision;
	
	
	public MenuCompra(Heroe p, Juego j, BatallaMidlet bm) {
		player = p;
		juego = j;
		midlet = bm;
		c_vel = new Command("Comprar velocidad", Command.ITEM, 1);
		c_escudo = new Command("Comprar escudo", Command.ITEM, 1);
		c_pot = new Command("Comprar potencia disparo", Command.ITEM, 1);
		c_vision = new Command("Comprar cristales", Command.ITEM, 1);
		salir = new Command("Salir", Command.EXIT, 1);
		
		juego.addCommand(salir);
		juego.addCommand(c_vel);
		juego.addCommand(c_escudo);
		juego.addCommand(c_pot);
		juego.addCommand(c_vision);
		juego.setCommandListener(this);
		
		p_vel = player.getVelocidad()*10;
		p_escudo = player.getEscudoMax()*5;
		p_pot = player.getPotencia()*10;
		p_vision = player.getVision();
		
		//informar precios iniciales:
		juego.ActualizarInfoPrecios(Integer.toString(p_vel), Integer.toString(p_escudo), player.getEscudo(), Integer.toString(p_pot), Integer.toString(p_vision), player.getPlata());
	}

	private void ComprarVelocidad() {
		if (player.getPlata() >= p_vel){
			player.GastarPlata(p_vel);
			player.AumentarVelocidad();
			
			//actualizar precio
			p_vel = player.getVelocidad()*10;
			juego.ActualizarInfoPrecios(Integer.toString(p_vel), null, 0, null, null, player.getPlata());
			
		}
	}
	
	public void ComprarEscudo() {
		if (player.getPlata() >= p_escudo){
			player.GastarPlata(p_escudo);
			player.AumentarEscudo();
			p_escudo = player.getEscudoMax()*5;
			juego.ActualizarInfoPrecios(null, Integer.toString(p_escudo), player.getEscudo(), null, null, player.getPlata());
		}
	}
	
	private void ComprarPotencia() {
		if (player.getPlata() >= p_pot){
			player.GastarPlata(p_pot);
			player.AumentarPotencia();
			p_pot = player.getPotencia()*10;
			juego.ActualizarInfoPrecios(null, null, 0, Integer.toString(p_pot), null, player.getPlata());
		}
	}
	
	private void ComprarVision() {
		if (player.getPlata() >= p_vision){
			player.GastarPlata(p_vision);
			player.AumentarVision();
			p_vision = player.getVision();
			juego.ActualizarInfoPrecios(null, null, 0, null, Integer.toString(p_vision), player.getPlata());
		}
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == salir)
			midlet.Volver();
		else if (c == c_escudo)
			ComprarEscudo();
		else if (c == c_vel)
			ComprarVelocidad();
		else if (c == c_pot)
			ComprarPotencia();
		else if (c == c_vision)
			ComprarVision();
	}
	
	public void DesactivarCompra() {
		juego.removeCommand(c_vel);
		juego.removeCommand(c_escudo);
		juego.removeCommand(c_pot);
		juego.removeCommand(c_vision);
	}
	
	
}
