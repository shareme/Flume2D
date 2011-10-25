package com.flume2d.masks;

import com.flume2d.math.Vector2;

public interface Mask
{
	
	public Vector2 collide(Mask mask);
	public boolean overlaps(Mask mask);
	
	public void setPosition(float x, float y);
	public boolean collideAt(int x, int y);
	
}
