package de.ioexception.me.ding.view;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.view.base.CoordinateProvider;
import de.ioexception.me.ding.view.base.Refreshable;
import de.ioexception.me.geo.services.MappingService;
import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.geo.services.maps.impl.BingMapsProvider;
import de.ioexception.me.geo.util.GeoConversion;
import de.ioexception.me.geo.util.Point;
import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * @author Tobias Schlecht
 */
public final class MapSearchView extends Canvas implements CoordinateProvider, Refreshable
{
	private static final int MIN_ZOOM = 9;
	private static final int MAX_ZOOM = 18;
	private static final int INIT_ZOOM = 15;
	private static final int PIXEL_VARIATION = 15;

	private final Main midlet;
	private final MappingService mappingService;

	private int zoom;

	private double mapCenterLat;
	private double mapCenterLng;

	private int pinX;
	private int pinY;

	private Image navigation = null;
	private Image pressedButton = null;
	private Image inactiveButton = null;
	private Image mapImage = null;
	private Image pin = null;
	private Image loadingDark = null;

	private int pointerPressedX;
	private int pointerPressedY;

	private int pressedButtonField = 0;

	private boolean isRefreshing = false;
	private boolean isChangingView = false;
	private boolean isChangeOriginHome = false;
	private boolean isLoading = false;

	public MapSearchView(Main midlet, Wgs84Coordinate initCenter)
	{
		super();

		setFullScreenMode(true);

		this.midlet = midlet;

		zoom = INIT_ZOOM;

		mapCenterLat = initCenter.getLatitude();
		mapCenterLng = initCenter.getLongitude();

		pinX = getWidth() / 2;
		pinY = getHeight() / 2;

		mappingService = new BingMapsProvider();
	}

	public void refresh()
	{
		if(mapImage == null)
		{
			isRefreshing = true;
			loadMap();
		}
	}

	private void loadMap()
	{
		isLoading = true;
		repaint();

		mappingService.getMap(mapCenterLat, mapCenterLng, zoom, getWidth(), getHeight(), new MapsCallback()
		{
			public void handleMap(final Image image)
			{
				midlet.getDisplay().callSerially(new Runnable()
				{
					public void run()
					{
						setMap(image);

						if(isLoading)
						{
							isLoading = false;
						}

						if(isChangeOriginHome)
						{
							isChangeOriginHome = false;
						}

						repaint();
					}
				});
			}
		});
	}

	private void setMap(Image mapImage)
	{
		if(isRefreshing)
		{
			isRefreshing = false;
		}

		this.mapImage = mapImage;
	}

	public boolean isChangeOriginHome()
	{
		return isChangeOriginHome;
	}

	public void isChangeOriginHome(boolean isChangeOriginHome)
	{
		this.isChangeOriginHome = isChangeOriginHome;
	}

	public void paint(Graphics g)
	{
		if(isLoading)
		{
			if(loadingDark == null)
			{
				try
				{
					loadingDark = Image.createImage("/loadingDark.png");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			g.drawImage(loadingDark, 0, 0, Graphics.TOP | Graphics.LEFT);
			g.setColor(0x2b80be);
			g.fillRect(0, (getHeight() / 2) - 30, getWidth(), 60);
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
			g.setColor(0xFFFFFF);
			g.drawString("Lade Karte...", getWidth() / 2, (getHeight() / 2) - 14, Graphics.HCENTER | Graphics.TOP);

			return;
		}

		if(isChangingView)
		{
			g.setColor(0xffffff);
			g.fillRect(0, 0, getWidth(), getHeight());

			isChangingView = false;

			return;
		}

		if(mapImage != null)
		{
			g.drawImage(mapImage, 0, 0, Graphics.TOP | Graphics.LEFT);
		}

		if(pin == null)
		{
			try
			{
				pin = Image.createImage("/pin.png");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		g.drawImage(pin, pinX, pinY, Graphics.HCENTER | Graphics.BOTTOM);

		if(pressedButton == null)
		{
			try
			{
				pressedButton = Image.createImage("/pressedButton.png");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		if(pressedButtonField != 0)
		{
			if(pressedButtonField == 1)
			{
				g.drawImage(pressedButton, -1, 0, Graphics.TOP | Graphics.LEFT);
			}
			else if(pressedButtonField == 2)
			{
				g.drawImage(pressedButton, 59, 0, Graphics.TOP | Graphics.LEFT);
			}
			else if(pressedButtonField == 3)
			{
				g.drawImage(pressedButton, 119, 0, Graphics.TOP | Graphics.LEFT);
			}
			else if(pressedButtonField == 4)
			{
				g.drawImage(pressedButton, 179, 0, Graphics.TOP | Graphics.LEFT);
			}

			pressedButtonField = 0;
		}

		if(navigation == null)
		{
			try
			{
				navigation = Image.createImage("/mapNavigation.png");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		g.drawImage(navigation, 0, 0, Graphics.TOP | Graphics.LEFT);

		if(zoom == MIN_ZOOM || zoom == MAX_ZOOM)
		{
			if(inactiveButton == null)
			{
				try
				{
					inactiveButton = Image.createImage("/inactiveButton.png");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			if(zoom == MIN_ZOOM)
			{
				g.drawImage(inactiveButton, 60, 1, Graphics.TOP | Graphics.LEFT);
			}
			else if(zoom == MAX_ZOOM)
			{
				g.drawImage(inactiveButton, 120, 1, Graphics.TOP | Graphics.LEFT);
			}
		}
	}

	private Wgs84Coordinate getLatLng(int x, int y)
	{
		Point globalPoint = GeoConversion.latLngToPixel(new Wgs84Coordinate(mapCenterLat, mapCenterLng), zoom);

		double globalX = globalPoint.getX();
		double globalY = globalPoint.getY();

		globalX = (globalX - (getWidth() / 2)) + x;
		globalY = (globalY - (getHeight() / 2)) + y;

		return GeoConversion.pixelToLatLng(new Point((int) globalX, (int) globalY), zoom);
	}

	private Point getPixel(double latitude, double longitude)
	{
		Point point = GeoConversion.latLngToPixel(new Wgs84Coordinate(latitude, longitude), zoom);
		Point centerPoint = GeoConversion.latLngToPixel(new Wgs84Coordinate(mapCenterLat, mapCenterLng), zoom);

		int deltaX = point.getX() - centerPoint.getX();
		int deltaY = point.getY() - centerPoint.getY();

		return new Point((getWidth() / 2) + deltaX, (getHeight() / 2) + deltaY);
	}

	public Wgs84Coordinate getSelectedCoordinate()
	{
		return getLatLng(pinX, pinY);
	}

	public void pointerPressed(int x, int y)
	{
		if(isLoading)
		{
			if(isChangeOriginHome)
			{
				isChangeOriginHome = false;
			}

			return;
		}

		pointerPressedX = x;
		pointerPressedY = y;

		if(y <= 45)
		{
			if(x < 60)
			{
				pressedButtonField = 1;
				repaint();
				serviceRepaints();
				isChangingView = true;
				repaint();
				midlet.goBack();
			}
			else if(x < 120)
			{
				if(zoom > MIN_ZOOM)
				{
					pressedButtonField = 2;
					repaint();
					serviceRepaints();

					Wgs84Coordinate wgs84Coordinate = getLatLng(pinX, pinY);

					zoom--;

					Point point = getPixel(wgs84Coordinate.getLatitude(), wgs84Coordinate.getLongitude());

					pinX = point.getX();
					pinY = point.getY();

					loadMap();
				}
			}
			else if(x < 180)
			{
				if(zoom < MAX_ZOOM)
				{
					pressedButtonField = 3;
					repaint();
					serviceRepaints();

					Wgs84Coordinate wgs84Coordinate = getLatLng(pinX, pinY);

					zoom++;

					Point point = getPixel(wgs84Coordinate.getLatitude(), wgs84Coordinate.getLongitude());

					pinX = point.getX();
					pinY = point.getY();

					loadMap();
				}
			}
			else
			{
				pressedButtonField = 4;
				repaint();
				serviceRepaints();
				isChangingView = true;
				repaint();
				midlet.setView(Main.GEO_RESULT_LIST_VIEW);
			}
		}
	}

	public void pointerReleased(int x, int y)
	{
		if(isChangeOriginHome)
		{
			isChangeOriginHome = false;

			return;
		}

		if(isRefreshing || isLoading)
		{
			return;
		}

		if(x < pointerPressedX + PIXEL_VARIATION && x > pointerPressedX - PIXEL_VARIATION && y < pointerPressedY + PIXEL_VARIATION && y > pointerPressedY - PIXEL_VARIATION)
		{
			if(y > 45)
			{
				pinX = x;
				pinY = y;

				repaint();
			}
		}
		else
		{
			pinX = pinX + (x - pointerPressedX);
			pinY = pinY + (y - pointerPressedY);

			Wgs84Coordinate newMapCenter = getLatLng((getWidth() / 2) - (x - pointerPressedX), (getHeight() / 2) - (y - pointerPressedY));

			mapCenterLat = newMapCenter.getLatitude();
			mapCenterLng = newMapCenter.getLongitude();

			loadMap();
		}
	}

	public void keyPressed(int keyCode)
	{
		if(isRefreshing || isLoading)
		{
			return;
		}

		if(keyCode == Canvas.KEY_NUM5 || keyCode == Canvas.FIRE)
		{
			isChangingView = true;
			repaint();
			midlet.setView(Main.GEO_RESULT_LIST_VIEW);
		}
		else if(keyCode == Canvas.KEY_NUM1 || keyCode == Canvas.KEY_NUM3 || keyCode == Canvas.KEY_NUM7 || keyCode == Canvas.KEY_NUM9)
		{
			if(keyCode == Canvas.KEY_NUM1)
			{
				pinX = pinX - (getWidth() / 8);
				pinY = pinY - (getHeight() / 8);
			}
			else if(keyCode == Canvas.KEY_NUM3)
			{
				pinX = pinX + (getWidth() / 8);
				pinY = pinY - (getHeight() / 8);
			}
			else if(keyCode == Canvas.KEY_NUM7)
			{
				pinX = pinX - (getWidth() / 8);
				pinY = pinY + (getHeight() / 8);
			}
			else if(keyCode == Canvas.KEY_NUM9)
			{
				pinX = pinX + (getWidth() / 8);
				pinY = pinY + (getHeight() / 8);
			}

			repaint();
		}
		else
		{
			int deltaX = 0;
			int deltaY = 0;

			if(keyCode == Canvas.KEY_NUM2 || keyCode == Canvas.UP)
			{
				deltaX = 0;
				deltaY = (int) -(getWidth() / 3);
			}
			else if(keyCode == Canvas.KEY_NUM8 || keyCode == Canvas.DOWN)
			{
				deltaX = 0;
				deltaY = (int) (getWidth() / 3);
			}
			else if(keyCode == Canvas.KEY_NUM4 || keyCode == Canvas.LEFT)
			{
				deltaX = (int) -(getWidth() / 3);
				deltaY = 0;
			}
			else if(keyCode == Canvas.KEY_NUM6 || keyCode == Canvas.RIGHT)
			{
				deltaX = (int) (getWidth() / 3);
				deltaY = 0;
			}

			pinX = pinX - deltaX;
			pinY = pinY - deltaY;

			Wgs84Coordinate newMapCenter = getLatLng((getWidth() / 2) + deltaX, (getHeight() / 2) + deltaY);

			mapCenterLat = newMapCenter.getLatitude();
			mapCenterLng = newMapCenter.getLongitude();

			loadMap();
		}
	}
}
