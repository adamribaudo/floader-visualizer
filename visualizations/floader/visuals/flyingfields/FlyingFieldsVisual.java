package floader.visuals.flyingfields;

import processing.core.PApplet;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;

public class FlyingFieldsVisual extends AbstractVisual implements IVisual {

	PApplet app;
	boolean firstPlay = true;
	boolean played = false;
	int timeOffset;
	int playHead;

	boolean[] triggers;
	int[] fills;
	float[] widths;
	float[] heights;
	float[] startDepths;
	float[] endDepths;
	float[] translateInnerShapeX;
	float[] translateInnerShapeY;
	float translateInnerShapeRadius = 100;
	float[] faceColors;
	float cameraSpeed = .2f;
	float cameraZ = 0;
	float objectStartZ = 0;

	float colorHue = 0;
	float colorHueChange = .001f;

	float perspectiveX = 1;

	float maxWidth = 600;
	float maxHeight = 200;
	float minWidth = 1;
	float minHeight = 1;
	int[] events = new int[1000];
	  

	boolean drawCurvedMesh = false;
	
	public FlyingFieldsVisual(PApplet app) {
		this.app = app;
	}
	
	public void setup()
	{
	  app.background(0);
	  
	  triggers = new boolean[events.length];
	  fills = new int[events.length];
	  startDepths = new float[events.length];
	  endDepths = new float[events.length];
	  widths = new float[events.length];
	  heights = new float[events.length];
	  translateInnerShapeX = new float[events.length];
	  translateInnerShapeY = new float[events.length];

	  faceColors = new float[events.length];
	}
	
	public void draw()
	{
	  app.colorMode(PApplet.HSB, 1);
	  app.background(.5f);
	  float fov = PApplet.PI/3.0f;
	 
	  playHead = app.frameCount;
	  
	    cameraZ = -playHead - 500;
	    objectStartZ = -playHead;

	    
	app.pushMatrix();
	    app.stroke(1);
	    //fill(0);
	    app.translate(0, 0, 1000);
	   scale(3);
	    
	   // rotateZ(470/100.0 - frameCount/1000.0);
	    //rotateY(430/100.0  - frameCount/1000.0);
	    app.rotateZ(470/100.0f);
	    app.rotateY(430/100.0f);
	    //drawGeoDisc();
	    app.popMatrix();

	    app.pushMatrix();
	    app.translate(0, 0, -cameraZ);

	    executeGlobalEvents();
	    executeEvents();
	    incrementColorHue();

	    app.popMatrix();
	  }

	public void ctrlEvent(int index, float val) {
		if (index == VisualConstants.LOCAL_EFFECT_1 ) {
			//start box
			
		}
	}

int findMaxEvent()
{
  int maxEvent = -1;

  for (int i=0; i<events.length; i++)
  {
    if (events[i] <= playHead)
      maxEvent = i;
    else return maxEvent;
  }

  return maxEvent;
}

void incrementColorHue()
{
  colorHue += colorHueChange;
  if (colorHue>1)colorHue = 0;
}

int findFirstEvent()
{
  int firstEvent = findMaxEvent() - 12;
  return firstEvent < 0 ? 0 : firstEvent;
}

/*
Ideas for global events
 -bg color change
 -shape color change
 -shape translate
 */

float translateInnerShapeXCue = 0;
float translateInnerShapeYCue = 0;

void executeGlobalEvents()
{
  if (playHead >= 0 && playHead < 739)
  {
    maxWidth = 400;
    maxHeight = 200;
  } else if (playHead >= 739 && playHead < 2247)
  {
  } else if (playHead >= 2247 && playHead < 3738)
  {
    translateInnerShapeXCue = -200;
  } else if (playHead >= 3738 && playHead < 5237)
  {
    translateInnerShapeXCue = 200;
  } else if (playHead >= 5237 && playHead < 6749)
  {
    translateInnerShapeXCue = 0;
  
  } else if (playHead >= 6749 && playHead < 6847)
  {
    perspectiveX = 100;
  } else if (playHead >= 6846 && playHead < 8200)
  {

    perspectiveX = 1;
  } else if (playHead >= 8200 && playHead < 8825)
  {
      drawCurvedMesh = true;
    } else if (playHead >= 8825)
  {
    minWidth = 1;
    maxWidth = 600;
    minHeight = 1;
    maxHeight = 200;
  }

  
}

public void executeEvents()
{
  for (int i=findMaxEvent (); i>=findFirstEvent (); i--)
  {
    if (!triggers[i])
    {
      fills[i] = (int)app.random(255);
      startDepths[i] = objectStartZ;
      triggers[i] = true;
      widths[i] = app.random(minWidth, maxWidth);
      heights[i] = app.random(minHeight, maxHeight);

      //Translate inner shape
      translateInnerShapeX[i] = translateInnerShapeXCue;
      translateInnerShapeY[i] = translateInnerShapeYCue;

      faceColors[i] = colorHue;

      if (i > 0)
      {
        endDepths[i-1] = startDepths[i] - startDepths[i-1];
      }
    }

    float endDepthPoint=0;

    app.pushMatrix();
    app.translate(0, 0, startDepths[i]  +100);

    float faceColor = 0;
    //If shape is the current shape
    if (i == findMaxEvent())
    {
      faceColor = colorHue;

      endDepthPoint = objectStartZ - startDepths[i] - 1;
    }
    //process older shapes
    else if (findMaxEvent() > 0)
    {
      faceColor = faceColors[i];
      endDepthPoint = endDepths[i];
    }
    //fill(fills[i]);
    app.noStroke();
    app.pushMatrix();
    app.translate(translateInnerShapeX[i], translateInnerShapeY[i], 0);
    drawBox(widths[i], heights[i], endDepthPoint, faceColor, .8f, .9f, 0, 0, 0, 0, 0, .3f);
    app.popMatrix();

    app.stroke(1);
    app.scale(10);
    app.noFill();
    app.rotateZ(PApplet.radians(widths[i]));

    if(drawCurvedMesh)
      drawCurveHeMesh();
    
    //drawGeoDisc();
    app.popMatrix();
  }
}

void drawBox(float boxWidth, float boxHeight, float endDepthPoint, float faceR, float faceG, float faceB, float startR, float startG, float startB, float endR, float endG, float endB)
{
	app.beginShape(PApplet.QUADS);
	app.fill(endR, endG, endB);
	app.vertex(-boxWidth/2, boxHeight/2, 0);
	app.vertex( boxWidth/2, boxHeight/2, 0);
	app.vertex( boxWidth/2, -boxHeight/2, 0);
	app.vertex(-boxWidth/2, -boxHeight/2, 0);

	app.fill(endR, endG, endB);
	app.vertex( boxWidth/2, boxHeight/2, 0);
	app.fill(startR, startG, startB);
	app.vertex( boxWidth/2, boxHeight/2, endDepthPoint);
	app.vertex( boxWidth/2, -boxHeight/2, endDepthPoint);
	app.fill(endR, endG, endB);
	app.vertex( boxWidth/2, -boxHeight/2, 0);

	app.fill(faceR, faceG, faceB);
	app.vertex( boxWidth/2, boxHeight/2, endDepthPoint);
	app.vertex(-boxWidth/2, boxHeight/2, endDepthPoint);
	app.vertex(-boxWidth/2, -boxHeight/2, endDepthPoint);
	app.vertex( boxWidth/2, -boxHeight/2, endDepthPoint);

	app.fill(startR, startG, startB);
	app.vertex(-boxWidth/2, boxHeight/2, endDepthPoint);
	app.fill(endR, endG, endB);
	app.vertex(-boxWidth/2, boxHeight/2, 0);
	app.vertex(-boxWidth/2, -boxHeight/2, 0);
	app.fill(startR, startG, startB);
	app.vertex(-boxWidth/2, -boxHeight/2, endDepthPoint);

	app.fill(startR, startG, startB);
	app.vertex(-boxWidth/2, boxHeight/2, endDepthPoint);
	app.vertex( boxWidth/2, boxHeight/2, endDepthPoint);
	app.fill(endR, endG, endB);
	app.vertex( boxWidth/2, boxHeight/2, 0);
	app.vertex(-boxWidth/2, boxHeight/2, 0);

	app.fill(startR, startG, startB);
	app.vertex(-boxWidth/2, -boxHeight/2, endDepthPoint);
	app.vertex( boxWidth/2, -boxHeight/2, endDepthPoint);
	app.fill(endR, endG, endB);
	app.vertex( boxWidth/2, -boxHeight/2, 0);
	app.vertex(-boxWidth/2, -boxHeight/2, 0);
	app.endShape(PApplet.CLOSE);
}
void drawCurveHeMesh()
{
  app.beginShape();
  app.curveVertex(30.0f, -2.706345629988333E-15f, -50.0f);
  app.curveVertex(30.0f, 3.416888365748433E-15f, 50.0f);
  app.curveVertex(9.270509831248425f, -28.531695488854602f, 50.0f);
  app.curveVertex(9.270509831248425f, -28.53169548885461f, -50.0f);
  app.curveVertex(9.270509831248425f, -28.531695488854602f, 50.0f);
  app.curveVertex(-24.27050983124842f, -17.633557568774194f, 50.0f);
  app.curveVertex(-24.27050983124842f, -17.6335575687742f, -50.0f);
  app.curveVertex(-24.27050983124842f, -17.633557568774194f, 50.0f);
  app.curveVertex(-24.270509831248425f, 17.633557568774194f, 50.0f);
  app.curveVertex(-24.270509831248425f, 17.633557568774187f, -50.0f);
  app.curveVertex(-24.270509831248425f, 17.633557568774194f, 50.0f);
  app.curveVertex(9.270509831248418f, 28.531695488854613f, 50.0f);
  app.curveVertex(9.270509831248418f, 28.531695488854606f, -50.0f);
  app.curveVertex(9.270509831248418f, 28.531695488854613f, 50.0f);
  app.curveVertex(30.0f, -2.706345629988333E-15f, -50.0f);

  app.endShape();
}


	
}
