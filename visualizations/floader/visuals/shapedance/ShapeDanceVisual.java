package floader.visuals.shapedance;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;
import floader.looksgood.ani.Ani;

public class ShapeDanceVisual extends AbstractVisual {
	PApplet app;
	PGraphics g;
	float rotateMin = 20;
	float rotateMax = 60;
	public float curRotate = rotateMin;
	Ani rotateAni;
	Ani scaleAni;
	Ani xOffsetAni;
	Ani yOffsetAni;

	float rectWidth;
	float rectHeight;
	float maxRectWidth = 40;
	float minRectWidth = 10;
	float maxRectHeight = 40;
	float minRectHeight = 10;
	float rectPadding = 20;
	int numRows = 12;
	int numCols = 12;
	float rectScale = 1;
	float xOffset = 0;
	float yOffset = 0;
	int xOffsetDir = 1;
	int yOffsetDir = 1;

	public ShapeDanceVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		Ani.setDefaultEasing(Ani.SINE_IN_OUT);
		rotateAni = new Ani(this, .5f, "curRotate", rotateMax);
		rotateAni.pause();
		scaleAni = new Ani(this, .5f, "rectScale", rectScale);
		scaleAni.pause();

		xOffsetAni = new Ani(this, .5f, "xOffset", rectWidth * 2);
		xOffsetAni.pause();

		yOffsetAni = new Ani(this, .5f, "yOffset", rectHeight * 2);
		yOffsetAni.pause();
	}

	public void draw(PGraphics g) {
		this.g = g;
		g.background(0);
		g.rectMode(PApplet.CENTER);
		g.noStroke();

		if (xOffsetAni.isEnded())
			xOffset = 0;
		if (yOffsetAni.isEnded())
			yOffset = 0;

		// TODO mess with width and height
		/*
		 * rectWidth = maxRectWidth - minRectWidth + minRectWidth; rectHeight =
		 * maxRectHeight - minRectHeight + minRectHeight;
		 */
		rectWidth = 30;
		rectHeight = 30;

		// change the Ani's once the widths have changed
		yOffsetAni.setEnd(rectHeight + rectPadding);
		xOffsetAni.setEnd(rectWidth + rectPadding);

		numCols = (int) (this.frustrumWidth / (rectWidth + rectPadding)) / 2 + 3;
		numRows = (int) (this.frustrumHeight / (rectHeight + rectPadding)) / 2 + 3;

		drawRects(1, 1);
		drawRects(-1, -1);
		drawRects(-1, 1);
		drawRects(1, -1);
	}

	void drawRects(int xDir, int yDir) {
		for (int r = 0; r < numRows; r++) {
			g.pushMatrix();
			g.translate(0, yDir * (rectHeight + rectPadding) * r);
			for (int c = 0; c < numCols; c++) {
				g.pushMatrix();
				g.translate(xDir * (rectWidth + rectPadding) * c, 0);
				// Draw First rect
				g.fill(curColorScheme.getColor(0).getRGB());
				g.pushMatrix();
				g.translate(xOffsetDir * xOffset, yOffsetDir * yOffset);
				drawPyramid();
				g.popMatrix();
				g.pushMatrix();
				// Draw Second rect
				g.translate(-(rectWidth + rectPadding) / 2,
						-(rectHeight + rectPadding) / 2);

				g.scale(rectScale);
				g.fill(curColorScheme.getColor(1).getRGB());
				// always draw these under the other rects
				g.translate(0, 0, -1);
				drawPyramid();
				g.popMatrix();

				g.popMatrix();
			}
			g.popMatrix();
		}
	}

	void drawBox() {
		g.pushMatrix();
		g.rotateZ(PApplet.radians(curRotate));
		g.box(rectWidth, rectHeight, rectWidth);
		g.popMatrix();
	}
	
	void drawPyramid() {
		g.pushMatrix();
		g.rotateZ(PApplet.radians(curRotate));
		drawPyramid(rectWidth/2, rectHeight);
		g.popMatrix();
	}

	void drawPyramid(float t, float t2) {
		g.beginShape(PApplet.TRIANGLES);
		g.vertex(-t, -t, -t);
		g.vertex(t, -t, -t);
		g.vertex(0, 0, t2);

		g.vertex(t, -t, -t);
		g.vertex(t, t, -t);
		g.vertex(0, 0, t2);

		g.fill(0);
		g.vertex(t, t, -t);
		g.vertex(-t, t, -t);
		g.vertex(0, 0, t2);

		g.vertex(-t, t, -t);
		g.vertex(-t, -t, -t);
		g.vertex(0, 0, t2);

		g.endShape();
	}

	public void ctrlEvent(int index, float val) {
		// Rotate Z amount
		if (index == VisualConstants.LOCAL_EFFECT_1) {
			if (val > 0) {

				if (val <= .2) {
					rotateAni.setBegin(curRotate);
					rotateAni.setEnd(0);
					rotateAni.start();
				} else if (val > .2 && val <= .4) {
					rotateAni.setBegin(curRotate);
					rotateAni.setEnd(45);
					rotateAni.start();
				} else if (val > .4 && val <= .6) {
					rotateAni.setBegin(curRotate);
					rotateAni.setEnd(90);
					rotateAni.start();
				} else if (val > .6 && val <= .8) {
					rotateAni.setBegin(curRotate);
					rotateAni.setEnd(135);
					rotateAni.start();
				} else if (val > .8) {
					rotateAni.setBegin(curRotate);
					rotateAni.setEnd(180);
					rotateAni.start();
				}
			}
		}
		// Shrink/Grow alternating pattern
		else if (index == VisualConstants.LOCAL_EFFECT_2) {
			if (val > 0) {
				if (val >= .5) {
					scaleAni.setBegin(rectScale);
					scaleAni.setEnd(1);
					scaleAni.start();
				} else if (val < .5) {
					scaleAni.setBegin(rectScale);
					scaleAni.setEnd(0);
					scaleAni.start();
				}
			}
		} else if (index == VisualConstants.LOCAL_EFFECT_3) {
			if (val > 0) {
				if (val <= .5) {
					xOffsetDir = 1;
					xOffsetAni.setBegin(xOffset);
					xOffsetAni.setEnd(rectWidth * 2);
					xOffsetAni.start();
				} else if (val > .5) {
					xOffsetDir = -1;
					xOffsetAni.setBegin(xOffset);
					xOffsetAni.setEnd(-rectWidth * 2);
					xOffsetAni.start();
				}
			}
		} else if (index == VisualConstants.LOCAL_EFFECT_4) {
			if (val > 0) {
				if (val <= .5) {
					yOffsetDir = 1;
					yOffsetAni.setBegin(yOffset);
					yOffsetAni.setEnd(rectHeight * 2);
					yOffsetAni.start();
				} else if (val > .5) {
					yOffsetDir = -1;
					yOffsetAni.setBegin(yOffset);
					yOffsetAni.setEnd(-rectHeight * 2);
					yOffsetAni.start();
				}
			}
		}
	}

	/*
	 * void keyPressed() {
	 * 
	 * 
	 * if(key == 'x') { xOffsetAni.start(); }
	 * 
	 * if(key == 'y') { yOffsetAni.start(); } }
	 */

}
