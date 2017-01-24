package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;
import oscP5.*;

public class AbletonOscNote {
	
	public static final int CLIP_CHANNEL = 0;
	public static final int KEYBOARD_CHANNEL = 3;
	
	public static int convertInputToIndex(OscMessage msg) {
		if (!msg.checkAddrPattern("/mtn/note"))
			return -1;

		int index = -1;
		int note = msg.get(VisualConstants.OSC_NOTE_INDEX).intValue();

		switch (note) {

		case 0:
			index = VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME;
			break;
		case 1:
			index = VisualConstants.GLOBAL_EFFECT_CLIPY;
			break;
		case 2:
			index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
			break;
		case 3:
			index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
			break;
		case 4:
			index = VisualConstants.LOCAL_EFFECT_1;
			break;
		case 5:
			index = VisualConstants.LOCAL_EFFECT_2;
			break;
		case 6:
			index = VisualConstants.LOCAL_EFFECT_3;
			break;
		case 7:
			index = VisualConstants.LOCAL_EFFECT_4;
			break;
		case 8:
			index = VisualConstants.LOCAL_EFFECT_5;
			break;
		case 9:
			index = VisualConstants.LOCAL_EFFECT_6;
			break;
		case 10:
			index = VisualConstants.LOCAL_EFFECT_7;
			break;
		case 11:
			index = VisualConstants.LOCAL_EFFECT_8;
			break;
		case 12:
			index = VisualConstants.GLOBAL_TRIGGER_CAPTUREBG;
			break;
		case 13:
			index = VisualConstants.GLOBAL_TRIGGER_CUBE;
			break;
		case 14:
			index = VisualConstants.GLOBAL_TRIGGER_RESET;
			break;
		case 15:
			index = VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION;
			break;
		case 16:
			index = VisualConstants.GLOBAL_TRIGGER_MIRROR;
			break;
		case 17:
			index = VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL;
			break;
		case 18:
			index = VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME;
			break;
		case 19:
			index = VisualConstants.GLOBAL_TRIGGER_TRIPLE;
			break;
		case 20:
			index = VisualConstants.GLOBAL_EFFECT_COLOR_0;
			break;
		case 21:
			index = VisualConstants.GLOBAL_EFFECT_COLOR_1;
			break;
		case 22:
			index = VisualConstants.GLOBAL_EFFECT_COLOR_2;
			break;
		case 23:
			index = VisualConstants.GLOBAL_EFFECT_COLOR_3;
			break;
		case 24:
			index = VisualConstants.GLOBAL_EFFECT_COLOR_4;
			break;
		case 100:
			return VisualConstants.GLOBAL_SCENE_RECTANGLES;
		case 101:
			return VisualConstants.GLOBAL_SCENE_PERCENTAGES;
		case 102:
			return VisualConstants.GLOBAL_SCENE_SPINCYCLE;
		case 103:
			return VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS;
		case 104:
			return VisualConstants.GLOBAL_SCENE_HANGON;
		case 105:
			return VisualConstants.GLOBAL_SCENE_NEVELING;
		case 106:
			return VisualConstants.GLOBAL_SCENE_DENSITY;
		case 107:
			return VisualConstants.GLOBAL_SCENE_BATTISTA;
		case 108:
			return VisualConstants.GLOBAL_SCENE_SHAPEDANCE;
		default:
			System.err
					.println("Error: unidentified ctrl num in AbletonOscNoteClip conversion: "
							+ note);
			break;
		}

		return index;
	}
}
