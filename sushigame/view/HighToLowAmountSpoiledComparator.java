package sushigame.view;

import java.util.Comparator;

import sushigame.model.Chef;

public class HighToLowAmountSpoiledComparator implements Comparator<Chef> {

	@Override
	public int compare(Chef a, Chef b) {
		return (int) (Math.round(b.getFoodAmountSpoiled()*100.0) - 
				Math.round(a.getFoodAmountSpoiled()*100));
	}

}
