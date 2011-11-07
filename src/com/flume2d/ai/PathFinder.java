package com.flume2d.ai;

/* 
 * A* algorithm implementation.
 * Copyright (C) 2007, 2009 Giuseppe Scrivano <gscrivano@gnu.org>

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
			
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

import java.util.*;

import com.flume2d.graphics.Tilemap;

/*
 * Example.
 */
public class PathFinder
{
	
	private PathNode[][] nodes;
	private LinkedList<PathNode> open;
	private LinkedList<PathNode> closed;
	
	private IWalkable walkable;
	
	private static int COST_ORTHOGONAL = 10;
	private static int COST_DIAGONAL = 14;
	
	private int columns;
	private int rows;
	
	public boolean allowDiagonal = false;
	public boolean calculateNearestPoint = false;
	
	public PathFinder(int columns, int rows, IWalkable walkable)
	{
		this.columns = columns;
		this.rows = rows;
		this.walkable = walkable;
		
		// Initialize node array;
		nodes = new PathNode[columns][rows];
		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++)
				nodes[x][y] = new PathNode(x, y);
	}
	
	public List<PathNode> findPath(int startX, int startY, int destX, int destY)
	{
		open = new LinkedList<PathNode>();
		closed = new LinkedList<PathNode>();
		
		// nullify the node parents
		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++)
				nodes[x][y].parent = null;
		
		PathNode start = nodes[startX][startY];
		PathNode dest = nodes[destX][destY];
		
		open.push(start);
		
		start.g = 0;
		start.h = heuristic(start, dest);
		start.f = start.h;
		
		while (open.size() > 0)
		{
			int f = Integer.MAX_VALUE;
			PathNode currentNode = null;
			
			// choose the node with the lesser f cost
			Iterator<PathNode> it = open.iterator();
			while (it.hasNext())
			{
				PathNode node = it.next();
				if (node.f < f)
				{
					currentNode = node;
					f = currentNode.f;
				}
			}
			
			if (currentNode == dest)
			{
				return rebuildPath(currentNode);
			}
			
			open.remove(currentNode);
			closed.push(currentNode);
			
			for (PathNode n : getNeighbors(currentNode))
			{
				// skip nodes that have already been closed
				if (closed.contains(n))
					continue;
				
				int g = currentNode.g + n.cost;
				if (!open.contains(n))
				{
					open.push(n);
					n.parent = currentNode;
					n.g = g;
					n.h = heuristic(n, dest);
					n.f = n.g + n.h;
				}
				else if (g < n.g)
				{
					n.parent = currentNode;
					n.g = g;
					n.h = heuristic(n, dest);
					n.f = n.g + n.h;
				}
			}
		}
		
		if (calculateNearestPoint)
		{
			int min = Integer.MAX_VALUE;
			PathNode nearestNode = null;
			
			for (PathNode c : closed)
			{
				int dist = heuristic(c, dest);
				if (dist < min)
				{
					min = dist;
					nearestNode = c;
				}
			}
			return rebuildPath(nearestNode);
		}
		
		return null;
	}

	private List<PathNode> rebuildPath(PathNode dest) {
		LinkedList<PathNode> path = new LinkedList<PathNode>();
		int dir = 0;
		
		int DIR_HORIZ = 1;
		int DIR_VERT = 2;
		
		if (dest == null)
			return null;
		
		PathNode n = dest;
		while (n.parent != null)
		{
			if (n.y == n.parent.y && dir != DIR_VERT)
			{
				path.push(n);
				dir = DIR_VERT;
			}
			if (n.x == n.parent.x && dir != DIR_HORIZ)
			{
				path.push(n);
				dir = DIR_HORIZ;
			}
			
			n = n.parent;
		}
		
		return path;
	}

	private List<PathNode> getNeighbors(PathNode node)
	{
		int x = node.x;
		int y = node.y;
		PathNode currentNode = null;
		LinkedList<PathNode> neighbors = new LinkedList<PathNode>();
		
		if (x > 0 && walkable.isWalkable(x - 1, y))
		{
			currentNode = nodes[x - 1][y];
			currentNode.cost = COST_ORTHOGONAL;
			neighbors.push(currentNode);
		}
		if (x < columns && walkable.isWalkable(x + 1, y))
		{
			currentNode = nodes[x + 1][y];
			currentNode.cost = COST_ORTHOGONAL;
			neighbors.push(currentNode);
		}
		if (y > 0 && walkable.isWalkable(x, y - 1))
		{
			currentNode = nodes[x][y - 1];
			currentNode.cost = COST_ORTHOGONAL;
			neighbors.push(currentNode);
		}
		if (y < rows && walkable.isWalkable(x, y + 1))
		{
			currentNode = nodes[x][y + 1];
			currentNode.cost = COST_ORTHOGONAL;
			neighbors.push(currentNode);
		}
		if (allowDiagonal)
		{
			if (x > 0 && y > 0 && walkable.isWalkable(x - 1, y - 1))
			{
				currentNode = nodes[x - 1][y - 1];
				currentNode.cost = COST_DIAGONAL;
				neighbors.push(currentNode);
			}
			if (x > 0 && y < rows && walkable.isWalkable(x - 1, y + 1))
			{
				currentNode = nodes[x - 1][y + 1];
				currentNode.cost = COST_DIAGONAL;
				neighbors.push(currentNode);
			}
			if (x < columns && y < rows && walkable.isWalkable(x + 1, y + 1))
			{
				currentNode = nodes[x + 1][y + 1];
				currentNode.cost = COST_DIAGONAL;
				neighbors.push(currentNode);
			}
			if (x < columns && y > 0 && walkable.isWalkable(x + 1, y - 1))
			{
				currentNode = nodes[x + 1][y - 1];
				currentNode.cost = COST_DIAGONAL;
				neighbors.push(currentNode);
			}
		}
		return neighbors;
	}

	private int heuristic(PathNode start, PathNode dest)
	{
		return Math.abs(start.x - dest.x) + Math.abs(start.y - dest.y);
	}

}