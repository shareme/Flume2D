package com.flume2d.ai;

public class PathNode
{
	public int x;
	public int y;
	
	public int f;
	public int g;
	public int h;
	public int cost;
	
	public PathNode parent;
	
	public PathNode(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
}