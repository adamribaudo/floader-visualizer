package floader.visuals.winter;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class WinterVisual extends AbstractVisual {

	PApplet app;
	PGraphics g;
	float diameter = 645;
	float radius;
	int maxNumRows = 4;
	int numRows = maxNumRows;
	int newNumRows = 1;
	int strokeWeight = 3;
	int maxStrokeWeight = 5;

	int numLines = 150;
	int min, max;
	float movement = 0;
	float movementAmount = .003f;
	float maxMovementAmount = .05f;
	float scalar;
	float noiseStart[][][];
	float noiseEnd[][][];
	float rotateX = 80;
	float maxRotateX = 90;
	int curColorIndex = 0;

	ArrayList<PVector> stars = new ArrayList<PVector>();
	float h2;// =height/2
	float w2;// =width/2
	float d2;// =diagonal/2
	float starVar = 0;
	float maxStarVar = 4000;

	public WinterVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		min = 1;
		max = 10000;
		noiseStart = new float[numLines][numRows][numRows];
		noiseEnd = new float[numLines][numRows][numRows];
		for (int i = 0; i < numLines; i++)
			for (int j = 0; j < numRows; j++)
				for (int k = 0; k < numRows; k++) {
					// noiseStart[i][j][k] = random(0, TWO_PI);
					// noiseEnd[i][j][k] = random(0, TWO_PI);
					noiseStart[i][j][k] = app.random(0, PApplet.TWO_PI);
					noiseEnd[i][j][k] = app.noise((float) i / 100,
							(float) j / 100, (float) k / 100) * PApplet.TWO_PI;
				}

	}

	public void draw(PGraphics g) {
		
		this.g = g;
		//g.noLights();
		g.noFill();

		drawLines();
		drawMountains();
		drawStars();
	}

	void drawStars() {
		float width = 5500;
		float height = 900;
		w2 = width / 2;
		h2 = height / 2;
		d2 = PApplet.dist(0, 0, w2, h2);
		g.pushMatrix();
		
		g.translate(-w2, -h2*2, -1100);
		g.noStroke();
		g.fill(curColorScheme.getColor(3).getRGB());

		for (int i = 0; i < 20; i++) { // star init
			stars.add(new PVector(app.random(width), app.random(height), app
					.random(1, 3)));
		}

		for (int i = 0; i < stars.size(); i++) {
			float x = stars.get(i).x;// local vars
			float y = stars.get(i).y;
			float d = stars.get(i).z;

			/* movement+"glitter" */
			stars.set(
					i,
					new PVector(x - PApplet.map(starVar, 0f, width, -0.05f, 0.05f)
							* (w2 - x), y
							- PApplet.map(starVar, 0f, height, -0.05f, 0.05f)
							* (h2 - y), d + 0.2f - 0.6f
							* app.noise(x, y, app.frameCount)));

			if (d > 3 || d < -3)
				stars.set(i, new PVector(x, y, 3));
			if (x < 0 || x > width || y < 0 || y > height)
				stars.remove(i);
			if (stars.size() > 300)
				stars.remove(1);
			g.ellipse(x, y, d + 15, d + 15);// draw stars
		}

		g.popMatrix();
	}

	void drawLines() {
		g.strokeWeight(strokeWeight);
		g.stroke(curColorScheme.getColor(curColorIndex).getRGB());

		numRows = newNumRows;
		movement += movementAmount;

		radius = diameter / 2;

		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numRows; c++) {
				g.pushMatrix();
				g.rotateX(PApplet.radians(rotateX));
				g.translate(((radius * 2) * c), ((radius * 2) * r));

				// Draw lines
				for (int i = 0; i < numLines; i++) {
					// float a = random(0, TWO_PI);

					float a;
					a = noiseStart[i][r][c]
							+ (app.noise(movement + app.noise((float) r / 100)
									+ app.noise(i)) * PApplet.TWO_PI);
					float x1 = radius * PApplet.cos(a);
					float y1 = radius * PApplet.sin(a);
					// a = random(0, TWO_PI);
					a = noiseEnd[i][r][c]
							+ (app.noise(movement - app.noise((float) r / 100)
									+ app.noise(i)) * PApplet.TWO_PI);
					float x2 = radius * PApplet.cos(a);
					float y2 = radius * PApplet.sin(a);

					g.line(x1, y1, x2, y2);
				}
				g.popMatrix();
			}
		}
	}

	void drawMountains() {
		g.pushMatrix();
		g.translate(0, -290, -1100);

		g.noStroke();

		drawPyramid(200, 300, curColorScheme.getColor(1).getRGB(),
				curColorScheme.getColor(2).getRGB());

		// Immediate Siblings
		g.pushMatrix();
		g.translate(-200, 0);
		drawPyramid(200, 200, curColorScheme.getColor(1).getRGB(),
				curColorScheme.getColor(2).getRGB());
		g.popMatrix();

		g.pushMatrix();
		g.translate(200, 0);
		drawPyramid(200, 200, curColorScheme.getColor(1).getRGB(),
				curColorScheme.getColor(2).getRGB());
		g.popMatrix();

		// Furthest out
		g.pushMatrix();
		g.translate(-400, 50);
		drawPyramid(150, 150, curColorScheme.getColor(1).getRGB(),
				curColorScheme.getColor(2).getRGB());
		g.popMatrix();

		g.pushMatrix();
		g.translate(400, 50);
		drawPyramid(150, 150, curColorScheme.getColor(1).getRGB(),
				curColorScheme.getColor(2).getRGB());
		g.popMatrix();

		g.popMatrix();
	}

	public void ctrlEvent(int index, float val) {

		switch (index) {
		case VisualConstants.LOCAL_EFFECT_1:
			// movement amount
			movementAmount = val * maxMovementAmount;
			break;
		case VisualConstants.LOCAL_EFFECT_2:
			starVar = val * maxStarVar;
			break;
		case VisualConstants.LOCAL_EFFECT_3:
			strokeWeight = (int) (val * maxStrokeWeight) + 1;
			break;
		case VisualConstants.LOCAL_EFFECT_4:
			
			break;
		case VisualConstants.LOCAL_EFFECT_5:
			if (val > 0) {
				curColorIndex = curColorIndex + 1 >= curColorScheme.getLength() ? 0
						: curColorIndex + 1;
			}
			break;
		case VisualConstants.LOCAL_EFFECT_6:

			break;
		default:
			System.err.println("Unrecognized effect " + index
					+ " sent to Density ctrlEvent");
			break;
		}

	}

	void drawPyramid(float t, float t2, int color1, int color2) {
		g.pushMatrix();
		g.rotateY(PApplet.radians(45));
		g.rotateX(PApplet.radians(90));
		g.pushMatrix();
		
		g.beginShape(PApplet.TRIANGLES);

		g.fill(color1);

		g.vertex(-t, -t, -t);
		g.vertex(t, -t, -t);

		g.vertex(0, 0, t2);

		g.fill(color1);
		g.vertex(t, -t, -t);
		g.vertex(t, t, -t);

		g.vertex(0, 0, t2);

		g.fill(color1);
		g.vertex(t, t, -t);
		g.vertex(-t, t, -t);

		g.vertex(0, 0, t2);

		g.fill(color2);
		g.vertex(-t, t, -t);
		g.vertex(-t, -t, -t);

		g.vertex(0, 0, t2);

		g.endShape();
		g.popMatrix();
		g.popMatrix();
	}

}