package floader.visuals;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import floader.looksgood.ani.Ani;
import floader.visuals.alba_francesca_battista.Battista;
import floader.visuals.colorschemes.BlackAndWhite;
import floader.visuals.colorschemes.BlueSunset;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.SeaGreenSeaShell;
import floader.visuals.colorschemes.SpinCyclz;
import floader.visuals.colorschemes.Terminal;
import floader.visuals.flyingobjects.*;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.hardwarecontrollers.AbletonOscCtrlClip;
import floader.visuals.hardwarecontrollers.AbletonOscNote;
import floader.visuals.hardwarecontrollers.ComputerKeyboard;
import floader.visuals.hardwarecontrollers.MonomeMidi;
import floader.visuals.hardwarecontrollers.NanoKontrol2Midi;
import floader.visuals.hardwarecontrollers.NanoKontrol2Osc;
import floader.visuals.kinect.KinectVisual;
import floader.visuals.neveling_untitled_2.Neveling;
import floader.visuals.particles.*;
import floader.visuals.percentages.PercentagesVisual;
import floader.visuals.rectanglearmy.RectangleArmyVisual;
import floader.visuals.shapedance.ShapeDanceVisual;
import floader.visuals.spincycle.SpinCycleVisual;
import floader.visuals.turingfractal.TuringFractalVisual;
import floader.visuals.winter.WinterVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;
import themidibus.*;
import wblut.processing.WB_Render;
import processing.opengl.*;
import remixlab.proscene.*;

public class StartVisual extends PApplet {

	int ctrl0;
	int ctrl1;
	int ctrl2;
	int clipX;
	int clipY;

	boolean changeScene = false;
	int changeSceneIndex = -1;

	boolean midiReady = false;
	boolean applyReset = false;
	AbstractVisual viz;
	OscP5 oscP5;
	MidiBus midiBus;
	PShader edges;
	PApplet offlineApp;
	PImage bgImage;
	Scene scene;
	boolean mirrorTriggered = false;
	boolean applyEdges = false;
	boolean applyCube = false;
	boolean applyBgCapture = false;
	boolean applyTriple = false;
	float cubeRotate;

	boolean applyMirror = false;
	int bgAlpha = 0;
	boolean applyBackground = true;
	// Color schemes
	int curColorSchemeIndex;
	ColorScheme colorSchemes[];

	Ani cameraDistanceAni;
	float maxCameraDistance = 1200;// 2300;
	float minCameraDistance = 200;
	float curCameraDistance = minCameraDistance;

	float dimAmt = 0;

	Ani perspectiveAni;
	float perspective = 0;
	float maxPerspective = .999f;

	Ani lightFallOffAni;
	float lightFallOffAmt = 0;
	float maxLightFallOffAmt = .95f;

	public static final int OSC_PORT = 7400;

	public void setup() {
		size(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);

		// Color
		colorSchemes = new ColorScheme[5];
		colorSchemes[0] = new Terminal();
		colorSchemes[1] = new BlackAndWhite();
		colorSchemes[2] = new BlueSunset();
		colorSchemes[3] = new SeaGreenSeaShell();
		colorSchemes[4] = new SpinCyclz();

		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		// Camera Distance Ani
		cameraDistanceAni = new Ani(this, .5f, "curCameraDistance",
				maxCameraDistance);
		cameraDistanceAni.setEasing(Ani.EXPO_OUT);
		cameraDistanceAni.pause();

		// Perspective Ani
		perspectiveAni = new Ani(this, 0, "perspective", maxPerspective);
		perspectiveAni.setEasing(Ani.EXPO_OUT);
		perspectiveAni.pause();

		// Light falloff Ani
		lightFallOffAni = new Ani(this, .5f, "lightFallOffAmt",
				maxLightFallOffAmt);
		lightFallOffAni.setEasing(Ani.EXPO_OUT);
		lightFallOffAni.setPlayMode(Ani.YOYO);
		lightFallOffAni.pause();

		// Offline drawing
		offlineApp = new PApplet();
		offlineApp.g = createGraphics(VisualConstants.WIDTH,
				VisualConstants.HEIGHT, PApplet.OPENGL);

		// Proscene
		scene = new Scene(this, (PGraphics3D) offlineApp.g);
		scene.disableKeyboardHandling();
		scene.setGridIsDrawn(VisualConstants.PROSCENE_GUIDES_ENABLED);
		scene.setAxisIsDrawn(VisualConstants.PROSCENE_GUIDES_ENABLED);
		// scene.camera().setSceneRadius(maxCameraDistance);
		// scene.camera().setFocusDistance(2000);

		edges = loadShader("edges.glsl");

		oscP5 = new OscP5(this, OSC_PORT);

		if (VisualConstants.NANOKONTROL2MIDI_ENABLED
				|| VisualConstants.MONOMEMIDI_ENABLED)
			midiBus = new MidiBus(this, VisualConstants.MIDI_DEVICE, "");

		// Load the viz - complete
		// viz = new RectangleArmyVisual(offlineApp);
		// viz = new SpinCycleVisual(offlineApp);
		// viz = new HangOnVisual(offlineApp);
		// viz = new Neveling(offlineApp);
		// viz = new WinterVisual(offlineApp);
		viz = new Battista(offlineApp);
		// viz =new PercentagesVisual(offlineApp);
		// viz = new ShapeDanceVisual(offlineApp);

		// todo
		// viz = new FlyingObjectsVisual(this);
		// viz = new LeakierPhysicsVisual(this); //Doesn't seem to work

		offlineApp.g.beginDraw();
		viz.setup();
		offlineApp.g.endDraw();
		reset();
		textureMode(NORMAL);
		midiReady = true;

	}

	void reset() {
		perspectiveAni.pause();
		cameraDistanceAni.pause();
		background(0);
		applyBackground = true;
		perspective = 0;
		scene.camera().setPosition(new PVector(0, 0, maxCameraDistance));
		bgImage = null;
		applyEdges = false;
		viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
		applyMirror = false;
		clipX = VisualConstants.WIDTH;
		clipY = VisualConstants.HEIGHT;
		applyTriple = false;
	}

	public void draw() {
		if (changeScene) {
			changeScene(changeSceneIndex);
			changeScene = false;
		}

		if (applyReset) {
			reset();
			viz.reset();
			applyReset = false;
		}

		background(0);

		// Set camera zoom
		scene.camera().setPosition(
				new PVector(scene.camera().at().x, scene.camera().at().y,
						curCameraDistance));

		float frustrumHeight = (float) (2.0f * curCameraDistance * Math
				.tan(PApplet.radians(scene.camera().fieldOfView() * 0.5f)));

		// 83.3 was calculated by comparing actual height to the computed height
		// to get a rough approximation
		viz.frustrumHeight = frustrumHeight * 85.47008547008547f;
		viz.frustrumWidth = viz.frustrumHeight * scene.camera().aspectRatio();

		// TODO figure out how to use clipping to handle mirror effect
		/*
		 * // offline buffer if(applyMirror){ offlineApp.setBounds(0, 0,
		 * VisualConstants.WIDTH/2, VisualConstants.HEIGHT); offlineApp.clip(0,
		 * 0, VisualConstants.WIDTH / 2, VisualConstants.HEIGHT); } else {
		 * offlineApp.setBounds(0, 0, VisualConstants.WIDTH,
		 * VisualConstants.HEIGHT); offlineApp.noClip(); }
		 */

		// Set global background image
		if (bgImage != null)
			image(bgImage, 0, 0);

		// Set viz background image

		offlineApp.g.beginDraw();

		offlineApp.g.lightFalloff(1 - lightFallOffAmt, 0, 0);
		offlineApp.g.ambientLight(150 - (dimAmt * 128), 150 - (dimAmt * 128),
				150 - (dimAmt * 128));
		offlineApp.g.directionalLight(128 - (dimAmt * 128),
				128 - (dimAmt * 128), 128 - (dimAmt * 128), 0, 0, -1);
		offlineApp.g.lightSpecular(0, 0, 0);
		offlineApp.g.lightFalloff(1.5f, 0, 0);
		offlineApp.g.pointLight(150, 150, 150, 0, 0, 50);

		if (applyBackground)
			offlineApp.g.background(0, 0);

		scene.beginDraw();

		applyPerspective(offlineApp);

		/*
		 * if (!applyBackground) { int rectOuterSize = 40; int rectInnserSize =
		 * 30; int numRects = 50; offlineApp.pushMatrix(); offlineApp.fill(0);
		 * offlineApp.translate(-(rectOuterSize * numRects) / 2, -(rectOuterSize
		 * * numRects) / 2); for (int i = 0; i < numRects; i++) for (int k = 0;
		 * k < numRects; k++) offlineApp.rect(i * rectOuterSize, k *
		 * rectOuterSize, rectInnserSize, rectInnserSize);
		 * offlineApp.popMatrix(); }
		 */

		viz.draw(offlineApp.g);

		scene.endDraw();
		offlineApp.g.endDraw();

		// Clipping
		offlineApp.g.clip(0, 0, clipX, clipY);

		if (applyTriple) {
			pushMatrix();
			translate(VisualConstants.WIDTH / 3, 0);
			image(offlineApp.g, 0, 0);
			popMatrix();
			pushMatrix();
			translate(-VisualConstants.WIDTH / 3, 0);
			image(offlineApp.g, 0, 0);
			popMatrix();
		}

		if (applyMirror) {
			image(offlineApp.g, 0, 0);
			pushMatrix();
			translate(VisualConstants.WIDTH, 0);
			scale(-1f, 1f);
			image(offlineApp.g, 0, 0);
			popMatrix();
		} else {
			image(offlineApp.g, 0, 0);
		}

		if (applyEdges)
			filter(edges);

		if (applyCube) {
			this.pushMatrix();
			this.translate(VisualConstants.WIDTH / 2,
					VisualConstants.HEIGHT / 2 + 10);
			this.scale(238);
			this.rotateX(10);
			this.rotateY(PApplet.radians(cubeRotate));
			TexturedCube(this.g, this.g.get());
			this.popMatrix();
		}

		cubeRotate += .5f;
		cubeRotate = cubeRotate % 360;

		if (applyBgCapture) {
			if (bgImage != null)
				bgImage.blend(this.g, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, PImage.BLEND);
			else
				bgImage = this.g.get(0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT);

			applyBgCapture = false;
		}
	}

	void applyPerspective(PApplet p) {

		float fov = PI / 3.0f;
		float cameraZ = (float) ((VisualConstants.HEIGHT / 2.0f) / Math
				.tan(fov / 2.0f));
		p.perspective(fov, (float) VisualConstants.WIDTH
				/ (float) VisualConstants.HEIGHT * (1 - perspective),

		cameraZ / 10.0f, cameraZ * 50.0f);
	}

	void TexturedCube(PGraphics g, PImage tex) {
		g.beginShape(QUADS);
		g.texture(tex);

		// Given one texture and six faces, we can easily set up the uv
		// coordinates
		// such that four of the faces tile "perfectly" along either u or v, but
		// the other
		// two faces cannot be so aligned. This code tiles "along" u, "around"
		// the X/Z faces
		// and fudges the Y faces - the Y faces are arbitrarily aligned such
		// that a
		// rotation along the X axis will put the "top" of either texture at the
		// "top"
		// of the screen, but is not otherwised aligned with the X/Z faces.
		// (This
		// just affects what type of symmetry is required if you need seamless
		// tiling all the way around the cube)

		// +Z "front" face
		g.vertex(-1, -1, 1, 0, 0);
		g.vertex(1, -1, 1, 1, 0);
		g.vertex(1, 1, 1, 1, 1);
		g.vertex(-1, 1, 1, 0, 1);

		// -Z "back" face
		g.vertex(1, -1, -1, 0, 0);
		g.vertex(-1, -1, -1, 1, 0);
		g.vertex(-1, 1, -1, 1, 1);
		g.vertex(1, 1, -1, 0, 1);

		// +Y "bottom" face
		g.vertex(-1, 1, 1, 0, 0);
		g.vertex(1, 1, 1, 1, 0);
		g.vertex(1, 1, -1, 1, 1);
		g.vertex(-1, 1, -1, 0, 1);

		// -Y "top" face
		g.vertex(-1, -1, -1, 0, 0);
		g.vertex(1, -1, -1, 1, 0);
		g.vertex(1, -1, 1, 1, 1);
		g.vertex(-1, -1, 1, 0, 1);

		// +X "right" face
		g.vertex(1, -1, 1, 0, 0);
		g.vertex(1, -1, -1, 1, 0);
		g.vertex(1, 1, -1, 1, 1);
		g.vertex(1, 1, 1, 0, 1);

		// -X "left" face
		g.vertex(-1, -1, -1, 0, 0);
		g.vertex(-1, -1, 1, 1, 0);
		g.vertex(-1, 1, 1, 1, 1);
		g.vertex(-1, 1, -1, 0, 1);

		g.endShape();
	}

	// TODO make keyboard just another hardware controller
	public void keyPressed() {
		if (VisualConstants.COMPUTERKEYBOARD_ENABLED) {
			ComputerKeyboard.captureToggle(this.key);
			globalEffectChange(ComputerKeyboard.convertKeyPress(this.key),
					ComputerKeyboard.convertKeyPressToValue(ComputerKeyboard
							.convertKeyPress(this.key)));
		}
	}

	public boolean sketchFullScreen() {
		return VisualConstants.FULLSCREEN;
	}

	public static void main(String args[]) {
		PApplet.main("floader.visuals.StartVisual", args);
	}

	public void noteOn(int chan, int note, int vel) {
		if (midiReady) {
			// System.out.println("Channel: " + chan + ", Note: " + note
			// + ", Vel: " + vel);

			int effect;
			float amount = PApplet.map(vel, 0, 127, 0, 1);
			if (VisualConstants.MONOMEMIDI_ENABLED) {
				if (chan == MonomeMidi.NOTE_GLOBAL_KEYBOARD_CHANNEL) {
					globalKeyboardEffect(chan, note, amount);
				} else {
					effect = MonomeMidi.convertNote(chan, note);
					if (VisualConstants.isGlobalEffect(effect))
						globalEffectChange(effect, amount);
					else
						vizEffectChange(effect, amount);
				}
			}
		}
	}

	public void noteOff(int chan, int note, int vel) {
		if (midiReady) {
			// System.out.println("Channel: " + chan + ", Note: " + note
			// + ", Vel: " + vel);

			int effect = MonomeMidi.convertNote(chan, note);
			if (VisualConstants.MONOMEMIDI_ENABLED && effect != -1) {
				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, 0);
				else
					vizEffectChange(effect, 0);
			}
		}
	}

	public void controllerChange(int chan, int num, int val) {
		// Some junk MIDI is being spewed out every time the port is opened by
		// midiBus
		if (midiReady) {
			// System.out.println("Chan: " + chan + ", Ctrl Num: " + num +
			// ", Val: " + val);
			float amount = PApplet.map(val, 0, 127, 0, 1);
			int effect;

			if (VisualConstants.NANOKONTROL2MIDI_ENABLED) {
				effect = NanoKontrol2Midi.convertInputToIndex(chan, num);

				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, amount);
				else
					vizEffectChange(effect, amount);
			}

			if (VisualConstants.MONOMEMIDI_ENABLED) {
				effect = MonomeMidi.convertController(chan, num);
				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, amount);
				else
					vizEffectChange(effect, amount);
			}
		}
	}

	private void vizEffectChange(int index, float amount) {
		viz.ctrlEvent(index, amount);
	}

	private void globalEffectChange(int index, float amount) {
		switch (index) {
		case VisualConstants.GLOBAL_EFFECT_BLUR:

			break;
		case VisualConstants.GLOBAL_EFFECT_CAMDISTANCE:
			float pDistance = Math.abs(curCameraDistance - amount
					* maxCameraDistance);
			cameraDistanceAni.setBegin(curCameraDistance);
			cameraDistanceAni.setEnd((amount * maxCameraDistance)
					+ minCameraDistance);
			cameraDistanceAni
					.setDuration(.5f * (1 / ((pDistance / maxCameraDistance) + .1f)));
			cameraDistanceAni.start();
			break;
		case VisualConstants.GLOBAL_EFFECT_PERSPECTIVE:
			float perspectiveDelta = Math.abs(perspective
					- (amount * maxPerspective));
			perspectiveAni.setBegin(perspective);
			perspectiveAni.setEnd(amount * maxPerspective);
			perspectiveAni
					.setDuration(.5f * (1 / ((perspectiveDelta / maxPerspective) + .1f)));
			perspectiveAni.start();
			break;
		case VisualConstants.GLOBAL_EFFECT_SCALE:
			viz.scale(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEX:
			viz.rotateX(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEY:
			viz.rotateY(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEZ:
			viz.rotateZ(amount);
			break;
		case VisualConstants.GLOBAL_TRIGGER_CUBE:
			if (amount > 0)
				applyCube = !applyCube;
			break;
		case VisualConstants.GLOBAL_TRIGGER_CAPTUREBG:
			if (amount > 0) {
				applyBgCapture = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_TRIPLE:
			if (amount > 0) {
				if (amount <= .5)
					applyTriple = false;
				else
					applyTriple = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION:
			if (amount > 0) {
				if (amount <= .5)
					applyEdges = false;
				else
					applyEdges = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME:
			if (amount > 0) {
				curColorSchemeIndex = ++curColorSchemeIndex
						% colorSchemes.length;
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_RESET:
			if (amount > 0) {
				applyReset = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_MIRROR:
			if (amount > 0) {
				if (amount <= .5)
					applyMirror = false;
				else
					applyMirror = true;
			}

			break;
		case VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL:
			if (amount > 0) {
				if (amount <= .5)
					applyBackground = false;
				else
					applyBackground = true;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_RECTANGLES:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_RECTANGLES;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_PERCENTAGES:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_PERCENTAGES;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_SPINCYCLE:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_SPINCYCLE;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_KINECT:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_KINECT;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_HANGON:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_HANGON;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_NEVELING:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_NEVELING;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_DENSITY:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_DENSITY;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_BATTISTA:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_BATTISTA;
			}
			break;
		case VisualConstants.GLOBAL_SCENE_SHAPEDANCE:
			if (amount > 0) {
				changeScene = true;
				changeSceneIndex = VisualConstants.GLOBAL_SCENE_SHAPEDANCE;
			}
			break;
		case VisualConstants.GLOBAL_EFFECT_CLIPX:
			if (amount < .01)
				clipX = 0;
			else if (amount > .98)
				clipX = VisualConstants.WIDTH;
			else
				clipX = (int) (amount * VisualConstants.WIDTH);
			break;
		case VisualConstants.GLOBAL_EFFECT_CLIPY:
			if (amount < .01)
				clipY = 0;
			else if (amount > .98)
				clipY = VisualConstants.HEIGHT;
			else
				clipY = (int) (amount * VisualConstants.HEIGHT);
			break;
		case VisualConstants.GLOBAL_EFFECT_LIGHTFALLOFF:
			lightFallOffAmt = amount;
			if (lightFallOffAmt > .95)
				lightFallOffAmt = .95f; // Ensure the falloff never drops to 0
			break;
		case VisualConstants.GLOBAL_EFFECT_LIGHTDIM:
			dimAmt = amount;
			break;
		case VisualConstants.GLOBAL_EFFECT_COLOR_0:
			viz.setColorScheme(colorSchemes[0]);
			this.curColorSchemeIndex = 0;
			break;
		case VisualConstants.GLOBAL_EFFECT_COLOR_1:
			viz.setColorScheme(colorSchemes[1]);
			this.curColorSchemeIndex = 1;
			break;
		case VisualConstants.GLOBAL_EFFECT_COLOR_2:
			viz.setColorScheme(colorSchemes[2]);
			this.curColorSchemeIndex = 2;
			break;
		case VisualConstants.GLOBAL_EFFECT_COLOR_3:
			viz.setColorScheme(colorSchemes[3]);
			this.curColorSchemeIndex = 3;
			break;
		case VisualConstants.GLOBAL_EFFECT_COLOR_4:
			viz.setColorScheme(colorSchemes[4]);
			this.curColorSchemeIndex = 4;
			break;
		}
	}

	void globalKeyboardEffect(int chan, int note, float amount) {
		if (amount > 0) {
			// Light falloff Ani
			lightFallOffAmt = 0;
			lightFallOffAni = new Ani(this, 2f, "lightFallOffAmt",
					maxLightFallOffAmt);
			lightFallOffAni.setEasing(Ani.EXPO_OUT);

			lightFallOffAni.setEnd(maxLightFallOffAmt);
			lightFallOffAni.setPlayMode(Ani.BACKWARD);
			// lightFallOffAni.repeat(2);
			// lightFallOffAni.start();
		}
	}

	void oscEvent(OscMessage msg) {
		int effect = -1;
		float value = 0;

		if (msg.checkAddrPattern("/mtn/ctrl")
				&& msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == VisualConstants.ABLETON_OSC_NANOKONTROL_CHANNEL) {
			effect = NanoKontrol2Osc.convertInputToIndex(msg);
			value = PApplet.map(msg.get(VisualConstants.OSC_VALUE_INDEX)
					.intValue(), 0, 127, 0, 1);
		} else if (msg.checkAddrPattern("/mtn/ctrl")
				&& msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == VisualConstants.ABLETON_OSC_CTRL_CHANNEL) {

			effect = AbletonOscCtrlClip.convertInputToIndex(msg);
			value = PApplet.map(msg.get(VisualConstants.OSC_VALUE_INDEX)
					.intValue(), 0, 127, 0, 1);
		} else if (msg.checkAddrPattern("/mtn/note")) {
			if (msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == AbletonOscNote.CLIP_CHANNEL) {
				effect = AbletonOscNote.convertInputToIndex(msg);
				// TODO not sure why the VEL index is different for notes
				value = PApplet.map(msg.get(VisualConstants.OSC_VEL_INDEX)
						.intValue(), 0, 127, 0, 1);
			} else if (msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == AbletonOscNote.KEYBOARD_CHANNEL) {
				value = PApplet.map(msg.get(VisualConstants.OSC_VEL_INDEX)
						.intValue(), 0, 127, 0, 1);
				globalKeyboardEffect(0, 0, value);
				effect = -2; // Effect isn't relevant here
			} else {
				System.err
						.println("Unrecognized OSC channel in StartVisual -> oscEvent ");
			}
		}

		if (effect != -1) {

			if (VisualConstants.isGlobalEffect(effect))
				globalEffectChange(effect, value);
			else
				vizEffectChange(effect, value);
		} else if (effect == -1)
			System.err
					.println("Error converting OSC event in oscEvent(OscMessage msg)");

	}

	void changeScene(int scene) {
		switch (scene) {
		case VisualConstants.GLOBAL_SCENE_RECTANGLES:
			viz = new RectangleArmyVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_PERCENTAGES:
			viz = new PercentagesVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_SPINCYCLE:
			viz = new SpinCycleVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS:
			viz = new FlyingObjectsVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_KINECT:
			viz = new KinectVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_HANGON:
			viz = new HangOnVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_NEVELING:
			viz = new Neveling(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_DENSITY:
			viz = new WinterVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_BATTISTA:
			viz = new Battista(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		case VisualConstants.GLOBAL_SCENE_SHAPEDANCE:
			viz = new ShapeDanceVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
			break;
		}
	}
}
