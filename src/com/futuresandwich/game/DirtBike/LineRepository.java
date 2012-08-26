package com.futuresandwich.game.DirtBike;


public class LineRepository {
	private DynamicLine[] list;
	private final int mMaxLines; 
	private int lastLine = -1;
	public LineRepository(int maxlines)
	{
		this.mMaxLines = maxlines;
		list = new DynamicLine[maxlines];
	}
	
	public DynamicLine[] getLines()
	{
		return list;
	}
	
	public DynamicLine addLine(DynamicLine line)
	{
		DynamicLine leftover = null;
		if(++lastLine>= mMaxLines)
			lastLine = 0;
		if(list[lastLine] != null)
		{
			leftover = list[lastLine];
		}
		list[lastLine] = line;
		return leftover;
	}
	
	public DynamicLine get(int i)
	{
		return list[i];
	}
	
	public DynamicLine getLatest()
	{
		return list[lastLine];
	}
}
