package de.ioexception.me.ding.view;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.service.BusStation;
import de.ioexception.me.ding.service.DingStations;
import de.ioexception.me.ding.service.ProximitySearchResult;
import de.ioexception.me.ding.view.base.BusStationProvider;
import de.ioexception.me.ding.view.base.Refreshable;
import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * @author Benjamin Erb
 * @author Tobias Schlecht
 * @author Michael Mueller
 */
public final class GeoResultListView extends Form implements CommandListener, BusStationProvider, Refreshable, ItemStateListener
{
	private final Main midlet;

	private final ChoiceGroup choiceGrp;

	private Command screenCommand;
	private Command itemCommand;
	private Command exitCommand;

	private Vector proximitySearchResultList;

	private BusStation selectedBusStation = null;

	public GeoResultListView(Main midlet)
	{
		super(Main.GEO_RESULT_LIST_VIEW_TITLE);

		this.midlet = midlet;

		screenCommand = new Command("Zurück", Command.SCREEN, 1);
		itemCommand = new Command("OK", Command.ITEM, 1);
		exitCommand = new Command("Ende", Command.EXIT, 1);

		addCommand(screenCommand);
		addCommand(itemCommand);
		addCommand(exitCommand);
		
		setCommandListener(this);
		setItemStateListener(this);

		choiceGrp = new ChoiceGroup("Treffer in der Nähe", Choice.EXCLUSIVE, new String[] {}, null);

		append(choiceGrp);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == screenCommand)
		{
			midlet.goBack();
		}
		else if(c == itemCommand)
		{
			gotoStationList();
		}
		else if(c == exitCommand)
		{
			midlet.destroyApp(false);
			midlet.notifyDestroyed();
		}
	}

	public BusStation getSelectedStation()
	{
		return selectedBusStation;
	}

	public void refresh()
	{
		choiceGrp.deleteAll();
		Wgs84Coordinate coordinate = midlet.getPreviousViewCoordinate();
		proximitySearchResultList = DingStations.getStationsByPosition(coordinate, 5);
		
		for(int i = 0; i < proximitySearchResultList.size(); i++)
		{
			ProximitySearchResult result = (ProximitySearchResult) proximitySearchResultList.elementAt(i);
			choiceGrp.append(result.getStation().getName() + " (ca. " + result.getApproximateDistance() + "m)", null);
			choiceGrp.setFont(i, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
		}
	}

	public void gotoStationList()
	{
		if(choiceGrp.size() > 0 && proximitySearchResultList != null && proximitySearchResultList.size() > 0)
		{
			ProximitySearchResult result = (ProximitySearchResult) proximitySearchResultList.elementAt(choiceGrp.getSelectedIndex());
			selectedBusStation = result.getStation();
			midlet.setView(Main.STATION_DETAILS_VIEW);
		}
	}

	public void itemStateChanged(Item c)
	{
		if(c == choiceGrp)
		{
			gotoStationList();
		}
	}
}
