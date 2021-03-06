package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;

public class ComputerKeyboard {
	public static boolean edgeToggle = false;
	public static boolean tripleToggle = false;
	public static boolean bgFillToggle = true;
	public static boolean mirrorToggle = false;
	

	public static int convertKeyPress(int key) {
		switch (key) {
		case 'x':
			return VisualConstants.GLOBAL_TRIGGER_CUBE;
		case 'r':
			return VisualConstants.GLOBAL_TRIGGER_RESET;
		case 'c':
			return VisualConstants.GLOBAL_TRIGGER_CAPTUREBG;
		case 'e':
			return VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION;
		case 'm':
			return VisualConstants.GLOBAL_TRIGGER_MIRROR;
		case 'b':
			return VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL;
		case 's':
			return VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME;
		case 't':
			return VisualConstants.GLOBAL_TRIGGER_TRIPLE;
		case '1':
			return VisualConstants.GLOBAL_SCENE_RECTANGLES;
		case '2':
			return VisualConstants.GLOBAL_SCENE_PERCENTAGES;
		case '3':
			return VisualConstants.GLOBAL_SCENE_SPINCYCLE;
		case '4':
			return VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS;
		case '5':
			return VisualConstants.GLOBAL_SCENE_HANGON;
		case '6':
			return VisualConstants.GLOBAL_SCENE_NEVELING;
		case '7':
			return VisualConstants.GLOBAL_SCENE_DENSITY;
		case '8':
			return VisualConstants.GLOBAL_SCENE_BATTISTA;
		case '9':
			return VisualConstants.GLOBAL_SCENE_SHAPEDANCE;
		case '!':
			return VisualConstants.GLOBAL_EFFECT_COLOR_0;
		case '@':
			return VisualConstants.GLOBAL_EFFECT_COLOR_1;
		case '#':
			return VisualConstants.GLOBAL_EFFECT_COLOR_2;
		case '$':
			return VisualConstants.GLOBAL_EFFECT_COLOR_3;
		case '%':
			return VisualConstants.GLOBAL_EFFECT_COLOR_4;
			// Escape key
		case 27:
			return -1;
		}

		System.err
				.println("Unidentified key press in ComputerKeyboard class, convertKeyPress function, key: "
						+ key);
		return -1;
	}

	public static float convertKeyPressToValue(int effect) {
		switch (effect) {
		case VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION:
			if (edgeToggle)
				return 1;
			else
				return .4f;
		case VisualConstants.GLOBAL_TRIGGER_TRIPLE:
			if (tripleToggle)
				return 1;
			else
				return .4f;
		case VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL:
			if (bgFillToggle)
				return 1;
			else
				return .4f;
		case VisualConstants.GLOBAL_TRIGGER_MIRROR:
			if (mirrorToggle)
				return 1;
			else
				return .4f;
		}

		return 1;

	}

	public static void captureToggle(int key) {
		switch (key) {
		case 'e':
			edgeToggle = !edgeToggle;
			break;
		case 't':
			tripleToggle = !tripleToggle;
			break;
		case 'b':
			bgFillToggle = !bgFillToggle;
			break;
		case 'm':
			mirrorToggle = !mirrorToggle;
			break;
		}
	}

}
