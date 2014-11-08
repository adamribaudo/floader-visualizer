package floader.visuals.neveling_untitled_2;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class Neveling extends AbstractVisual {

	int rows = 1;
	int cols = 1;
	float offsetX;
	float offsetY;
	int sqSize = 266;
	int maxSqSize = 500;
	int minSqSize = 200;
	float sizeDifference = 5;
	float maxSizeDifference = 14;
	float strokeWeight = 1;
	float maxStrokeWeight = 15;
	float movement;
	float movementAmount = .01f;
	float maxMovementAmount = 20;
	
	
	PApplet app;

	public Neveling(PApplet app) {
		this.app = app;
	}

	public void setup() {
		offsetX = 3;
		offsetY = 3;
		
		// animate this
		// sizeDifference = 1;
	}

	public void draw(PGraphics g) {
		
		rows = 4;
		cols = 10;
		
		g.translate(-sqSize * cols/2, -sqSize * rows/2);
		
		g.stroke(curColorScheme.getColor(0).getRGB());
		g.strokeWeight(strokeWeight);
		g.fill(0);

		for (int r = 0; r < rows; r++) {
			// for every column...
			for (int c = 0; c < cols; c++) {
				// choose a new offset
				offsetX = movementAmount; // random(-3,3);
				offsetY = movementAmount; // random(-3,3);
				
				// Draw grid
				g.rect(c * sqSize, r * sqSize, sqSize, sqSize);

				for (int i = 1; i < 20; i++) {
					
					g.rect((c * sqSize) + (i * offsetX), (r * sqSize)
							+ (i * offsetY), sqSize - (i * sizeDifference),
							sqSize - (i * sizeDifference));
				}
			}
		}

	}
	
	public void ctrlEvent(int index, float val) {
		
		switch(index)
		{
		case VisualConstants.LOCAL_EFFECT_1:
			//movement amount
			movementAmount = val * maxMovementAmount;
			break;
		case VisualConstants.LOCAL_EFFECT_2:
			//square size
			sizeDifference = val * maxSizeDifference;
			break;
		case VisualConstants.LOCAL_EFFECT_3:
			
			strokeWeight = val * maxStrokeWeight + 1;
			
			break;
case VisualConstants.LOCAL_EFFECT_4:
			
			sqSize = (int) (val * maxSqSize);
			if(sqSize < minSqSize)
				sqSize = minSqSize;
			
			break;
		default:
			System.err.println("Unrecognized effect " + index + " sent to Neveling ctrlEvent");
			break;
		}
}

}
