package de.ioexception.me.ding.view;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.view.base.CoordinateProvider;
import de.ioexception.me.ding.view.items.LoadItem;
import de.ioexception.me.geo.services.geocoder.impl.BingGeoCodingProvider;
import de.ioexception.me.geo.services.geocoder.impl.GeoCodingCallback;
import de.ioexception.me.geo.services.geocoder.impl.GeoCodingResult;
import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * @author Benjamin Erb
 * @author Tobias Schlecht
 * @author Michael Mueller
 */
public final class GeocodingSearchView extends Form implements CommandListener, ItemStateListener, CoordinateProvider
{
	private final Main midlet;

	private final BingGeoCodingProvider geoCodingProvider;

	private final TextField searchField;
	private final ChoiceGroup choiceGrp;

	private final Command screenCommand;
	private final Command searchCommand;

	private final LoadItem loadItem = new LoadItem();

	private Vector lastResults = new Vector();

	private Wgs84Coordinate selectedCoordinate = null;

	public GeocodingSearchView(Main midlet)
	{
		super(Main.GEOCODING_SEARCH_VIEW_TITLE);

		this.midlet = midlet;

		screenCommand = new Command("Zurück", Command.SCREEN, 1);
		searchCommand = new Command("Suche", Command.ITEM, 1);

		addCommand(screenCommand);
		addCommand(searchCommand);
		setCommandListener(this);

		searchField = new TextField("Suche nach Ort", "", 64, TextField.ANY);
		append(searchField);

		geoCodingProvider = new BingGeoCodingProvider();

		choiceGrp = new ChoiceGroup("Vorschläge", Choice.EXCLUSIVE, new String[] {}, null);

		this.setItemStateListener(this);
	}

	public void commandAction(Command c, Displayable d)
	{
		final Form that = this;

		if(c == screenCommand)
		{
			midlet.goBack();
		}
		else if(c == searchCommand)
		{
			if(searchField.getString().length() > 2)
			{
				choiceGrp.deleteAll();

				removeItem(choiceGrp);

				append(loadItem);

				geoCodingProvider.geocode(searchField.getString(), "de", new GeoCodingCallback()
				{
					public void handleResults(final Vector results)
					{
						Display.getDisplay(midlet).callSerially(new Runnable()
						{
							public void run()
							{
								for(int i = 0; i < results.size(); i++)
								{
									choiceGrp.append(((GeoCodingResult) results.elementAt(i)).getAddressName(), null);
									choiceGrp.setFont(i, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
								}

								if(results.size() > 0)
								{
									setLastResults(results);
								}
								
								removeItem(loadItem);

								that.append((Item) choiceGrp);
							}
						});
					}
				});
			}
		}
	}

	private void setLastResults(Vector results)
	{
		this.lastResults = results;
	}

	public void itemStateChanged(Item c)
	{
		if(c == choiceGrp)
		{
			if(lastResults != null && lastResults.size() > 0)
			{
				GeoCodingResult result = (GeoCodingResult) lastResults.elementAt(choiceGrp.getSelectedIndex());
				selectedCoordinate = result.getPosition();
			}
			
			midlet.setView(Main.GEO_RESULT_LIST_VIEW);
		}
		else if(c == searchField)
		{
			if(searchField.getString().equals(""))
			{
				choiceGrp.deleteAll();

				removeItem(choiceGrp);
			}
		}
	}

	public Wgs84Coordinate getSelectedCoordinate()
	{
		return selectedCoordinate;
	}

	private void removeItem(Item item)
	{
		boolean found = false;
		
		for(int i = 0; i < this.size() && !found; i++)
		{
			if(this.get(i).equals(item))
			{
				this.delete(i);
				found = true;
			}
		}
	}
}
