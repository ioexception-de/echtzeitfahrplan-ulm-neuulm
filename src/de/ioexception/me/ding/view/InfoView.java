package de.ioexception.me.ding.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.view.base.Refreshable;

/**
 * Display a simple, scrollable infoscreen with app-informations and license
 * text.
 * 
 * @author Michael Mueller
 */
public final class InfoView extends Form implements CommandListener, Refreshable, ItemStateListener
{
	private final Main midlet;

	private final Command itemCommand;

	private StringItem stringItem = null;
	private StringItem mitItem = null;
	private StringItem privacyItem = null;

	private Font stdFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

	private final String infoTxt = "Diese Anwendung bietet vereinfachten Zugriff auf die Echtzeit-Fahrplandaten der Webseiten der SWU Stadtwerke Ulm/Neu-Ulm GmbH " + "und wurde im Rahmen des Nokia HackCamps 2011 entwickelt.\n\n" + "Alle Angaben ohne Gew�hr.\n\n" + "Die Anwendung stellt kein offizielles Angebot der SWU Stadtwerke Ulm/Neu-Ulm GmbH oder des Donau-Iller-Nahverkehrsverbund GmbH dar.\n" + "Ebenso wenig stehen die Entwickler in Beziehung zu diesen Gesellschaften.\n\n" + "Der Quelltext ist zur freien Verwendung unter MIT-Lizenz ver�ffentlicht:\n\n" + "https://github.com/ioexception-de/\n \n";

	private final String mitLicense = "Copyright (c) 2011\n\n" + "    Benjamin Erb\n    Michael M�ller\n    Tobias Schlecht\n\n" + "Hiermit wird unentgeltlich, jeder Person, die eine Kopie der Software und der zugeh�rigen Dokumentationen (die \"Software\") erh�lt, die Erlaubnis erteilt, uneingeschr�nkt zu benutzen, inklusive und ohne Ausnahme, dem Recht, sie zu verwenden, kopieren, �ndern, fusionieren, verlegen, verbreiten, unterlizenzieren und/oder zu verkaufen, und Personen, die diese Software erhalten, diese Rechte zu geben, unter den folgenden Bedingungen:\n\n" + "Der obige Urheberrechtsvermerk und dieser Erlaubnisvermerk sind in allen Kopien oder Teilkopien der Software beizulegen.\n\n" + "DIE SOFTWARE WIRD OHNE JEDE AUSDR�CKLICHE ODER IMPLIZIERTE GARANTIE BEREITGESTELLT, EINSCHLIESSLICH DER GARANTIE ZUR BENUTZUNG F�R DEN VORGESEHENEN ODER EINEM BESTIMMTEN ZWECK SOWIE JEGLICHER RECHTSVERLETZUNG, JEDOCH NICHT DARAUF BESCHR�NKT. IN KEINEM FALL SIND DIE AUTOREN ODER COPYRIGHTINHABER F�R JEGLICHEN SCHADEN ODER SONSTIGE ANSPR�CHE HAFTBAR ZU MACHEN, OB INFOLGE DER ERF�LLUNG EINES VERTRAGES, EINES DELIKTES ODER ANDERS IM ZUSAMMENHANG MIT DER SOFTWARE ODER SONSTIGER VERWENDUNG DER SOFTWARE ENTSTANDEN.";

	private final String privacy = "Diese Anwendung speichert die Favoritenliste lokal im Speicher des Telefons. F�r die Orts- und Kartenabfragen sowie die Abfahrtszeiten werden Web-Dienste von Microsoft Corporation und SWU Stadtwerke Ulm/Neu-Ulm GmbH genutzt. Hierf�r gelten die dortigen Datenschutzbestimmungen.";

	public InfoView(Main midlet)
	{
		super(Main.INFO_VIEW_TITLE);

		this.midlet = midlet;

		itemCommand = new Command("Zur�ck", Command.ITEM, 1);

		addCommand(itemCommand);

		stringItem = new StringItem("�ber diese Anwendung", infoTxt);
		stringItem.setFont(stdFont);
		append(stringItem);

		privacyItem = new StringItem("Datenschutz", privacy);
		privacyItem.setFont(stdFont);
		append(privacyItem);

		mitItem = new StringItem("Lizenz", mitLicense);
		mitItem.setFont(stdFont);
		append(mitItem);

		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == itemCommand)
		{
			midlet.setView(Main.HOME_VIEW);
		}
	}

	public void refresh()
	{
		
	}

	public void itemStateChanged(Item arg0)
	{
		
	}
}
