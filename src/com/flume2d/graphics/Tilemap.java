package com.flume2d.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tilemap implements Graphic
{
	
	public Tilemap(String filename, int width, int height, int tileWidth, int tileHeight)
	{
		tileset = new Texture(filename);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		columns = (int) Math.ceil(width / tileWidth);
		rows = (int) Math.ceil(height / tileHeight);
		this.width = columns * tileWidth;
		this.height = rows * tileHeight;
		
		tiles = new int[columns * rows];
		for (int i = 0; i < tiles.length; i++)
			tiles[i] = -1;
		dirty = false;
	}

	@Override
	public void render(SpriteBatch b, int x, int y)
	{
		// if the tilemap has changed we redraw it
		if (dirty)
		{
			redraw();
		}
//		b.drawImage(buffer, x, y, x + width, x + height, 0, 0, width, height, null);
	}
	
	private void redraw()
	{
		int tile, tileX, tileY, imgX, imgY;
		int tileCols = tileset.getWidth() / tileWidth;
		for (int i = 0; i < columns; i++)
		{
			for (int j = 0; j < rows; j++)
			{
				tile = tiles[j * columns + i];
				if (tile == -1) continue;
				tileX = tile % tileCols * tileWidth;
				tileY = (int) Math.floor(tile / tileCols) * tileHeight;
				imgX = i * tileWidth;
				imgY = j * tileHeight;
//				g.drawImage(tileset,
//					imgX, imgY, imgX + tileWidth, imgY + tileHeight,
//					tileX, tileY, tileX + tileWidth, tileY + tileHeight,
//					null);
			}
		}
		dirty = false;
	}

	public void setTile(int x, int y, int tile)
	{
		int index = y * columns + x;
		if (index > tiles.length) return;
		
		tiles[index] = tile;
		dirty = true;
	}
	
	public void setRect(int x, int y, int width, int height, int tile)
	{
		for (int j = y; j < y + height; j++)
		{
			for (int i = x; i < x + width; i++)
			{
				setTile(i, j, tile);
			}
		}
		dirty = true;
	}
	
	@Override public void update() { }
	@Override public boolean isActive() { return false; }
	@Override public boolean isVisible() { return true; }
	
	private int columns;
	private int rows;
	
	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	
	private int[] tiles;
	private Texture tileset;
	private boolean dirty;

}
