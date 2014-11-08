package floader.visuals.alba_francesca_battista;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class Battista extends AbstractVisual {

	PApplet app;
	int x = 20;
	float y = 50;
	int i = 0;
	int j = 0;
	
	int strokeWeight = 4;
	int maxStrokeWeight = 8;
	int offset = 50;
	float A;
	float G;
	float scale = .5f;
	boolean animate = true;
	int height;

	public Battista(PApplet app) {
		this.app = app;
	}

	public void setup() {
		height = VisualConstants.HEIGHT;
	}

	public void draw(PGraphics g) {
		//g.translate((int)(-VisualConstants.WIDTH/3), (int)(-VisualConstants.HEIGHT/1.3));
		height = (int)this.frustrumHeight;
		//larghezza = (int)this.frustrumWidth;
		
		g.smooth();
		g.stroke(curColorScheme.getColor(0).getRGB());
		g.strokeWeight(strokeWeight);
		

		if (!animate) {
			y = 50;
			x = 20;
		}
		
		int larghezza = (int) frustrumHeight/2;
		int lunghezza = 70;
		
		drawDots(g, lunghezza, larghezza);
		
		lunghezza = 320;
		larghezza = 90;
		
		g.pushMatrix();
		g.translate(-frustrumWidth/3, 0);
		drawDots(g, lunghezza, larghezza);
		g.popMatrix();
		
		g.pushMatrix();
		g.translate(frustrumWidth/3, 0);
		drawDots(g, lunghezza, larghezza);
		g.popMatrix();
		
		/*float boxWidth = 200;
		g.strokeWeight(20);
		g.point(0, 0);
		g.point(this.frustrumWidth/2 - 10, 0);
		g.point(-this.frustrumWidth/2 + 10, 0);
		
		g.stroke(255);
		g.point(boxWidth, 0);
		g.point(-boxWidth, 0);*/
		
	}
	
	void drawDots(PGraphics g, int lunghezza, int larghezza)
	{
		for (int i = -50; i < lunghezza; i = i + (int) app.random(2, 3)) {
			for (int j = 99 - i; j < larghezza; j = j + (int) app.random(1, 6)) {
				x = j - (i / 2);

					g.point(x, y - height/2);
					g.point(-x, y - height/2);
			}
			y = (y <= height ? y = y + scale + j : 0);
		}
	}
	
public void ctrlEvent(int index, float val) {
		
		switch(index)
		{
		case VisualConstants.LOCAL_EFFECT_1:
			//movement amount
			scale = val * 40;
			break;
		case VisualConstants.LOCAL_EFFECT_2:
			//movement amount
			strokeWeight = (int)(val * maxStrokeWeight) + 1;
			break;
			
		case VisualConstants.LOCAL_EFFECT_3:
			//movement amount
			if(val<.5)
				animate = false;
			else animate = true;
			break;
		
		default:
			System.err.println("Unrecognized effect " + index + " sent to Neveling ctrlEvent");
			break;
		}
	
}
}
