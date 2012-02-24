package pkgJuego;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import pkgUtils.ImagenVault;
import pkgUtils.Utils;

public class Heroe {

	private static int refsize = 24;
	private static int limites = Utils.MAPA_SIZE; //TODO: que los límites sean las paredes
	private static int collision_pos = refsize-refsize/2, collision_len = refsize;
	
	private Sprite sprite, img_vision;
	private int vel; //mi velocidad
	private int dx, dy, auxx, auxy;
	private int E, Emax; //escudo
	private int vidas, vision, puntos, plata;
	private int xinicial, yinicial; //posiciones iniciales para volver a nacer
	private boolean vivo; //si perdí el juego o no
	private TiledLayer obstaculos;
	private Tiro tiro;
	
	
	public Heroe(Sprite s, TiledLayer obs, Tiro t) {
		sprite = s;
		obstaculos = obs;
		tiro = t;
		//valores iniciales:
		vel = 5;
		E = 100; Emax = 100;
		vidas = 3;
		vision = 100;
		plata = 100;
		vivo = true;
	}
	
	public void HuevoDePascua() {
		sprite.setImage(ImagenVault.getMegaman(), 48, 48);
	}
	
	public void setPosInicial(int x, int y) {
		xinicial = x;
		yinicial = y;
		IrPosInicial();
	}
	
	public void SumarPlata(int p) {
		plata += p;
	}
	
	public int getPlata() {
		return plata;
	}
	
	public void GastarPlata(int p) {
		plata -= p;
	}
	
	public void IrPosInicial() {
		if (vidas != 0) //si me morí me quedo donde estaba
			sprite.setRefPixelPosition(xinicial, yinicial);
		dx = dy = 0;
	}
	
	public void setDireccion(int dir) {
		sprite.setFrame(dir);
	}
	
	public int getDireccion() {
		return sprite.getFrame();
	}
	
	public int getPotencia() {
		return tiro.getPotencia();
	}
	
	public void setVisible(boolean v) {
		sprite.setVisible(v);
	}
	
	public boolean ChocaCon(Sprite s) {
		sprite.defineCollisionRectangle(0, 0, 48, 48);
		return sprite.collidesWith(s, true);
	}
	
//	public void VerificarColision(TiledLayer t) {
//		if (sprite.collidesWith(t, true)){
//			sprite.move(-dx*vel, -dy*vel);
//		}
//	}
	
	public void TirarTiro() {
		tiro.Lanzar(getX(), getY(), getDireccion());
	}
	
	public int getX() {
		return sprite.getRefPixelX();
	}
	
	public int getY() {
		return sprite.getRefPixelY();
	}
	
	public int getPuntos() {
		return puntos;
	}
	
	public int getVelocidad() {
		return vel;
	}
	
	public void setPuntos(int p) {
		puntos = p;
	}
	
//	public void setEscudo(int e) {
//		E = e;
//	}
	
	public int getEscudo() {
		return E;
	}
	
	public int getEscudoMax() {
		return Emax;
	}
	
	public int getVision() {
		return vision;
	}
	
	public Sprite InicializarCampoVision(int w, int h) {
		img_vision = new Sprite(ImagenVault.getCampoVision(vision, w, h));
		img_vision.defineReferencePixel(w/2, h/2);
		return img_vision;
	}
	
	public boolean SigoVivo() {
		return vivo;
	}
	
	
	//devuelve true si me sacaron una vida
	public boolean MeDieron(int pot) {
		E = E - pot;
		if (E <= 0){
			E = Emax;
			vidas--;
			if (vidas == 0)
				vivo = false;
			return true;
		}
		return false;
	}
	
	public int getVidas() {
		return vidas;
	}
	
	//movimientos:
	private static int tol = 5;
	
	protected void mover() {
		sprite.defineCollisionRectangle(collision_pos, collision_pos, collision_len, collision_len);
		auxx = dx*tol; auxy = dy*tol;
		sprite.move(auxx, auxy);
		//auxx = dx; auxy = dy;
		for (int i=1; i<=vel; ++i){
			sprite.move(dx, dy);
			if (sprite.collidesWith(obstaculos, true)){
				sprite.move(-auxx-dx, -auxy-dy);
				return;
			}
		}
		sprite.move(-auxx, -auxy);
	}
	
	public void MoverN() {
		setDireccion(0);
		if (getY()-refsize > 0){
			dx = 0; dy = -1;
			mover();
		}
	}
	
	public void MoverNE() {
		setDireccion(1);
		if (getX()+refsize < limites) dx = 1;
		else dx = 0;
		if (getY()-refsize > 0) dy = -1;
		else dy = 0;
		mover();
	}
	
	public void MoverE() {
		setDireccion(2);
		if (getX()+refsize < limites){
			dx = 1; dy = 0;
			mover();
		}
	}
	
	public void MoverSE() {
		setDireccion(3);
		if (getX()+refsize < limites) dx = 1;
		else dx = 0;
		if (getY()+refsize < limites) dy = 1;
		else dy = 0;
		mover();
	}
	
	public void MoverS() {
		setDireccion(4);
		if (getY()+refsize < limites){
			dx = 0; dy = 1;
			mover();
		}
	}
	
	public void MoverSO() {
		setDireccion(5);
		if (getX()-refsize > 0) dx = -1;
		else dx = 0;
		if (getY()+refsize < limites) dy = 1;
		else dy = 0;
		mover();
	}
	
	public void MoverO() {
		setDireccion(6);
		if (getX()-refsize > 0){
			dx = -1; dy = 0;
			mover();
		}
	}
	
	public void MoverNO() {
		setDireccion(7);
		if (getX()-refsize > 0) dx = -1;
		else dx = 0;
		if (getY()-refsize > 0) dy = -1;
		else dy = 0;
		mover();
	}
	
	public void Rebotar(Sprite s) {
		if (!sprite.collidesWith(obstaculos, true))
			sprite.move(-10*dx, -10*dy);
	}
	
	
	//mejoras:
	
	public void AumentarVelocidad() {
		//cada mejora aumenta en 2 píxeles
		vel += 2;
		tiro.setVel(2*vel); //mi tiro tiene el doble de mi velocidad
	}
	
	public void AumentarEscudo() {
		int mejora = (int)Math.floor(E*0.1f);
		E += mejora;
		mejora = (int)Math.floor(Emax*0.1f);
		Emax += mejora;
	}
	
	public void AumentarPotencia() {
		int mejora = (int)Math.floor(tiro.getPotencia()*0.1f);
		tiro.AumentarPotencia(mejora);
	}
	
	public void AumentarVision() {
		int mejora = (int)Math.floor(vision*0.2f);
		vision += mejora;
		int h = img_vision.getHeight(), w = img_vision.getWidth();
		img_vision.setImage(ImagenVault.getCampoVision(vision, w, h), w, h);
	}
}