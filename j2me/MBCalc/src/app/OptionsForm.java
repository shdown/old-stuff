package app;

import javax.microedition.lcdui.*;
import options.*;

public class OptionsForm extends Form implements CommandListener {
	protected Calc midlet;
	protected Command saveCommand = new Command("Save", Command.OK, 1);
	
	protected ChoiceGroup angleUnit = new ChoiceGroup(
		"Angle units",
		ChoiceGroup.EXCLUSIVE,
		new String[] {
			"Radians",
			"Degrees",
		},
		null);
	protected int cgIndexToAngleUnit(int index) {
		switch(index) {
		case 0:
			return OptionsManager.ANGLE_UNIT_RAD;
		case 1:
			return OptionsManager.ANGLE_UNIT_DEG;
		default:
			throw new IllegalArgumentException();
		}
	}
	protected int angleUnitToCgIndex(int angleUnit) {
		switch(angleUnit) {
		case OptionsManager.ANGLE_UNIT_RAD:
			return 0;
		case OptionsManager.ANGLE_UNIT_DEG:
			return 1;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	protected ChoiceGroup autoInsertBraces = new ChoiceGroup(
		"",
		ChoiceGroup.MULTIPLE,
		new String[] {
			"Automatically insert braces after a function token is inserted",
		},
		null);
	
	protected TextField plotterMaxHintChars = new TextField(
		"Maximum plotter hint width in characters",
		"",
		9,
		TextField.NUMERIC);

	public OptionsForm(Calc midlet) {
		super("Options");
		this.midlet = midlet;
		
		append(angleUnit);
		append(autoInsertBraces);
		append(plotterMaxHintChars);
		
		angleUnit.setSelectedIndex(angleUnitToCgIndex(OptionsManager.angleUnit.getValue()), true);
		autoInsertBraces.setSelectedIndex(0, OptionsManager.autoInsertBraces.getValue());
		plotterMaxHintChars.setString(Integer.toString(OptionsManager.plotterMaxHintChars.getValue()));
		
		setCommandListener(this);
		addCommand(saveCommand);
	}
	
	protected void validateAndSave() {
		OptionsManager.angleUnit.setValue(
			cgIndexToAngleUnit(angleUnit.getSelectedIndex()));

		OptionsManager.autoInsertBraces.setValue(
			autoInsertBraces.isSelected(0));

		try {
			OptionsManager.plotterMaxHintChars.setValue(
				Integer.parseInt(plotterMaxHintChars.getString()));
		} catch(NumberFormatException ex) {
			midlet.showAlert(
				"Can't save",
				"Value of '" + plotterMaxHintChars.getLabel() + "' is not an integer");
			return;
		} catch(IllegalArgumentException ex) {
			midlet.showAlert(
				"Can't save",
				"Value of '" + plotterMaxHintChars.getLabel() + "' is illegal: " + ex.getMessage());
			return;
		}

		try {
			OptionsManager.save();
			midlet.showMenu();
		} catch(OptionsException ex) {
			midlet.showMenu();
			midlet.showAlert("Error occurred", "Options were not saved: " + ex.toString());
		}
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == saveCommand) {
			validateAndSave();
		}
	}
}