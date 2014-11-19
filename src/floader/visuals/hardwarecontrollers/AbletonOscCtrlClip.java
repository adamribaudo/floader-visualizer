package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;
import oscP5.*;

public class AbletonOscCtrlClip  {
		public static int convertInputToIndex(OscMessage msg)
		{			
			if(!msg.checkAddrPattern("/mtn/ctrl"))
				return -1;
			
			int index = -1;
			int ctrlNum = msg.get(VisualConstants.OSC_CTRL_INDEX).intValue();
			switch (ctrlNum) {
				
			case 1:
				index = VisualConstants.LOCAL_EFFECT_1;
				break;
			case 2:
				index = VisualConstants.LOCAL_EFFECT_2;
				break;
			case 3:
				index = VisualConstants.LOCAL_EFFECT_3;
				break;
			case 4:
				index = VisualConstants.LOCAL_EFFECT_4;
				break;
			case 5:
				index = VisualConstants.LOCAL_EFFECT_5;
				break;
			case 6:
				index = VisualConstants.LOCAL_EFFECT_6;
				break;
			case 10:
				index = VisualConstants.GLOBAL_EFFECT_CLIPX;
				break;
			case 11:
				index = VisualConstants.GLOBAL_EFFECT_CLIPY;
				break;
			case 12:
				index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
				break;
			case 13:
				index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
				break;
			case 14:
				index = VisualConstants.GLOBAL_EFFECT_LIGHTFALLOFF;
				break;
			case 15:
				index = VisualConstants.GLOBAL_EFFECT_SCALE;
				break;
			case 16:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEZ;
				break;
			case 17:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEX;
				break;
			default:
				System.err.println("Error: unidentified ctrl num in AbletonOscCtrlClip conversion: " + ctrlNum);
				break;
			}
			return index;
		}
}
